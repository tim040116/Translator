package etec.src.translator.file.assignment.service.create_list;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.file.model.BasicParams;
import etec.src.translator.view.panel.SearchFunctionPnl;



public class CreateCallSPList {
	
	private static final String TITLE = "\"FILE_PATH\",\"FILE_NAME\",\"SP_NAME\",\"PARAMS\"";
	
	private static boolean IS_TITLE = true;
	
	/**
	 * <h1>列出所有call store porsiture</h1>
	 * <p></p>
	 * <p>
	 * <br>FILE_PATH : 資料夾
	 * <br>FILE_NAME : 檔名
	 * <br>SP_NAME	 : SP NAME
	 * <br>PARAMS	 : 使用到的參數
	 * </p>
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
	public static void createCallList(String category,String fileNm,String content) throws Exception {
		SearchFunctionPnl.tsLog.setLog("資訊","CreateCallSPList");
		IS_TITLE = true;
		//建立清單檔
		String detlListNm = BasicParams.getOutputPath()+"\\list\\call_sp_list.csv";//列出所有檔案用到的方法
		if(IS_TITLE) {
			FileTool.addFile(detlListNm,TITLE);//路徑,檔名,段落,方法名
			IS_TITLE = false;
		}
		/**
		 * <p>功能 ：</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從  到 </p>
		 * <h2>群組 ：</h2>
		 * 	1.SP name
		 *  2.params
		 * <h2>備註 ：</h2>
		 * <p>
		 * </p>
		 * <h2>異動紀錄 ：</h2>
		 * 2024年6月7日	Tim	建立邏輯
		 * */
		String reg = "(?i)CALL\\s+([^\\s()]+)\\s*\\(\\s*([^)]+)\\s*\\)";
		Matcher m = Pattern.compile(reg).matcher(content);
		while (m.find()) {
			//寫入總表
			FileTool.addFile(detlListNm,
					    "\"" + category
					+"\",\"" + fileNm
					+"\",\"" + m.group(1)
					+"\",\"" + m.group(2)
					+"\"");
		}
		
	}
}
