package etec.src.controller;

import java.io.File;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.model.BasicParams;
import etec.common.utils.FileTool;
import etec.main.Params;
import etec.src.interfaces.Controller;
import etec.src.service.CreateListService;
import etec.src.service.FamilyMartFileTransduceService;
import etec.src.service.IOpathSettingService;
import etec.view.panel.SearchFunctionPnl;
import src.java.element.WriteFileElement;

public class FamilyMartController implements Controller {

	public void run() throws Exception {

		// 儲存參數
		SearchFunctionPnl.clearLog();
		SearchFunctionPnl.setStatus(RunStatusEnum.WORKING);
		IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
		SearchFunctionPnl.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
		SearchFunctionPnl.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
		// 取得檔案清單
		List<File> lf = null;
		lf = FileTool.getFileList(BasicParams.getInputPath());
		BasicParams.setListFile(lf);
		SearchFunctionPnl.setLog("資訊", "取得檔案清單");
		int cntf = lf.size();
		int i = 0;
		for (File f : lf) {
			i++;
			// 讀檔案
			SearchFunctionPnl.setLog("資訊", "讀取檔案：" + f.getPath());
			// 寫入檔案清單
			CreateListService.createFileList(f);
			// 置換
			FamilyMartFileTransduceService.run(f);
			// 寫檔案
			SearchFunctionPnl.setLog("資訊", "產製檔案：" + BasicParams.getTargetFileNm(f.getPath()));
			SearchFunctionPnl.setProgressBar(i * 100 / cntf);
		}
		SearchFunctionPnl.setLog("資訊", "產生完成");
		SearchFunctionPnl.setStatus(RunStatusEnum.SUCCESS);
	}

}
