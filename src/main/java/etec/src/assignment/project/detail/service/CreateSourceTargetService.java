package etec.src.assignment.project.detail.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.convert_safely.model.Mark;
import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.framework.context.translater.interfaces.TranslaterFactory;
import etec.framework.file.readfile.service.CharsetTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.sql.az.translater.AzTranslater;

public class CreateSourceTargetService {

	private List<String> lstStaticTable = new ArrayList<String>();
	
	private List<String> lstSourceTable = new ArrayList<String>();
	
	private List<String> lstTargetTable = new ArrayList<String>();
	
	public static void run(File f) throws MalformedURLException, IOException {
		Charset chs = CharsetTool.getCharset(f.getPath());
		String context = CharsetTool.readFileInCharset(chs.name(),f.getPath());
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
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
					.replaceAll("\\bSEL\\b", "SELECT")
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
	}
	
	public void importFile(String fileName, String content) {
		content = cleanScript(content);
		// 找語法
		Matcher m = Pattern.compile("(?i)\\b(CREATE|INSERT|RENAME|MERGE)\\b[^;]+;").matcher(content);
		while (m.find()) {
			switch (m.group(1).toUpperCase()) {
			case "CREATE":
				
				break;
			case "INSERT":

				break;
			case "RENAME":

				break;
			case "MERGE":

				break;
			default:
				break;
			}
		}
	}
	
	private void mappingCreateTable(String sql) {
		Matcher m = Pattern.compile("(?i)\\bCREATE.*?(?:TABLE|VIEW)\\s+([\\w#${}.]+)([^;]+)").matcher(sql);
		if (m.find()) {
			String target = m.group(1);
			String content = m.group(2);
			
		}
	}

	private void getSourceTable(String sql) {
		Matcher m = Pattern.compile("(?i)(?:FROM|JOIN|USING)").matcher(sql);
		if (m.find()) {
			String target = m.group(1);
			String content = m.group(2);
			
		}
	}
	
	
	/**
	 * <h1>清理語句</h1>
	 * <p>
	 * 為了方便之後的轉換，對語法進行初步的加工
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * <h2>異動紀錄</h2> <br>
	 * 2024年3月13日 Tim 建立功能
	 * 
	 * @author Tim
	 * @since 4.0.0.0
	 * @param script 原城市
	 * @throws @see
	 * @return
	 */
	private static String cleanScript(String script) {
		String res = script;
		/**
		 * <p>
		 * 功能 ：清除註解
		 * </p>
		 * 1.清除//跟--類型的註解 2.將 *\/ 轉換成標記符號 3.刪除從/*到標記符號的內容 4.\r\n
		 */
		res = res.replaceAll("(?://|--).*", "").replaceAll("\\*/", Mark.MAHJONG_BLACK)
				.replaceAll("/*[^" + Mark.MAHJONG_BLACK + "]+" + Mark.MAHJONG_BLACK, "")
				.replaceAll("(?<!\r)\n", "\r\n");
		/**
		 * <p>
		 * 功能 ：整理欄位設定
		 * </p>
		 * 因為切欄位資訊時會以空白切分，要將依些設定合併 1.CHARACTER SET LATIN 2.NOT CASESPECIFIC 3.NOT NULL
		 * 4.TITLE 'AAA' 5.FORMAT 'AAA' 6.DEFAULT 'AAA'
		 */
		res = res.replaceAll("(?i)\\bCHARACTER\\s+SET\\s+(\\S++)", "CHARACTER_SET_$1")
				.replaceAll("(?i)\\bNOT\\s+(CASESPECIFIC|NULL)", "NOT_$1")
				.replaceAll("(?i)\\b(TITLE|FORMAT)\\s+'([^']++)'", "$1_'$2'")
				.replaceAll("(?i)\\bDEFAULT\\s+(\\S++)", "DEFAULT_$1");
		return res;
	}
}

class SourceTargetModel {
	String fileName;
	String sourceTable;
	String targetTable;
	MappingType type;
}

enum MappingType {
	CTAS, INSERT, JOIN, UPDATE, RENAME
}
