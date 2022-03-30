package src.java.service.transformer;

import java.io.IOException;
import java.util.List;

import src.java.model.BasicModel;
import src.java.tools.ReadFileTool;
import src.java.tools.TransformTool;

public class ExportTransformer {
	public static String run(String fn, String fc) throws IOException {
		String result = "Success";
		createSQLFile(fn, fc);
		createExportLst(fn, fc);
		return result;
	}

	private static String createSQLFile(String fn, String fc) throws IOException {
		System.out.println("buildExportSQLFile");
		String result = "Success";
		//語法轉換
		List<String> lstSql = TransformTool.getRegexTarget("[Ss][Ee][Ll][Ee][Cc][Tt][^;]*;", fc);
		String content = "\r\nSET NOCOUNT ON;\r\n\r\n";
		for(String sql:lstSql) {
			String txt = TransformTool.changeGroupBy(sql);
			txt = TransformTool.arrangeSQL(txt);
			content += txt + "\r\n\r\n";
		}
		//產檔
		String file = BasicModel.getTargetFileNm(fn);
		String[] arfn = file.split("\\\\");
		String frn = arfn[arfn.length-1];
		String fnn = "exp_" + frn.replace(".pl", ".sql");
		file = file.replace(frn, fnn);
		ReadFileTool.createFile(file,content);
		return result;
	}

	private static String createExportLst(String fn, String fc) throws IOException {
		System.out.println("buildExportListFile");
		String result = "Success";
		String file = BasicModel.getOutputPath() + "lst\\lst_export.txt";
		List<String> lstTrg = TransformTool.getRegexTarget("my \\$OUTPUT_FILE[^;]*;", fc);
		if(!lstTrg.isEmpty()) {
			String target = lstTrg.get(0).replaceAll("my|\\s|\\$OUTPUT_FILE|=|\"|;", "");
			ReadFileTool.addFile(file,fn + " " + target);
		}
		return result;
	}
}
