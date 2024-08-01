package etec.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.convert_safely.ConvertFunctionsSafely;

/**
 * 正則或類正則相關功能
 * @deprecated	建議改用Matcher
 * @author 	Tim
 * @version	1.2
 * */
public class RegexTool {
	
	/**
	 * 取得函數中的參數 function(param...)中括號中的內容
	 * 
	 * @author	Tim
	 * @since	2023/04/29
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
	/*
	 * @Deprecated	考量到group功能無法使用，此功能效能上有待加強，建議改用JAVA原生Matcher語法
	 * */
	public static List<String> getRegexTarget(String regex, String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		while (m.find()) {
//			for (int i = 0; i <= m.groupCount(); i++) {
//				lstRes.add(m.group());
//			}
			lstRes.add(m.group(0));
		}
		return lstRes;
	}
	/**
	 * <h1>是否包含特定格式</h1>
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * */
	public static boolean contains(String regex, String content) {
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		return m.find();
	}
	public static String getRegexTargetFirst(String regex, String content) {
		String res = "";
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		if (m.find()) {
			res = m.group(0);
		}
		return res;
	}
//	/**
//	 * @author	Tim
//	 * @deprecated
//	 * 
//	 * 取得符合正規表達式的字串(原本的語法有時會出錯)，
//	//盡量不要用(?=)類型的語法
//	 * 
//	 * <br>2023-12-12 Tim 原功能已修復請改用：
//	 * <br> {@link #getRegexTarget(String, String)}
//	 * */
//	public static List<String> getRegexTarget2(String regex, String content) {
//		List<String> lstRes = new ArrayList<String>();
//		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
//		Matcher m = p.matcher(content);
//		while (m.find()) {
//			for (int i = 0; i < m.groupCount(); i++) {
//				lstRes.add(m.group(i));
//			}
//		}
//		return lstRes;
//	}
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

	// 解決$造成比對失敗
	@Deprecated
	public static String encodeSQL(String sql) {
		String res = sql;
		res = res
				.replaceAll("\\/\\*", "<encodingCode_Remark_Begin>")
				.replaceAll("\\*\\/", "<encodingCode_Remark_End>")
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
	@Deprecated
	public static String decodeSQL(String sql) {
		String res = sql;
		res = res
				.replaceAll("<encodingCode_Remark_Begin>","\\/\\*")
				.replaceAll("<encodingCode_Remark_End>","\\*\\/")
				.replaceAll("<encodingCode_Money>", "\\$")
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
	/**
	 * @author	Tim
	 * @since	2023年12月13日
	 * @deprecated
	 * 
	 * <h1>把括弧內的逗號置換掉</h1>
	 * 
	 * 請統一改用<br>
	 *  {@link ConvertFunctionsSafely #saveTranslateFunction(String, Function)}
	 * 
	 * */
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
	/**
	 * @author	Tim
	 * @since	2023年12月13日
	 * @deprecated
	 * 
	 * <h1>把括弧內的逗號置換掉</h1>
	 * 
	 * 請統一改用<br>
	 *  {@link ConvertFunctionsSafely #saveTranslateFunction(String, Function)}
	 * 
	 * */
	public static String decodeCommaInBracket(String context) {
		String res = context.replaceAll("<encodingCode_Comma>", ",");
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月26日
	 * 在特殊符號都加空格的情境下執行
	 * */
	public static String spaceRun(String content,Function<String, String> function) {
		String text = content;
		text = text.replaceAll("([\\(\\)\\+\\=\\,\\'])"," $1 ");
		text = function.apply(text);
		text = text.replaceAll(" ([\\(\\)\\+\\=\\,\\']) ","$1");
		return text;
	}
}
