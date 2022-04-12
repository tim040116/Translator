package src.java.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransduceTool {
	//select語句轉換
	public static String selectSQLTransduce(String sql) {
		String txt = sql;
		//轉換
		txt = TransduceTool.changeGroupBy(txt);
		txt = TransduceTool.changeAddMonth(txt);
		txt = TransduceTool.easyReplace(txt);
		//整理
		txt = TransduceTool.arrangeSQL(txt);
		return txt + "\r\n\r\n";
	}
	
	
	
	
	
	//單純的置換
	public static String easyReplace(String sql) {	
		String res = sql;
		res = res
				.replaceAll("\\|\\|", "+")
				.replaceAll(getReg("SUBSTR"), "SUBSTRING")
				;
		
		return res;
	}
	// 取得符合正規表達式的字串
	public static List<String> getRegexTarget(String regex, String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
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

	// 置換 group by 語法
	public static String changeGroupBy(String sql) {
		String res = "";
		// 沒有group by 就直接跳過
		if (!sql.matches("[\\S\\s]*[Gg][Rr][Oo][Uu][Pp] *[Bb][Yy][0-9, ]*[\\S\\s]*")) {
			return sql;
		}
		// 拆分每個詞
		List<String> lst = getSingleWord(sql);
		// 是否有子查詢
		if (sql.matches("[\\S\\s]*\\(\\s*[Ss][Ee][Ll][Ee][Cc][Tt][\\S\\s]*")) {
			boolean flag = false;
			String temp = "";
			int flag2 = 0;
			// 找尋子查詢
			for (int i = 0; i < lst.size(); i++) {
				// 為子查詢時開始錄製
				if ((!flag) && lst.get(i).equals("(") && lst.get(i + 1).matches(getReg("select"))) {
					temp = "";
					flag = true;
					flag2 = 1;
					i++;
					res +="( ";
				}
				// 開始錄製
				if (flag) {
					// 數括號
					flag2 += lst.get(i).equals("(") ? 1 : lst.get(i).equals(")") ? -1 : 0;
					// 結束
					if (flag2 == 0) {
						flag = false;
						res += changeGroupBy(temp) + " ) ";
						continue;
					}
					// 繼續
					temp += lst.get(i) + " ";
				} else {
					res += lst.get(i) + " ";
				}
			}
		} else {
			res = sql;
		}
		// 轉換group by
		//取得select項目
		List<String> lst2 = getSingleWord(res);
		boolean flag3 = false;
		String strSelect = "";
		for(String str : lst2) {
			if(str.toUpperCase().equals("FROM")) {
				flag3 = false;
			}
			if(flag3) {
				strSelect += str+" ";
			}
			if(str.toUpperCase().equals("SELECT")) {
				strSelect = "";
				flag3 = true;
			}
		}
		String[] arColum = strSelect.split(",(?!('\\||\\w*\\)))");
		for(int i=0;i<arColum.length;i++) {
			arColum[i] = arColum[i].replaceAll(" *[Aa][Ss] *[\\w\\s]*", "");
		}
		//取得group by
		List<String> lstGroupBy = getRegexTarget("[Gg][Rr][Oo][Uu][Pp] *[Bb][Yy] *[0-9]+[0-9, ]*",res);
		for(String gb : lstGroupBy) {
			String[] argb = gb.replaceAll(getReg("group by"),"").replaceAll(" ", "").split(",");
			String newgb = "group by ";
			for(String strid : argb) {
				int id = Integer.parseInt(strid);
				if(arColum[id-1].matches("'\\w*'")) {
					continue;
				}
				if(!newgb.equals("group by ")) {
					newgb +=",";
				}
				newgb += arColum[id-1];
			}
			res = res.replaceAll(gb, Matcher.quoteReplacement(newgb));
		}
		return res;
	}
	//AddMonth修改
	public static String changeAddMonth(String sql) {
		if(!sql.matches("[\\S\\s]*[Aa][Dd]{2}_[Mm][Oo][Nn][Tt][Hh][Ss]\\([^\\)]*\\)[\\S\\s]*")) {
			return sql;
		}
		
		String res = sql;
		List<String> lst = getRegexTarget("[Aa][Dd]{2}_[Mm][Oo][Nn][Tt][Hh][Ss]\\([^\\)]*\\)",res);
		for(String str : lst) {
			String[] param = getRegexTarget("(?<=\\()[^\\)]*[^\\)]",str).get(0).split(",");
			res = encodeSQL(sql);
			String oldstr = encodeSQL(str);
			String newstr = "DateAdd(MONTH,"+param[1].trim()+","+param[0].trim()+")";
			res = res.replaceAll(oldstr,encodeSQL(newstr));
			res = decodeSQL(res);
		}
		return res;
	}
	
	//整理SQL
	public static String arrangeSQL(String sql) {
		String res = "";
		String space = "";
		String spaceItem = "    ";
		List<String> lst = getSingleWord(sql);
		for(int i=0;i<lst.size();i++) {
			String str = lst.get(i);
			if(str.matches(getReg("select"))) {
				String tmp = "\r\n" + space + "select";
				i++;
				while(!lst.get(i).matches(getReg("from"))) {
					if(lst.get(i).substring(0,1).equals(",")) {
						tmp += "\r\n" + space + spaceItem + lst.get(i);
					}else{
						tmp += " "+lst.get(i);
					}
					i++;
				}
				res += tmp + "\r\n" + space + "from";
			}else if(str.matches(getReg("where|and|on|group|order|union|sample"))) {
				res += "\r\n"+space+str;
			}else if(str.equals("(")){
				res += " (";
				space += spaceItem;
			}else if(str.equals(")")){
				space = space.replaceFirst(spaceItem,"");
				res += "\r\n"+space+")";
			}else {
				res += " " + str;
			}
			if(i<lst.size()-1) {
				if(lst.get(i+1).matches(getReg("join"))){
					if(str.matches(getReg("inner|left|right|full|cross|outer"))) {
						res += "\r\n" + space + str;
					}else {
						res += " " + str + "\r\n"; 
					}
				}
			}
		}
		return res;
	}
	//正則表達式不區分大小寫
	private static String getReg(String str) {
		String[] ar = str.split("");
		String res = "";
		for(String s : ar) {
			if(s.matches("[A-Za-z]")) {
				res+="["+s.toUpperCase()+s.toLowerCase()+"]";
			}else if(s.matches(" ")){
				res+=" *";
			}else {
				res+=s;
			}
		}
		return res;
	}
	//解決$造成比對失敗
	public static String encodeSQL(String sql) {
		String res = sql;
		res = res
				.replaceAll("\\$", "SsLlIi")
				.replaceAll("\\.", "PpNnTt")
				.replaceAll("\\?", "QqTtMm")
				.replaceAll("\\*", "SsTtRr")
				.replaceAll("\\{", "LlBbBb")
				.replaceAll("\\}", "RrBbBb")
				.replaceAll("\\[", "LlMmBb")
				.replaceAll("\\]", "RrMmBb")
				.replaceAll("\\(", "LlSsBb")
				.replaceAll("\\)", "RrSsBb")
				;
		return res;
	}
	public static String decodeSQL(String sql) {
		String res = sql;
		res = res
				.replaceAll("SsLlIi", "\\$")
				.replaceAll("PpNnTt", ".")
				.replaceAll("QqTtMm", "?")
				.replaceAll("SsTtRr", "*")
				.replaceAll("LlBbBb", "{")
				.replaceAll("RrBbBb", "}")
				.replaceAll("LlMmBb", "[")
				.replaceAll("RrMmBb", "]")
				.replaceAll("LlSsBb", "(")
				.replaceAll("RrSsBb", ")")
				;
		return res;
	}
}
