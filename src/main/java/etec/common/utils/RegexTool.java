package etec.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正則或類正則相關功能
 * @author 	Tim
 * @version	1.2
 * */
public class RegexTool {
	
	/**
	 * 取得函數中的參數 function(param...)中括號中的內容
	 * 
	 * @author	Tim
	 * @since	2023/04/29
	 * @
	 * @param	functionNm	括號前面的象徵性字串
	 * @param	context		文章
	 * @return	String		括號中的內容
	 * */
	public static String getFunction(String functionNm,String context) {
		String res = "";
		String temp = "";
		int	bracketCnt = -1;
		for(String c : context.split("")) {
			temp+=c;
			if(temp.toUpperCase().replaceAll("\\s+", " ").contains((functionNm+" (").toUpperCase().replaceAll("\\s+", " "))) {
				bracketCnt = 1;
			}
			if(c.equals("(")) {
				bracketCnt++;
			}else if(c.equals(")")) {
				bracketCnt--;
			}
			if(bracketCnt==0) {
				break;
			}
			res+=c;
		}
		return res;
	}
	// 取得符合正規表達式的字串
	//盡量不要用(?=)類型的語法
	public static List<String> getRegexTarget(String regex, String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				lstRes.add(m.group(i));
			}
		}
		return lstRes;
	}
	public static String getRegexTargetFirst(String regex, String content) {
		String res = null;
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		while (m.find()) {
			res = m.group(0);
		}
		return res;
	}
	// 取得符合正規表達式的字串(原本的語法有時會出錯)
	//盡量不要用(?=)類型的語法
	public static List<String> getRegexTarget2(String regex, String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		while (m.find()) {
			for (int i = 0; i < m.groupCount(); i++) {
				lstRes.add(m.group(i));
			}
		}
		return lstRes;
	}
	// 拆分成每個單詞
	public static List<String> getSingleWord(String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile("\\S+");
		Matcher m = p.matcher(content);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				lstRes.add(m.group(i));
			}
		}
		return lstRes;
	}
	// 正則表達式不區分大小寫
	public static String getReg(String str) {
		String[] ar = str.split("");
		String res = "";
		for (String s : ar) {
			if (s.matches("[A-Za-z]")) {
				res += "[" + s.toUpperCase() + s.toLowerCase() + "]";
			} else if (s.matches(" ")) {
				res += " *";
			} else {
				res += s;
			}
		}
		return res;
	}

	// 解決$造成比對失敗
	public static String encodeSQL(String sql) {
		String res = sql;
		res = res
				.replaceAll("\\$", "<encodingCode_Money>")
				.replaceAll("\\.", "<encodingCode_Node>")
				.replaceAll("\\?", "<encodingCode_QuestionMark>")
				.replaceAll("\\*", "<encodingCode_Star>")
				.replaceAll("\\#", "<encodingCode_HashTag>")
				.replaceAll("\\+", "<encodingCode_Equals>")
				.replaceAll("\\!", "<encodingCode_ExclamationMark>")
				.replaceAll("\\%", "<encodingCode_Persent>")
				.replaceAll("\\^", "<encodingCode_Caret>")
				.replaceAll("\\/", "<encodingCode_Slash>")
				//.replaceAll("\\\\", "<encodingCode_BackSlash>")
				.replaceAll("\\|", "<encodingCode_VerticalBar>")
				.replaceAll("\\{", "<encodingCode_CurlyBracketLeft>")
				.replaceAll("\\}", "<encodingCode_CurlyBracketRight>")
				.replaceAll("\\[", "<encodingCode_SquareBracketLeft>")
				.replaceAll("\\]", "<encodingCode_SquareBracketRight>")
				.replaceAll("\\(", "<encodingCode_ParentBracketLeft>")
				.replaceAll("\\)", "<encodingCode_ParentBracketRight>")
				
				;
		return res;
	}

	public static String decodeSQL(String sql) {
		String res = sql;
		res = res.replaceAll("<encodingCode_Money>", "\\$")
				.replaceAll("<encodingCode_Node>", ".")
				.replaceAll("<encodingCode_QuestionMark>", "?")
				.replaceAll("<encodingCode_Star>", "*")
				.replaceAll("<encodingCode_HashTag>","#")
				.replaceAll("<encodingCode_Equals>","+")
				.replaceAll("<encodingCode_ExclamationMark>","!")
				.replaceAll("<encodingCode_Persent>","%")
				.replaceAll("<encodingCode_Caret>","^")
				.replaceAll("<encodingCode_Slash>","/")
				//.replaceAll("<encodingCode_BackSlash>","\\\\")
				.replaceAll("<encodingCode_VerticalBar>","|")
				.replaceAll("<encodingCode_CurlyBracketLeft>", "{")
				.replaceAll("<encodingCode_CurlyBracketRight>", "}")
				.replaceAll("<encodingCode_SquareBracketLeft>", "[")
				.replaceAll("<encodingCode_SquareBracketRight>", "]")
				.replaceAll("<encodingCode_ParentBracketLeft>", "(")
				.replaceAll("<encodingCode_ParentBracketRight>", ")");
		return res;
	}
	//檔案路徑的置換
	public static String replaceRootPath(String path,String root) {
		String eroot = encodeSQL(root).replaceAll("\\\\", "<encodingCode_BackSlash>");
		String epath = encodeSQL(path).replaceAll("\\\\", "<encodingCode_BackSlash>");
		String tmp = epath.replaceAll(eroot, "");
		return decodeSQL(tmp).replaceAll("<encodingCode_BackSlash>","\\\\");
	}
	//把括弧內的逗號置換掉
	public static String encodeCommaInBracket(String context) {
		String res = "";
		int cntBracket = 0;
		for(String c : context.split("")) {
			if("(".equals(c)) {
				cntBracket++;
			}
			else if(")".equals(c)) {
				cntBracket--;
			}
			if(",".equals(c)&&cntBracket!=0) {
				c="<encodingCode_Comma>";
			}
			res+=c;
		}
		return res;
	}
	public static String decodeCommaInBracket(String context) {
		String res = context.replaceAll("<encodingCode_Comma>", ",");
		return res;
	}
}
