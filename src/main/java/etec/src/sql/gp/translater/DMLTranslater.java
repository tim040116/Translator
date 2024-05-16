package etec.src.sql.gp.translater;

import etec.common.exception.sql.SQLTransduceException;
import etec.common.utils.log.Log;

public class DMLTranslater {
	
	/**
	 * <h1>轉換DML</h1>
	 * <p>
	 * <br>轉換 INSERT INTO
	 * <br>轉換 INSERT SELECT
	 * <br>轉換 DELETE TABLE
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月15日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public String easyReplace(String sql) throws SQLTransduceException {
		if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+\\S+\\s+VALUES\\b[\\S\\s]+")) {
			Log.debug("\t\t細分：INSERT  INTO");
		}else if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+[\\S\\s]+")) {
			Log.debug("\t\t細分：INSERT  SELECT");
			sql = changeInsertSelect(sql);
		}else {
			sql = changeDeleteTableUsing(sql);
		}
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
		select = GreenPlumTranslater.dql.easyReplace(select);
 		res = insert+select;
		return res;
	}
}
