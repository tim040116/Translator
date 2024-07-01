 package etec.src.file.azure.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.common.utils.charset.CharsetTool;
import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.common.utils.file.FileTool;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.framework.translater.enums.SQLTypeEnum;
import etec.framework.translater.exception.SQLTranslateException;
import etec.framework.translater.exception.UnknowSQLTypeException;
import etec.framework.translater.interfaces.TranslaterFactory;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.translater.AzTranslater;
import etec.src.sql.az.translater.DDLTranslater;
import etec.src.sql.az.translater.DMLTranslater;
import etec.src.sql.az.translater.DQLTranslater;
import etec.src.sql.az.translater.OtherTranslater;
import etec.src.sql.td.classifier.TeradataClassifier;

/**
 * @author	Tim
 * @since	2023年10月4日
 * @version	3.3.0.0
 * 
 * 全家的城市轉換
 * */
public class AzureTranslateService {
	
	
	/**
	 * <h1>執行入口</h1>
	 * <p>
	 * <br>單純的測試SQL語法
	 * <br>沒有前後格式，單純用分號區隔
	 * <br>雙斜線為註解，會先清除再進行轉換
	 * </p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年06月17日	Tim	建立功能
	 * @author Tim
	 * @since 2024年6月17日	
	 * @param context 檔案的內容
	 * @see
	 * @return
	 * @throws IOException
	 */
	public static void run(File f) throws IOException {
		/* 2024/05/06	Tim	強制轉換成指定編碼
		 * */
//		String context = FileTool.readFile(f);
		Charset chs = CharsetTool.getCharset(f.getPath());
		String context = CharsetTool.readFileInCharset(chs.name(),f.getPath());
		context = context
				//locking
				.replaceAll("(?i)\\bFOR\\s+ACCESS(?!\\s*;)", "$0;")
				//.
				.replaceAll("\\n\\s*\\..*", "$0;")
				.replaceAll(";\\s*;", ";")
			;
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
		Log.debug("清理註解");
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
				 * <h2>備註 ：</h2>
				 * 	會先將TranslaterService裡的各類型title用|串接，
				 *  作為開頭
				 * <h2>異動紀錄 ：</h2>
				 * 2024年4月10日	Tim	建立邏輯
				 * 2024年5月6日	Tim	增加所有類型的title
				 * */
				String reg = "(#\\*s)?\\b(?:" + String.join("|",TranslaterFactory.getTitleList()) + ")\\b[^;]+?;";
				Matcher m = Pattern.compile(reg, Pattern.CASE_INSENSITIVE).matcher(t);
				while (m.find()) {
					// 處理前後空白
					String sql = m.group().trim();
					sql = AzTranslater.translate(sql);
					m.appendReplacement(sb, Matcher.quoteReplacement(sql + "\r\n"));
				}
				m.appendTail(sb);
			} catch (SQLTranslateException e) {
				e.printStackTrace();
			}
			return sb.toString();
		});
		
		/*將bteq語法清除*/
		//newContext = newContext.replaceAll("\\r\\n\\..*", "");
//		newContext = GreenPlumTranslater.dql.changeMultAnalyze(newContext);
		FileTool.createFile(newFileName, newContext, chs);
	}
}
