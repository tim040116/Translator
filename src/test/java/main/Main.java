package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.file.readfile.service.FileTool;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class Main {

	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";

	static String outputFile = "D:\\JobServer\\EXPORT_OUT\\etec\\SV_ITA_DAILY.${TXDATE1}.txt"; 
	
	public static void main(String[] args) {
		
		try {
			String content = FileTool.readFile("C:\\Users\\user\\Desktop\\Trans\\T0\\fast export\\new.sql");
			for(String sql : content.split(";")) {
				sql = cleanCode(sql);
				String reg = "(?i)CREATE\\s+TABLE\\s+(?<tableName>[\\w.]+)\\s*\\("
						+ "(?<col>[\\S\\s]+?)\\)\\s*$";
				Matcher m = Pattern.compile(reg).matcher(sql);
				while(m.find()) {
					String tableName = m.group("tableName");
					String strCol = m.group("col");
					String str = buildContent(tableName,strCol);
					FileTool.createFile("C:\\Users\\user\\Desktop\\Trans\\T0\\fast export\\"+tableName.replaceAll("^\\w+\\.", "")+".sql", str);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String buildContent(String tableName,String strCol) {
		List<String> lst = new ArrayList<String>();
		for(String col : strCol.split("\\r?\\n")) {
			col = col.trim().replaceAll("^(\\w+).*","$1");
			if(!col.matches("\\s*")) {
				lst.add(col);
			}
		}
		String res = "\r\n"
				+ ".LOGON ${USERID},${PASSWD}\r\n"
				+ "\r\n"
				+ ".OS DEL FILE = " + outputFile + "\r\n"
				+ ".EXPORT REPORT FILE = " + outputFile + "\r\n"
				+ "\r\n"
				+ ".SET RECORDMODE OFF;\r\n"
				+ ".SET FORMAT OFF ;\r\n"
				+ ".SET TITLEDASHES OFF;\r\n"
				+ ".SET SEPARATOR '\",\"';\r\n"
				+ ".SET WIDTH 65531\r\n"
				+ "\r\n"
				+ "SELECT\r\n"
				+ "\t\t'\"'\t || COALESCE(TRIM("
				+ String.join("),'')\r\n	|| '\",\"' || COALESCE(TRIM(",lst)
				+ "),'')\r\n"
				+ "\t	|| \t'\"'\r\n"
				+ "(TITLE '" + String.join(",", lst) + "')\r\n"
				+ "FROM " + tableName + "\r\n"
				+ "WHERE BATCH_NO = ${TXDATE}\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ "\r\n"
				+ ".EXPORT RESET;\r\n"
				+ "\r\n"
				+ ".LOGOFF;\r\n"
				+ "";
		return res;
	}
	public static String cleanCode(String sql) {
		sql = sql.replaceAll("(?i)CREATE(?:\\s+MULTI(?:SET)?)?\\s+TABLE\\s+([^,]+)\\s*,", "CREATE TABLE $1");
		sql = sql.replaceAll("(?i)CHARACTER\\s+SET\\s+\\w+", "");
		sql = sql.replaceAll("(?i)(?:NOT\\s+)?CASESPECIFIC", "");
		sql = sql.replaceAll("(?i)TITLE\\s*'[^']+'", "");
		sql = sql.replaceAll("(?i)NOT\\s+NULL", "");
		sql = sql.replaceAll("(?i)DEFAULT\\s*[\\w.']+,?", "");
		sql = sql.replaceAll("(?i)\\s*FALLBACK\\s*,?","");
		sql = sql.replaceAll("(?i)\\s*NO\\s+BEFORE\\s+JOURNAL\\s*,?","");
		sql = sql.replaceAll("(?i)\\s*NO\\s+AFTER\\s+JOURNAL\\s*,?","");
		sql = sql.replaceAll("(?i)\\s*CHECKSUM\\s*=\\s*DEFAULT\\s*,?","");
		sql = sql.replaceAll("(?i)\\s*DEFAULT\\s+MERGEBLOCKRATIO\\s*,?","");
		sql = sql.replaceAll("(?i)\\s*MAP\\s*=\\s*\\w+\\s*,?", "");
		;
		sql = sql
			.replaceAll("(?i)(?:PRIMARY)\\s*INDEX\\s*\\([^)]+\\)","")
			.replaceAll("(?i)PARTITION\\s+BY\\s*[^;]+", "")
		;
		return sql;
	}

}
