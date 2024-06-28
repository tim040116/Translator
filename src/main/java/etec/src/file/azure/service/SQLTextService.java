package etec.src.file.azure.service;

import java.io.IOException;

import etec.common.utils.file.FileTool;
import etec.src.file.model.BasicParams;

/*
 * 因此種類已經使用MS SQL語法，
 * 故不進行處理
 * */
public class SQLTextService {
	public static String run(String fn, String fc) throws IOException {
		System.out.println("SQLTextTransducer");
		String result = "Success";
		String sql = fc;
		CreateListService.createODBC(fn, sql);
		createSQLFile(fn, sql);
		// createLst(fn, fc);
		return result;
	}

	// SQL轉換產檔
	private static String createSQLFile(String fn, String fc) throws IOException {
		System.out.println("createSQLFile");
		String result = "Success";
		// 產檔
//		try {
//			File newFile = new File(BasicParams.getTargetFileNm(fn));
//			File oldFile = new File(fn);
//			newFile.getParentFile().mkdirs();
//			newFile.createNewFile();
//			//Files.copy(oldFile.toPath(), newFile.toPath());
//		} finally {
//		}
//		//確認檔案格式
//		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fn));
//		int p = (bin.read() << 8) + bin.read();
//		String code = null;
//		switch (p) {
//		case 0xefbb:
//			code = "utf-8";
//			break;
//		default:
//			break;
//		}
//		if(code==null) {
//			System.out.println(p);
//		}else {
//			System.out.println(code);
//		}
		String file = BasicParams.getTargetFileNm(fn);
		FileTool.createFile(file, fc);
		return result;
	}

}
