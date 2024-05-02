package etec.src.sql.gp.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.sql.SQLFormatException;
import etec.common.exception.sql.UnknowSQLTypeException;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.param.Params;

public class DDLTranslater {
	
	public String easyReplace(String sql) throws UnknowSQLTypeException, SQLFormatException {
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
	 * 
	 * @author	Tim
	 *
	 * @since	4.0.0.0
	 * */
	/**
	 * <h1>EPLACE VIEW</h1>
	 * <p>在GP中如果使用REPLACE VIEW，確定轉換前後的欄位有沒有變化
	 * <br>否則推薦DROP後再CREATE</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月2日	Tim	建立功能
	 * <br>2024年5月2日	增加處理後面的select語法
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sql
	 * @throws	SQLFormatException 
	 * @throws	UnknowSQLTypeException 
	 * @see		
	 * @return	String
			 */
	public String changeReplaceView(String sql) throws UnknowSQLTypeException, SQLFormatException {
		if(sql.matches("(?i)\\s*REPLACE\\s+VIEW[^;]*;?")) {
			String res = "";
			Pattern p = Pattern.compile("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS\\s+([^;]+;?)");
			Matcher m = p.matcher(sql);
			while(m.find()) {
				String select = m.group(2);
				select = GreenPlumTranslater.other.easyReplace(select);
				select = GreenPlumTranslater.dql.easyReplace(select);
				res = "DROP VIEW IF EXISTS "+m.group(1)+";"
						+ "\r\nCREATE VIEW "+m.group(1)+" AS \r\n"
						+ select
				;
			}
//			String res = sql
//					.replaceAll("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS", "DROP VIEW IF EXISTS $1;\r\nCREATE VIEW $1 AS")
//					;
			return res;
		}
		return sql;
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
