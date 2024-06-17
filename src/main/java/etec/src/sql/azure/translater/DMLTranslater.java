package etec.src.sql.azure.translater;

import etec.framework.translater.exception.SQLTranslateException;
import etec.framework.translater.exception.UnknowSQLTypeException;

public class DMLTranslater {
	
	public String easyReplace(String sql) throws SQLTranslateException {
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
	public String changeInsertSelect(String sql) throws SQLTranslateException {
		String res = "";
		String insert = sql.replaceAll("(?i)(\\s*INSERT\\s+INTO\\s+\\S+\\s+)[\\S\\s]+", "$1");
		String select = sql.replaceAll("(?i)\\s*INSERT\\s+INTO\\s+\\S+\\s+", "");
		select = AzureTranslater.dql.easyReplace(select);
		
		res = insert+select;
		return res;
	}
}
