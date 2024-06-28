package etec.src.file.ddim.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import etec.common.enums.FileTypeEnum;
import etec.common.utils.RegexTool;
import etec.common.utils.file.FileTool;
import etec.src.file.azure.service.CreateListService;
import etec.src.file.azure.service.SQLTextService;
import etec.src.file.azure.service.TransformService;
import etec.src.file.family_mart.service.POCTransduserService;
import etec.src.sql.az.translater.OtherTranslater;

public class DDIMFileTransduceService {
	// 依照檔案的種類執行對應的邏輯
	public static String run(File f) throws IOException {
		System.out.println("FileTransduceService");
		String result = "";
		String fn = f.getPath();
		String ofc = FileTool.readFile(f).replaceAll("\uFEFF", "");
		if (f.getName().contains(".txt") || f.getName().contains(".sql")) {
			POCTransduserService.run(fn, ofc);
		} else {
			// 置換參數
			ofc = replaceParams(ofc);
			ofc = ofc.replaceAll("\\} \\.", "}.");
			// 清除註解
//			String fc = OtherTranslater.cleanSql(ofc);
			String fc = ofc;
			// 區分種類
			// String file = BasicParams.getTargetFileNm(fn);
			// ReadFileTool.createFile(file,fc);
			FileTypeEnum type = getType(ofc);
			System.out.println("file type : " + type);
//			CreateListService.createCreateTable(fn, fc);
			CreateListService.createGroupBy(fn, fc);
//			CreateListService.createChar10(fn, ofc);
			switch (type) {
			case SQLTEXT:
				result = SQLTextService.run(fn, ofc);
				break;
			case EXPORT:
				result = ExportService.run(fn, fc);
				break;
			case FASTLOAD:
				result = FastloadService.run(fn, ofc);
				break;
			case TRANSFORM:
				result = TransformService.run(fn, fc);
				break;
			}
		}
		return result;
	}

	// 區分類型
	private static FileTypeEnum getType(String c) {
		String t = c.toUpperCase().replaceAll("\\s+", "");
		if (t.contains("$SQLTEXT")) {
			return FileTypeEnum.SQLTEXT;
		} else if (t.contains("OUTPUT_FILE")) {
			return FileTypeEnum.EXPORT;
		} else if (t.contains("BEGINLOADING")) {
			return FileTypeEnum.FASTLOAD;
		} else {
			return FileTypeEnum.TRANSFORM;
		}
	}

	// 將perl的參數置換到sql語句中
	public static String replaceParams(String fc) {
		String result = RegexTool.encodeSQL(fc);
		// 列出參數清單
		List<String> paramList = RegexTool.getRegexTarget("(?<=my\\s{0,10}\\$)[^=\\s]+\\s*=\\s*\\$ENV[^;]+", fc);
		Map<String, String> paramMap = new HashMap<String, String>();
		// 把參數加到map
		for (String param : paramList) {
			String[] arparam = param.split("=");
			String paramNm = "${" + arparam[0].trim() + "}";
			String paramVal = arparam[1].replaceAll("(ENV)|\"", "").trim();
			paramMap.put(RegexTool.encodeSQL(paramNm), RegexTool.encodeSQL(paramVal));
		}
		// 置換參數
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			result = result.replaceAll(entry.getKey(), entry.getValue());
		}
		return RegexTool.decodeSQL(result);
	}
}
