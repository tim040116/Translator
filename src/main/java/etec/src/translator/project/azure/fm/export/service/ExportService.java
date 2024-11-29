package etec.src.translator.project.azure.fm.export.service;

import java.util.List;

public class ExportService {
	
	public static String buildContent(String outputFile,String tableName,List<String> lst,String where) {
		String zipNm = outputFile.replaceAll("\\.\\w+$", ".gz");
		String res = "\r\n"
				+ ".LOGON ${USERID},${PASSWD}\r\n"
				+ "\r\n"
				+ ".OS DEL FILE = " + outputFile + "\r\n"
				+ ".EXPORT REPORT FILE = " + outputFile + "\r\n"
				+ "\r\n"
				+ ".SET RECORDMODE OFF;\r\n"
				+ ".SET FORMAT OFF ;\r\n"
				+ ".SET TITLEDASHES OFF;\r\n"
				+ ".SET SEPARATOR ',';\r\n"
				+ ".SET WIDTH 65531\r\n"
				+ "\r\n"
				+ "SELECT\r\n"
				+ "\t\t'\"'\t || COALESCE(TRIM("
				+ String.join("),'')\r\n	|| '\",\"' || COALESCE(TRIM(",lst)
				+ "),'')\r\n"
				+ "\t|| \t'\"'\r\n"
				+ "(TITLE '" + String.join(",", lst) + " ".repeat(100) + "')\r\n"
//				+ "(TITLE '')\r\n"/**20241030 Tim 依照浩鈞要求，title要放空字串，且必須保留**/
				+ "FROM " + tableName + "\r\n"
				+ "WHERE " + where + "\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n.EXPORT RESET;\r\n"
				+ "\r\n"
				+ "\r\n.IF ERRORCODE <> 0 THEN .QUIT 1;"
				+ "\r\n\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "--ZIP\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "\r\n"
				+ ".os del " + zipNm + ";\r\n"
				+ "\r\n"
				+ ".os \"C:\\Program Files (x86)\\7-Zip\\7z.exe\" a -tgzip " + zipNm + " " + outputFile + "\r\n"
				+ "\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "--SFTP\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "\r\n"
				+ ".os ECHO cd ../iga/dev/todla> D:\\JobServer\\DATA\\HIST_BACKUP\\TEST\\SftpDLA_TDOLDATA.txt\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO mput " + zipNm + ">> D:\\JobServer\\DATA\\HIST_BACKUP\\TEST\\SftpDLA_TDOLDATA.txt\r\n"
						+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO bye>> D:\\JobServer\\DATA\\HIST_BACKUP\\TEST\\SftpDLA_TDOLDATA.txt\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os echo y | D:\\JobServer\\BIN\\psftp.exe tfmngdpdwdatalakeprem.iga@10.202.81.158 -pw oAruQWnYmgoUGiKEj1CFHzUIEFU5D9mE -b D:\\JobServer\\DATA\\HIST_BACKUP\\TEST\\SftpDLA_TDOLDATA.txt\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "\r\n.LOGOFF;"
				+ "\r\n.QUIT 0;"
		;
		return res;
	}
	
	@Deprecated
	private static String cleanCode(String sql) {
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
		sql = sql.replaceAll("(?i)(?:PRIMARY)\\s*INDEX\\s*\\([^)]+\\)","");
		sql = sql.replaceAll("(?i)PARTITION\\s+BY\\s*[^;]+", "");
		return sql;
	}
}
