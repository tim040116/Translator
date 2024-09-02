package etec.src.translator.file.fm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FmSqlService {
	
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
	public static String replaceTempTableName(String sql) {
		String res = sql;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)(?:#|tempdb\\.\\.|TEMP_TABLE\\.)TP(\\d+)?(_\\w+)").matcher(res);
		while(m.find()) {
			String tmpId = m.group(1);
			String tblNm = m.group(2);
			tmpId = tmpId==null?"":"_"+tmpId;
			tblNm = tblNm.toLowerCase();
			String rpm = "dev.stg"+tblNm+tmpId;
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
	public static String toLowerCase(String sql) {
		String res = sql;
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
	
}
