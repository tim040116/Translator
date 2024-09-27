package etec.src.translator.sql.az.translater.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxdateService {

	public static String easyReplace(String content) {
		String res = content;
//		res = toVarchar(res);
		res = replaceLastnnTxdate(res);
		return res;
	}

//	public static String toVarchar(String content) {
//		return content.replaceAll("'(\\$\\{\\w+\\})'","cast\\($1 as varchar\\)");
//	}
//
	public static String replaceLastnnTxdate(String content){
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

}
