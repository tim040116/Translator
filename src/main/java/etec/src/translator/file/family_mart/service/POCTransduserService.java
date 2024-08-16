package etec.src.translator.file.family_mart.service;

import java.io.IOException;
import java.util.List;

import etec.common.utils.RegexTool;
import etec.framework.context.translater.exception.SQLFormatException;
import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.file.model.BasicParams;
import etec.src.translator.sql.az.translater.DDLTranslater;
import etec.src.translator.sql.az.translater.DMLTranslater;
import etec.src.translator.sql.az.translater.DQLTranslater;

/**
 * 全家POC
 */
public class POCTransduserService {
	private static int cntarea = 1;
	// 執行
	public static String run(String fn, String fc) throws IOException {
		StringBuffer context = new StringBuffer();
		System.out.println("POCTransduserService");
		String targetFile = BasicParams.getTargetFileNm(fn).replace(".txt", ".sql");
		String newfc = addSignInRemark(fc).toUpperCase();
		cntarea = 1;
		context.append("\r\nSET NOCOUNT ON;\r\n\r\n");
		// 每一段
		for (String sql : newfc.toUpperCase().split(";")) {
			String newSql = transformSingleSQL(sql.trim());
			newSql = convertStoreFunction(newSql);
			newSql = convertPOC(newSql);
			context.append(newSql);
		}
		FileTool.createFile(targetFile, context.toString());
		return "Success";
	}
	private static String convertPOC(String sql) {
		String res = sql;
		List<String> lst = RegexTool.getRegexTarget("CAST\\s*\\(CAST\\s*\\(\\s*\\S+\\s+AS\\s+DATE\\s*\\)\\s*AS\\s+INTEGER\\s*\\)\\s*\\+\\s*19000000", sql);
		for(String cast : lst) {
			String column = cast
					.replaceAll("CAST\\s*\\(CAST\\s*\\(\\s*", "")
					.replaceAll("\\s+AS\\s+DATE\\s*\\)\\s*AS\\s+INTEGER\\s*\\)\\s*\\+\\s*19000000","");
			res = res.replace(cast, " CONVERT(int,CONVERT(varchar(8),"+column+", 112))");
		}
		return res;
	}
	private static String convertStoreFunction(String sql) {
		String res = sql
				.replaceAll("(?i)BIT_AND\\s*\\(", "dbo.bit_and(")
				.replaceAll("(?i)BIT_EXTRACT\\s*\\(", "dbo.bit_extract(")
				.replaceAll("(?i)BIT_GEN_AGGT\\s*\\(", "dbo.bit_gen_aggt(")
				.replaceAll("(?i)BIT_OR\\s*\\(", "dbo.bit_or(")
				.replaceAll("(?i)BIT_OR_AGGT\\s*\\(", "dbo.bit_or_aggt(")
		
				;
		return res;
	}
	/*
	 * 拆分每一段語法並處裡
	 * 
	 * 並依照種類進行處理
	 */
	private static String transformSingleSQL(String sql) throws IOException {
		String res = "";
		String newSql = sql.toUpperCase();
		/*
		 * 20220615 針對有含 like '%;%'的分號字元，先將%;%置換成TtEeSsTt 再處理 List<String> lstSql =
		 * RegexTool.getRegexTarget("(?!=;)[^;]*;",fc); =>List<String> lstSql =
		 * RegexTool.getRegexTarget("(?!=;)[^;]*;",fc.replaceAll("%;%","TtEeSsTt"));
		 */
		
		if(sql.matches("\\s*")) {
			res = "";
		}
		//註解
		else if(sql.matches("\\s*((--)|(\\/\\*))[^;]+")) {
			res = sql+"\r\n";
		}
		// drop table
		else if (newSql.matches("DROP\\s+TABLE\\s+\\S+\\s*")) {
			res = DDLTranslater.runDropTable(newSql);
		}
		// REPLACE VIEW
		else if (newSql.matches("REPLAC\\s+VIEW\\s+\\S+\\s+AS\\s+[^;]+")) {
			res = DDLTranslater.runReplaceView(newSql);
		}
		// CTAS
		else if (newSql.matches("CREATE(\\s+VOLATILE)?\\s+TABLE\\s+\\S+\\s+AS\\s*\\([^;]+")) {
			res = addTryCatch(DDLTranslater.runCTAS(newSql));
		}
		// create table no select
		//必須放在CTAS後面
		else if (newSql.matches("CREATE\\s*(MULTISET|SET)?\\s+TABLE\\s+\\S+\\s+[^;]+")) {
			res = addTryCatch(DDLTranslater.runCreateTable(newSql));
		}
		// MERGE INTO 
		else if (newSql.matches("MERGE\\s+INTO\\s+\\S+\\s+[^;]+")) {
			res = addTryCatch(DMLTranslater.runMergeInto(newSql));
		}
		// INSERT SELECT
		else if (newSql.matches("INSERT\\s+INTO\\s+\\S+\\s+SELECT\\s+[^;]+")) {
			res = addTryCatch(DMLTranslater.runInsertSelect(newSql));
		}
		// INSERT INTO
		else if (newSql.matches("INSERT\\s+INTO\\s+\\S+[^;]+")) {
			res = addTryCatch(DMLTranslater.runInsertInto(newSql));
		}
		//SELECR
		else if (newSql.matches("SELECT\\s+[^;]+")) {
			res = DQLTranslater.easyReplace(newSql)+"\r\n;";
		}
		
		// drop view
		else if (newSql.matches("(?i)[^;]*drop\\s+view[^;]*;")) {
			// drop view
			int sqlindex = newSql.replaceAll("[Dd][Rr][Oo][Pp]", "drop").indexOf("drop");
			String BFsql = "";
			if (sqlindex > 0) {
				BFsql = newSql.substring(0, sqlindex);
				newSql = newSql.substring(sqlindex);
			}
			String tableVw = newSql.replaceAll("(?i)(\\s*DROP\\s*VIEW\\s*)|;|( *--\\S* *)", "").trim();
			newSql = BFsql + "IF OBJECT_ID(N'" + tableVw + "') IS NOT NULL \r\n" + "DROP VIEW " + tableVw + " ; \r\n";
			res += newSql + "\r\n";
		}
		// rename table
		else if (newSql.matches("(?i)[^;]*rename\\s+table\\s+[^;]*;")) {
			// rename table
			int sqlindex = sql.replaceAll("(?i)RENAME", "rename").indexOf("rename");
			String BFsql = "";
			if (sqlindex > 0) {
				// 取出 rename 前面的字串
				BFsql = sql.substring(0, sqlindex);
				sql = sql.substring(sqlindex);
			}
			String[] tableNm = sql.replaceAll("(?i),|rename\\s+table", "").split("(?i)\\s+TO\\s+");
			// 20220613 update
			// String renameSql = " exec sp_rename N'"+tableNm[0]+"', N'"+tableNm[1]+"';
			// \r\n\r\n";
			// newSql = BFsql + "exec sp_rename '"+tableNm[0].replaceAll(" ", "").trim()+"'
			// to
			// '"+tableNm[1].replaceAll(RegexTool.getReg("\\$\\{[^\\.]*\\."),"").replaceAll(";",
			// "").trim()+"'; \r\n\r\n";
			newSql = BFsql + " rename object " + tableNm[0].replaceAll(" ", "").trim() + " to "
					+ tableNm[1].replaceAll("\\$\\{[^\\.]*\\.", "").replaceAll(";", "").trim()
					+ "; \r\n\r\n";

			res += newSql + "\r\n";
		}
		// COLLECT STATISTICS
		else if (sql.matches("(?i)[^;]*COLLECT\\s+STATISTICS\\s+[^;]*;")) {
			newSql = sql.replaceAll("(?i)COLLECT\\s+STATISTICS\\s+ON", "UPDATE STATISTICS");
			res += newSql + "\r\n";
		}
		// COMMENT ON
		else if (sql.matches("(?i)[^;]*COMMENT\\s+ON\\s+[^;]*;")) {
			newSql = "";
			res += newSql + "\r\n";
		}
		// drop table
		else if (newSql.matches("SELECT.*")) {
			res = DQLTranslater.easyReplace(newSql);
		}
//		res = res.replaceAll("TtEeSsTt", "%;%");
		return res;
	}

	/*
	 * 清除不需要的語法
	 * 
	 */
	private static String easyRemove(String fc) {

		/*
		 * 20220613 Modify .replaceAll("COMPRESS", "") =>
		 * .replaceAll("COMPRESS[^\\r\\n]*", "") 20220613 add
		 * .replaceAll(RegexTool.getReg("\\slogon \\$\\{USERID\\}, \\$\\{PASSWD\\};"),
		 * "") 20220623 add
		 * .replaceAll(RegexTool.getReg("\\.IF ACTIVITY")+"[^\\r\\n]*\\r\\n", "")
		 */

		String res = fc.replaceAll("\\.[Ss][Ee][Tt] [^\\r\\n]*\\r\\n", "")
				.replaceAll("\\.[Qq][Uu][Ii][Tt] *[0-9]*;", "")
				.replaceAll("(?i)\\.IF\\s+ERRORCODE\\s+<>\\s+0\\s+THEN[^\\r\\n]*\\r\\n", "")
				.replaceAll("(?i)\\.SET\\s+ERROROUT\\s+STDOUT[^\\r\\n]*\\r\\n", "")
				.replaceAll("(?i)\\.GOTO\\s+ERRORSFOUND[^\\r\\n]*\\r\\n", "")
				.replaceAll("(?i)\\.LABEL\\s+ERRORSFOUND[^\\r\\n]*\\r\\n", "")
				.replaceAll("(?i)\\.IF\\s+ACTIVITY[^\\r\\n]*\\r\\n", "")
				.replaceAll(
						"(?i)[^\\r\\n]*logon\\s+\\$\\{USERID\\},\\s*\\$\\{PASSWD\\};[^\\r\\n]*\\r\\n",
						"")
				.replaceAll("(?i)[^\\r\\n]*LOGOFF;[^\\r\\n]*\\r\\n", "")
				.replaceAll("(?i)\\.EXPORT\\s+RESET;", "");

		return res;
	}



	// 轉換create table
	private static String replaceCreateTitle(String sql) {
		String result = sql.replaceAll("\\s*,\\s*NO\\s*FALLBACK\\s*", " ")
				.replaceAll("\\s*,\\s*NO\\s*[A-Za-z]+\\s*JOURNAL\\s*", " ")
				.replaceAll("\\s*,\\s*CHECKSUM\\s*=\\s*[A-Za-z]+\\s*", " ")
				.replaceAll("\\s*,\\s*DEFAULT\\s*MERGEBLOCKRATIO\\s*", " ").replaceAll("CHARACTER SET \\S+", " ")
				.replaceAll("(NOT\\s+)?CASESPECIFIC", " ").replaceAll("TITLE\\s+'[^']+'", " ");
		return result;
	}

	// 添加drop if exist
	private static String addDropIfExist(String sql) throws SQLFormatException {
		String result = "";
		List<String> lstCreate = RegexTool.getRegexTarget("CREATE\\s+TABLE\\s+\\S+", sql.toUpperCase());
		if (lstCreate.isEmpty()) {
			throw SQLFormatException.missingKeyWord("CREATE TABLE");
		}
		String tableNm = lstCreate.get(0).replaceAll("CREATE\\s+TABLE\\s+", "");
		result = "IF OBJECT_ID(N'" + tableNm + "') IS NOT NULL\r\n" + "DROP TABLE " + tableNm + ";\r\n";
		return result;
	}

	// 轉換 index with
	private static String addWith(String sql) {
		String result = "\r\nWITH (" + "\r\n\tCLUSTERED COLUMNSTORE INDEX,";
		String temp = sql.toUpperCase();
		// 取得欄位
		List<String> lstPrimaryIndex = RegexTool.getRegexTarget("UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
		temp = temp.replaceAll("UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", "");
		List<String> lstIndex = RegexTool.getRegexTarget("PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
		// 添加欄位
		String column = "";
		if (!lstPrimaryIndex.isEmpty()) {
			String indexCol = lstPrimaryIndex.get(0).replaceAll("UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\(", "")
					.replaceAll("\\)", "").replaceAll("\\s", "").trim();
			column += indexCol;
		}
		if (!lstIndex.isEmpty()) {
			if (!lstPrimaryIndex.isEmpty()) {
				column += ",";
			}
			String indexCol = lstIndex.get(0).replaceAll("PRIMARY\\s+INDEX\\s+\\(", "").replaceAll("\\)", "")
					.replaceAll("\\s", "").trim();
			column += indexCol;
		}
		String hash = "\r\n\tDISTRIBUTION = " + ("".equals(column) ? "REPLICATE" : "HASH(" + column + ")");
		result += hash + "\r\n)\r\n;";
		return result;
	}

	// 清除TD特有的語法
	private static String replaceTDsql(String sql) {
		String result = sql.replaceAll("(?i)CREATE\\s+MULTISET\\s+TABLE", "CREATE TABLE")// MULTISET
				.replaceAll("(?i)CREATE\\s+SET\\s+TABLE", "CREATE TABLE")// SET TABLE
				.replaceAll("(?i)(UNIQUE\\s+)?(PRIMARY\\s+)?INDEX\\s+\\([^\\)]+\\)", " ")// UNIQUE PRIMARY INDEX
				.replaceAll("(?i)NO\\s+PRIMARY\\s+INDEX", "")
				.replaceAll("(?i)RANGE_N\\s*\\([^\\)]+\\)", " ")// PARTITION BY
				.replaceAll("(?i)PARTITION\\s+BY(\\s*\\([^\\)]*\\))?", " ")// PARTITION BY
		;
		return result;
	}

	// 清除欄位的多餘設定
	private static String replaceColumn(String sql) {
		String result = sql.replaceAll("CHARACTER SET \\S+", " ").replaceAll("NOT CASESPECIFIC", " ")
				.replaceAll("TITLE\\s+'[^']+'", " ").replaceAll("\\s*FORMAT\\s+'[^']+'\\s*", " ")
				.replaceAll("TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME").replaceAll("VARBYTE", "VARBINARY")
				.replaceAll(" +,", ",");
		return result;
	}
	//在註解的後面加上分號以方便切分
	private static String addSignInRemark(String content) {
		StringBuffer sb = new StringBuffer();
		boolean ismark = false;
		boolean isFirst = true;
		for(String line : content.split("\r\n")) {
			if(isFirst) {
				if(line.matches("\\s*--.*")){
					line = line+";";
				}
				else if(line.matches("\\s*\\/\\*.*")) {
					ismark = true;
				}
				if(ismark&&line.matches(".*\\*\\/\\s*")) {
					line = line+";";
					ismark = false;
				}
			}
			sb.append(line+"\r\n");
			String ts = sb.toString().trim();
			isFirst = ts.lastIndexOf(";") == ts.length()-1;
		}
		String res = sb.toString();
		return res;
	}
	// try catch
	private static String addTryCatch(String sql) {
		if ("".equals(sql.trim())) {
			return "";
		}
		String res = "\r\nBEGIN TRY\r\n\r\n";
		res += sql.trim() + "\r\n\r\n";
		res += 	  "END TRY\r\n" 
				+ "BEGIN CATCH\r\n" 
				+ "  SELECT '" + cntarea + "' AS ErrorArea,\r\n"
				+ "    ERROR_NUMBER() AS ErrorNumber,\r\n" 
				+ "    ERROR_STATE() AS ErrorState,\r\n"
				+ "    ERROR_SEVERITY() AS ErrorSeverity,\r\n" 
				+ "    ERROR_PROCEDURE() AS ErrorProcedure,\r\n"
				+ "    ERROR_MESSAGE() AS ErrorMessage;\r\n" 
				+ "  RETURN ERROR_STATE();\r\n" 
				+ "END CATCH;\r\n";
		cntarea++;
		return res;
	}
}
