package etec.src.translator.project.azure.fm.formal.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FmSqlService {
	
	private static List<String[]> lstrpl = new ArrayList<String[]>();
	
	static {
		try (FileInputStream fis = new FileInputStream(new File("config\\replace_list\\fm\\replace_list.csv"));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {
			while (br.ready()) {
				String[] line = br.readLine().replace("\uFEFF","").split(",");
				lstrpl.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String easyReplace(String content) {
		String res = content;
		res = replaceTempTableName(res);
		res = toLowerCase(res);
		res = dropCreate(res);
		res = removeBteq(res);
		res = cleanCode(res);
		res = addSP(res);
		res = replaceAll(res);
		res = fdpUpt(res);
		return res;
	}
	//依開發規範轉換
	public static String designPattern(String content) {
		String res = content;
		res = res.replaceAll("(?i)\\bgetDate\\(\\)","dateadd\\(hour,8,getdate\\(\\)\\)");
		return res;
	}
	//依照參數檔批量轉換
	public static String replaceAll(String content) {
		String res = content;
		for(String[] arr : lstrpl) {
			String reg = "(?i)"+Pattern.quote(arr[0].trim())+"\\b";
			String rpm = Matcher.quoteReplacement(arr[1].trim());
			res = res.replaceAll(reg,rpm);
		}
		return res;
	}
	/**
	 * 加sp語法
	 */
	public static String addSP(String content) {
		String res = content;
		String txdate = "@tx_date";
		String txdate1 = "@v_tx_date";
		res = "CREATE PROCEDURE dev.sp__ldtf\r\n\t" 
			+ txdate + " varchar(10)" 
			+ "\r\nAS BEGIN"
			+ IOPTableLog(txdate)
			+ "\r\n\r\n\tDECLARE " + txdate1 + " int;"
			+ "\r\n\tSET " + txdate1 + " cast(convert(varchar(8),cast(" + txdate + " as date),112) as int);\r\n"
			+ res.trim().replaceAll("\n", "\n\t") 
			+ "\r\n\r\nEND";
		//TXDATE處理
		res = res
			.replaceAll(Pattern.quote("${TXDATE1}"), txdate1)
			.replaceAll("(?i)CAST\\s*\\(\\s*'\\$\\{TXDATE\\}'\\s*AS\\s*DATE\\s*\\)",Matcher.quoteReplacement("cast("+txdate+" as date)"))
			.replaceAll("'\\$\\{TXDATE\\}'",txdate)
		;
		return res;
	}

	/**
	 * 關於temp table 的處理
	 * 
	 * 依宇皇提供的開發規範(2024/09/02) table name一律小寫 存於dev. stg_開頭 若有複數temp table則編號放在最後
	 * 
	 */
	public static String replaceTempTableName(String content) {
		String res = content;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)(?:#|tempdb\\.\\.|TEMP_TABLE\\.)(?:TP(\\d+)?_)?(\\w+)").matcher(res);
		while (m.find()) {
			String all = m.group(0);
			String tmpId = m.group(1);
			String tblNm = m.group(2);
			tmpId = tmpId == null ? "" : "_" + tmpId;
			tblNm = tblNm.toLowerCase();
			String rpm = "dev.stg_" + tblNm + tmpId;
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 關於temp table 的處理
	 * 
	 * 依宇皇提供的開發規範(2024/09/02) table name及column name一律小寫
	 * 
	 */
	public static String toLowerCase(String content) {
		String res = content;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)([${}\\w]+)\\.(\\w+)").matcher(res);
		while (m.find()) {
			String dbNm = m.group(1);
			String tbNm = m.group(2);
			if (!dbNm.matches("\\$\\{\\w+\\}")) {
				dbNm = dbNm.toLowerCase();
			}
			tbNm = tbNm.toLowerCase();
			String rpm = dbNm + "." + tbNm;
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 依照全家提供drop create的語法規格 順便排除奇怪的SP語法
	 */
	public static String dropCreate(String content) {
		String res = content;
		res = res
				.replaceAll("(?i)(IF OBJECT_ID\\('[^']+')\\) IS NOT NULL (DROP TABLE \\S+)",
						"$1,'U'\\) IS NOT NULL \r\nBEGIN\r\n$2;\r\nEND")
				.replaceAll("(?i)EXEC\\s+[^.]+\\.p_drop_table\\s+'([^']+)'.*;",
						"IF OBJECT_ID\\('$1','U'\\) IS NOT NULL\r\nBEGIN\r\nDROP TABLE $1; \r\nEND");

		return res;
	}

	/**
	 * 移除bteq語法
	 **/
	public static String removeBteq(String content) {
		String res = content;
		res = res
			.replaceAll("(?i)\\.IF\\s+ERRORCODE\\b[^;]+;", "")
			.replaceAll("(?i)\\.SET\\s+ERROROUT\\s+STDOUT\\s*;?", "")
			.replaceAll("(?i)\\.SET\\s+SESSION\\b[^;]+;", "")
			.replaceAll("(?i)\\.LOGON\\s+[^,]+,\\s*\\S+\\s*", "")			
			.replaceAll("(?i)\\.LOGOFF\\s*;\\s*\\.QUIT\\s*0\\s*;","")
		;
		return res;
	}

	/**
	 * 清理跑版的語法
	 **/
	public static String cleanCode(String content) {
		String res = content;
		res = res
				.replaceAll("\uFEFF", "")
				.replaceAll("(?i)\r?\nDATABASE\\s*\\S+\\s*;", "")// database
				.replaceAll("(?i)\t+CASE", "CASE")// CASE
				.replaceAll("(?i),\\s+ROLLUP\\(", ",ROLLUP\\(")// ,\r\nROLLUP
				.replaceAll("    ","\t")
				.replaceAll("[ \\t]+\\r?\\n","\r\n")
				// DROP兩次
				.replaceAll(
						"(?i)IF OBJECT_ID\\('([^']+)','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END\\s+(IF OBJECT_ID\\('\\1','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END)",
						"$2");
		return res;
	}
	
	private static String IOPTableLog(String txdate) {
		String res = "\r\n\t/*--------------------------------------------------------------------------"
				+ "\r\n\tSP參數"
				+ "\r\n\t\t"+txdate+" = ${TXDATE}"
				+ "\r\n\tINPUT"
				+ "\r\n\t\t"
				+ "\r\n\tOUTPUT"
				+ "\r\n\t"
				+ "\r\n\t--------------------------------------------------------------------------*/"
				+ "";
		return res;
	}

	/**
	 * Merge into語法增加fdp_upt欄位，是異動日期
	 **/
	public static String fdpUpt(String content) {
		String res = content;
		res = res
			.replaceAll("(?i)(when\\s+matched\\s+then\\s+[\\S\\s]+?)(\\s+when\\s*not\\s*matched|;)", "$1\r\n\t\t\t,fdp_upt = dateadd\\(hour,8,getdate\\(\\)\\)$2")
			.replaceAll("(?i)(when\\s*not\\s*matched\\s*then\\s*insert\\s*\\([^)]+?)(\\s*\\)\\s*values\\s*\\([^;]+?)(\\s*\\)\\s*;)", "$1\r\n\t\t\t,fdp_upt$2\r\n\t\t\t,dateadd\\(hour,8,getdate\\(\\)\\)$3")
		;
		return res;
	}
}
