package etec.src.file.gp_test.controller;

import java.io.File;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.interfaces.Controller;
import etec.common.utils.file.FileTool;
import etec.common.utils.param.Params;
import etec.src.file.gp_test.service.GreenPlumFileService;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.service.CreateListService;
import etec.src.sql.az.service.IOpathSettingService;
import etec.view.panel.SearchFunctionPnl;

/**
 * <h1>測試GreenPlum轉換</h1>
 * <p></p>
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年3月4日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		GreenPlumFileService
 */
public class GreenPlumFileController implements Controller {

	public void run() throws Exception {

		// 儲存參數
		SearchFunctionPnl.tsLog.clearLog();
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.WORKING);
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
			// 置換
			GreenPlumFileService.run(f);
			// 寫檔案
			SearchFunctionPnl.tsLog.setLog("資訊", "產製檔案：" + BasicParams.getTargetFileNm(f.getPath()));
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "產生完成");
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
	}

}
