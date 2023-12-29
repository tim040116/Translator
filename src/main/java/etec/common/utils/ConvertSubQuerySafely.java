package etec.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author	Tim
 * @since	2023年12月26日
 * @version	4.0.0.0
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
 * */
public class ConvertSubQuerySafely {
	
	public static final String SPLIT_CHAR_RED =  "🀄";
	public static final String SPLIT_CHAR_WHITE =  "🀆";
	public static final String SPLIT_CHAR_GREEN =  "🀅";
	public static final String SPLIT_CHAR_BLACK =  "🀫";
	public static final String SPLIT_CHAR_CH_01 =  "蛬";
	
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
			
			//尋找小括號
			String temp = "";
			String tempSub = "";
			Map<String,String> mapSubquery = new HashMap<String,String>();
			boolean isSub = false;
			boolean isQuery = false;
			for(String str : res.split("\\b")) {
				if(str.matches("\\s*\\(\\s*")) {
					isSub = true;
				}
				if(isSub) {
					if(isSub&&str.matches("(?i)\\s*SELECT\\s*")) {
						isQuery = true;
						isSub = false;
						tempSub = str;
					}else {
						isSub = false;
					}
				}
				if(isQuery) {
					if(str.matches("\\s*\\)\\s*")) {
						isQuery = false;
						String id = markName("SubQuery", subQueryId);
						subQueryId++;
						mapSubquery.put(id, tempSub);
						temp += id+str;
					}else {
						tempSub+=str;
					}
					
				}else {
					temp+=str;
				}
			}
			
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
		return "<ConvertSubQuerySafelyMark_"+type+"_"+i+">";
	}
}
