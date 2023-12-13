package etec.sql.gp.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.ConvertFunctionsSafely;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * td 轉 gp 通用語法轉換
 * 
 * */
public class SQLTranslater {
	
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * <h1>轉換SQL語法</h1><br>
	 * 
	 * <br>1.MINUS 轉成 EXCEPT
	 * <br>2.SEL 轉成 SELECT
	 * <br>3.OREPLACE 轉成 REPLACE
	 * <br>4.STRTOK 轉成 SPLIT_PART
	 * <br>5.DATE FORMAT 正規化
	 * <br>6.INDEX改成POSITION
	 * <br>7.ZEROIFNULL改成COALESCE
	 * <br>8.IN後面一定要有括號
	 * <br>9.NullIfZero改成NULLIF
	 * <br>10.LIKE ANY('','','') 轉成 LIKE ANY(ARRAY['','',''])	
	 * <br>11.日期運算轉換 
	 * <br> {@link SQLTranslater#changeAddMonths(String)}
	 * <br> {@link SQLTranslater#changeDateFormat(String)}
	 * <br>DATE 轉成 CURRENT_DATE
	 * */
	public String easyReplase(String script) {
		String res = script
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				.replaceAll("(?i)\\bAS\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化
				.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化
				.replaceAll("(?i)(?<!\\bAS )\\bDATE\\b(\\s*)(?!')", "CURRENT_DATE$1")//DATE 轉成 CURRENT_DATE
//				.replaceAll("(?<!')0\\d+(?!'|\\d)", "'$0'")//0字頭的數字要包在字串裡
				;
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.saveTranslateFunction(res, (String t)->{
			t = t
				.replaceAll("(?i)INDEX\\s*\\(([^,]+),([^\\)]+)\\)", "POSITION\\($2 IN $1\\)")//INDEX改成POSITION
				.replaceAll("(?i)ZEROIFNULL\\s*\\(([^\\)]+)\\)", "COALESCE\\($1,0\\)")//ZEROIFNULL改成COALESCE
				.replaceAll("(?i)\\bIN\\s+(?<n1>'[^']+'(,'[^']+')+)", "IN \\(${n1}\\)")//IN後面一定要有括號
				.replaceAll("(?i)NULLIFZERO\\s*\\(([^\\)]+)\\)", "NULLIF\\($1,0\\)")//NullIfZero改成NULLIF
				.replaceAll("(?i)LIKE\\s+ANY\\s*\\(('[^']+'(,'[^']+')+)\\)", "LIKE ANY \\(ARRAY[$1])")//LIKE ANY('','','') >> LIKE ANY(ARRAY['','',''])	
			;
			t = changeAddMonths(t);
			t = changeDateFormat(t);
			return t;
		});
		res = changeTypeConversion(res);
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * <br>INTERVAL可進行月份加減
	 * <br>但會將資料型態轉變為 timestamp
	 * <br>因此需要配合CAST語法進行轉換
	 * <br>
	 * <br>ADD_MONTHS($1,$2) 轉成 CAST($1-INTERVAL'$2 MONTH' AS DATE)
	 * 
	 * */
	public String changeAddMonths(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*\\-([^)]+)\\)", "CAST\\($1-INTERVAL'$2 MONTH' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*([^)]+)\\)", "CAST\\($1+INTERVAL'$2 MONTH' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s*([\\+\\-]\\s*INTERVAL'[^']+'\\s+AS\\s+DATE)\\s*\\)","AS DATE\\)$2 FORMAT $1\\)")//ADD_MONTH(CAST AS DATE
		;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * <br>1. $1(FORMAT '$2') 轉成 TO_CHAR($1,'$2')
	 * <br>2. SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR($1,'$2')
	 * <br>		2-1. 放棄單獨處理 AS FORMAT 一律於easyReplase()中正規化為 AS DATE FORMAT
	 * <br>		2-2  先處理日期截取
	 * <br>3. CAST(CAST($1 AS DATE FORMAT '$2') AS VARCHAR(d+)) 轉成 TO_CHAR($1,$2)
	 * <br>
	 * <br>
	 * <br>
	 * <br>2023/12/06	Tim	因涉及邏輯問題暫時廢棄
	 * <br>2023/12/12	Tim 重啟功能，分層處理
	 * 		
	 * 		
	 * */
//	@Deprecated
	public String changeDateFormat(String sql) {
		String res = sql;
		
		res = res
			//1 (FORMAT 'YYYY-MM-DD')
			.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('[^']+')\\s*\\)", "TO_CHAR\\($1$2, $3\\)")
		;
		/**  2.處理日期截取語法
		 * 1. 先將SUBSTR(CAST(AS DATE FORMAT語法轉換成TO_CHAR，FORMAT 跟 SUBSTR 合併
		 * 	SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR($1,SUBSTR($2,$3,$4))
		 * 2.SUBSTR($2,$3,$4)轉成一般字串
		 * */
		res = res
			//步驟2-1
			.replaceAll("(?i)SUBSTR\\s*\\(\\s*CAST\\s*\\(\\s*([^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\),(\\d+),(\\d+)\\)", "TO_CHAR\\($1,SUBSTR\\($2,$3,$4\\)\\)")
		;
		//步驟2-2
		Matcher m = Pattern.compile("(?i)SUBSTR\\s*\\(\\s*'([^']+)'\\s*,(\\d+)\\s*,\\s*(\\d+)\\s*\\)",Pattern.CASE_INSENSITIVE).matcher(res);
		while (m.find()) {
			String format = m.group(1);
			int substr1 = Integer.parseInt(m.group(2))-1;
			int substr2 = substr1+Integer.parseInt(m.group(3));
			format = "'"+format.substring(substr1, substr2)+"'";
			res = res.replace(m.group(0),format);
		}
		//步驟3
		res = res
			.replaceAll("(?i)CAST\\s*\\(\\s*CAST\\s*\\((\\s*[^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s+AS\\s+(VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "TO_CHAR\\($1,$2\\)")
			.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT\\s+'YYYYMMDD'\\s*\\)\\s*([\\+\\-]\\d+)", "AS DATE\\)$1")
		;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * <br>轉換欄位強制轉換的語法
	 * <br>
	 * <br>col_name(CHAR(7)) >> cast(col_name as char(7))
	 * 
	 * */
	public String changeTypeConversion(String sql) {
		String res = sql;
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.saveTranslateFunction(sql, (String t)->{
			t = t
				.replaceAll("(?i)([\\w\\.]+)(\\([^\\)]+\\))?\\((CHAR<[^>]+>\\d+<[^>]+>)\\)", "CAST\\($1$2 AS $3\\)")
			;
			return t;
		});
		return res;
	}
}
