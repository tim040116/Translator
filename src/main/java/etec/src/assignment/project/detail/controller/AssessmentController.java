package etec.src.assignment.project.detail.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Controller;
import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.file.readfile.service.BigFileSplitTool;
import etec.framework.file.readfile.service.CharsetTool;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.framework.ui.search_func.enums.RunStatusEnum;
import etec.src.assignment.project.detail.service.SearchFunctionService;
import etec.src.assignment.project.sd.service.CreateMultisetListService;
import etec.src.assignment.project.sd.service.CreateSDMService;
import etec.src.assignment.project.sp.service.CreateCallSPList;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.common.service.IOpathSettingService;
import etec.src.translator.project.azure.fm.poc.service.TransduceStoreFunctionService;
import etec.src.translator.view.panel.SearchFunctionPnl;

public class AssessmentController implements Controller{
	public static Map<String,Integer> mapFunc;
	public static List<String> lstSkip = Arrays.asList(Params.searchFunction.SKIP_LIST);

	@Override
	public void run(Map<String,Object> args) throws Exception {
		mapFunc = new HashMap<>();

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
		if(Params.config.BIG_FILE_SPLIT) {
			BigFileSplitTool.splitFile(BasicParams.getInputPath());
		}
		// log 讀取檔案
		SearchFunctionPnl.tsLog.setLog("資訊", "開始讀取檔案");
		SearchFunctionPnl.progressBar.reset();
		SearchFunctionPnl.progressBar.setUnit(lf.size());

		int i = 0;
		// 讀取每一份檔案
		for (File f : lf) {

			SearchFunctionPnl.tsLog.setLog("資訊","讀取檔案：" + f.getPath());
			CreateMultisetListService.CreateList(BasicParams.getInputPath(), f);
			// 讀取檔案
			//String content = FileTool.readFile(f,Charset.forName("utf-8"));
			String content = CharsetTool.readFileInCharset("UTF-8", f.getPath());
			String category = "\\" + f.getPath()
			.replace(BasicParams.getInputPath(), "")
			.replace(f.getName(), "")
			.replaceAll("\\\\$", "")
			;

			/* Generate file List */
//			SearchFileList(f,content,category);

			// 清除註解
			content= ConvertRemarkSafely.cleanRemark(content);
			//拆除SQLSTR
			content = TransduceStoreFunctionService.findSQLSTR(content);
			/* Generate SDI file */
			SearchSDIList(f.getName(),content);
			/* call store porsidure*/
			CreateCallSPList.createCallList(category, f.getName(), content);
			/* Generate function List */
			SearchFunctionList(f,content,category);


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

		/**
		 * 20240409	Tim	把有小數點的項目放在後面
		 * */
		Map<String,Integer> mapNodeItem = new HashMap<>();
		for(Entry<String, Integer> entry : mapFunc.entrySet()) {
			if(entry.getKey().contains(".")) {
				mapNodeItem.put(entry.getKey(), entry.getValue());
				continue;
			}
			FileTool.addFile(funcListNm,
					    "\""+entry.getKey()
					+"\",\"" + entry.getValue()
					+"\"");
			SearchFunctionPnl.progressBar.plusOne();
		}
		FileTool.addFile(funcListNm,"\r\n");
		//有小數點的部分
		for(Entry<String, Integer> entry : mapNodeItem.entrySet()) {
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
	public void SearchSDIList(String  fileName,String content) throws Exception {

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
					String res = CreateSDMService.createSD(fileName,mCol.group(0)); // 查到的CREATE 語法
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

	/**
	 * <h1></h1>
	 * <p></p>
	 * <p></p>
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月17日	Tim	增加排除資料型態強制轉換的功能
	 * <br>2024年4月26日	Tim	增加欄位function的前後文
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	f	來源檔
	 * @param	content	內容
	 * @param	category	資料夾
	 * @throws	Exception
	 * @see
	 * @return	void
	 */
	public void SearchFunctionList(File f,String content,String category) throws Exception {
		SearchFunctionPnl.tsLog.setLog("資訊","SearchFunctionList");
		content = SearchFunctionService.getSqlContent(content);
		//每一段sql
		int j = 1;
		String temp = "";
		//建立清單檔
		String detlListNm = BasicParams.getOutputPath()+Params.searchFunction.DETL_LIST_NAME;//列出所有檔案用到的方法
		if(Params.searchFunction.IS_TITLE) {
			FileTool.addFile(detlListNm,"\"FILE_PATH\",\"FILE_NAME\",\"PART_ID\",\"FUNCTION_NAME\"");//路徑,檔名,段落,方法名
		}
		/**
		 * <p>功能 ：捕獲SQL語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：gmi</p>
		 * <p>範圍 ：從 開頭 到 分號</p>
		 * <h2>群組 ：</h2>
		 * <h2>備註 ：</h2>
		 * 	會先將TranslaterService裡的各類型title用|串接，
		 *  作為開頭
		 * <h2>異動紀錄 ：</h2>
		 * 2024年4月10日	Tim	建立邏輯
		 * 2024年5月6日	Tim	增加所有類型的title
		 * */
		String regm = "(#\\*s)?\\b(?:SELECT|UPDATE|MERGE|WHERE)\\b[^;]+?;";
		Matcher mm = Pattern.compile(regm, Pattern.CASE_INSENSITIVE).matcher(content);
		while (mm.find()) {
			// 處理前後空白
			String sql = mm.group().trim();
			/**初步排除
			 * 2024年4月17日	Tim	增加排除資料型態強制轉換的功能
			 * 2024年4月22日 Tim 增加只查詢SELECT語法的功能
			 * 2024年4月22日 Tim 排除FORMAT語法
			 * 2024年5月2日	Tim	排除Alias name的語法
			 * 2024年5月2日	Tim	排除單引號裡的括號
			 * 2024年5月6日	Tim	排除雙引號裡的括號
			 * 2024年5月10日	Tim	排除 CTAS 裡面的 INDEX
			 * 2024年8月20日	Tim	刪除只查詢SELECT語法的功能
			 * */
//			sql = "SELECT"+sql.split("(?i)\\bSELECT\\b", 2)[1];
			sql = sql
					.replaceAll("(?i)\\(\\s*FORMAT\\b", "")//FORMAT語法   ex: CURRENT_DATE(FORMAT 'YYYY-MM-DD')
					.replaceAll("(?i)\\(\\s*("+Params.searchFunction.DATA_TYPE_LIST+")(?:\\s*\\([^)]+\\))?\\s*\\)", "")//強制轉換 ex: CURRENT_DATE(VARCHAR(10))
					.replaceAll("(?i)\\)\\s*\\w+\\s*\\((?!\\s*SELECT\\b)[\\s\\w,]+\\)","")//Alias name ex: join (select aa,trim(bb) from tbl_nm) b (aa,bb_new)
					.replaceAll("'[^']+'","''")//單引號裡的括號 ex: ,'比例(金額)' as desc_title
//					.replaceAll("\"[^\"]+\"","\"\"")//雙引號裡的括號
					.replaceAll("(?i)\\bWith\\s+Data[\\w\\s]+?Index\\s*\\(","")//CTAS的INDEX
					;
			/**
			 * <p>功能 ：查function</p>
			 * <p>類型 ：搜尋</p>
			 * <p>修飾詞：i</p>
			 * <p>範圍 ：從 function name 到 \n | ; | \) |下一個function name</p>
			 * <h2>群組 ：</h2>
			 * 	1.function name
			 * <h2>敘述</h2>
			 * <p>
			 * <br>1.如果名稱前方為QUALIFY或AS倆的單詞，需一併包括進來
			 * <br>2.$1為function name
			 * <br>3.方便查詢該函式於檔案中的位置，加上函式內的參數，於$0
			 * <br>4.函式內參數的補獲會於[\n;\)]停止
			 * <br>5.若遇到下一個函式名會轉為下一個捕獲
			 * </p>
			 * <h2>異動紀錄 ：</h2>
			 * <br>2024年4月17日	Tim	建立邏輯
			 * <br>2024年4月26日	Tim	增加function後面的文字
			 * <br>2024年5月3日	Tim	將查詢的字串由context改為sql
			 * */
			String reg = "(?mi)((?:QUALIFY +|AS +)?[\\w\\$\\{\\}\\.]+)\\s*\\([^;\\r\\n]*?(?=[\\w\\$\\{\\}\\.]+?\\s*\\(|$|[;])";
			Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(sql);
			while (m.find()) {
				String func = m.group(1);
				System.out.println(func);
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
				if(func.matches("\\s*[0-9]+\\s*") || lstSkip.contains(func)) {
					continue;
				}
				//寫入總表
				FileTool.addFile(detlListNm,
						    "\"" + category
						+"\",\"" + f.getName()
						+"\",\"" + j
						+"\",\"" + func
						+"\",\"" + m.group(0)
						+"\"");
				//加到函式表
				mapFunc.put(func,mapFunc.get(func)==null?1:mapFunc.get(func)+1);
			}
			j++;
		}
	}

	@Override
	public Map<String, Object> getArgs() {
		// TODO Auto-generated method stub
		return null;
	}


}
