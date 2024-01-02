package etec.common.utils;

import java.util.function.Function;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * <h1>安全的轉換SQL語句</h1>
 * <br>因應SQL語法中很多在方法中包覆其他方法的語法
 * <br>避免在取代的時候受到小括號跟逗號的影響導致轉換錯誤
 * <br>
 * <br>使用savelyConvert()可以安全的進行轉換，
 * <br>會對特殊符號進行轉換，
 * <br>在從外圍依序還原符號，
 * <br>以確保方法的參數不會受到其他小括號及逗號影響
 * 
 * */
public class ConvertFunctionsSafely {
	
	public int maxCnt = 0;
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	public String savelyConvert(String script,Function<String, String> function) {
		String res = "";
		int cntBracket = 0;
		maxCnt = 0;
		//encode
		for(String c : script.split("")) {
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
		if(!res.equals(script)) {
			res = savelyConvert(res,function);
		}
		return res;
	}
	protected static String markName(String type,int i) {
		return markName(type,Integer.toString(i));
	}
	protected static String markName(String type,String i) {
		return "<saveTranslateFunctionMark_"+type+"_"+i+">";
	}
	/**
	 * @author	Tim
	 * @since	2023年12月20日
	 * 
	 * 將字串解編
	 * */
	public static String decodeMark(String script) {
		String res = script
			.replaceAll(markName("leftquater","\\d+"), "\\(")
			.replaceAll(markName("rightquater","\\d+"), "\\)")
			.replaceAll(markName("comma","\\d+"), ",")
		;
		return res;
	}
}
