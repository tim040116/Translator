package src.java.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import src.java.enums.FileTypeEnum;
import src.java.service.transformer.ExportTransformer;
import src.java.service.transformer.FastloadTransformer;
import src.java.tools.ReadFileTool;

public class FileTransduceService {
	//依照檔案的種類執行對應的邏輯
	public static String run(File f) throws IOException {
		String result  = "";
		String fn  = f.getPath();
		String fc = ReadFileTool.readFile(f);
		//區分種類
		FileTypeEnum type =  getType(fc);
		switch (type) {
		case EXPORT:
			result = ExportTransformer.run(fn,fc);
		break;
		case FASTLOAD:
			result = FastloadTransformer.run(fn,fc);
		break;
		case TRANSFORM:
			result = buildTransformFile(fn,fc);
			break;
		}
		return result;
	}
	//區分類型
	private static FileTypeEnum getType(String c) {
		String t = c.toUpperCase().replaceAll("\\s+", "");
		if(t.contains("OUTPUT_FILE")) {
			return FileTypeEnum.EXPORT;
		}else if(t.contains("BEGINLOADING")) {
			return FileTypeEnum.FASTLOAD;
		}else {
			return FileTypeEnum.TRANSFORM; 
		}
	}
	
	private static String buildTransformFile(String fn,String fc) {
		String result = "";
		System.out.println("buildTransactionFile");

		return result;
	}
}
