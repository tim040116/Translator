package etec.framework.file.readfile.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BigFileSplitTool {

	private static String tmpFilePath = "C:\\Assignment_Temp\\Temp_Data\\";

	private static String targetFilePath = "C:\\Assignment_Temp\\Target_Data\\";

	public static void concatFile(String zipFile) throws IOException {
		//解
		System.out.println("第一階段");
		CompressTool.unZipFiles(new File(zipFile), tmpFilePath);
		//移
		System.out.println("第二階段");
		dec(tmpFilePath+"sample\\");
		//清
		System.out.println("第三階段");
		File delf = new File(tmpFilePath);
		FileTool.deleteFile(new File(tmpFilePath+"sample\\"));
		delf.delete();
	}

	public static void splitFile(String rootPath) {
		try {
			//移
			go(rootPath);
			//包
			SimpleDateFormat sf = new SimpleDateFormat("YYYY_MM_DD_HHmmss");
			CompressTool.compressFolderToZip("C:\\Assignment_Temp\\Temp_Data\\","sample","C:\\Assignment_Temp\\sample_"+sf.format(new Date())+".zip");
			//清
			File delf = new File("C:\\Assignment_Temp\\Temp_Data");
			FileTool.deleteFile(new File("C:\\Assignment_Temp\\Temp_Data"));
			delf.delete();

			System.out.println("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dec(String rootPath) throws IOException {
		List<File> lf = FileTool.getFileList(rootPath);
		for (File f : lf) {
			// 名
			String fileName = f.getPath().replace(rootPath, "");
			fileName = targetFilePath + fileName.replaceAll("@", "");

			File newFile = new File(fileName);
			newFile.getParentFile().mkdirs();
			if (newFile.exists()) {
				newFile.delete();
			}
			newFile.createNewFile();
		    try (
		    		InputStream is = new FileInputStream(f);
		    	    OutputStream os = new FileOutputStream(newFile);
	    		){
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }

		    }catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private static void go(String rootPath) throws IOException {
		List<File> lf = FileTool.getFileList(rootPath);
		for (File f : lf) {
			// 名
			System.out.println(f.getPath());
			String fileName = f.getPath().replace(rootPath, "");
//			fileName = tmpFilePath + fileName.replaceAll(".", strQ + "$0" + strE);
			fileName = tmpFilePath + fileName.replaceAll("(?<=.)", "@");

			File newFile = new File(fileName);
			newFile.getParentFile().mkdirs();
			if (newFile.exists()) {
				newFile.delete();
			}
			newFile.createNewFile();
		    try (
		    		InputStream is = new FileInputStream(f);
		    	    OutputStream os = new FileOutputStream(newFile);
//		    		FileChannel sourceChannel = new FileInputStream(f).getChannel();
//		    		FileChannel destChannel = new FileOutputStream(newFile).getChannel();
	    		){
//		        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }

		    }catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
