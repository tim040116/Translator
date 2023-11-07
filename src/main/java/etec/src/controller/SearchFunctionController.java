package etec.src.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import etec.common.enums.RunStatusEnum;
import etec.common.model.BasicParams;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import etec.main.Params;
import etec.src.interfaces.Controller;
import etec.src.service.CreateListService;
import etec.src.service.FamilyMartFileTransduceService;
import etec.src.service.IOpathSettingService;
import etec.src.service.SearchFunctionService;
import etec.view.panel.SearchFunctionPnl;

public class SearchFunctionController implements Controller{

	@Override
	public void run() throws Exception {
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
		//建立清單檔
		String fileListNm = BasicParams.getOutputPath()+Params.searchFunction.FILE_LIST_NAME;//列出所有檔案
		String funcListNm = BasicParams.getOutputPath()+Params.searchFunction.FUNC_LIST_NAME;//列出所有方法
		String detlListNm = BasicParams.getOutputPath()+Params.searchFunction.DETL_LIST_NAME;//列出所有檔案用到的方法
		String ddlListNm  = BasicParams.getOutputPath()+Params.searchFunction.DDL_LIST_NAME;//DDL用到的table
		if(Params.searchFunction.IS_TITLE) {
			FileTool.addFile(fileListNm,"\"FILE_PATH\",\"FILE_NAME\",\"FILE_TYPE\",\"FILE_SIZE\"");//路徑,檔名,副檔名,大小
			FileTool.addFile(funcListNm,"\"FUNCTION_NAME\",\"USE_CNT\"");//方法名
			FileTool.addFile(detlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"PART_ID\",\"FUNCTION_NAME\"");//路徑,檔名,段落,方法名
			FileTool.addFile(ddlListNm ,"\"FILE_PATH\",\"FILE_NAME\",\"TYPE\",\"TABLE_NAME\"");//路徑,檔名,類型,資料表名
		}
		Map<String,Integer> mapFunc = new HashMap<String,Integer>();
		List<String> lstSkip = Arrays.asList(Params.searchFunction.SKIP_LIST);
		// 讀取檔案
		SearchFunctionPnl.tsLog.setLog("資訊", "開始讀取檔案");
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(lf.size());
		int i = 0;
		// 讀取每一份檔案
		for (File f : lf) {
			SearchFunctionPnl.tsLog.setLog("資訊","讀取檔案：" + f.getPath());
			// 寫入檔案清單
			String category = "\\" + f.getPath()
					.replace(BasicParams.getInputPath(), "")
					.replace(f.getName(), "")
					.replaceAll("\\\\$", "")
					;
			String[] arrFileType = f.getName().split("\\.");
			String fileType = arrFileType[arrFileType.length-1];
			FileTool.addFile(fileListNm,																
					  "\""   +category 
					+ "\",\""+f.getName()
					+ "\",\""+fileType
					+ "\",\""+f.length()
					+ "\"");
			// 讀取檔案
			String content = FileTool.readFile(f,Charset.forName("utf-8"));
			content = SearchFunctionService.getSqlContent(content);
			
			List<String> lstFile = new ArrayList<String>();
			
			//SD
			if("SD_MAKER".equals(Params.config.APPLICATION_TYPE)) {
				//src轉換
				String newContent = content.replaceAll("\"[^\"]+\"\\s+", "");
				newContent = FamilyMartFileTransduceService.transduceSQLScript(newContent);
				String srcFileName = RegexTool.getRegexTargetFirst("\\bP\\w+\\.\\w+",RegexTool.getRegexTargetFirst("^.*",newContent));
				if(lstFile.contains(srcFileName)) {
					Log.error(srcFileName);
				} else {
					lstFile.add(srcFileName);
				}
				String srcFilePath = BasicParams.getOutputPath()+"\\src\\"+srcFileName+".sql";
				FileTool.addFile(srcFilePath, newContent);
				//SD List
				String tp1 = CreateListService.createSD(content);
				if("Success".equals(tp1)) {
					i++;
					SearchFunctionPnl.progressBar.plusOne();
					continue;
				}
			}
			//轉換語法
			
			//搜尋DDL
			Map<String,String> mapDDL = new HashMap<String,String>();
			mapDDL.put("CREATE", "CREATE\\s+TABLE");
			mapDDL.put("DROP", "DROP\\s+TABLE");
			mapDDL.put("INSERT", "INSERT\\s+INTO");
			mapDDL.put("UPDATE", "UPDATE");
			mapDDL.put("DELETE", "DELETE\\s+FROM");
			mapDDL.put("MERGE", "MERGE\\s+INTO");
			SearchFunctionPnl.tsLog.setL("資訊", "搜尋項目: ");
			for (Entry<String, String> entry : mapDDL.entrySet()) {
				SearchFunctionPnl.tsLog.setOg(" >"+entry.getKey());
				List<String> lstMergeInto = RegexTool.getRegexTarget("(^|\\s)"+entry.getValue()+"\\s+\\S+", content);
				for(String tblNm : lstMergeInto) {
					tblNm = tblNm.trim().replaceAll(entry.getValue()+"\\s+", "");
					if("SET".equals(tblNm)) {
						continue;
					}
					FileTool.addFile(ddlListNm,
						    "\"" + category
						+"\",\"" + f.getName()
						+"\",\"" + entry.getKey()
						+"\",\"" + tblNm
						+"\"");
				}
			}
			SearchFunctionPnl.tsLog.setOg("\r\n");
			//搜尋DQL
			List<String> lstDQLTable = SearchFunctionService.searchDQL(content);
			SearchFunctionPnl.tsLog.setLog("資訊", "建立清單");
			for(String tblNm : lstDQLTable) {
				FileTool.addFile(ddlListNm,																
						  "\""   +category 
						+ "\",\""+f.getName()
						+ "\",\""+"SELECT"
						+ "\",\""+tblNm
						+ "\"");
			}
			//搜尋function
			List<String> lstSql = RegexTool.getRegexTarget("SELECT[^;]*", content);
//			String[] arrSql = content.split(";");
			//每一段sql
			int j = 1;
			String temp = "";
			
//			//SP name
//			List<String> sp;
//			sp = RegexTool.getRegexTarget("REPLACE\\s+PROCEDURE\\s+\\S+", content);
//			if(sp.isEmpty()) {
//				sp = RegexTool.getRegexTarget("CREATE\\s+PROCEDURE\\s+\\S+", content);
//			}
//			String spn = sp.get(0).replaceAll("\\S+\\s+PROCEDURE\\s+", "");
			
			for(String sql : lstSql) {
				List<String> lfc = RegexTool.getRegexTarget2("(QUALIFY +|AS +)?[A-Z0-9_\\$\\{\\}\\.]+\\s*\\(", sql);
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
//					RegexTool.
					//寫入總表
					FileTool.addFile(detlListNm,
							    "\"" + category
							+"\",\"" + f.getName()
//							+"\",\"" + spn
							+"\",\"" + j
							+"\",\"" + func
							+"\"");
					//加到函式表
					mapFunc.put(func,mapFunc.get(func)==null?1:mapFunc.get(func)+1);
				}
				j++;
			}
			i++;
			SearchFunctionPnl.progressBar.plusOne();
		}
		// 寫函式檔
		SearchFunctionPnl.tsLog.setLog("資訊","產生函式檔");
//		SearchFunctionPnl.setProgressBar(0);
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(mapFunc.size());
		for(Entry<String, Integer> entry : mapFunc.entrySet()) {
			FileTool.addFile(funcListNm,
					    "\""+entry.getKey()
					+"\",\"" + entry.getValue()
					+"\"");
			SearchFunctionPnl.progressBar.plusOne();
		}
		SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.SUCCESS);
		SearchFunctionPnl.tsLog.setLog("資訊","產生完成，共 "+i+" 個檔案");
		Log.info("完成，共 "+i+" 個檔案");
	}

}
