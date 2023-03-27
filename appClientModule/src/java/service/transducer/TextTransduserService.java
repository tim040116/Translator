package src.java.service.transducer;

import java.io.IOException;

import src.java.params.BasicParams;
import src.java.tools.ReadFileTool;
import src.java.tools.RegexTool;

public class TextTransduserService {
	public static String run(String fn, String fc) throws IOException {
		System.out.println("TextTransduserService");
		String content = "";
		//Create table
		String create = fc;
		create = replaceCreateTitle(create);
		create = replaceTDsql(create);
		create = replaceColumn(create);
		content = addTryCatch(create);
		//產檔
		String file = BasicParams.getTargetFileNm(fn);
		String[] arfn = file.split("\\\\");
		String frn = arfn[arfn.length-1];
		//產出檔名調整
		String fnn = frn.replace(".txt", ".sql").replace(".btq", "	.sql");
		file = file.replace(frn, fnn);
		ReadFileTool.createFile(file,content);
		String result = "Success";
		return result;
	}
	//create table
	private static String replaceCreateTitle(String sql) {
		String result = sql
				.replaceAll("\\s*,\\s*NO\\s*FALLBACK\\s*"," ")
				.replaceAll("\\s*,\\s*NO\\s*[A-Za-z]+\\s*JOURNAL\\s*"," ")
				.replaceAll("\\s*,\\s*CHECKSUM\\s*=\\s*[A-Za-z]+\\s*", " ")
				.replaceAll("\\s*,\\s*DEFAULT\\s*MERGEBLOCKRATIO\\s*", " ")
				.replaceAll("CHARACTER SET \\S+", " ")
				.replaceAll("(NOT\\s+)?CASESPECIFIC", " ")
				.replaceAll("TITLE\\s+'[^']+'", " ")
				;
		return result;
	}
	//
	private static String replaceTDsql(String sql) {
		String result = sql
				.replaceAll(RegexTool.getReg("CREATE MULTISET TABLE"), "CREATE TABLE")//MULTISET
				.replaceAll(RegexTool.getReg("CREATE SET TABLE"), "CREATE TABLE")//SET TABLE
				.replaceAll("(UNIQUE\\s+)?(PRIMARY\\s+)?INDEX\\s+\\([^\\)]+\\)", " ")//UNIQUE PRIMARY INDEX
				.replaceAll(RegexTool.getReg("RANGE_N")+"\\s*\\([^\\)]+\\)", " ")//PARTITION BY
				.replaceAll(RegexTool.getReg("PARTITION BY")+"(\\s*\\([^\\)]*\\))?", " ")//PARTITION BY
				;
		return result;
	}
	//
	private static String replaceColumn(String sql) {
		String result = sql
				.replaceAll("CHARACTER SET \\S+", " ")
				.replaceAll("NOT CASESPECIFIC", " ")
				.replaceAll("TITLE\\s+'[^']+'", " ")
				.replaceAll("\\s*FORMAT\\s+'[^']+'\\s*", " ")
				.replaceAll("TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
				;
		return result;
	}
	//
	private static String addTryCatch(String sql) {
		String[] singleSQL = sql.split(";");
		String res = "\r\nSET NOCOUNT ON;\r\n";
		int i = 1;
		for(String s : singleSQL) {
			if("".equals(s.trim())) {
				continue;
			}
			res+="\r\nBEGIN TRY\r\n"
					+s+";"
					+"\r\n\r\nEND TRY\r\n\r\n"
					+"BEGIN CATCH\r\n" + 
					"  SELECT '"+i+"' as ErrorArea\r\n" + 
					"    ERROR_NUMBER() AS ErrorNumber,\r\n" + 
					"    ERROR_STATE() AS ErrorState,\r\n" + 
					"    ERROR_SEVERITY() AS ErrorSeverity,\r\n" + 
					"    ERROR_PROCEDURE() AS ErrorProcedure,\r\n" + 
					"    ERROR_MESSAGE() AS ErrorMessage;\r\n" + 
					"END CATCH;\r\n"
					;
			i++;
		}
		return res;
	}
}
