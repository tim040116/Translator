package etec.src.sql.azure.translater;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.Mark;
import etec.common.utils.RegexTool;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.convert_safely.ConvertSubQuerySafely;
import etec.common.utils.convert_safely.SplitCommaSafely;
import etec.framework.translater.exception.SQLFormatException;
import etec.framework.translater.exception.UnknowSQLTypeException;
import etec.src.sql.td.model.SelectTableModel;

/**
 * @author	Tim
 * @since	2023年12月19日
 * @version	4.0.0.0
 * 
 * <h1>查詢語法轉換</h1>
 * 
 * <br>此類別並不是用來轉換SQL裡的語法
 * <br>若需要轉換語法請使用
 * <br>{@link etec.src.sql.azure.translater.SQLTranslater}
 * <br>
 * <br>此類別著重於SQL語句的拆解，
 * <br>負責將sub query,join,union,with...等
 * <br>複合型語句拆解成單純的查詢語句
 * */
public class DQLTranslater {
	
	/**
	 * <h1>統整DQL的所有轉換</h1>
	 * <p>qualify row_number over 處理</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2023年12月26日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @see
	 * @param	script 單一段完整的SQL語法
	 * @throws	UnknowSQLTypeException
	 * @throws	SQLFormatException
	 * @return	String	轉換完成的SQL
			 */
	public String easyReplace(String script) throws UnknowSQLTypeException, SQLFormatException {		
		String res = AzureTranslater.sql.easyReplase(script);
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		res = csqs.savelyConvert(res, (t)->{
			try {
				t = changeQualifaRank(t);//Qualify Row Number
			} catch (UnknowSQLTypeException e) {
				e.printStackTrace();
			}
			return t;
		});
		return res;
	}
	
	
	/**
	 * @author	Tim
	 * @throws UnknowSQLTypeException 
	 * @since	2023年12月19日
	 * 
	 * <h1>QUALIFY ROW_NUMBER()語法轉換</h1>
	 * 
	 * */
	public String changeQualifaRank(String sql) throws UnknowSQLTypeException {
		if(!RegexTool.contains("(?i)QUALIFY\\s+ROW_NUMBER",sql)) {
			return sql;
		}
		String res = "";
		String temp = sql;
		/*
		 *  這個語法是建立在沒有sub query的前提下
		 *  所以要先處理子查詢
		 *  
		 * 第一步要先將UNION語法分段 
		 * 再把一些階段的關鍵字加上標記
		 *再用split切分 
		 * 
		 * */
		//安插標記
		String[] arrSplitType = {//所有特殊階段的關鍵字
			"SEL(?:ECT)?"
			,"WITH"
			,"FROM"
//			,"((OUTER|INNER|LEFT|RIGHT|CROSS)\\s+)?JOIN"
//			,"UNION"
			,"WHERE"
//			,"GROUP\\s+BY"
//			,"ORDER\\s+BY"
			,"QUALIFY\\s+ROW_NUMBER"
		};
		String regKey = "(?i)"+arrSplitType[0];
		for(int i = 1; i<arrSplitType.length;i++) {
			regKey+="|"+arrSplitType[i];
		}
		temp = temp
				.replaceAll(regKey, Mark.MAHJONG_BLACK+"$0")
				.replaceAll("(?i)(TRIM\\s*\\([\\S\\s]*?)"+Mark.MAHJONG_BLACK+"(FROM)", "$1$2")//排除TRIM(FROM)
		;
		String[] arrSplitStr = temp.split(Mark.MAHJONG_BLACK);
		//依照特徵分裝
		String select=""
				,from=""
				,where=""
//				,groupBy=""
//				,orderBy=""
				,rowNumber="";
//		List<String> lstJoin = new ArrayList<String>();
		for(String str : arrSplitStr) {
			if(str.matches("\\s*")) {
				res+=str;
				continue;
//			}else if(str.matches("(?i)WITH[\\S\\s]+")) {
//				lstWith.add(str);
			}else if(str.matches("(?i)SEL(?:ECT)?[\\S\\s]+")) {
				select = str;
			}else if(str.matches("(?i)FROM[\\S\\s]+")) {
				from = str;
//			}else if(str.matches("(?i)((OUTER|INNER|LEFT|RIGHT|CROSS)\\\\s+)?JOIN[\\S\\s]+")) {
//				lstJoin.add(str);
//				lstArea.add(SelectAreaEnum.JOIN);
			}else if(str.matches("(?i)WHERE[\\S\\s]+")) {
				where = str;
//			}else if(str.matches("(?i)GROUP\\s+BY[\\S\\s]+")) {
//				groupBy = str;
//			}else if(str.matches("(?i)ORDER\\s+BY[\\S\\s]+")) {
//				orderBy = str;
			}else if(str.matches("(?i)\\bQUALIFY\\s+ROW_NUMBER[\\S\\s]+")) {
				rowNumber = ConvertFunctionsSafely.decodeMark(str);
			}else {
				throw new UnknowSQLTypeException(str, null);
			}
		}
		/* 
		 * 轉換邏輯
		 * 	- select
		 * 		1.有邏輯的部分全部只留Alias name
		 * 	- from
		 * 		1.包sub query
		 * 		2.加上row_number
		 * 	- where
		 * 		1.加上where row_number = 1
		 * 
		 * 2023/12/26 Tim 跟Jason討論過，可以將邏輯放在子查詢
		 * */
		//select只留Alias name
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		String newSelect = select.replaceAll("(?i)(SELECT(\\s+DISTINCT)?\\s+)[\\S\\s]+", "$1")+
			cfs.savelyConvert(select.replaceAll("(?i)SELECT(\\s+DISTINCT)?\\s+", ""), (t) ->{
				return t
					.replaceAll("(?i)[^,]+\\s+AS\\s+([^\\s,]+)", "$1")//只保留Alias name
					.replaceAll("\\S+\\.(\\S+)","tmp_qrn.$1")// 清除Table Alias name
				;
			});
		String newFrom = "FROM ( "
			+select.replaceAll("(?i)\\s+DISTINCT", "")
			+"\t,"+rowNumber.replaceAll("(?i)QUALIFY\\s+", "").replaceAll("([\\S\\s]*\\))[^\\)]+$", "$1")+" AS ROW_NUMBER\r\n\t"
			+from+where+" ) tmp_qrn \r\n where tmp_qrn.ROW_NUMBER "
			+rowNumber.replaceAll("[\\S\\s]*\\)([^\\)]+)$", "$1")
		;
		res += newSelect+newFrom;
		return res;
	}
	
	/**
	 * <h1>Alias name 轉換</h1>
	 * <p>Teradata環境中
	 * <br>GROUP BY語法可以使用SELECT裡的Alias name
	 * <br>但是GreenPlum不行，
	 * <br>所以需要包一層sub Query 再進行group by
	 * </p>
	 * <p>僅支援</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月26日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	script	要轉換的SQL語法
	 * @throws	
	 * @see
	 * @return	String	轉換後的SQL
	 * @throws UnknowSQLTypeException 
			 */
	public String changeAliasName(String script) throws UnknowSQLTypeException {
		String res = "";
		//排除子查詢
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		res = csqs.savelyConvert(res, (t)->{
			try {
				//解析查詢語句
				SelectTableModel stm = new SelectTableModel(t);
				//取得col清單
				String sel = stm.select.replaceAll("(?i)^\\s*SELECT\\s+", "");
				List<String> lstCol = SplitCommaSafely.splitComma(sel);
				//取得Alias name清單
				Pattern p = Pattern.compile("([^\\s\\.]+)$");
				List<String> lstAlias = SplitCommaSafely.splitComma(sel,(col) ->{
					Matcher m = p.matcher(col);
					m.find();
					m.group();
					return m.group();
				});
				//取得group by欄位
				String[] arrGroupBy = stm.groupBy.replaceAll("(?i)^\\s*GROUP\\s+BY\\s*|\\s+","").toUpperCase().split(",");
				//先處理數字的group by
				for (int i = 0; i < arrGroupBy.length; i++) {
					if(arrGroupBy[i].matches("\\d+")){
						arrGroupBy[i] = lstAlias.get(Integer.parseInt(arrGroupBy[i]));
					}
				}
				//處理Alias name
				/* 邏輯：
				 * 1.
				 * 
				 * */
			} catch (UnknowSQLTypeException e) {
				e.printStackTrace();
			}
			
			return t;
		});
		
		return res;
	}
}
