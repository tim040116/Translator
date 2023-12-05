package etec.sql.gp.translater;

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
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
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
	 * 但會將資料型態轉變為 timestamp
	 * 因此需要配合CAST語法進行轉換
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
	 * format語法改成to_char
	 * 
	 * */
	public String changeDateFormat(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('[^']+')\\s*\\)", "TO_CHAR\\($1$2, $3\\)")
			.replaceAll("(?i)CAST\\(\\s*CAST\\(([^\\(]+)\\s+AS\\s+FORMAT\\s+('[^']+')\\)\\s+AS\\s+(VARCHAR\\(\\d\\))\\)", "TO_CHAR\\($1, $2\\)")
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
