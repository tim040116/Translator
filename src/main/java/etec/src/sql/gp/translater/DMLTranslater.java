package etec.src.sql.gp.translater;

import etec.common.exception.sql.SQLTransduceException;
import etec.common.exception.sql.UnknowSQLTypeException;

public class DMLTranslater {
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
		String insert = sql.replaceAll("(?i)(\\s*INSERT\\s+INTO\\s+\\S+\\s+)[\\S\\s]+", "$1");
		String select = sql.replaceAll("(?i)\\s*INSERT\\s+INTO\\s+\\S+\\s+", "");
		select = GreemPlumTranslater.dql.easyReplace(select);
		
		res = insert+select;
		return res;
	}
}
