package etec.src.translator.common.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BasicParams {

	private static String inputPath;

	private static String outputPath;

	private static List<File> listFile;

	public static List<File> getListFile() {
		return listFile;
	}
	public static void setListFile(List<File> listFile) {
		BasicParams.listFile = listFile;
	}
	public static String getInputPath() {
		return inputPath;
	}
	public static void setInputPath(String inputPath) {
		String r = inputPath.trim();
		//
		if(!"\\".equals(r.substring(r.length() - 1))) {
			r=r+"\\";
		}
		BasicParams.inputPath = r;
	}
	public static String getOutputPath() {
		return outputPath;
	}
	public static void setOutputPath(String outputPath) {
		String r = outputPath.trim();
		//
		if(!"\\".equals(r.substring(r.length() - 1))) {
			r=r+"\\";
		}
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		BasicParams.outputPath = r+sdf.format(now)+"\\";
	}
	//取得out put檔案路徑
	public static String getTargetFileNm(String fileName) {
		String ip = getInputPath();
		String op = getOutputPath()+"src\\";
		return fileName.replace(ip, op);
	}

}
