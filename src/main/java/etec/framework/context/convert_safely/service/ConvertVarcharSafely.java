package etec.framework.context.convert_safely.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>排除字串</h1>
 * <p>
 * <br>做轉換時排除''的寫死字串中包含關鍵字
 * <br>會先把字串轉換調
 * </p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年6月6日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class ConvertVarcharSafely {
	
	public static int idRemarkDash = 0;
	
	private static Pattern p = Pattern.compile("'[^']*?'");
	
	/**
	 * <h1>排除字串</h1>
	 * <p>
	 * <br>做轉換時排除''的寫死字串中包含關鍵字
	 * <br>會先把字串轉換調
	 * </p>
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月6日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	script, function
	 * @throws	e
	 * @see		
	 * @return	String
	 */
	public static String savelyConvert(String script,Function<String, String> function) {
		String res = script;
		
		Map<String,String> mapChar = new HashMap<String,String>();
		Matcher m = p.matcher(script);
		while (m.find()) {
			String id = markName("char", idRemarkDash);
			String mark = m.group(0);
			idRemarkDash++;
			mapChar.put(id, mark);
			res = res.replace(mark, id);
		}
		res = function.apply(res);
		for(Entry<String,String> e : mapChar.entrySet()) {
			res = res.replaceAll(Pattern.quote(e.getKey()),Matcher.quoteReplacement(e.getValue()));
		}
		return res;
	}
	
	public static String markName(String type,int i) {
		return markName(type,Integer.toString(i));
	}
	public static String markName(String type,String i) {
		return "___ConvertVarcharSafely_"+type+"_"+i+"___";
	}
	public static boolean equals(String a,String b) {
		return equals(a,b,(t)->{return t;});
	}
	public static boolean equals(String a,String b,Function<String, String> function) {
		return  function.apply(a.replaceAll(markName("\\w+","\\d+"), "")).toUpperCase().replaceAll("\\s+", "")
		.equals(function.apply(b.replaceAll(markName("\\w+","\\d+"), "")).toUpperCase().replaceAll("\\s+", ""));
	}
	public static boolean match(String reg,String str) {
		return str.replaceAll(markName("\\w+","\\d+"), "").trim().matches(reg);
	}
}
