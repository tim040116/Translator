package etec.common.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Base64;

public class BigFileSplitTool {

	private static String tmpFilePath = "C:\\Assignment_Temp\\Temp_Data\\";
	
	private static String targetFilePath = "C:\\Assignment_Temp\\Target_Data\\";
	
	private static String strQ = "D12E";
	
	private static String strE = "QExA";
	
	public static void splitFile(String rootPath) {
		try {
			for (File f : FileTool.getFileList(rootPath)) {
				// 名
				String content = "";
				String fileName = f.getPath().replace(rootPath, "");
				fileName = tmpFilePath + fileName.replaceAll(".", strQ+"$0"+strE);
				// 讀
				try (FileInputStream fis = new FileInputStream(f);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr);) {
					StringBuffer sb;
					sb = new StringBuffer();
					while (br.ready()) {
						String line = br.readLine();
						sb.append(line + "\r\n");
					}
					content.hashCode();
					content = sb.toString();
				}
				// 編
				Base64.Encoder encoder = Base64.getEncoder();
				content = encoder.encodeToString(content.getBytes("UTF-8"));
				// 寫
				File newFile = new File(fileName);
				newFile.getParentFile().mkdirs();
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				try (FileOutputStream writerStream = new FileOutputStream(fileName);
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writerStream));) {
					
					newFile.getParentFile().mkdirs();
					newFile.createNewFile();
					bw.write(content);
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
				String fileName = f.getPath().replace(rootPath, "");
				fileName = targetFilePath + fileName.replaceAll(strQ+"(.)"+strE, "$1");
				// 讀
				try (FileInputStream fis = new FileInputStream(f);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr);) {
					StringBuffer sb;
					sb = new StringBuffer();
					while (br.ready()) {
						String line = br.readLine();
						sb.append(line + "\r\n");
					}
					content.hashCode();
					content = sb.toString();
				}
				// 編
				Base64.Decoder decoder = Base64.getDecoder();
				content = new String(decoder.decode(content));
				// 寫
				try (FileOutputStream writerStream = new FileOutputStream(fileName);
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writerStream));) {
					File newFile = new File(fileName);
					newFile.getParentFile().mkdirs();
					if (!newFile.exists()) {
						newFile.createNewFile();
					}
					newFile.getParentFile().mkdirs();
					newFile.createNewFile();
					bw.write(content);
				}
			}
		} catch (Exception e) {

		}
	}
}
