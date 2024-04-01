package etec.src.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.enums.RunStatusEnum;
import etec.common.interfaces.Controller;
import etec.common.utils.FileTool;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.service.IOpathSettingService;
import etec.src.sql.az.service.SearchFunctionService;
import etec.src.sql.azure.service.CreateSDIService;
import etec.view.panel.SearchFunctionPnl;

public class AssessmentController implements Controller{
	public static Map<String,Integer> mapFunc = new HashMap<String,Integer>();
	public static List<String> lstSkip = Arrays.asList(Params.searchFunction.SKIP_LIST);
	
	@Override
	public void run() throws Exception {
		
		/*
		 SearchDDLToSDIController.run(); 
		 SearchFunctionController.run(); 
		 */
		   
		// 儲存參數
		SearchFunctionPnl.tsLog.clearLog();
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.WORKING);
		/**
		 * 2023/08/25 Tim
		 * 應jason要求，取消輸入產出路徑的功能
		 * */
		//IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(), SearchFunctionPnl.tfOp.getText());
		IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
		SearchFunctionPnl.tsLog.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
		SearchFunctionPnl.tsLog.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
		// 取得檔案清單
		List<File> lf = null;
		lf = FileTool.getFileList(BasicParams.getInputPath());
		BasicParams.setListFile(lf);
		SearchFunctionPnl.tsLog.setLog("資訊", "取得檔案清單");
		
		// log 讀取檔案
		SearchFunctionPnl.tsLog.setLog("資訊", "開始讀取檔案");
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(lf.size());
		
		int i = 0;
		// 讀取每一份檔案
		for (File f : lf) {
			
			SearchFunctionPnl.tsLog.setLog("資訊","讀取檔案：" + f.getPath());
			
			// 讀取檔案
			String content = FileTool.readFile(f,Charset.forName("utf-8"));
			content= TransduceTool.cleanRemark(content);
			String category = "\\" + f.getPath()
			.replace(BasicParams.getInputPath(), "")
			.replace(f.getName(), "")
			.replaceAll("\\\\$", "")
			;
			
			/* Generate SDI file */
			SearchSDIList(content);
			
			/* Generate file List */
//			SearchFileList(f,content,category);
			
			/* Generate function List */
//			SearchFunctionList(f,content,category);
			
			
			i++;
			SearchFunctionPnl.progressBar.plusOne();
		}
		// 寫函式檔
		SearchFunctionPnl.tsLog.setLog("資訊","產生函式檔");
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(mapFunc.size());
		
		//建立清單檔
		String funcListNm = BasicParams.getOutputPath()+Params.searchFunction.FUNC_LIST_NAME;//列出所有方法
		if(Params.searchFunction.IS_TITLE) {
			FileTool.addFile(funcListNm,"\"FUNCTION_NAME\",\"USE_CNT\"");//方法名
		}

		for(Entry<String, Integer> entry : mapFunc.entrySet()) {
			FileTool.addFile(funcListNm,
					    "\""+entry.getKey()
					+"\",\"" + entry.getValue()
					+"\"");
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
		SearchFunctionPnl.tsLog.setLog("資訊","Function資訊產生完成，共 "+i+" 個檔案");
		Log.info("完成，共 "+i+" 個檔案");
	}
	
	/* Generate SDI file */
	public void SearchSDIList(String content) throws Exception {

		//String newFileName = BasicParams.getTargetFileNm(f.getPath())+f.getName();
		SearchFunctionPnl.tsLog.setLog("資訊","SearchSDIList" );
		
		content = content.replaceAll("//.*",";$0;");
				
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
				if(!mCol.group(0).matches("(?si).*\\bSELECT\\b.*;")){
					String res = CreateSDIService.createSD(mCol.group(0)); // 查到的CREATE 語法						
				}
			}
		}
	}
	
	
	
	/* Generate fileList */
	public void SearchFileList(File f,String content,String category) throws Exception {
		
		SearchFunctionPnl.tsLog.setLog("資訊","SearchFileList" );
		//建立清單檔
		String fileListNm = BasicParams.getOutputPath()+Params.searchFunction.FILE_LIST_NAME;//列出所有檔案
		//是否要包含title
		if(Params.searchFunction.IS_TITLE) {
			FileTool.addFile(fileListNm,"\"FILE_PATH\",\"FILE_NAME\",\"FILE_TYPE\",\"FILE_SIZE\"");//路徑,檔名,副檔名,大小			
		}	
		
		String[] arrFileType = f.getName().split("\\.");
		String fileType = arrFileType[arrFileType.length-1];
		
		
		//計算檔案行數
		String[] arrFileLine = content.split("\\r\\n");
		int fileLine = arrFileLine.length+1;
		
		
		FileTool.addFile(fileListNm,																
				  "\""   +category 
				+ "\",\""+f.getName()
				+ "\",\""+fileType
				+ "\",\""+fileLine  
				+ "\"");
	}
	
	/* Generate functionList */
	public void SearchFunctionList(File f,String content,String category) throws Exception {

		SearchFunctionPnl.tsLog.setLog("資訊","SearchFunctionList" );

		content = SearchFunctionService.getSqlContent(content);
		List<String> lstSql = RegexTool.getRegexTarget("SELECT[^;]*", content);
		//每一段sql
		int j = 1;
		String temp = "";
		//建立清單檔
		String detlListNm = BasicParams.getOutputPath()+Params.searchFunction.DETL_LIST_NAME;//列出所有檔案用到的方法
		if(Params.searchFunction.IS_TITLE) {
			FileTool.addFile(detlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"PART_ID\",\"FUNCTION_NAME\"");//路徑,檔名,段落,方法名
		}

		for(String sql : lstSql) {
			List<String> lfc = RegexTool.getRegexTarget("(?i)(QUALIFY +|AS +)?[A-Z0-9_\\$\\{\\}\\.]+\\s*\\(", sql);
			for(String func : lfc) {
				if(func==null) {
					continue;
				}
				func = func.replaceAll("\\(", "").trim();
				//例外處理
				if("RANK".equals(temp)) {//rank over
					temp = "";
					if("OVER".equals(func)) {
						continue;
					}
				}
				if(func.contains("RANK")) {//rank over
					temp = "RANK";
				}
				if("ROW_NUMBER".equals(temp)) {//ROW_NUMBER
					temp = "";
					if("OVER".equals(func)) {
						continue;
					}
				}
				if(func.contains("ROW_NUMBER")) {//ROW_NUMBER
					temp = "ROW_NUMBER";
				}
				if(func.contains("AS ")) {//CAST AS
					continue;
				}
				if(func.equals("ANY")) {//LIKE ANY
					func = "LIKE ANY";
				}
				if(func.matches("\\s*[0-9]+\\s*")) {//純數字是出現在強制轉換
					continue;
				}
				if(lstSkip.contains(func)) {
					continue;
				}
				//寫入總表
				FileTool.addFile(detlListNm,
						    "\"" + category
						+"\",\"" + f.getName()
						+"\",\"" + j
						+"\",\"" + func
						+"\"");
				//加到函式表
				mapFunc.put(func,mapFunc.get(func)==null?1:mapFunc.get(func)+1);
			}
			j++;
		}
	}
	
}
