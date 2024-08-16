package etec.framework.context.convert_safely.service;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <h1>安全的轉換SQL語句</h1>
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
public class ConvertFunctionsSafely {
	
	public int maxCnt = 0;
	
	public static int maxObjId = 0;
	
	public int objId = 0;
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	
	{
		maxObjId++;
		objId = maxObjId;
		
	}
	
	public String savelyConvert(String script,Function<String, String> function) {
		String res = "";
		int cntBracket = 0;
		maxCnt = 0;
		//排除字串內的括號
		StringBuffer sb = new StringBuffer();
		String regvc = "'[^']*'";
		Matcher mvc = Pattern.compile(regvc).matcher(script);
		while(mvc.find()) {
			String tmp = mvc.group(0)
					.replaceAll("\\(", markName("vc_l",-1))
					.replaceAll("\\)", markName("vc_r",-1))
					.replaceAll(",", markName("vc_c",-1))
				;
			mvc.appendReplacement(sb, Matcher.quoteReplacement(tmp));
		}
		mvc.appendTail(sb);
		//encode
		for(String c : sb.toString().split("")) {
			if( "(".equals(c)) {
				cntBracket++;
				c = markName("leftquater", cntBracket);
				
			}else if(")".equals(c)) {
				
				c = markName("rightquater", cntBracket);
				cntBracket--;
			}else if(",".equals(c)) {
				c = markName("comma", cntBracket);
			}
			if(cntBracket>maxCnt) {
				maxCnt = cntBracket;
			}
			res+=c;
		}
		
		//decode
		for(int i = 0;i<=maxCnt+1;i++) {
			String leftQuaterMark = markName("leftquater", i);
			String rightQuaterMark = markName("rightquater", i);
			String commaMark = markName("comma", i);
			res = function.apply(res);
			res = res
					.replaceAll(leftQuaterMark, "(")
					.replaceAll(rightQuaterMark, ")")
					.replaceAll(commaMark, ",")
					;
			
		}
		
		//解編字串裡的括號
		res = res
				.replaceAll(markName("vc_l",-1),"\\(")
				.replaceAll(markName("vc_r",-1),"\\)")
				.replaceAll(markName("vc_c",-1),",")
			;
		
		if(!res.equals(script)) {
			res = savelyConvert(res,function);
		}
		return res;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	public static String convert(String script,Function<String, String> function) {
		return (new ConvertFunctionsSafely()).savelyConvert(script, function);
	}
	
	protected String markName(String type,int i) {
		return markName(type,Integer.toString(i));
	}
	protected String markName(String type,String i) {
		return "<saveTranslateFunctionMark_"+objId+"_"+type+"_"+i+">";
	}
	protected static String markAllName(String type,String i) {
		return "<saveTranslateFunctionMark_\\d+_"+type+"_"+i+">";
	}
	/**
	 * @author	Tim
	 * @since	2023年12月20日
	 * 
	 * 將字串解編
	 * */
	public static String decodeMark(String script) {
		String res = script
			.replaceAll(markAllName("leftquater","\\d+"), "\\(")
			.replaceAll(markAllName("rightquater","\\d+"), "\\)")
			.replaceAll(markAllName("comma","\\d+"), ",")
		;
		return res;
	}
}
