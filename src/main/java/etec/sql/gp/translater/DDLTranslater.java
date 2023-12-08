package etec.sql.gp.translater;

import etec.common.utils.TransduceTool;

public class DDLTranslater {
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * 在GP中如果使用REPLACE VIEW，確定轉換前後的欄位有沒有變化
	 * 否則推薦DROP後再CREATE
	 * 
	 * */
	public String changeReplaceView(String sql) {
		String res = sql
			.replaceAll("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS", "DROP VIEW IF EXISTS $1;\r\nCREATE VIEW $1 AS")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * Create table加上if not exist語法
	 * 
	 * */
	public String easyReplaceCreateTable(String sql) {
		String res = sql
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				;
		res = TransduceTool.saveTranslateFunction(res, (String t)->{
			t = changeCreateTableIfNotExist(t);
			return t;
		});
//		res = changeTypeConversion(res);
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * CREATE TABLE 要加上 if not exist
	 * 
	 * */
	public String changeCreateTableIfNotExist(String sql) {
		String res = sql
			.replaceAll("(?i)CREATE\\s+TABLE\\s+(\\S+)\\s+AS\\s*([\\S\\s]+)\\s*WITH\\s+NO\\s+DATA", "CREATE TABLE IF NOT EXISTS $1\r\n\\(LIKE $2\\)")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * DROP TABLE 要加上 if exist
	 * 
	 * */
	public String changeDropTableIfExist(String sql) {
		String res = sql
			.replaceAll("(?i)DROP\\s+TABLE\\s+(\\S+)\\s*;", "DROP TABLE IF EXISTS $1;")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * RENAME TABLE 改成 ALTER TABLE 加上 RENAME TO
	 * 
	 * */
	public String changeRenameTable(String sql) {
		String res = sql
			.replaceAll("(?i)RENAME\\s+TABLE\\s+(\\S+)\\s+TO\\s+([^\\.]+\\.)?(\\S+)\\s*;", "ALTER TABLE $1\r\nRENAME TO $3;")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * CREATE VOLATILE TABLE 改成 CREATE temp TABLE
	 * 
	 * */
	public String changeCreateVolaTileTable(String sql) {
		String res = sql
			.replaceAll("(?i)\\bVOLATILE\\b", "temp")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月6日
	 * 
	 * INTEGER GENERATED ALWAYS AS IDENTITY (CYCLE) 改成 SERIAL
	 * 
	 * */
	public String changeIntegerGeneratedAlwaysAsIdentity(String sql) {
		String res = sql
			.replaceAll("(?i)\\bINTEGER\\s+GENERATED\\s+ALWAYS\\s+AS\\s+IDENTITY\\s*\\([^\\)]+\\)", "SERIAL")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月6日
	 * 
	 * PRIMARY INDEX 要改成 DISTRIBUTED
	 * 
	 * */
	public String changePrimaryIndex(String sql) {
		String res = sql
			.replaceAll("(?i)PRIMARY\\s+INDEX\\s*\\(", "DISTRIBUTED BY \\(")
			;
		return res;
	}
}
