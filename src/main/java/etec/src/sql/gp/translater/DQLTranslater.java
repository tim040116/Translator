package etec.src.sql.gp.translater;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.sql.SQLFormatException;
import etec.common.exception.sql.UnknowSQLTypeException;
import etec.common.utils.Mark;
import etec.common.utils.RegexTool;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.convert_safely.ConvertSubQuerySafely;
import etec.common.utils.convert_safely.SplitCommaSafely;

/**
 * @author	Tim
 * @since	2023年12月19日
 * @version	4.0.0.0
 * 
 * <h1>查詢語法轉換</h1>
 * 
 * <br>此類別並不是用來轉換SQL裡的語法
 * <br>若需要轉換語法請使用
 * <br>{@link etec.src.sql.gp.translater.SQLTranslater}
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
		String res = GreemPlumTranslater.sql.easyReplase(script);
		res = changeQualifaRank(res);//Qualify Row Number
		res = changeAliasName(res);
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
		String res = "";
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
		String regKey = "(?i)"+String.join("|", arrSplitType);
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		res = csqs.savelyConvert(sql, (t)->{
			String rt = "";
			if(!RegexTool.contains("(?i)QUALIFY\\s+ROW_NUMBER",t)) {
				return t;
			}
			String temp = t; 
			temp = temp//先排除依些可能ˊ會造成問題的項目
					.replaceAll(regKey, Mark.MAHJONG_BLACK+"$0")
					.replaceAll("(?i)(TRIM\\s*\\([\\S\\s]*?)"+Mark.MAHJONG_BLACK+"(FROM)", "$1$2")//排除TRIM(FROM)
			;
			String[] arrSplitStr = temp.split(Mark.MAHJONG_BLACK);
			
			//依照特徵分裝
			String select=""
					,from=""
					,where=""
//					,groupBy=""
//					,orderBy=""
					,rowNumber="";
			for(String str : arrSplitStr) {
				if(str.matches("\\s*")) {
					rt+=str;
					continue;
//				}else if(str.matches("(?i)WITH[\\S\\s]+")) {
//					lstWith.add(str);
				}else if(str.matches("(?i)SEL(?:ECT)?[\\S\\s]+")) {
					select = str;
				}else if(str.matches("(?i)FROM[\\S\\s]+")) {
					from = str;
//				}else if(str.matches("(?i)((OUTER|INNER|LEFT|RIGHT|CROSS)\\\\s+)?JOIN[\\S\\s]+")) {
//					lstJoin.add(str);
//					lstArea.add(SelectAreaEnum.JOIN);
				}else if(str.matches("(?i)WHERE[\\S\\s]+")) {
					where = str;
//				}else if(str.matches("(?i)GROUP\\s+BY[\\S\\s]+")) {
//					groupBy = str;
//				}else if(str.matches("(?i)ORDER\\s+BY[\\S\\s]+")) {
//					orderBy = str;
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
				cfs.savelyConvert(select.replaceAll("(?i)SELECT(\\s+DISTINCT)?\\s+", ""), (t2) ->{
					return t2
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
			rt += newSelect+newFrom;
			return rt;
		});
		
		return res;
		
		
		
		
		
		
		
		
	}
	
	/**
	 * <h1>Alias name 轉換</h1>
	 * <p>Teradata環境中
	 * <br>GROUP BY語法可以使用SELECT裡的Alias name
	 * <br>但是GreenPlum不行，
	 * <br>所以需要包一層sub Query 再進行group by
	 * </p>
	 * 
	 * <p>
	 * <br>先將程式取出 SELECT~FROM ,取得所有是Alias name的欄位
	 * <br>找到WHERE 中有沒有使用ALIAS 欄位
	 * <br>有的話外面包一層
	 * </p>
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月26日	Tim	建立功能
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
		
		List<String> lstAliasNm = new ArrayList<String>();
		//排除子查詢
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		res = csqs.savelyConvert(res, (t)->{
			//取出 SELECT~FROM
			String col = t.replaceAll("(?is)select\\s+(.*)\\s+from.*", "$1");
			//取出Alias
			Pattern p = Pattern.compile("(?is)\\bAS\\s+(\\w+)(?!\\s*\\()(?=[\\s,]+|\\s*$)");
			Matcher m = p.matcher(col);
			while(m.find()) {
				lstAliasNm.add(m.group(1));
			}
			//取出 WHERE
			String where = t.replaceAll("(?is)select\\s+(.*)\\s+from.*", "$1").toUpperCase();
			//判斷有沒有Alias 語法
			boolean isAlias = false;
			for(String alias : lstAliasNm) {
				if(where.contains(alias)) {
					isAlias = true;
					break;
				}
			}
			//有Alias 語法，進行處理
			if(!isAlias) {
				return t;
			}
			//處理外面層的
			String newCol = col;
			String tempNm = "TP_GRP_"+csqs.loopId;
			List<String> lstCol = SplitCommaSafely.splitComma(newCol, (t2) ->{
				t2 = t2.trim().replaceAll("(?i).*\\bAS\\s+", "");
				return t2;
			});
			String newSQL = "\r\nSELECT"
					+ "\r\n\t" + String.join(",\r\n\t",lstCol)
					+ "\r\nFROM ( "
					+ "\r\n\t" + t.replaceAll("(?i)\\bWHERE\\s+.*","")
					+ "\r\n) " + tempNm
					+ "\r\nWHERE"
					+ "\r\n\t" + t.replaceAll(".*\\bWHERE\\s+", "")
					+ "\r\n;\r\n"
			;
			return newSQL;
		});
		return res;
	}
}
