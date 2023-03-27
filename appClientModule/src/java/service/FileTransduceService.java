package src.java.service;

import java.io.File;
import java.io.IOException;

import src.java.enums.FileTypeEnum;
import src.java.service.transducer.ExportTransducer;
import src.java.service.transducer.FastloadTransducer;
import src.java.service.transducer.SQLTextTransducer;
import src.java.service.transducer.TextTransduserService;
import src.java.service.transducer.TransformTransducer;
import src.java.tools.ReadFileTool;
import src.java.tools.TransduceTool;

public class FileTransduceService {
	//依照檔案的種類執行對應的邏輯
	public static String run(File f) throws IOException {
		System.out.println("FileTransduceService");
		String result  = "";
		String fn  = f.getPath();
		String ofc =  ReadFileTool.readFile(f);
		if(f.getName().contains(".txt")) {
			TextTransduserService.run(fn, ofc);
		}
		else {
			//置換參數
			ofc = TransduceTool.replaceParams(ofc);
			ofc = ofc.replaceAll("\\} \\.", "}.");
			//清除註解
			String fc = TransduceTool.cleanSql(ofc);
			//區分種類
			//String file = BasicParams.getTargetFileNm(fn);
			//ReadFileTool.createFile(file,fc);
			FileTypeEnum type =  getType(ofc);
			System.out.println("file type : "+type);
//			CreateListService.createCreateTable(fn, fc);
			CreateListService.createGroupBy(fn, fc);
//			CreateListService.createChar10(fn, ofc);
			switch (type) {
				case SQLTEXT:
					result = SQLTextTransducer.run(fn,ofc);
					break;
				case EXPORT:
					result = ExportTransducer.run(fn,fc);
					break;
				case FASTLOAD:
					result = FastloadTransducer.run(fn,ofc);
					break;
				case TRANSFORM:
					result = TransformTransducer.run(fn,fc);
					break;
			}
		}
		return result;
	}
	//區分類型
	private static FileTypeEnum getType(String c) {
		String t = c.toUpperCase().replaceAll("\\s+", "");
		if(t.contains("$SQLTEXT")) {
			return FileTypeEnum.SQLTEXT;
		}else if(t.contains("OUTPUT_FILE")) {
			return FileTypeEnum.EXPORT;
		}else if(t.contains("BEGINLOADING")) {
			return FileTypeEnum.FASTLOAD;
		}else {
			return FileTypeEnum.TRANSFORM; 
		}
	}
}
