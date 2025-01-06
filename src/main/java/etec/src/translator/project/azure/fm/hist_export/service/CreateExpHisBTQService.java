package etec.src.translator.project.azure.fm.hist_export.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateExpHisBTQService {
	
	public static String buildContent(String outputFile,String dbName,String tableName,List<String> lst,String where) {
		String zipNm  = outputFile.replaceAll("\\.\\w+$", ".gz");
		String sftpNm = outputFile.replaceAll("([^\\\\]+)\\.\\w+$", "SftpDLA_$1.txt");
		Map<String,String> mapargs = new HashMap<String,String>();
		mapargs.put("", zipNm);
		String res = "\r\n"
				+ ".LOGON ${USERID},${PASSWD}\r\n"
				+ ".SET SESSION CHARSET 'UTF8'\r\n"
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
				+ "select a\r\n"
				+ "(TITLE '')\r\n"
				+ "from (select '"+String.join(",", lst)+"' as a) a\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".IF ERRORCODE <> 0 THEN .QUIT 1;\r\n"
				+ "\r\n"
				+ ".EXPORT REPORT FILE = "+outputFile+"\r\n"
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
				+ "(TITLE '')\r\n"
//				+ "(TITLE '" + String.join(",", lst) + " ".repeat(100) + "')\r\n"
//				+ "(TITLE '')\r\n"/**20241030 Tim 依照浩鈞要求，title要放空字串，且必須保留**/
				+ "FROM " + dbName + "." + tableName + "\r\n"
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
				+ "--SFTP hotlake\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "\r\n"
				+ ".os ECHO cd ../iga/dev/todla> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO mput " + zipNm + ">> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO bye>> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os echo y | D:\\JobServer\\BIN\\psftp.exe tfmngdpdwdatalakeprem.iga@10.202.81.158 -pw oAruQWnYmgoUGiKEj1CFHzUIEFU5D9mE -b "+sftpNm+"\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "--SFTP coldlake\r\n"
				+ "---------------------------------------------------------------------\r\n"
				+ "\r\n"
				+ ".os ECHO mkdir "+tableName+"> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO cd "+tableName+">> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO mkdir ${TX4YM}>> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO bye>> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os echo y | D:\\JobServer\\BIN\\psftp.exe tfmngdpdwdatalake.td-hist.iga@10.202.81.156 -pw ICqsoSd3aNNgCTUDNBOajxlPVL0w6HGo -be -b "+sftpNm+"\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "----------------------------\r\n"
				+ "\r\n"
				+ ".os ECHO cd "+tableName+"/${TX4YM}> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ";\r\n"
				+ ".os ECHO mput "+zipNm+">> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os ECHO bye>> "+sftpNm+"\r\n"
				+ ";\r\n"
				+ "\r\n"
				+ ".os echo y | D:\\JobServer\\BIN\\psftp.exe tfmngdpdwdatalake.td-hist.iga@10.202.81.156 -pw ICqsoSd3aNNgCTUDNBOajxlPVL0w6HGo -b "+sftpNm+"\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "\r\n"
				+ "-----------------------\r\n"
				+ "\r\n"
				+ ".os del "+zipNm+";\r\n"
				+ ".OS DEL "+outputFile+"\r\n"
				+ "\r\n"
				+ ".IF SYSTEMRETURNCODE != 0 THEN .QUIT 1;\r\n"
				+ "\r\n"
				+ ".LOGOFF;\r\n"
				+ ".QUIT 0;"
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
