package etec.src.translator.project.azure.ddim.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import etec.common.model.element.WriteFileElement;
import etec.framework.code.interfaces.Controller;
import etec.framework.ui.search_func.enums.RunStatusEnum;
import etec.src.translator.common.model.BasicParams;

public class DDIMWriteFileController implements Controller {
	@Override
	public void run(Map<String,Object> args) throws IOException {
		List<File> lf = BasicParams.getListFile();
		String rootPath = BasicParams.getInputPath();
		String targetPath = BasicParams.getOutputPath();
		WriteFileElement.tsLog.setLog("INFO","產檔開始...");
		WriteFileElement.lblStatus.setStatus(RunStatusEnum.WORKING);
		WriteFileElement.tsLog.setLog("INFO","原始目錄：" + rootPath);
		WriteFileElement.tsLog.setLog("INFO","目標目錄：" + targetPath);
		WriteFileElement.progressBar.setUnit(lf.size());
		WriteFileElement.progressBar.reset();
		for (File f : lf) {
			// 讀檔案
			WriteFileElement.tsLog.setLog("INFO","讀取檔案：" + f.getPath());
			// 置換
			DDIMFileTransduceService.run(f);
			// 寫檔案
			WriteFileElement.tsLog.setLog("INFO","產製檔案：" + BasicParams.getTargetFileNm(f.getPath()));
			WriteFileElement.progressBar.plusOne();
		}
		WriteFileElement.tsLog.setLog("INFO","產生完成");
		WriteFileElement.lblStatus.setStatus(RunStatusEnum.SUCCESS);
	}

	@Override
	public Map<String, Object> getArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
