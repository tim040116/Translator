package etec.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class BigFileSplitTool {

	private static String tmpFilePath = "C:\\Assignment_Temp\\Temp_Data\\";

	private static String targetFilePath = "C:\\Assignment_Temp\\Target_Data\\";

	private static String strQ = "D12E";

	private static String strE = "QExA";

	public static void splitFile(String rootPath) {
		// 讀
		try {
			for (File f : FileTool.getFileList(rootPath)) {
				// 名
				String content = "";
				String fileName = f.getPath().replace(rootPath, "");
				fileName = tmpFilePath + fileName.replaceAll(".", strQ + "$0" + strE);
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

	public static void dec(String rootPath) {
		try {

			for (File f : FileTool.getFileList(tmpFilePath)) {
				String content = "";
				// 名
				String fileName = f.getPath().replace(tmpFilePath, "");
				fileName = targetFilePath + fileName.replaceAll(strQ + "(.)" + strE, "$1");
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

}
