package etec.sql.gp.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.TransduceTool;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * td 轉 gp 
 * 通用語法轉換
 * 
 * */
public class SQLTranslater {
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * 
	 * */
	public String easyReplase(String script) {
		String res = script
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				.replaceAll("(?i)\\bAS\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化
				;
		res = TransduceTool.saveTranslateFunction(res, (String t)->{
			t = changeAddMonths(t);
			t = changeDateFormat(t);
			t = changeIndex(t);
			t = changeZeroIfNull(t);
			t = changeLikeAny(t);
			t = changeIndex(t);
			t = changeNullIfZero(t);
			t = changeIn(t);
			return t;
		});
		res = changeTypeConversion(res);
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * INTERVAL可進行月份加減
	 *  但會將資料型態轉變為 timestamp
	 *  因此需要配合CAST語法進行轉換
	 * 
	 * */
	public String changeAddMonths(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*\\-([^)]+)\\)", "CAST\\($1-INTERVAL'$2 MONTH' AS DATE\\)")
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*([^)]+)\\)", "CAST\\($1+INTERVAL'$2 MONTH' AS DATE\\)")
		;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * 1. $1(FORMAT '$2') 轉成 TO_CHAR($1,'$2')
	 * 2. SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR($1,'$2')
	 * 		2-1. 放棄單獨處理 AS FORMAT 一律於easyReplase()中正規化為 AS DATE FORMAT
	 * 		2-2  先處理日期截取
	 * 3. CAST(CAST($1 AS DATE FORMAT '$2') AS VARCHAR(d+)) 轉成 TO_CHAR($1,$2)
	 * 
	 * 
	 * 
	 * 2023/12/06	Tim	因涉及邏輯問題暫時廢棄
	 * 2023/12/12	Tim 重啟功能，分層處理
	 * 		
	 * 		
	 * */
//	@Deprecated
	public String changeDateFormat(String sql) {
		String res = sql;
		//1
		res = res
			//(FORMAT 'YYYY-MM-DD')
			.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('[^']+')\\s*\\)", "TO_CHAR\\($1$2, $3\\)")
		;
		/**  2.處理日期截取語法
		 * 1. 先將SUBSTR(CAST(AS DATE FORMAT語法轉換成TO_CHAR，FORMAT 跟 SUBSTR 合併
		 * 	SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR($1,SUBSTR($2,$3,$4))
		 * 2.SUBSTR($2,$3,$4)轉成一般字串
		 * */
		res = res
			//步驟2-1
			.replaceAll("(?i)SUBSTR\\s*\\(\\s*CAST\\s*([^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\),(\\d+),(\\d+)\\)", "TO_CHAR\\($1,SUBSTR\\($2,$3,$4\\)\\)")
		;
		//步驟2-2
		Matcher m = Pattern.compile("(?i)SUBSTR\\s*\\(\\s*'([^']+)'\\s*,(\\d+)\\s*,\\s*(\\d+)\\s*\\)",Pattern.CASE_INSENSITIVE).matcher(res);
		while (m.find()) {
			String format = m.group(1);
			int substr1 = Integer.parseInt(m.group(2))-1;
			int substr2 = substr1+Integer.parseInt(m.group(3));
			format = format.substring(substr1, substr2);
			res = res.replace(m.group(0),format);
		}
		//步驟3
		res = res
			.replaceAll("(?i)CAST\\s*\\(\\s*CAST\\s*\\((\\s*[^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s+AS\\s+(VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "TO_CHAR\\($1,$2\\)")
		;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 
	 * 轉換欄位強制轉換的語法
	 * 
	 * col_name(CHAR(7)) >> cast(col_name as char(7))
	 * 
	 * */
	public String changeTypeConversion(String sql) {
		String res = sql;
		res = TransduceTool.saveTranslateFunction(sql, (String t)->{
			t = t
				.replaceAll("(?i)([\\w\\.]+)(\\([^\\)]+\\))?\\((CHAR<[^>]+>\\d+<[^>]+>)\\)", "CAST\\($1$2 AS $3\\)")
			;
			return t;
		});
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月01日
	 * 
	 * like any 語法轉換 
	 * LIKE ANY('','','') >> LIKE ANY(ARRAY['','',''])
	 * 
	 * */
	public String changeLikeAny(String sql) {
		String res = sql
			.replaceAll("(?i)LIKE\\s+ANY\\s*\\(('[^']+'(,'[^']+')+)\\)", "LIKE ANY \\(ARRAY[$1])")//like any
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月01日
	 * 
	 * INDEX 轉換成 POSITION
	 * 
	 * */
	public String changeIndex(String sql) {
		String res = sql
			.replaceAll("(?i)INDEX\\s*\\(([^,]+),([^\\)]+)\\)", "POSITION\\($2 IN $1\\)")//INDEX
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月05日
	 * 
	 * ZEROIFNULL 轉成 COALESCE
	 * 
	 * */
	public String changeZeroIfNull(String sql) {
		String res = sql
			.replaceAll("(?i)ZEROIFNULL\\s*\\(([^\\)]+)\\)", "COALESCE\\($1,0\\)")//ZEROIFNULL
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月05日
	 * 
	 * IN後面一定要有括號
	 * 
	 * */
	public String changeIn(String sql) {
		String res = sql
			.replaceAll("(?i)\\bIN\\s+(?<n1>'[^']+'(,'[^']+')+)", "IN \\(${n1}\\)")//IN
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月05日
	 * 
	 * NULLIFZERO改成NULLIF
	 * 
	 * */
	public String changeNullIfZero(String sql) {
		String res = sql
			.replaceAll("(?i)NULLIFZERO\\s*\\(([^\\)]+)\\)", "NULLIF\\($1,0\\)")//NullIfZero
			;
		return res;
	}
}
