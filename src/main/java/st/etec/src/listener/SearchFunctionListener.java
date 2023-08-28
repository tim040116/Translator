package st.etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import etec.common.enums.RunStatusEnum;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import src.java.params.BasicParams;
import st.etec.src.params.Params;
import st.etec.src.service.IOpathSettingService;
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
					//建立清單黨
					String fileListNm = BasicParams.getOutputPath()+"list\\file_list.csv";
					String funcListNm = BasicParams.getOutputPath()+"list\\func_list.csv";
					String detlListNm = BasicParams.getOutputPath()+"list\\detl_list.csv";
					FileTool.addFile(fileListNm,"\"資料夾\",\"檔名\"");
					FileTool.addFile(funcListNm,"\"函式名\"");
					FileTool.addFile(detlListNm,"\"資料夾\",\"檔名\",\"段落\",\"函式名\"");
					List<String> lstFunc = new ArrayList<String>();
					List<String> lsSkip = Arrays.asList(Params.searchFunction.SKIP_LIST);
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
						FileTool.addFile(fileListNm,"\""+category+"\",\""+f.getName()+"\"");
						// 讀取檔案
						String content = FileTool.readFile(f);
						content = getSqlContent(content);
						List<String> lstSql = RegexTool.getRegexTarget("SELECT[^;]*", content);
//						String[] arrSql = content.split(";");
						//每一段sql
						int j = 1;
						String temp = "";
						for(String sql : lstSql) {
							List<String> lfc = RegexTool.getRegexTarget2("(QUALIFY +|AS +)?[A-Z0-9_\\.]+\\s*\\(", sql);
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
								if(func.contains("ROW_NUMBER")) {//rank over
									temp = "ROW_NUMBER";
								}
								if(func.contains("AS ")) {//CAST AS
									continue;
								}
								if(lsSkip.contains(func)) {
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
					i = 0;
					for(String fc : lstFunc) {
						FileTool.addFile(funcListNm,"\""+fc+"\"");
						i++;
						SearchFunctionPnl.setProgressBar(i * 100 / cntf);
					}
					SearchFunctionPnl.setStatus(RunStatusEnum.SUCCESS);
					SearchFunctionPnl.setLog("資訊","產生完成");
					Log.info("完成");
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
				.replaceAll("\\n\\..+","")
				.replaceAll("--.+","")
				.replaceAll("\\(\\s*TITLE[^\\)]*\\)","")
				.trim();
		return res;
	}
}
