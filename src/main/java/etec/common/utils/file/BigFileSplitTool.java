package etec.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BigFileSplitTool {

	private static String tmpFilePath = "C:\\Assignment_Temp\\Temp_Data\\";

	private static String targetFilePath = "C:\\Assignment_Temp\\Target_Data\\";

	public static void splitFile(String rootPath) {
		try {
			// 讀
			go(rootPath);
			//包
			SimpleDateFormat sf = new SimpleDateFormat("YYYY_MM_DD_HHmmss");
			CompressTool.compressFolderToZip("C:\\Assignment_Temp\\Temp_Data\\","sample","C:\\Assignment_Temp\\sample_"+sf.format(new Date())+".zip");
			//清
			File delf = new File("C:\\Assignment_Temp\\Temp_Data");
			FileTool.deleteDirectory( Path.of("C:\\Assignment_Temp\\Temp_Data"));
			delf.delete();
			
			System.out.println("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dec(String rootPath) {
		try {

			for (File f : FileTool.getFileList(tmpFilePath)) {
				String content = "";
				// 名
				String fileName = f.getPath().replace(tmpFilePath, "");
				fileName = targetFilePath + fileName.replaceAll("@", "");
				File newFile = new File(fileName);
				newFile.getParentFile().mkdirs();
				if (newFile.exists()) {
					newFile.delete();
				}
				newFile.createNewFile();
				try (
			    		FileChannel sourceChannel = new FileInputStream(f).getChannel();
			    		FileChannel destChannel = new FileOutputStream(newFile).getChannel();
		    		){
			        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
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
