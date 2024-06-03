package etec.src.sql.gp.translater.service;

public class MergeIntoService {
	
	public static String convert(String type,String tableNm,String using,String sql) {
		String res = "";
		switch(type.toUpperCase()) {
		case "UPDATE":
			res = update(tableNm,using,sql);
			break;
		case "INSERT":
			res = insert(tableNm,using,sql);
			break;
		}
		return res;
	}
	
	
	public static String insert(String tableNm,String using,String sql) {
		String alias = sql.replaceAll("\\S+\\s+(\\S+)\\s*", "$1");
		String table = sql.replaceAll("(\\S+)\\s+\\S+\\s*", "$1");
		/**
		 * 1.using table
		 * 2.on
		 * */
		String reg = "(?is)(.*)on\\s*(?!.*on.*)(.*)";
		String rpm = "FROM $1"
				+ "\\r\\nWHERE NOT EXISTS \\("
				+ "\\r\\n\\tSELECT '*'"
				+ "\\r\\n\\tFROM " + table
				+ "\\r\\n\\tWHERE $2"
				+ "\\r\\n\\)";
		String from  = using
				.replaceAll("\\b"+alias+"\\.", "")
				.replaceAll(reg,rpm);
		String res = "INSERT INTO " + table
				+ "\r\n" + from
				+ "\r\n;"
				;
		return res;
	} 
	
	public static String update(String tableNm,String using,String sql) {
		String update = "UPDATE " + tableNm
				+ "\r\n" + sql
				+ "\r\nFROM " + using
				+ "\r\n;"
				;
		return update;
	}
}
