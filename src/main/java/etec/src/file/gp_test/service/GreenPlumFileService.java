package etec.src.file.gp_test.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.sql.SQLTransduceException;
import etec.common.utils.charset.CharsetTool;
import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.common.utils.file.FileTool;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;
import etec.src.sql.gp.translater.GreenPlumTranslater;

/**
 * <h1>測試GP功能</h1>
 * <p>
 * <br>單純的測試SQL語法
 * <br>沒有前後格式，單純用分號區隔
 * <br>雙斜線為註解，會先清除再進行轉換
 * 
 * </p>
 * <h2>屬性</h2>
 * <p>
 * </p>
 * <h2>方法</h2>
 * <p>
 * run(String) : 執行 {@link #run(String)}
 * </p>
 * 
 * <h2>異動紀錄</h2> <br>
 * <br>2024年3月1日 Tim 建立功能
 * 
 * @author Tim
 * @version 4.0.0.0
 * @since 4.0.0.0
 * @see #run(String)
 */
public class GreenPlumFileService {

	/**
	 * <h1>執行入口</h1>
	 * <p>
	 * <br>單純的測試SQL語法
	 * <br>沒有前後格式，單純用分號區隔
	 * <br>雙斜線為註解，會先清除再進行轉換
	 * </p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年03月01日	Tim	建立功能
	 * <br>2024年04月22日	Tim	修正副檔名有兩個的錯誤
	 * <br>2024年05月06日	Tim	擴大捕獲SQL的範圍
	 * <br>2024年05月06日	Tim	強制轉換成指定編碼
	 * @author Tim
	 * @since 4.0.0.0
	 * @param context 檔案的內容
	 * @see
	 * @return
	 * @throws IOException
	 */
	public static void run(File f) throws IOException {

		/* 2024/05/06	Tim	強制轉換成指定編碼
		 * */
//		String context = FileTool.readFile(f);
		String context = CharsetTool.readFileInCharset(Params.gp.CHARSET,f.getPath());
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
		Log.debug("清理資料");
		String newContext = ConvertRemarkSafely.savelyConvert(context, (t) -> {
			StringBuffer sb = new StringBuffer();
			try {
				Log.debug("開始捕獲語法");
				/**
				 * <p>功能 ：捕獲SQL語法</p>
				 * <p>類型 ：搜尋</p>
				 * <p>修飾詞：gmi</p>
				 * <p>範圍 ：從 開頭 到 分號</p>
				 * <h2>群組 ：</h2>
				 * 	1.
				 * 	2.
				 * 	3.
				 * <h2>備註 ：</h2>
				 * 	會先將GreenPlumTranslater裡的各類型title用|串接，
				 *  作為開頭
				 * <h2>異動紀錄 ：</h2>
				 * 2024年4月10日	Tim	建立邏輯
				 * 2024年5月6日	Tim	增加所有類型的title
				 * */
				List<String> titleList = new ArrayList<String>();
				titleList.addAll(Arrays.asList(GreenPlumTranslater.arrDQL));
				titleList.addAll(Arrays.asList(GreenPlumTranslater.arrDML));
				titleList.addAll(Arrays.asList(GreenPlumTranslater.arrDDL));
				titleList.addAll(Arrays.asList(GreenPlumTranslater.arrOther));
				String reg = "\\b(?:" + String.join("|",titleList) + ")\\b[^;]+?;";
				Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(t);
				while (m.find()) {
					// 處理前後空白
					String sql = m.group().trim();
					sql = GreenPlumTranslater.translate(sql);
					m.appendReplacement(sb, Matcher.quoteReplacement(sql + "\r\n"));
				}
				m.appendTail(sb);
			} catch (SQLTransduceException e) {
				e.printStackTrace();
			}
			return sb.toString();
		});
		
		//將bteq語法清除
		newContext = newContext.replaceAll("\\r\\n\\..*", "");
		
		FileTool.createFile(newFileName, newContext, Params.familyMart.WRITE_FILE_CHARSET);
	}
}
