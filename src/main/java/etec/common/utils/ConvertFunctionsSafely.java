package etec.common.utils;

import java.util.function.Function;

public class ConvertFunctionsSafely {
	
	public static final String SPLIT_CHAR_RED =  "🀄";
	public static final String SPLIT_CHAR_WHITE =  "🀆";
	public static final String SPLIT_CHAR_GREEN =  "🀅";
	public static final String SPLIT_CHAR_BLACK =  "🀫";
	public static final String SPLIT_CHAR_CH_01 =  "蛬";
	
	public int maxCnt = 0;
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	public String saveTranslateFunction(String script,Function<String, String> function) {
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
			res = saveTranslateFunction(res,function);
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
