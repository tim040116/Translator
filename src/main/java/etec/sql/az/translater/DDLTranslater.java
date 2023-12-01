package etec.sql.az.translater;

import java.io.IOException;
import java.util.List;

import etec.common.utils.RegexTool;

public class DDLTranslater {
	// create table
	public static String runCreateTable(String sql) throws IOException {
		// create語法轉換
		String create = sql.replaceAll(";","");
		create = replaceCreateTitle(create);
		create = replaceTDsql(create);
		create = replaceColumn(create);
		create = create.trim();
		// Index語法轉換
		String with = addWith(sql);
		// drop if exist
		String drop = "";
		create = drop + create+with+"\r\n;";
		return create;
	}
	public static String easyReplace(String sql) throws IOException {
		String res = sql;
		res = runDropTable(res);
		res = runRenameTable(res);
		res = runStatistics(res);
		res = runReplaceView(res);
		res = replaceCreateTitle(res);
		res = replaceTDsql(res);
		return res;
	}
	// CTAS
	public static String runCTAS(String sql) throws IOException {
		String res = "";
		String create = "";
		String select = "";
		String with = "";
		// create
		create = RegexTool.getRegexTarget("(?i)CREATE\\s+TABLE\\s+\\S+", 
					sql.replaceAll("(?i)\\bVOLATILE\\b", " ")
					.replaceAll("(?i)\\bMULTISET\\b", "")
					.replaceAll("(?i)\\bSET\\s+TABLE", "TABLE")
				).get(0)
				;
		// with
		List<String> lstPrimaryIndex = RegexTool.getRegexTarget("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", sql);
		List<String> lstIndex = RegexTool.getRegexTarget("(?i)PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", sql);
		String column = "";
		if (!lstPrimaryIndex.isEmpty()) {
			String indexCol = lstPrimaryIndex.get(0).replaceAll("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\(", "")
					.replaceAll("\\)", "").replaceAll("\\s", "").trim();
			column += indexCol;
		}
		if (!lstIndex.isEmpty()) {
			if (!lstPrimaryIndex.isEmpty()) {
				column += ",";
			}
			String indexCol = lstIndex.get(0).replaceAll("(?i)PRIMARY\\s+INDEX\\s+\\(", "").replaceAll("\\)", "")
					.replaceAll("\\s", "").trim();
			column += indexCol;
		}
		String hash = "DISTRIBUTION = " + ("".equals(column) ? "REPLICATE" : "HASH(" + column + ")");
		with = "WITH (" + "\r\n\tCLUSTERED COLUMNSTORE INDEX," + "\r\n\t" + hash + "\r\n)";
		// select
		String oldselect = sql
				.replaceAll("(?i)CREATE(\\s+\\S+)*\\s+TABLE\\s+\\S+\\s+AS\\s*\\(", "")
				.replaceAll("(?i)\\)\\s*WITH\\s+DATA\\s*[^;]+", "")
				.replaceAll("TtEeSsTt", "%;%").trim();
		select = DQLTranslater.transduceSelectSQL(oldselect);
		res = create.trim() + "\r\n" + with.trim() + "\r\nAS\r\n" + select.trim()+"\r\n;";
		res = res.replaceAll(";\\s*;", ";");
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * 可用於drop table跟drop view
	 * 會加上if exist的語法
	 * 
	 * */
	public static String runDropTable(String sql) throws IOException {
		String res = sql
				.replaceAll("(?i)DROP\\s+TABLE\\s+([^;]+)", "IF OBJECT_ID(N'$1') IS NOT NULL\r\nDROP TABLE $1");
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * rename table 改成rename object
	 * 新名稱不要加DBname
	 * */
	public static String runRenameTable(String sql) throws IOException {
		String result = sql;
		result = result.replaceAll("(?i)RENAME\\s+TABLE\\s+(\\S+)\\s+TO\\s+([^;]+)", "RENAME OBJECT $1 TO $2");
		return result;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * COLLECT STATISTICS ON 改成 UPDATE STATISTICS
	 * */
	public static String runStatistics(String sql) throws IOException {
		String result = sql.replaceAll("(?i)\\bCOLLECT\\s+STATISTICS\\s+ON", "UPDATE STATISTICS");
		return result;
	}
	// ReplaceView
	public static String runReplaceView(String sql) throws IOException {
		String res = "";
		res = sql.replaceAll("(?i)REPLACE\\s+VIEW", "ALTER VIEW")+ "\r\n;";
		return res;
	}
	// 轉換create table
	private static String replaceCreateTitle(String sql) {
		String result = sql.replaceAll("(?i)\\s*,\\s*NO\\s*FALLBACK\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*NO\\s*[A-Za-z]+\\s*JOURNAL\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*CHECKSUM\\s*=\\s*[A-Za-z]+\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*DEFAULT\\s*MERGEBLOCKRATIO\\s*", " ")
				.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
				.replaceAll("(?i)(NOT\\s+)?CASESPECIFIC", " ")
				.replaceAll("(?i)TITLE\\s+'[^']+'", " ");
		return result;
	}
	// 轉換 index with
	private static String addWith(String sql) {
		String result = "\r\nWITH (" + "\r\n\tCLUSTERED COLUMNSTORE INDEX,";
		String temp = sql.toUpperCase();
		// 取得欄位
		List<String> lstPrimaryIndex = RegexTool.getRegexTarget("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
		temp = temp.replaceAll("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", "");
		List<String> lstIndex = RegexTool.getRegexTarget("(?i)PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
		// 添加欄位
		String column = "";
		if (!lstPrimaryIndex.isEmpty()) {
			String indexCol = lstPrimaryIndex.get(0).replaceAll("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\(", "")
					.replaceAll("\\)", "").replaceAll("\\s", "").trim();
			column += indexCol;
		}
		if (!lstIndex.isEmpty()) {
			if (!lstPrimaryIndex.isEmpty()) {
				column += ",";
			}
			String indexCol = lstIndex.get(0).replaceAll("(?i)PRIMARY\\s+INDEX\\s+\\(", "").replaceAll("\\)", "")
					.replaceAll("\\s", "").trim();
			column += indexCol;
		}
		String hash = "\r\n\tDISTRIBUTION = " + ("".equals(column) ? "REPLICATE" : "HASH(" + column + ")");
		result += hash + "\r\n)";
		return result;
	}
	// 清除TD特有的語法
	private static String replaceTDsql(String sql) {
		String result = sql
				.replaceAll("(?i)\\bVOLATILE\\b", "")// VOLATILE
				.replaceAll("(?i)\\bMULTISET\\b", "")// MULTISET
				.replaceAll("(?i)SET\\s+TABLE", "TABLE")// SET TABLE
				.replaceAll("(?i)(UNIQUE\\s+)?(PRIMARY\\s+)?INDEX\\s+\\([^\\)]+\\)", " ")// UNIQUE PRIMARY INDEX
				.replaceAll("(?i)NO\\s+PRIMARY\\s+INDEX", "")
				.replaceAll("(?i)RANGE_N" + "\\s*\\([^\\)]+\\)", " ")// PARTITION BY
				.replaceAll("(?i)PARTITION\\s+BY\\s*(\\s*\\([^\\)]*\\))?", " ")// PARTITION BY
		;
		return result;
	}
	// 清除欄位的多餘設定
	private static String replaceColumn(String sql) {
		String result = sql.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
				.replaceAll("(?i)NOT\\s+CASESPECIFIC", " ")
				.replaceAll("(?i)TITLE\\s+'[^']+'", " ")
				.replaceAll("(?i)\\s*FORMAT\\s+'[^']+'\\s*", " ")
				.replaceAll("(?i)TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
				.replaceAll("(?i)VARBYTE", "VARBINARY")
				.replaceAll(" +,", ",");
		return result;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月17日
	 * 
	 * 解決ms sql 不支援CTAS語法問題，將CTAS轉為select into語法
	 * */
	public static String runCTASToSelectInto(String sql) {
		String res = "";
		String tableNm = sql.replaceAll("(?i)\\s*CREATE\\s+TABLE\\s+(\\S+)\\s+[\\S\\s]+", "$1");
		String selectSrc = sql.replaceAll("(?i)[\\S\\s]+\\s+AS\\s+SELECT", "SELECT");
		String[] arrFrom = selectSrc.split("(?i)FROM");
		for(String str: arrFrom) {
			res+="".equals(res)?(str.trim()+"\r\nINTO "+tableNm+"\r\n"):("FROM"+str);
		}
		res = res.trim()
//				.replaceAll(regex, replacement)
				;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月24日
	 * 
	 * 解決ms sql 不支援CTAS語法問題，將CTAS轉為select into語法
	 * */
	public static String runSelectIntoToCTAS(String sql) {
		String res = "";
		String tableNm = sql.replaceAll("(?i)[\\S\\s]+INTO\\s+(\\S+)\\s+FROM[\\S\\s]+", "$1");
		String selectSrc = sql.replaceAll("(?i)INTO\\s+(\\S+)\\s+", "");
		res = "CREATE TABLE " + tableNm + "\r\n"
				+ "WITH ( \r\n"
				+ "\tCLUSTERED COLUMNSTORE INDEX,\r\n"
				+ "\tDISTRIBUTION = REPLICATE\r\n"
				+ ")\r\nAS\r\n"+selectSrc
				;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月27日
	 * 
	 * 去除ms sql 不支援的語法
	 * */
	public static String runCreateTableAzToMs(String sql) {
		String res = "";
		res = sql
				.replaceAll("(?i)\\bON\\s+COMMIT.*", "")
				.replaceAll("(?i)\\bWITH\\s*\\([\\S\\s]+", ";")
				;
		return res;
	}
}
