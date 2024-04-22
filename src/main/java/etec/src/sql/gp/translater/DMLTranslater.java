package etec.src.sql.gp.translater;

import etec.common.exception.sql.SQLTransduceException;
import etec.common.exception.sql.UnknowSQLTypeException;

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
	 * @author	Tim
	 * @throws UnknowSQLTypeException 
	 * @since	2023年12月26日
	 * 
	 * INSERT SELECT語法轉換
	 * 
	 * 
	 * */
	public String changeInsertSelect(String sql) throws SQLTransduceException {
		String res = "";
		String[] arr = sql.split("(?i)\\bSELECT\\b", 1);
		String insert = arr[0];
		String select = "SELECT"+arr[1];
		select = GreemPlumTranslater.dql.easyReplace(select);
		res = insert+select;
		return res;
	}
}
