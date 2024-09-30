 package etec.src.translator.project.azure.fm.poc.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.framework.context.translater.interfaces.TranslaterFactory;
import etec.framework.file.readfile.service.CharsetTool;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.project.azure.fm.formal.service.FmSqlService;
import etec.src.translator.sql.az.translater.AzTranslater;

/**
 * @author	Tim
 * @since	2023年10月4日
 * @version	3.3.0.0
 *
 * 全家的城市轉換
 * */
public class FamilyMartFileTransduceService {


	/**
	 * <h1>執行入口</h1>
	 * <br>抄AzureTranslateService
	 * <br>加上全家客製化功能
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年09月02日	Tim	建立功能
	 * @author Tim
	 * @since 2024年9月2日
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
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
		context = FmSqlService.preReplace(context);
		Log.debug("清理註解");
		String newContext = ConvertRemarkSafely.savelyConvert(context, (t) -> {
			StringBuffer sb = new StringBuffer();
			try {
				Log.debug("開始捕獲語法");
				t = t
					//locking
					.replaceAll("(?i)\\bFOR\\s+ACCESS(?!\\s*;)", "$0;")
					//.
					.replaceAll("\\n\\s*\\..*", "$0;")
					.replaceAll(";\\s*;", ";")
				;
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

		//全家客製化項目
		newContext = fmOnly(newContext);
		newFileName = newFileName.replaceAll("(?i)\\.btq.*", ".sql");
		/*將bteq語法清除*/
		//newContext = newContext.replaceAll("\\r\\n\\..*", "");
//		newContext = GreenPlumTranslater.dql.changeMultAnalyze(newContext);
		FileTool.createFile(newFileName, newContext, chs);
	}

	private static String fmOnly(String content) {
		String res = content;
		res = FmSqlService.easyReplace(res);
		return res;
	}
}
