package etec.src.translator.sql.azure.translater;

import etec.common.factory.Params;
import etec.framework.context.convert_safely.service.ConvertFunctionsSafely;

public class DDLTranslater {
	
	public String easyReplace(String sql) {
		if(sql.matches("(?i)\\s*CREATE\\s+[\\S\\s]+")) {
			sql = easyReplaceCreateTable(sql);
		}
		sql = changeReplaceView(sql);
		sql = changeDropTableIfExist(sql);
		sql = changeRenameTable(sql);
		sql = changeCreateVolaTileTable(sql);
		sql = changeIntegerGeneratedAlwaysAsIdentity(sql);
		sql = changePrimaryIndex(sql);
		return sql;
	}
	/**
	 * 在GP中如果使用REPLACE VIEW，確定轉換前後的欄位有沒有變化
	 * 否則推薦DROP後再CREATE
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String changeReplaceView(String sql) {
		String res = sql
			.replaceAll("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS", "DROP VIEW IF EXISTS $1;\r\nCREATE VIEW $1 AS")
			;
		return res;
	}
	/**
	 * <h1>Create Table 的轉換</h1>
	 * <li>SEL改成SELECT
	 * <li>if not exist的功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String easyReplaceCreateTable(String sql) {
		String res = sql.replaceAll("(?i)\\bSEL\\b", "SELECT");//SEL
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(res, (String t)->{
			t = changeCreateTableIfNotExist(t);
			return t;
		});
//		res = changeTypeConversion(res);
		return res;
	}
	/** 
	 * CREATE TABLE 要加上 if not exist
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String changeCreateTableIfNotExist(String sql) {
		String res = sql
			.replaceAll("(?i)CREATE\\s+TABLE\\s+(\\S+)\\s+AS\\s*([\\S\\s]+)\\s*WITH\\s+NO\\s+DATA", "CREATE TABLE IF NOT EXISTS $1\r\n\\(LIKE $2\\)")
			;
		return res;
	}
	/**
	 * DROP TABLE 要加上 if exist
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String changeDropTableIfExist(String sql) {
		String res = sql
			.replaceAll("(?i)DROP\\s+(TABLE|VIEW)\\s+(\\S+)\\s*;", "DROP $1 IF EXISTS $2"+(Params.gp.IS_CASCADE?" CASCADE":"")+";")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * RENAME TABLE 改成 ALTER TABLE 加上 RENAME TO
	 * 
	 * */
	public String changeRenameTable(String sql) {
		String res = sql
			.replaceAll("(?i)RENAME\\s+TABLE\\s+(\\S+)\\s+TO\\s+(?:[^\\.]+\\.)?(\\S+)\\s*;", "ALTER TABLE $1\r\nRENAME TO $2;")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
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
	 * @since	4.0.0.0
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
	 * @since	4.0.0.0
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
