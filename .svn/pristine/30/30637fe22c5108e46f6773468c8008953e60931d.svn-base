package src.java.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import src.java.element.WriteFileElement;
import src.java.model.BasicModel;

public class WriteFileService {
	public static void writeFile() throws IOException {
		List<File> lf = BasicModel.getListFile();
		String rootPath = BasicModel.getInputPath();
		String targetPath = BasicModel.getOutputPath();
		WriteFileElement.setLog("產檔開始...");
		WriteFileElement.setLog("原始目錄：" + rootPath);
		WriteFileElement.setLog("目標目錄：" + targetPath);
		int cntf = lf.size();
		int i=0;
		for (File f : lf) {
			i++;
			// 讀檔案
			FileTransduceService.run(f);
			//置換
			// 寫檔案
			WriteFileElement.setLog("產製檔案：" + f.getPath());
			WriteFileElement.setProgressBar(i*100/cntf);
		}
		WriteFileElement.setLog("產生完成");
	}
}
