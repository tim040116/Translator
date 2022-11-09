package src.java.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ReadFileTool {
	public static List<File> getFileList(String rootPath) throws IOException {
		List<File> lf = new ArrayList<>();
		// 取得標案的根目錄
		File rp = new File(rootPath);
		if (rp == null || !rp.isDirectory())
			throw new IOException("系統搜尋不到文件的根目錄");
		// 取得所有檔案資料
		return getInsideFileList(lf, rp);
	}
	//讀取檔案
	public static String readFile(File f) throws IOException {
		System.out.println("讀取檔案：" + f.getPath());
		String content;
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		StringBuffer sb;
		fis = new FileInputStream(f);
		isr = new InputStreamReader(fis
				);
		br = new BufferedReader(isr);
		sb = new StringBuffer();
		while (br.ready()) {
			String line = br.readLine();
			sb.append(line + "\r\n");
		}
		content = sb.toString();
		br.close();
		isr.close();
		fis.close();
		return content;
	}
	//產檔案
	public static boolean createFile(String filePath,String content) throws IOException {
		System.out.println("產生檔案：" + filePath);
		File newFile = new File(filePath);
		newFile.getParentFile().mkdirs();
		newFile.createNewFile();
//		FileWriter fw = new FileWriter(newFile);
//		BufferedWriter bw = new BufferedWriter(fw);
		FileOutputStream writerStream = new FileOutputStream(filePath);    
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8")); 
		bw.write(content);
//		fw.flush();
		bw.close();
//		fw.close();
		return true;
	}
	//產檔案
	public static boolean addFile(String file,String content) throws IOException {
		System.out.println("新增資料到檔案：" + file);
		File newFile = new File(file);
		newFile.getParentFile().mkdirs();
		if (!newFile.exists()) {
			newFile.createNewFile();
        }
		FileWriter fw = new FileWriter(newFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content+"\r\n");
		fw.flush();
		bw.close();
		fw.close();
		return true;
	}
	// 取得子目錄下的檔案
	private static List<File> getInsideFileList(List<File> lf, File p) throws IOException {
		// 取得所有檔案資料
		File bidDoc[] = p.listFiles();
		for (int i = 0; i < bidDoc.length; i++) {
			String fileName = bidDoc[i].getName();
			System.out.println("讀取檔案： " + fileName);
			if (fileName.lastIndexOf(".meta") > 0) {
				continue;
			} else if (fileName.lastIndexOf(".nfs") > 0) {
				continue;
			} else if (fileName.contentEquals(".git")) {
				continue;
			} else if (fileName.contentEquals(".gitattributes")) {
				continue;
			} else if (fileName.equals("_structure")) {
				continue;
			} else if (bidDoc[i].isDirectory()) {
				lf = getInsideFileList(lf, bidDoc[i]);
			} else if (bidDoc[i].isFile()) {
				lf.add(bidDoc[i]);
			} else {
				throw new IOException("搜尋到的文件並非目錄或檔案，請稍後再試");
			}

		}
		return lf;
	}
}
