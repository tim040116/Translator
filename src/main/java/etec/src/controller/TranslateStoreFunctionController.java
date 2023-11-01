package etec.src.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.exception.UnknowSQLTypeException;
import etec.common.model.BasicParams;
import etec.common.model.SFSPModel;
import etec.common.utils.FileTool;
import etec.main.Params;
import etec.src.interfaces.Controller;
import etec.src.service.IOpathSettingService;
import etec.src.service.TranslateStoreFunctionService;
import etec.view.panel.SearchFunctionPnl;

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
		SearchFunctionPnl.clearLog();
		SearchFunctionPnl.setStatus(RunStatusEnum.WORKING);
		IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
		SearchFunctionPnl.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
		SearchFunctionPnl.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
		SearchFunctionPnl.setLog("資訊", "取得檔案清單");
		// 置換
		runSF();
		runSP();
		SearchFunctionPnl.setLog("資訊", "產生完成");
		SearchFunctionPnl.setStatus(RunStatusEnum.SUCCESS);
	}
	/**
	 * @author	Tim
	 * @since	2023年10月23日
	 * store function處理
	 * */
	public void runSF() throws IOException, UnknowSQLTypeException {
		SearchFunctionPnl.setLog("資訊", "轉換Store Function");
		List<File> lf = null;
//		Map<String, String> mapSF = new HashMap<String, String>();
		List<SFSPModel> lstm = new ArrayList<SFSPModel>();
		//取得檔案內容
		lf = FileTool.getFileList(BasicParams.getInputPath()+"sf\\");
		//轉換
		for(File f : lf) {
			String content = FileTool.readFile(f);
			String[] arrSF = content.toUpperCase().split("\"\\s*\"");
			int cntf = arrSF.length;
			int i = 0;
			SearchFunctionPnl.setProgressBar(0);
			for(String sql : arrSF) {
				i++;
				lstm.add(TranslateStoreFunctionService.transformSF(sql));
				SearchFunctionPnl.setProgressBar(i * 100 / cntf);
			}
		}
		SearchFunctionPnl.setLog("資訊", "轉換完成，開始產檔...");
		//產檔
		int cntf2 = lstm.size();
		int i2 = 0;
		for (SFSPModel m : lstm) {
			i2++;
			FileTool.createFile(BasicParams.getOutputPath()+"sf\\"+m.getName()+".sql",m.getScript());
			SearchFunctionPnl.setProgressBar(i2 * 100 / cntf2);
			}
		SearchFunctionPnl.setLog("資訊", "產檔完成，共 "+i2+" 個檔案");

	}
	/**
	 * @author	Tim
	 * @since	2023年10月23日
	 * store function處理
	 * */
	public void runSP() throws IOException, UnknowSQLTypeException {
		SearchFunctionPnl.setLog("資訊", "轉換Store Porsidure");
		List<File> lf = null;
//		Map<String, String> mapSF = new HashMap<String, String>();
		List<SFSPModel> lstm = new ArrayList<SFSPModel>();
		//取得檔案內容
		lf = FileTool.getFileList(BasicParams.getInputPath()+"sp\\");
		int cntf = lf.size();
		int i = 0;
		SearchFunctionPnl.setProgressBar(0);
		//轉換
		for(File f : lf) {
			i++;
			lstm.add(TranslateStoreFunctionService.transformSP(f));
//			mapSF.putAll(TranslateStoreFunctionService.transformSP(f));
			SearchFunctionPnl.setProgressBar(i * 100 / cntf);
		}
		SearchFunctionPnl.setLog("資訊", "轉換完成，開始產檔...");
		cntf = lstm.size();
		i = 0;
		//產檔
		for (SFSPModel m : lstm) {
			i++;
			FileTool.createFile(BasicParams.getOutputPath()+"sp\\"+m.getName()+".sql",m.getScript());
			SearchFunctionPnl.setProgressBar(i * 100 / cntf);
		}
		SearchFunctionPnl.setLog("資訊", "產檔完成，共 "+i+" 個檔案");
	}
}
