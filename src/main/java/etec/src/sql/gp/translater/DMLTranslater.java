package etec.src.sql.gp.translater;

import etec.common.exception.sql.SQLTransduceException;

public class DMLTranslater {
	
	public String easyReplace(String sql) throws SQLTransduceException {
		if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+[\\S\\s]+")) {
			sql = changeInsertSelect(sql);
		}
		sql = changeDeleteTableUsing(sql);
		return sql;
	}
	/**
	 * 
	 * DELETE TABLE 要加上 USING
	 * 
	 * @author	Tim
	 * @since	4.0.0.0 
	 * */
	public String changeDeleteTableUsing(String sql) {
		String res = sql
			.replaceAll("(?i)(DELETE\\s+FROM\\s+[^,;]+?)\\s+,", "$1\r\n USING ")//Locking
			;
		return res;
	}
	
	/**
	 * <h1>INSERT SELECT語法轉換</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2023年12月26日	Tim	建立功能
	 * <br>2024年 4月22日	Tim	修正insert語法有欄位名稱時會錯位的問題
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	SQLTransduceException
	 * @see		
	 * @return	String
	 */
	public String changeInsertSelect(String sql) throws SQLTransduceException {
		String res = "";
		String[] arr = sql.split("(?i)\\bSELECT\\b", 2);
		String insert = arr[0];
		String select = sql.replace(arr[0],"");
		select = GreemPlumTranslater.dql.easyReplace(select);
		res = insert+select;
		return res;
	}
}
