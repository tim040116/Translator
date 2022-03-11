package src.java.model;

import java.io.File;
import java.util.List;

public class BasicModel {
	private static String inputPath;
	private static String outputPath;
	private static List<File> listFile;
	
	public static List<File> getListFile() {
		return listFile;
	}
	public static void setListFile(List<File> listFile) {
		BasicModel.listFile = listFile;
	}
	public static String getInputPath() {
		return inputPath;
	}
	public static void setInputPath(String inputPath) {
		BasicModel.inputPath = inputPath;
	}
	public static String getOutputPath() {
		return outputPath;
	}
	public static void setOutputPath(String outputPath) {
		BasicModel.outputPath = outputPath;
	}
	
}
