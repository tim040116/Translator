package src.java.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import src.java.model.BasicModel;

public class WriteFileService {
	public static void writeFile() throws IOException {
		List<File> lf = BasicModel.getListFile();
		String rootPath = BasicModel.getInputPath();
		String targetPath = BasicModel.getOutputPath();
		System.out.println("產檔開始...");
		System.out.println("原始目錄：" + rootPath);
		System.out.println("目標目錄：" + targetPath);
		for (File f : lf) {
			String filePath = f.getPath();
			System.out.println("讀取檔案：" + filePath);
			// 讀檔案
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			while (br.ready()) {
				sb.append(br.readLine()+"\r\n");
			}
			br.close();
			String content = sb.toString();
			//置換
			content = content.replaceAll("#","@");
			// 寫檔案
			String newFilePath = filePath.replace(rootPath, targetPath);
			System.out.println("產生檔案：" + newFilePath);
			File newFile = new File(newFilePath);
			newFile.getParentFile().mkdirs();
			newFile.createNewFile();
			FileWriter fw = new FileWriter(newFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			fw.flush();
			br.close();
			bw.close();
			fw.close();
		}
	}
}
