package etec.src.translator.project.azure.fm.poc.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.factory.Params;
import etec.common.interfaces.Controller;
import etec.framework.context.translater.exception.UnknowSQLTypeException;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.common.model.SFSPModel;
import etec.src.translator.common.service.IOpathSettingService;
import etec.src.translator.project.azure.fm.poc.service.TransduceStoreFunctionService;
import etec.src.translator.view.panel.SearchFunctionPnl;

/**
 * @author	Tim
 * @since	2023年10月13日
 * @version	3.3.1.0
 * 
 * 	- 只有一個檔案以雙引號包住sql,沒有分隔符號
 * 	- 只轉換 RETURN 到  ; 中間的語法,其餘直接搬
 * 	- 註解全清掉
 * 	- 每隻sf產一個檔,檔名為function name
 * */
public class TranslateStoreFunctionController implements Controller {

	public void run() throws Exception {

		// 儲存參數
		SearchFunctionPnl.tsLog.clearLog();
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.WORKING);
		IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
		SearchFunctionPnl.tsLog.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
		SearchFunctionPnl.tsLog.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
		SearchFunctionPnl.tsLog.setLog("資訊", "取得檔案清單");
		// 置換
		runSF();
		runSP();
		SearchFunctionPnl.tsLog.setLog("資訊", "產生完成");
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
	}
	/**
	 * @author	Tim
	 * @since	2023年10月23日
	 * store function處理
	 * */
	public void runSF() throws IOException, UnknowSQLTypeException {
		SearchFunctionPnl.tsLog.setLog("資訊", "轉換Store Function");
		List<File> lf = null;
//		Map<String, String> mapSF = new HashMap<String, String>();
		List<SFSPModel> lstm = new ArrayList<SFSPModel>();
		//取得檔案內容
		lf = FileTool.getFileList(BasicParams.getInputPath()+"sf\\");
		//轉換
		for(File f : lf) {
			String content = FileTool.readFile(f);
			String[] arrSF = content.toUpperCase().split("\"\\s*\"");
			SearchFunctionPnl.progressBar.reset();
			SearchFunctionPnl.progressBar.setUnit(arrSF.length);
			for(String sql : arrSF) {
				lstm.add(TransduceStoreFunctionService.transformSF(sql));
				SearchFunctionPnl.progressBar.plusOne();
			}
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "轉換完成，開始產檔...");
		//產檔
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(lstm.size());
		for (SFSPModel m : lstm) {
			FileTool.createFile(BasicParams.getOutputPath()+"sf\\"+m.getName()+".sql",m.getScript());
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "產檔完成，共 "+SearchFunctionPnl.progressBar.getProgress()+" 個檔案");

	}
	/**
	 * @author	Tim
	 * @since	2023年10月23日
	 * store function處理
	 * */
	public void runSP() throws IOException, UnknowSQLTypeException {
		SearchFunctionPnl.tsLog.setLog("資訊", "轉換Store Porsidure");
		List<File> lf = null;
//		Map<String, String> mapSF = new HashMap<String, String>();
		List<SFSPModel> lstm = new ArrayList<SFSPModel>();
		//取得檔案內容
		lf = FileTool.getFileList(BasicParams.getInputPath()+"sp\\");
		SearchFunctionPnl.progressBar.setUnit(lf.size());
		SearchFunctionPnl.progressBar.reset();
		//轉換
		for(File f : lf) {
			lstm.add(TransduceStoreFunctionService.transformSP(f));
//			mapSF.putAll(TranslateStoreFunctionService.transformSP(f));
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "轉換完成，開始產檔...");
		SearchFunctionPnl.progressBar.setUnit(lstm.size());
		SearchFunctionPnl.progressBar.reset();
		//產檔
		for (SFSPModel m : lstm) {
			FileTool.createFile(BasicParams.getOutputPath()+"sp\\"+m.getName()+".sql",m.getScript());
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.tsLog.setLog("資訊", "產檔完成，共 "+SearchFunctionPnl.progressBar.getProgress()+" 個檔案");
		Log.info("轉換完成");
	}
}
