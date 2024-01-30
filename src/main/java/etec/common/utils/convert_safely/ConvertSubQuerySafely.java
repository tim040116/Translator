package etec.common.utils.convert_safely;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import etec.common.utils.Mark;
import etec.common.utils.RegexTool;

/**
 * 
 * <h1>安全的轉換SQL語句</h1>
 * <br>因應SQL語法中很多子查詢及聯立的語法
 * <br>避免在語法拆解的時候因此影響構造的判讀
 * <br>
 * <br>使用savelyConvert()可以安全的進行轉換，
 * <br>拆解每個子查詢再切聯立
 * <br>
 * <br>以確保方法的參數不會受到其他子查詢影響
 * 
 * @author	Tim
 * @since	2023年12月26日
 * @version	4.0.0.0
 * */
public class ConvertSubQuerySafely {
	
	
	
	public static int subQueryId = 0;
	public static int unionQueryId = 0;
	public int maxCnt = 0;
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 *
	 * */
	public String savelyConvert(String script,Function<String, String> function) {
		String res = ConvertFunctionsSafely.decodeMark(script).replaceAll("[\\(\\)]", Mark.MAHJONG_BLACK+"$0"+Mark.MAHJONG_BLACK);
		String subSrc = "";
		String subId = "";
		String fthSrc = "";
		Map<String,String> mapSub = new HashMap<String,String>();
		int intq = 0;
		int issub = 0;
		for(String ch : res.split("\\b")) {
			if(ch.matches(Mark.MAHJONG_BLACK+"+")) {
				continue;
			}
			if("(".equals(ch)) {
				intq++;
				if(intq==1) {
					issub=1;
				}
			}else if(")".equals(ch)) {
				intq--;
				if(issub==2) {
					issub=0;
				}
			}
			if(issub==1) {
				issub=ch.matches("\\s*")?1:ch.matches("(?i)SEL(?:ECT)")?2:0;
				if(issub==2) {
					subId = markName("subQuery",subQueryId);
					fthSrc += subId;
				}
			}
			if(issub==2) {
				subSrc+=ch;
			}else {
				
			}
		}
		return res;
	}
	/**
	 * 處理union的部分
	 * */
	private String savelyConvertUnion(String script,Function<String, String> function) {
		String res = "";
		for(String sub : script.split("(?i)\\b(?=union\\b)")){
			res+=RegexTool.getRegexTargetFirst("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", sub);
			String substr = sub.replaceAll("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", "");
			res += function.apply(substr);
			savelyConvertUnion(res,function);
			
			
			
			
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
	private String savelyConvertJoin(String script,Function<String, String> function) {
		Map<String,String>
	}
	private String savelyConvertUnion(String script,Function<String, String> function) {
		String res = "";
		for(String sub : script.split("(?i)\\b(?=union\\b)")){
			res += RegexTool.getRegexTargetFirst("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", sub);
			String subq = sub.replaceAll("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", "");
			res += function.apply(subq);
		}
		return res;
	}
	protected static String markName(String type,int i) {
		return markName(type,Integer.toString(i));
	}
	protected static String markName(String type,String i) {
		return "_____ConvertSubQuerySafelyMark_"+type+"_"+i+"_____";
	}
}
