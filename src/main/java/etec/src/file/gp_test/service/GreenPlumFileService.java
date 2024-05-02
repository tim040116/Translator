package etec.src.file.gp_test.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import etec.common.exception.sql.SQLTransduceException;
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
	 * @author Tim
	 * @since 4.0.0.0
	 * @param context 檔案的內容
	 * @see
	 * @return
	 * @throws IOException
	 */
	public static void run(File f) throws IOException {

		String context = FileTool.readFile(f);
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
		Log.debug("清理資料");
		String newContext = ConvertRemarkSafely.savelyConvert(context, (t) -> {
			StringBuffer sb = new StringBuffer();
			try {
				Log.debug("開始捕獲語法");
				// DDL
				String reg = "\\b(?:" + String.join("|", Stream
						.concat(Arrays.stream(GreenPlumTranslater.arrDDL), Arrays.stream(GreenPlumTranslater.arrDML))
						.toArray(String[]::new)) + ")\\b[^;]+?;";
				Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(context);
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
