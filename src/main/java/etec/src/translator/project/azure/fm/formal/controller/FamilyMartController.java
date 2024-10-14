package etec.src.translator.project.azure.fm.formal.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Controller;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.ui.search_func.enums.RunStatusEnum;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.common.service.IOpathSettingService;
import etec.src.translator.project.azure.ddim.service.CreateListService;
import etec.src.translator.project.azure.fm.poc.service.FamilyMartFileTransduceService;
import etec.src.translator.view.panel.SearchFunctionPnl;

public class FamilyMartController implements Controller {

	@Override
	public void run(Map<String,Object> args) throws Exception {

		// 儲存參數
		SearchFunctionPnl.reset();
		IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
		SearchFunctionPnl.tsLog.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
		SearchFunctionPnl.tsLog.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
		// 取得檔案清單
		List<File> lf = null;
		lf = FileTool.getFileList(BasicParams.getInputPath());
		BasicParams.setListFile(lf);
		SearchFunctionPnl.tsLog.setLog("資訊", "取得檔案清單");
		SearchFunctionPnl.progressBar.setUnit(lf.size());
		for (File f : lf) {
			// 讀檔案
			SearchFunctionPnl.tsLog.setLog("資訊", "讀取檔案：" + f.getPath());
			// 寫入檔案清單
			CreateListService.createFileList(f);
			// 置換
			FamilyMartFileTransduceService.run(f);
			// 寫檔案
			SearchFunctionPnl.tsLog.setLog("資訊", "產製檔案：" + BasicParams.getTargetFileNm(f.getPath()));
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "產生完成");
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
	}

}
