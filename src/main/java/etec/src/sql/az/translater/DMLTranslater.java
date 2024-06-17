package etec.src.sql.az.translater;

import java.io.IOException;

import etec.common.utils.RegexTool;
import etec.common.utils.log.Log;
import etec.framework.translater.exception.SQLTranslateException;
import etec.src.sql.gp.translater.GreenPlumTranslater;

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
	public String easyReplace(String title,String sql) throws SQLTranslateException {
		switch(title) {
		case "INSERT":
			if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+\\S+\\s+VALUES\\b[\\S\\s]+")) {
				Log.debug("\t細分：INSERT  INTO");
				sql = runInsertInto(sql);
			}else if(sql.matches("(?i)\\s*INSERT\\s+(?:INTO\\s+)?[\\S\\s]+")) {
				Log.debug("\t細分：INSERT  SELECT");
				sql = runInsertSelect(sql);
			}
			break;
		case "DELETE":
			Log.debug("\t細分：DELETE TABLE");
//			sql = changeDeleteTableUsing(sql);
			break;
		case "UPDATE":
			Log.debug("\t細分：UPDATE TABLE");
//			sql = changeUpdateTable(sql);
			break;
		case "MERGE":
			Log.debug("\t細分：MERGE INTO");
			sql = runMergeInto(sql);
			break;
		}
  		return sql;
	}
	//insert into
	public static String runInsertInto(String sql) throws SQLTranslateException {
		String res = sql;
		res = DQLTranslater.changeSample(res);
		res = DQLTranslater.easyReplace(res);
		return res+"\r\n;";
	}
	// insert select
	public static String runInsertSelect(String sql) throws SQLTranslateException {
//		String res = "";
//		String insert = RegexTool.getRegexTarget("INSERT\\s+(?:INTO\\s+)?\\S+\\s+", sql).get(0).trim();
//		String select = RegexTool.decodeSQL(RegexTool.encodeSQL(sql).replaceAll(RegexTool.encodeSQL(insert), ""));
//		res = insert+"\r\n"+DQLTranslater.easyReplace(select);
		
		String res = "";
		String[] arr = sql.split("(?i)\\bSELECT\\b", 2);
		String insert = arr[0];
		String select = sql.replace(arr[0],"");
		select = DQLTranslater.easyReplace(select);
		select = select.replaceAll("(?i)^\\s*SELECT(?:\\s+DISTINCT\\b)?", "SELECT DISTINCT\r\n");
 		res = insert+select;
		return res;
	}
	// Merge Into
	public static String runMergeInto(String sql) throws SQLTranslateException {
		String res = "";
		String mergeInto = sql.replaceAll("\\s*USING\\s*\\([^;]+","");
		String using = "";
		String when = "";
		String temp = "";
		String status = "BEGIN";
		int	bracketCnt = -1;
		for(String c : sql.split("")) {
			if("BEGIN".equals(status)) {
				temp+=c;
				if(temp.toUpperCase().replaceAll("\\s+", " ").contains(("USING"+" (").toUpperCase().replaceAll("\\s+", " "))) {
					status = "USING";
					bracketCnt = 1;
				}
			}
			else if("USING".equals(status)) {
				if(c.equals("(")) {
					bracketCnt++;
				}else if(c.equals(")")) {
					bracketCnt--;
				}
				if(bracketCnt==0) {
					status = "AFTER";
					continue;
				}
				using+=c;
			}
			else if("AFTER".equals(status)){
				when+=c;
			}
			
		}
		res = mergeInto+"\r\nUSING (\r\n"+using.trim()+"\r\n)"+when+"\r\n";
		return res;
	}
}
