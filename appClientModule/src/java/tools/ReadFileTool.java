package src.java.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFileTool {
	public static List<File> getFileList(String rootPath) throws IOException {
		List<File> lf = new ArrayList<>();
		// 取得標案的根目錄
		File rp = new File(rootPath);
		if (rp == null || !rp.isDirectory())
			throw new IOException("系統搜尋不到標案文件的根目錄");
		// 取得所有檔案資料
		return getInsideFileList(lf, rp);
	}
	private static List<File> getInsideFileList(List<File> lf,File p) throws IOException{
		// 取得所有檔案資料
		File bidDoc[] = p.listFiles();
		for (int i = 0; i < bidDoc.length; i++) {
			String fileName = bidDoc[i].getName();
			if (fileName.lastIndexOf(".meta") > 0) {
				continue;
			}else if (fileName.lastIndexOf(".nfs") > 0) {
				continue;
			}else if (".git".contentEquals(fileName)) {
				continue;
			}else if (".gitattributes".contentEquals(fileName)) {
				continue;
			}else if (fileName.equals("_structure")) {
				continue;
			}else if (bidDoc[i].isDirectory()) {
				lf = getInsideFileList(lf,bidDoc[i]);
			}else if (bidDoc[i].isFile()) {
				lf.add(bidDoc[i]);
			}else {
				throw new IOException("搜尋到的文件並非目錄或檔案，請稍後再試");
			}
				
		}
		return lf;
	}
}
