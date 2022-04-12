package src.java.service.transducer;

import java.io.IOException;
import java.util.List;

import src.java.params.BasicParams;
import src.java.tools.ReadFileTool;
import src.java.tools.TransduceTool;

public class ExportTransducer {
	public static String run(String fn, String fc) throws IOException {
		String result = "Success";
		createSQLFile(fn, fc);
		createExportLst(fn, fc);
		return result;
	}
	//SQL轉換產檔
	private static String createSQLFile(String fn, String fc) throws IOException {
		System.out.println("buildExportSQLFile");
		String result = "Success";
		//語法轉換
		List<String> lstSql = TransduceTool.getRegexTarget("[Ss][Ee][Ll][Ee][Cc][Tt][^;]*;", fc);
		String content = "\r\nSET NOCOUNT ON;\r\n\r\n";
		for(String sql:lstSql) {
			content += TransduceTool.selectSQLTransduce(sql);
		}
		//產檔
		String file = BasicParams.getTargetFileNm(fn);
		String[] arfn = file.split("\\\\");
		String frn = arfn[arfn.length-1];
		String fnn = "exp_" + frn.replace(".pl", ".sql");
		file = file.replace(frn, fnn);
		ReadFileTool.createFile(file,content);
		return result;
	}
	//產檔export_lst
	private static String createExportLst(String fn, String fc) throws IOException {
		System.out.println("buildExportListFile");
		String result = "Success";
		String file = BasicParams.getOutputPath() + "lst\\lst_export.txt";
		List<String> lstTrg = TransduceTool.getRegexTarget("my \\$OUTPUT_FILE[^;]*;", fc);
		if(!lstTrg.isEmpty()) {
			String target = lstTrg.get(0).replaceAll("my|\\s|\\$OUTPUT_FILE|=|\"|;", "");
			ReadFileTool.addFile(file,fn + " " + target);
		}
		return result;
	}
}
