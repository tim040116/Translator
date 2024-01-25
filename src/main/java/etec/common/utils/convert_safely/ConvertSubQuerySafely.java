package etec.common.utils.convert_safely;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

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
		String res = "";
		int cntBracket = 0;
		maxCnt = 0;
		//將小括號跟逗號依範圍加密
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
		//依次解密小括號
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
				if(isSub) {//找到sub query的起頭
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
		return "<ConvertSubQuerySafelyMark_"+type+"_"+i+">";
	}
}
