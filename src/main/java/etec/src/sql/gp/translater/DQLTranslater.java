package etec.src.sql.gp.translater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.enums.SelectAreaEnum;
import etec.common.exception.SQLFormatException;
import etec.common.exception.UnknowSQLTypeException;
import etec.common.utils.Mark;
import etec.common.utils.RegexTool;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.convert_safely.ConvertSubQuerySafely;

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
	 * @author	Tim
	 * @throws UnknowSQLTypeException 
	 * @since	2023年12月26日
	 * 
	 * <h1>統整DQL的所有轉換</h1>
	 * 
	 * <br>qualify row_number over 處理
	 * <br>
	 * 
	 * */
	public String easyReplace(String script) throws UnknowSQLTypeException {		String res = GreemPlumTranslater.sql.easyReplase(script);;
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
		if(RegexTool.contains("(?i)QUALIFY\\s+ROW_NUMBER",sql)) {
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
		List<String> lstWith = new ArrayList<String>();
//		List<String> lstJoin = new ArrayList<String>();
		List<SelectAreaEnum> lstArea = new ArrayList<SelectAreaEnum>();//紀錄各階段的順序
		for(String str : arrSplitStr) {
			if(str.matches("\\s*")) {
				res+=str;
				continue;
			}else if(str.matches("(?i)WITH[\\S\\s]+")) {
				lstWith.add(str);
				lstArea.add(SelectAreaEnum.WITH);
			}else if(str.matches("(?i)SEL(?:ECT)?[\\S\\s]+")) {
				select = str;
				lstArea.add(SelectAreaEnum.SELECT);
			}else if(str.matches("(?i)FROM[\\S\\s]+")) {
				from = str;
				lstArea.add(SelectAreaEnum.SELECT);
//			}else if(str.matches("(?i)((OUTER|INNER|LEFT|RIGHT|CROSS)\\\\s+)?JOIN[\\S\\s]+")) {
//				lstJoin.add(str);
//				lstArea.add(SelectAreaEnum.JOIN);
			}else if(str.matches("(?i)WHERE[\\S\\s]+")) {
				where = str;
				lstArea.add(SelectAreaEnum.WHERE);
//			}else if(str.matches("(?i)GROUP\\s+BY[\\S\\s]+")) {
//				groupBy = str;
//				lstArea.add(SelectAreaEnum.GROUP_BY);
//			}else if(str.matches("(?i)ORDER\\s+BY[\\S\\s]+")) {
//				orderBy = str;
//				lstArea.add(SelectAreaEnum.ORDER_BY);
			}else if(str.matches("(?i)QUALIFY\\s+ROW_NUMBER[\\S\\s]+")) {
				rowNumber = ConvertFunctionsSafely.decodeMark(str);
				lstArea.add(SelectAreaEnum.QUALFY_ROW_NUMBER);
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
	 * <h1>UNPIVOT</h1>
	 * 
	 * <h2>功能介紹</h2>
	 * 	<br>此語法為將多個欄位轉換成多筆資料
	 * 	<br>範例：
	 * 	<br>原本資料：
	 * 	<table>
	 * 		<tr>
	 * 			<td>jan_sales</td>
	 * 			<td>jan_expense</td>
	 * 			<td>feb_sales</td>
	 * 			<td>feb_expense</td>
	 * 			<td>dec_sales</td>
	 * 			<td>dec_expense</td>
	 * 		</tr>
	 * 	</table>
	 * 	<br>轉換後資料：
	 * 	<table>
	 * 		<tr>
	 * 			<td>jan</td>
	 * 			<td>jan_sales</td>
	 * 			<td>jan_expense</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>feb</td>
	 * 			<td>feb_sales</td>
	 * 			<td>feb_expense</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>dec</td>
	 * 			<td>dec_sales</td>
	 * 			<td>dec_expense</td>
	 * 		</tr>
	 * 	</table>
	 * <h2>TD架構</h2>
		<br>UNPIVOT(
		<br>    ON( select * from T)
		<br>    USING
		<br>        VALUE_COLUMNS('monthly_sales', 'monthly_expense') -- 轉換後的欄位名
		<br>        UNPIVOT_COLUMN('month') -- 第一個欄位的欄位名
		<br>        COLUMN_LIST('jan_sales, jan_expense', 'feb_sales,feb_expense', ..., 'dec_sales, dec_expense') -- 要取得的欄位
		<br>        COLUMN_ALIAS_LIST('jan', 'feb', ..., 'dec' ) -- 第一個欄位的資料
		<br>)
	 * <h2>GP架構</h2>
		<br>SELECT 
		<br>	unnest(ARRAY['jan', 'feb', ..., 'dec']) AS month,
		<br>	unnest(ARRAY[jan_sales, feb_sales, ..., dec_sales]) AS monthly_sales,
		<br>	unnest(ARRAY[jan_expense, feb_expense, ..., dec_expense]) AS monthly_expense
		<br>FROM T
	 * 
	 * @author	Tim
	 * @throws SQLFormatException	COLUMN_LIST 跟 COLUMN_ALIAS_LIST 數量不同時報錯
	 * @since	4.0.0.0
	 * 
	 * */
	public String changeUNPIVOT(String script) throws SQLFormatException {
		String res = script;
		//取得整段UNPIVOT語法
		Pattern p = Pattern.compile("UNPIVOT\\s*\\(\\s*ON\\s*\\(\\s*SELECT[\\S\\s]+FROM\\s+(\\S+)\\)\\s*USING((?:\\s*[^\\(]+\\([^\\)]+\\))+)\\s*\\)",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		while(m.find()) {
			String unpivot = m.group(0);//全文
			String replacement = "(\r\n\tSELECT";
			String temp = m.group(0)//暫存
					.replaceAll("\\)$","")//最後的括號
					.replaceAll("(?i)UNPIVOT\\s*\\(\\s*ON\\s*", "")//開頭
			;
			String table = temp.replaceAll("\\s*USING[\\S\\s]*", "");//表
			//分析參數
			//取得參數的map
			Map<String,String> mapUnpivot = new HashMap<String,String>();
			temp = temp.replaceAll("[\\S\\s]+USING\\s+", "");
			Pattern p2 = Pattern.compile("\\b([^\\(]+)\\s*\\(\\s*([^\\)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m2 = p2.matcher(temp);
			while (m2.find()) {
				mapUnpivot.put(m2.group(1).trim().toUpperCase(),m2.group(2));
			}
			//分析各參數
			//第一個欄位
			String comma = " ";
			if(mapUnpivot.containsKey("COLUMN_ALIAS_LIST")&&mapUnpivot.containsKey("UNPIVOT_COLUMN")) {
				String columnAliasList 	= mapUnpivot.get("COLUMN_ALIAS_LIST");	//第一個欄位的資料
				String unpivotColumn	= mapUnpivot.get("UNPIVOT_COLUMN");		//第一個欄位的欄位名
				replacement += "\r\n\t\t unnest(ARRAY['" + columnAliasList + "']) AS " + unpivotColumn;
				comma = ",";
			}
			//後面的欄位
			String[] arrValueColumns = mapUnpivot.get("VALUE_COLUMNS") //轉換後的欄位名
					.replaceAll("^\\s*'\\s*|\\s*'\\s*$", "")
					.split("\\s*'\\s*,\\s*'\\s*")
			;		
			String[] arrColumnList = mapUnpivot.get("COLUMN_LIST") //要取得的欄位
					.replaceAll("^\\s*'\\s*|\\s*'\\s*$", "")
					.split("\\s*'\\s*,\\s*'\\s*")
			;
			//處理欄位名
			for(int i = 0 ; i < arrValueColumns.length ; i++) {
				arrValueColumns[i] = "]) AS " + arrValueColumns[i];
			}
			//處理欄位值
			for(String collist : arrColumnList) {
				String[] arrcol = collist.split("\\s*,\\s*");
				//欄位數不同則報錯
				if(arrValueColumns.length != arrcol.length) {
					throw SQLFormatException.wrongParam("UNPIVOT",arrValueColumns.length,arrcol.length);
				}
				//將欄位值塞進去
				for(int i = arrValueColumns.length-1 ; i >= 0 ; i--) {
					arrValueColumns[i] = "," + arrcol[i] + arrValueColumns[i];
				}
			}
			//處理開頭
			for(int i = 0 ; i < arrValueColumns.length ; i++) {
				replacement += "\r\n\t\t" + comma + arrValueColumns[i].replaceAll("^,", "unnest(ARRAY[");
				if(" ".equals(comma)) {
					comma = ",";
				}
			}
			replacement += "\r\n\tFROM " + table + "\r\n)";
			res = res.replace(unpivot,replacement);
		}
		return res;
	}
}
