package etec.src.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.model.BasicParams;
import etec.src.interfaces.Controller;
import etec.src.service.DDIMFileTransduceService;
import src.java.element.WriteFileElement;

public class DDIMWriteFileController implements Controller {
	public void run() throws IOException {
		List<File> lf = BasicParams.getListFile();
		String rootPath = BasicParams.getInputPath();
		String targetPath = BasicParams.getOutputPath();
		WriteFileElement.setLog("產檔開始...");
		WriteFileElement.setStatus(RunStatusEnum.WORKING);
		WriteFileElement.setLog("原始目錄：" + rootPath);
		WriteFileElement.setLog("目標目錄：" + targetPath);
		int cntf = lf.size();
		int i = 0;
		for (File f : lf) {
			i++;
			// 讀檔案
			WriteFileElement.setLog("讀取檔案：" + f.getPath());
			// 置換
			DDIMFileTransduceService.run(f);
			
			// 寫檔案
			WriteFileElement.setLog("產製檔案：" + BasicParams.getTargetFileNm(f.getPath()));
			WriteFileElement.setProgressBar(i * 100 / cntf);
		}
		WriteFileElement.setLog("產生完成");
		WriteFileElement.setStatus(RunStatusEnum.SUCCESS);
	}
}
