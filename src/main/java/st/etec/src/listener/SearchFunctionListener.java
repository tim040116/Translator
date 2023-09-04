package st.etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import etec.common.enums.RunStatusEnum;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import src.java.params.BasicParams;
import st.etec.src.params.Params;
import st.etec.src.service.IOpathSettingService;
import st.etec.src.service.SearchFunctionService;
import st.etec.view.panel.SearchFunctionPnl;

public class SearchFunctionListener implements ActionListener {

	/*
	 * 整個流程
	 * 
	 */
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				try {
					// 儲存參數
					SearchFunctionPnl.clearLog();
					SearchFunctionPnl.setStatus(RunStatusEnum.WORKING);
					/**
					 * 2023/08/25 Tim
					 * 應jason要求，取消輸入產出路徑的功能
					 * */
					//IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(), SearchFunctionPnl.tfOp.getText());
					IOpathSettingService.setPath(SearchFunctionPnl.tfIp.getText(),Params.config.INIT_OUTPUT_PATH);
					SearchFunctionPnl.setLog("資訊", "取得資料目錄 : " + BasicParams.getInputPath());
					SearchFunctionPnl.setLog("資訊", "取得產檔目錄 : " + BasicParams.getOutputPath());
					// 取得檔案清單
					List<File> lf = null;
					lf = FileTool.getFileList(BasicParams.getInputPath());
					BasicParams.setListFile(lf);
					SearchFunctionPnl.setLog("資訊", "取得檔案清單");
					//建立清單檔
					String fileListNm = BasicParams.getOutputPath()+"list\\file_list.csv";//列出所有檔案
					String funcListNm = BasicParams.getOutputPath()+"list\\func_list.csv";//列出所有方法
					String detlListNm = BasicParams.getOutputPath()+"list\\detl_list.csv";//列出所有檔案用到的方法
					String ddlListNm = BasicParams.getOutputPath()+"list\\ddl_list.csv";//DDL用到的table
					String dqlListNm = BasicParams.getOutputPath()+"list\\dql_list.csv";//DQL用到的table
					FileTool.addFile(fileListNm,"\"FILE_PATH\",\"FILE_NAME\",\"FILE_SIZE\"");//路徑,檔名,大小
					FileTool.addFile(funcListNm,"\"FUNCTION_NAME\"");//方法名
					FileTool.addFile(detlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"PART_ID\",\"FUNCTION_NAME\"");//路徑,檔名,段落,方法名
					FileTool.addFile(ddlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"TYPE\",\"TABLE_NAME\"");//路徑,檔名,類型,資料表名
					FileTool.addFile(ddlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"TABLE_NAME\"");//路徑,檔名,資料表名
					List<String> lstFunc = new ArrayList<String>();
					List<String> lstSkip = Arrays.asList(Params.searchFunction.SKIP_LIST);
					List<String> lstOver = Arrays.asList(Params.searchFunction.OVER_LIST);
					// 讀取檔案
					SearchFunctionPnl.setLog("資訊", "開始讀取檔案");
					int cntf = lf.size();
					int i = 0;
					// 讀取每一份檔案
					for (File f : lf) {
						SearchFunctionPnl.setLog("資訊","讀取檔案：" + f.getPath());
						// 寫入檔案清單
						String category = "\\" + f.getPath()
								.replace(BasicParams.getInputPath(), "")
								.replace(f.getName(), "")
								.replaceAll("\\\\$", "")
								;
						FileTool.addFile(fileListNm,																
								  "\""   +category 
								+ "\",\""+f.getName()
								+ "\",\""+f.length()
								+ "\"");
						// 讀取檔案
						String content = FileTool.readFile(f,Charset.forName("utf-8"));
						content = getSqlContent(content);
						//搜尋DDL
						Map<String,String> mapDDL = new HashMap<String,String>();
						mapDDL.put("CREATE", "CREATE\\s+TABLE");
						mapDDL.put("DROP", "DROP\\s+TABLE");
						mapDDL.put("INSERT", "INSERT\\s+INTO");
						mapDDL.put("UPDATE", "UPDATE");
						mapDDL.put("DELETE", "DELETE\\s+FROM");
						mapDDL.put("MERGE", "MERGE\\s+INTO");
						SearchFunctionPnl.setL("資訊", "搜尋項目: ");
						for (Entry<String, String> entry : mapDDL.entrySet()) {
							SearchFunctionPnl.setLo(" >"+entry.getKey());
							List<String> lstMergeInto = RegexTool.getRegexTarget(entry.getValue()+"\\s+\\S+", content);
							for(String tblNm : lstMergeInto) {
								tblNm = tblNm.replaceAll(entry.getValue()+"\\s+", "");
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
						SearchFunctionPnl.setLo("\r\n");
						//搜尋DQL
						SearchFunctionPnl.setLog("資訊", "整理查詢使用的資料表");
						List<String> lstDQLTable = new ArrayList<String>();
						String dqlcontent = content
								.replaceAll("DELETE\\s+FROM", "")
								.replaceAll("\\(", " \\( ")
								.replaceAll("\\)", " \\) ")
								.replaceAll(",", " , ")
								.replaceAll(";", " ; ")
								;
						SearchFunctionService sfs = new SearchFunctionService();
						lstDQLTable.addAll(sfs.subQuery(dqlcontent));
						SearchFunctionPnl.setLog("資訊", "建立清單");
						for(String tblNm : lstDQLTable) {
							FileTool.addFile(dqlListNm,																
									  "\""   +category 
									+ "\",\""+f.getName()
									+ "\",\""+tblNm
									+ "\"");
						}
						//搜尋function
						List<String> lstSql = RegexTool.getRegexTarget("SELECT[^;]*", content);
//						String[] arrSql = content.split(";");
						//每一段sql
						int j = 1;
						String temp = "";
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
//								RegexTool.
								//寫入總表
								FileTool.addFile(detlListNm,
										    "\"" + category
										+"\",\"" + f.getName()
										+"\",\"" + j
										+"\",\"" + func
										+"\"");
								//加到函式表
								if(!lstFunc.contains(func)) {
									lstFunc.add(func);
								}
							}
							j++;
						}
						i++;
						SearchFunctionPnl.setProgressBar(i * 100 / cntf);
					}
					// 寫函式檔
					SearchFunctionPnl.setLog("資訊","產生函式檔");
//					SearchFunctionPnl.setProgressBar(0);
					cntf = lstFunc.size();
					int k = 0;
					for(String fc : lstFunc) {
						FileTool.addFile(funcListNm,"\""+fc+"\"");
						k++;
						SearchFunctionPnl.setProgressBar(k * 100 / cntf);
					}
					SearchFunctionPnl.setStatus(RunStatusEnum.SUCCESS);
					SearchFunctionPnl.setLog("資訊","產生完成，共 "+i+" 個檔案");
					Log.info("完成，共 "+i+" 個檔案");
				} catch (Exception e1) {
					SearchFunctionPnl.setStatus(RunStatusEnum.FAIL);
					e1.printStackTrace();
					SearchFunctionPnl.setLog("錯誤",e1.getMessage());
				}
			}
		}.start();
	}
	//將btq轉成sql
	public static String getSqlContent(String c) {
		String res = c.toUpperCase()
				.replaceAll("\\n\\/\\*\\*.+", "")
				.replaceAll("\\n\\*\\*\\*.+", "")
				.replaceAll("\\n\\*\\/.+", "")
				.replaceAll("\\/\\*.+\\*\\/", "")
				.replaceAll("\\n\\..+",";")
				.replaceAll("--.+","")
				.replaceAll("\\(\\s*TITLE[^\\)]*\\)","")
				.replaceAll("\'[^']*\'","''")//中文
				.trim();
		return res;
	}
}
