package etec.src.translator.sql.az.translater.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxdateService {

	public static String easyReplace(String content) {
		String res = content;
//		res = toVarchar(res);
		res = isolateTxdate(res);
		res = replaceLastnnTxdate(res);
		return res;
	}

//	public static String toVarchar(String content) {
//		return content.replaceAll("'(\\$\\{\\w+\\})'","cast\\($1 as varchar\\)");
//	}
//
	public static String replaceLastnnTxdate(String content){
		content = content.replaceAll("(?i)'(\\$\\{(?:NEXT|LAST)[^{}]+\\})'","$1");
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("\\$\\{(NEXT|LAST)(\\d+)(TXDATE1?)\\}").matcher(content);
		while(m.find()) {
			String minus = m.group(1).toUpperCase().equals("LAST")?"-":"";
			String day = m.group(2);
			String txdate = m.group(3);
			String rpm = "dateAdd(day,"+minus+day+",cast(${"+txdate+"} as date))";
			if(m.group(3).toUpperCase().equals("TXDATE1")) {
				rpm = "cast(convert(varchar,"+rpm+",112) as int)";
			}else if(m.group(3).toUpperCase().equals("TXDATE")){
				rpm = "convert(varchar,"+rpm+",23)";
			}
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 避免TXDATE語法參雜在字串中
	 * ex:'${LAST01TXDATE} 12:00:00 AM'
	 * @author	Tim
	 * @since	2024年10月28日
	 * */
	public static String isolateTxdate(String content) {
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("'[^'\r\n']+?'").matcher(content);
		while(m.find()) {
			if(!m.group().contains("$")) {
				continue;
			}
			if(m.group().matches("'\\$\\{[^${}']+\\}'")) {
				continue;
			}
			String str = "(" + m.group(0)
				.replaceAll("('[^${}']+)(?=\\$\\{)", "$1' + '")
				.replaceAll("(\\$\\{[^${}']+\\})([^']+')", "$1' + '$2")
				+ ")"
			;
			m.appendReplacement(sb,Matcher.quoteReplacement(str));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
