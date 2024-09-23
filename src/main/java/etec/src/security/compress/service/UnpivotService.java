package etec.src.security.compress.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.translater.exception.SQLFormatException;

/**
 * <h1>UNVIPOT轉換</h1>
 * <br>轉換UNVIPOT類型的語法
 * <br>目前有TD_UNPIVOT 跟 UNPIVOT兩種
 * <br>這兩種語法結構差異極大，但功能是相同的
 * <br>轉換後的架構也不一樣，單純是取決於方便轉換
 * <br>功能上都是一樣的
 * 
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p>changeTD_UNPIVOT 	: 轉換TD_UNPIVOT語法</p>
 * <p>changeUNPIVOT 	: 轉換UNPIVOT語法</p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年2月22日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		#changeTD_UNPIVOT(String)
 * @see		#changeUNPIVOT(String)
 */
public class UnpivotService {
	/**
	 * <h1>TD_UNPIVOT</h1>
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
<pre>
TD_UNPIVOT(
    ON( select * from T)
    USING
        VALUE_COLUMNS('monthly_sales', 'monthly_expense') -- 轉換後的欄位名
        UNPIVOT_COLUMN('month') -- 第一個欄位的欄位名
        COLUMN_LIST('jan_sales, jan_expense', 'feb_sales,feb_expense', ..., 'dec_sales, dec_expense') -- 要取得的欄位
        COLUMN_ALIAS_LIST('jan', 'feb', ..., 'dec' ) -- 第一個欄位的資料
)
</pre>
	 * <h2>GP架構</h2>
<pre>
SELECT 
	unnest(ARRAY['jan', 'feb', ..., 'dec']) AS month,
	unnest(ARRAY[jan_sales, feb_sales, ..., dec_sales]) AS monthly_sales,
	unnest(ARRAY[jan_expense, feb_expense, ..., dec_expense]) AS monthly_expense
FROM T
</pre>
	 * 
	 * @author	Tim
	 * @throws 	SQLFormatException	COLUMN_LIST 跟 COLUMN_ALIAS_LIST 數量不同時報錯
	 * @since	4.0.0.0
	 * @see		#changeUNPIVOT
	 * */
	public String changeTD_UNPIVOT(String script) throws SQLFormatException {
		//取得整段TD_UNPIVOT語法
		Pattern p = Pattern.compile("TD_UNPIVOT\\s*\\(\\s*ON\\s*\\(\\s*SELECT[\\S\\s]+FROM\\s+(\\S+)\\)\\s*USING((?:\\s*[^\\(]+\\([^\\)]+\\))+)\\s*\\)",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		if(m.find()){m.reset();}else{return script;}
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String replacement = "(\r\n\tSELECT";
			String temp = m.group(0)//暫存
					.replaceAll("\\)$","")//最後的括號
					.replaceAll("(?i)TD_UNPIVOT\\s*\\(\\s*ON\\s*", "")//開頭
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
					throw SQLFormatException.wrongParam("TD_UNPIVOT",arrValueColumns.length,arrcol.length);
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
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * <h1>UNPIVOT</h1>
	 * <p>轉換UNPIVOT語法</p>
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
	 * 			<td>jan_sales</td>
	 * 			<td>jan_expense</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>feb_sales</td>
	 * 			<td>feb_expense</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>dec_sales</td>
	 * 			<td>dec_expense</td>
	 * 		</tr>
	 * 	</table>
	 * <h2>TD架構</h2>
<pre>
SELECT 
	 sj2.yr
	,sj2.months
	,sj2.monthly_sales
	,sj2.monthly_cnt
FROM PDATA.sales_Tim
UNPIVOT((monthly_sales,monthly_cnt) FOR months IN (
		 (jan_sales,jan_cnt) AS 'January'
		,(feb_sales,feb_cnt) AS 'February'
		,(dec_sales,dec_cnt) AS 'December'
	)
) AS sj2;
</pre>
	 * <h2>GP架構</h2>
<pre>
SELECT
	 sj2.yr
	,sj2.months
	,sj2.monthly_sales
	,sj2.monthly_cnt
FROM PDATA.sales_Tim
JOIN LATERAL(VALUES
	 ('January'  ,jan_sales,jan_cnt)
	,('February' ,feb_sales,feb_cnt)
	,('December' ,dec_sales,dec_cnt)
) sj2 (months,monthly_sales,monthly_cnt) ON TRUE;
</pre>
	 *
	 * 
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月22日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	script	要轉換的語法
	 * @throws
	 * @see		#changeTD_UNPIVOT
	 * @return	String	轉換後的程式碼
			 */
	public String changeUNPIVOT(String script){
		/**
		 * <h1>[搜尋]	整段UNPIVOT語法</h1>
		 * <p>取得範圍：從 UNPIVOT 到 AS ALIAS_NAME</p>
		 * <p>群組：
		 * 		1.column name(單一欄位)
		 * 		2.column name(複數欄位)
		 * 		3.column name(新增的那一個欄位)
		 * 		4.欄位值的來源
		 * 		5.Alias name
		 * </p>
		 * 2024/02/22	Tim
		 * */
		Pattern p = Pattern.compile("\\bUNPIVOT\\s*\\(\\s*(?:([^\\(\\)\\s]+)|\\(([^\\)]+)\\))\\s+FOR\\s+(\\S+)\\s+IN\\s*\\(([\\S\\s]+?)\\)\\s*\\)(?:\\s*AS)?\\s+([^;\\)\\s]+)",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		if(m.find()){m.reset();}else{return script;}
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String newunpivot = "JOIN LATERAL(VALUES"
				/**
				 * <h1>[取代]	欄位值的寫法</h1>
				 * <p>群組：
				 * 		0.從 UNPIVOT 到 AS ALIAS_NAME
				 * 		1.資料來源(單一欄位)
				 * 		2.資料來源(複數欄位)
				 * 		3.新增的那一個欄位
				 * </p>
				 * 2024/02/22	Tim
				 * */
				+ m.group(4).replaceAll("(?i)(?:([^\\s\\(\\)]+)|\\(([^\\)]+)\\))\\s+AS\\s+('[^']+')","\\($3,$1$2\\)")
				+ ") AS " + m.group(5) + " (" + m.group(3) + "," + m.group(1) + m.group(2) + ") ON TRUE "
			;
			m.appendReplacement(sb, newunpivot);
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
