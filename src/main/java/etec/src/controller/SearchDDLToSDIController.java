package etec.src.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.enums.RunStatusEnum;
import etec.common.exception.TransduceException;
import etec.common.interfaces.Controller;
import etec.common.utils.FileTool;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;

//import etec.src.sql.azure.list.CreateListService;

import etec.src.sql.azure.service.CreateSDIService;
import etec.src.sql.az.service.FamilyMartFileTransduceService;
import etec.src.sql.az.service.IOpathSettingService;
import etec.src.sql.az.service.SearchFunctionService;
import etec.view.panel.SearchFunctionPnl;

public class SearchDDLToSDIController implements Controller{

	@Override
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

		// 讀取檔案
		SearchFunctionPnl.tsLog.setLog("資訊", "開始讀取檔案");
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(lf.size());
		
		int i = 0;
		// 讀取每一份檔案
		for (File f : lf) {
			SearchFunctionPnl.tsLog.setLog("資訊","讀取檔案：" + f.getPath());

			// 讀取檔案
			String content = FileTool.readFile(f,Charset.forName("utf-8"));

			String newFileName = BasicParams.getTargetFileNm(f.getPath())+f.getName();
			
			
			content = content.replaceAll("//.*",";$0;");
			//去除註解
			content=TransduceTool.cleanRemark(content);

			Pattern p = Pattern.compile("([^;]+);", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(content);
			StringBuffer sb = new StringBuffer();
			while(m.find()) {
				//跳過
				if(m.group().matches("\\s+;")) {
					m.appendReplacement(sb, m.group().replace(";","")+"\r\n");
					continue;
				}else if(m.group().matches("\\s*//[\\S\\s]+;")){
					Log.info("轉換項目：" + m.group().replaceAll("//|;", ""));
					m.appendReplacement(sb, m.group().replace(";","")+"\r\n");
					continue;
				}
				//處理前後空白
				String sql = m.group().trim();
				
				
				//確認為CREATE 語法
				//String regCol ="CREATE\\s+(?:MULTISET|SET)?\\s+TABLE[^;]+;";
				String regCol ="(?i)CREATE\\s*(MULTISET|SET)?(\\s+VOLATILE)?\\s+TABLE\\s+[\\S\\s]+;";
				Pattern pCol = Pattern.compile(regCol,Pattern.CASE_INSENSITIVE); //不用動
				Matcher mCol = pCol.matcher(sql); //參數放要處裡的字串
				while(mCol.find()) { 
					//排除 CTAS 語法
					if(!mCol.group(0).matches("[\\S\\s]*\\s+SELECT\\s+[\\S\\s]*;")){
						String res = CreateSDIService.createSD(f.getName(),mCol.group(0)); // 查到的CREATE 語法						
					}
				}
				//確認為CREATE 語法
				/* sql = sql.trim().replaceAll("^(\\S+)[\\S\\s]+","$1").toUpperCase();
				
			    if("CREATE".contains(sql)) {
				   //sql = ddl.easyReplace(sql);
				   //整理成SDI				   
				   String res = CreateSDIService.createSD(sql);
				   
			    }
			    */			
		   }		
		   i++;
	   }
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
		SearchFunctionPnl.tsLog.setLog("資訊","產生完成，共 "+i+" 個檔案");
		Log.info("完成，共 "+i+" 個檔案");

	}
}
