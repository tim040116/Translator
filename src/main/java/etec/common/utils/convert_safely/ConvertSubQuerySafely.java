package etec.common.utils.convert_safely;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

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
	
	
	
	public static long subQueryId = 0;
	public static long unionQueryId = 0;
	public long maxCnt = 0;
	/**
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
	 * @since	4.0.0.0
	 * 	
	 *
	 * */
	public String savelyConvert(String script,Function<String, String> function) {
		if(RegexTool.getRegexTarget("(?i)\\(\\s*SEL(?:ECT)?", script).isEmpty()) {
			return savelyConvertUnion(script, function);
		}
		String res = ConvertFunctionsSafely.decodeMark(script)
				.replaceAll("(?i)\\bON\\(","ON (")
				.replaceAll("[\\(\\)]", Mark.CH_01+"$0"+Mark.CH_01)
				;
		String subSrc = "";
		String fthSrc = "";
		String subId = "";
		Map<String,String> mapSub = new HashMap<String,String>();
		int intq = 0;
		int issub = 0;
		for(String ch : res.split("\\b")) {
			//清除記號
			ch = ch.replaceAll(Mark.CH_01, "");
			//計算括號
			if("(".equals(ch)) {
				intq++;
				if(intq==1) {
					issub=1;
				}
			}else if(")".equals(ch)) {
				intq--;
				if(issub==2) {
					mapSub.put(subId, subSrc);
					subSrc = "";
					issub = 0;
					continue;
				}
			}
			if(issub==1) {//遇到括號，判斷是否有SELECT
				if("".equals(ch.trim())||ch.matches("[\\(\\)]")) {
					issub=1;
				}
				else if(ch.matches("(?i)SEL(?:ECT)?")) {
					issub=2;
				}
				else {
					issub=0;
				}
//				issub="".equals(ch.trim())?1:ch.matches("(?i)SEL(?:ECT)?")?2:0;
				if(issub==2) {
					subId = markName("subQuery",subQueryId);
					subQueryId++;
					fthSrc = fthSrc.replaceAll("\\((\\s*)$","$1") + subId;
				}
			}
			if(issub==2) {
				subSrc += ch;
			}else {
				fthSrc += ch;
			}
		}
		//處理父查詢
		fthSrc = savelyConvertUnion(fthSrc,function);
		//處理子查詢
		for(Entry<String,String> e : mapSub.entrySet()) {
			e.setValue(savelyConvert(e.getValue(),function));
		}
		//合併
		for(Entry<String,String> e : mapSub.entrySet()) {
			fthSrc = fthSrc.replace(e.getKey(),"("+ e.getValue()+")");
		}
		
		return fthSrc;
	}
	/**
	 * 處理union的部分
	 * */
	private String savelyConvertUnion(String script,Function<String, String> function) {
		String res = "";
//		if(script.matches("(?i).*\\bUNION\\b.*")) {
//			return function.apply(script);
//		}
		for(String sub : script.split("(?i)\\b(?=union\\b)")){
			res += RegexTool.getRegexTargetFirst("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", sub);
			String subq = sub.replaceAll("(?i)^\\s+UNION(?:\\s+ALL)?\\s+", "");
			res += function.apply(subq);
		}
		return res;
	}
	protected static String markName(String type,long i) {
		return markName(type,Long.toString(i));
	}
	protected static String markName(String type,String i) {
		return "_____ConvertSubQuerySafelyMark_"+type+"_"+i+"_____";
	}
}
