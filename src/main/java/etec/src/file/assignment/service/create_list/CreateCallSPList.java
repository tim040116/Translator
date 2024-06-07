package etec.src.file.assignment.service.create_list;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;
import etec.common.utils.file.FileTool;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.service.SearchFunctionService;
import etec.view.panel.SearchFunctionPnl;

public class CreateCallSPList {
	
	private static final String TITLE = "\"FILE_PATH\",\"FILE_NAME\",\"PART_ID\",\"FUNCTION_NAME\"";
	
	private static boolean IS_TITLE = true;
	
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
	public void createCallList(String category,String fileNm,String content) throws Exception {
		SearchFunctionPnl.tsLog.setLog("資訊","CreateCallSPList");
		
		//建立清單檔
		String detlListNm = BasicParams.getOutputPath()+"call_sp_list.csv";//列出所有檔案用到的方法
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
		 * 	1.
		 * <h2>備註 ：</h2>
		 * <p>
		 * </p>
		 * <h2>異動紀錄 ：</h2>
		 * 2024年6月7日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)CALL\\s+([^\\s()]+)\\s*\\(\\s*([^)]+)\\s*\\)";
		Matcher m = Pattern.compile(reg).matcher(content);
		while (m.find()) {
			String str = m.group(0);
			//寫入總表
			FileTool.addFile(detlListNm,
					    "\"" + category
					+"\",\"" + f.getName()
					+"\",\"" + j
					+"\",\"" + func
					+"\",\"" + m.group(0)
					+"\"");
		}
		
	}
}
