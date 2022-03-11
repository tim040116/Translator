package src.java.model;

import java.io.File;
import java.util.List;

public class IOPathModel {
	private static String inputPath;
	private static String outputPath;
	private static List<File> listFile;
	
	public static List<File> getListFile() {
		return listFile;
	}
	public static void setListFile(List<File> listFile) {
		IOPathModel.listFile = listFile;
	}
	public static String getInputPath() {
		return inputPath;
	}
	public static void setInputPath(String inputPath) {
		IOPathModel.inputPath = inputPath;
	}
	public static String getOutputPath() {
		return outputPath;
	}
	public static void setOutputPath(String outputPath) {
		IOPathModel.outputPath = outputPath;
	}
	
}
