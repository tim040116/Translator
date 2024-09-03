package etec.src.translator.file.fm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FmSqlService {
	
	public static String easyReplace(String content) {
		String res = content;
		res = replaceTempTableName(res);
		res = toLowerCase(res);
		res = dropCreate(res);
		res = removeBteq(res);
		res = cleanCode(res);
		return res;
	}
	/**
	 * 加sp語法
	 * */
	public static String addSP(String content) {
		String res = content;
		res = res
			.replaceAll(Pattern.quote("${TXDATE1}"),"@YYYYMMDD")
		;
		res = "CREATE PROCEDURE dev.sp__ldtf\r\n"
			+ "@YYYYMMDD INT\r\n"
			+ "AS BEGIN\r\n\r\n"
			+ res
			+ "\r\nEND";
		return res;
	}
	/**
	 * 關於temp table 的處理
	 * 
	 * 依宇皇提供的開發規範(2024/09/02)
	 * table name一律小寫
	 * 存於dev.
	 * stg_開頭
	 * 若有複數temp table則編號放在最後
	 * 
	 * */
	public static String replaceTempTableName(String content) {
		String res = content;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)(?:#|tempdb\\.\\.|TEMP_TABLE\\.)(?:TP(\\d+)?_)?(\\w+)").matcher(res);
		while(m.find()) {
			String all = m.group(0);
			String tmpId = m.group(1);
			String tblNm = m.group(2);
			tmpId = tmpId==null?"":"_"+tmpId;
			tblNm = tblNm.toLowerCase();
			String rpm = "dev.stg_"+tblNm+tmpId;
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	/**
	 * 關於temp table 的處理
	 * 
	 * 依宇皇提供的開發規範(2024/09/02)
	 * table name及column name一律小寫
	 * 
	 * */
	public static String toLowerCase(String content) {
		String res = content;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)([${}\\w]+)\\.(\\w+)").matcher(res);
		while(m.find()) {
			String dbNm = m.group(1);
			String tbNm = m.group(2);
			if(!dbNm.matches("\\$\\{\\w+\\}")) {
				dbNm = dbNm.toLowerCase();
			}
			tbNm = tbNm.toLowerCase();
			String rpm = dbNm+"."+tbNm;
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	/**
	 * 依照全家提供drop create的語法規格
	 * 順便排除奇怪的SP語法
	 * */
	public static String dropCreate(String content) {
		String res = content;
		res = res.replaceAll("(?i)(IF OBJECT_ID\\('[^']+')\\) IS NOT NULL (DROP TABLE \\S+)","$1,'U'\\) IS NOT NULL \r\nBEGIN\r\n$2;\r\nEND")
				.replaceAll("(?i)EXEC\\s+[^.]+\\.p_drop_table\\s+'([^']+)'.*;", "IF OBJECT_ID\\('$1','U'\\) IS NOT NULL\r\nBEGIN\r\nDROP TABLE $1; \r\nEND");
		
		return res;
	}
	/**
	 * 移除bteq語法
	 **/
	public static String removeBteq(String content) {
		String res = content;
		res = res.replaceAll("(?i)^\\..*?;$","");
		return res;
	}
	/**
	 * 清理跑版的語法
	 **/
	public static String cleanCode(String content) {
		String res = content;
		res = res
			.replaceAll("(?i)\t+CASE","CASE")//					CASE
			.replaceAll("(?i),\\s+ROLLUP\\(", ",ROLLUP\\(")//,\r\nROLLUP
			//DROP兩次
			.replaceAll("(?i)IF OBJECT_ID\\('([^']+)','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END\\s+(IF OBJECT_ID\\('\\1','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END)", "$2")
		;
		return res;
	}
}
