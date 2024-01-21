package etec.common.utils.convert_safely;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <h1>轉換語句時排除註解</h1>
 * <br>因應SQL語法中很多在方法中包覆其他方法的語法
 * <br>避免在取代的時候受到小括號跟逗號的影響導致轉換錯誤
 * <br>
 * <br>使用savelyConvert()可以安全的進行轉換，
 * <br>會對特殊符號進行轉換，
 * <br>在從外圍依序還原符號，
 * <br>以確保方法的參數不會受到其他小括號及逗號影響
 * @author	Tim
 * @since	4.0.0.0
 * @version	4.0.0.0
 * */
public class ConvertRemarkSafely {
	
	public int idRemarkDash = 0;
	
	/**
	 * <h1>轉換語句時排除註解</h1>
	 * 
	 * <br>先把註解加密，最後再還原
	 * @author	Tim
	 * @since	4.0.0.0
	 * <p>2024年1月3日	Tim	先製作SQL兩槓的轉換</p>
	 * */
	public String savelyConvert(String script,Function<String, String> function) {
		String res = script;
		Map<String,String> mapRemark = new HashMap<String,String>();
		Pattern p = Pattern.compile("(?mi)\r?\n?\\s*--.*\r?\n",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		while (m.find()) {
			String id = markName("dash", idRemarkDash);
			String mark = m.group(0);
			idRemarkDash++;
			mapRemark.put(id, mark);
			res = res.replace(mark, id);
		}
		res = function.apply(res);
		for(Entry<String,String> e : mapRemark.entrySet()) {
			res = res.replace(e.getKey(), e.getValue());
		}
		res = res
			.replaceAll("(?mi)^((?:\\s*\\+)?\\s*')(\\s*)(--)?", "$2$3 $1 ")
		;
		return res;
	}
	
	public static String markName(String type,int i) {
		return markName(type,Integer.toString(i));
	}
	public static String markName(String type,String i) {
		return "<ConvertRemarkSafely_"+type+"_"+i+">";
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
