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
	
	public String easyReplase(String script) {
		String res = script;
		res = TransduceTool.saveTranslateFunction(res, (String t)->{
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
}
