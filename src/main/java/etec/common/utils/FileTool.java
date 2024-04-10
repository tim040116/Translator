package etec.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import etec.common.utils.log.Log;

public class FileTool {
	
    public static final String UTF8_BOM = "\uFEFF";
	
	//取得檔案清單
	public static List<File> getFileList(String rootPath) throws IOException {
		List<File> lf = new ArrayList<>();
		// 取得標案的根目錄
		File rp = new File(rootPath);
		if (rp == null || !rp.isDirectory())
			throw new IOException("系統搜尋不到文件的根目錄"+rootPath);
		// 取得所有檔案資料
		return getInsideFileList(lf, rp);
	} 
	//讀取檔案
	public static String readFile(File f) throws IOException {
		//return readFile(f,detectCharset(f.getPath()));
		
		return readFile(f,Charset.defaultCharset());
	}
	public static String readFile(String fileName) throws IOException {
		//return readFile(f,detectCharset(f.getPath()));
		return readFile(new File(fileName),Charset.defaultCharset());
	}
	public static String readFile(File f,Charset encoding) throws IOException {
		Log.info("讀取檔案：" + f.getPath());
		String content;
		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;
		StringBuffer sb;
//		encoding = Charset.forName("BIG5");
		fis = new FileInputStream(f);
		isr = new InputStreamReader(fis,encoding);
		br = new BufferedReader(isr);
		sb = new StringBuffer();
		while (br.ready()) {
			String line = br.readLine();
			sb.append(line + "\r\n");
		}
		content = sb.toString().replace(UTF8_BOM,"");
		br.close();
		isr.close();
		fis.close();
		return content;
	}
	//產檔案
	public static boolean createFile(String filePath,String content) throws IOException {
		Log.info("產生檔案：" + filePath);
		File newFile = new File(filePath);
		newFile.getParentFile().mkdirs();
		if (!newFile.exists()) {
			newFile.createNewFile();
        }
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
	public static boolean createFile(String filePath,String content,Charset charset) throws IOException {
		Log.info("產生檔案：" + filePath);
		File newFile = new File(filePath);
		newFile.getParentFile().mkdirs();
		newFile.createNewFile();
		FileOutputStream writerStream = new FileOutputStream(filePath);    
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writerStream,charset)); 
		bw.write(content);
		bw.close();
		return true;
	}
	//產檔案
	public static boolean addFile(String file,String content) throws IOException {
		Log.info("新增資料到檔案：" + file);
		return addFile(file,Charset.forName("UTF-8"),content);
	}
	//產檔案
	public static boolean addFile(String file,Charset charset,String content) throws IOException {
		Log.info("新增資料到檔案：" + file);
		File newFile = new File(file);
		newFile.getParentFile().mkdirs();
		if (!newFile.exists()) {
			newFile.createNewFile();
        }
		try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(file), charset,StandardOpenOption.APPEND)){
			bw.write(content+"\r\n");
		}catch (Exception e) {
			System.out.println(content);
			e.printStackTrace();
			System.out.println();
		}
		return true;
	}
	// 取得子目錄下的檔案
	private static List<File> readInsideFileList(List<File> lf, File p) throws IOException {
		// 取得所有檔案資料
		File bidDoc[] = p.listFiles();
		for (int i = 0; i < bidDoc.length; i++) {
			String fileName = bidDoc[i].getName();
//			System.out.println("讀取檔案： " + fileName);
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
				lf = readInsideFileList(lf, bidDoc[i]);
			} else if (bidDoc[i].isFile()) {
				lf.add(bidDoc[i]);
			} else {
				throw new IOException("搜尋到的文件並非目錄或檔案，請稍後再試");
			}

		}
		return lf;
	}
	// 取得子目錄下的檔案
	private static List<File> getInsideFileList(List<File> lf, File p) throws IOException {
		// 取得所有檔案資料
		File bidDoc[] = p.listFiles();
		for (int i = 0; i < bidDoc.length; i++) {
			String fileName = bidDoc[i].getName();
//			System.out.println("讀取檔案： " + fileName);
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
//	/**
//	 * 判斷檔案的編碼
//	 * 
//	 * */
//	public static Charset detectCharset(String fileName) throws IOException {
//		File file = new File(fileName);
//
//        Charset charset = Charset.forName("UTF-8");
//        FileInputStream fis = new FileInputStream(file);
//        InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
//        BufferedReader br = new BufferedReader(isr);
//        int ch;
//        while ((ch = br.read()) != -1) {
//        	
//            if (Character.charCount(ch) > 1) {
//                int length = Character.charCount(ch);
//                charset = length==2?Charset.forName("BIG5"):Charset.forName("UTF-8");
//                System.out.println(fileName+"   "+charset);
//                break;
//            }
//        }
//        br.close();
//        return charset;
//    }
}
