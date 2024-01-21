package etec.src.sql.az.translater;

import java.io.IOException;

import etec.common.utils.RegexTool;

public class DMLTranslater {
	//insert into
	public static String runInsertInto(String sql) throws IOException {
		String res = sql;
		res = DQLTranslater.changeSample(res);
		res = DQLTranslater.transduceSelectSQL(res);
		return res+"\r\n;";
	}
	// insert select
	public static String runInsertSelect(String sql) throws IOException {
		String res = "";
		String insert = RegexTool.getRegexTarget("INSERT\\s+INTO\\s+\\S+\\s+", sql).get(0).trim();
		String select = RegexTool.decodeSQL(RegexTool.encodeSQL(sql).replaceAll(RegexTool.encodeSQL(insert), ""));
		res = insert+"\r\n"+DQLTranslater.transduceSelectSQL(select);
		return res;
	}
	// Merge Into
	public static String runMergeInto(String sql) throws IOException {
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
