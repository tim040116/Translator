package etec.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class TransduceTool {
	//select語句轉換
	public static String transduceSelectSQL(String sql) {
		String txt = sql;
		//轉換
		//txt = changeGroupBy(txt);
		txt = easyReplaceSelect(txt);
		//整理 如果有註解會被Mark
		//txt = arrangeSQL(txt);
		//txt = changeGroupBy(txt);
		txt = changeAddMonth(txt);
		txt = changeSample(txt);
		txt = changeZeroifnull(txt);
		txt = changeCharindex(txt);
		txt = changeIndex(txt);
		return txt + "\r\n\r\n";
	}
	//create語句轉換
	public static String transduceCreateSQL(String sql) {
		String txt = sql;
		//轉換
		txt = easyReplaceCreate(txt);
		//txt = changeGroupBy(txt);
		txt = changeAddMonth(txt);
		//整理
		//txt = arrangeSQL(txt);
		return txt + "\r\n\r\n";
	}

	//select單純的置換
	public static String easyReplaceSelect(String sql) {	
		String res = sql;
		res = res
				// ||
				.replaceAll("\\|\\|", "+")
				// SUBSTR
				.replaceAll(RegexTool.getReg("SUBSTR \\("), "SUBSTRING(")
				// CAST(SUBSTR('${LAST01TX4YMB}',1,4)||'-01-01' AS DATE FORMAT 'YYYY-MM-DD')
				//.replaceAll("[Cc][Aa][Ss][Tt] *\\(|[Aa][Ss] *[Dd][Aa][Tt][Ee] *[Ff][Oo][Rr][Mm][Aa][Tt] *'[YyMmDdHhSs-]*'\\)","")
				//oreplace
				.replaceAll(RegexTool.getReg("oreplace\\("), "Replace(")
				//strtok
				.replaceAll(RegexTool.getReg("strtok \\("), "STRING_SPLIT (")
				//rank over
				.replaceAll("(?<!_|[A-Za-z0-9])[Rr][Aa][Nn][Kk]\\((?! |\\))", " RANK ( ) OVER ( order by ")//all
				//extract
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Dd][Aa][Yy] *[Ff][Rr][Oo][Mm]", "DatePart(day ,")//all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Mm][Oo][Nn][Tt][Hh] *[Ff][Rr][Oo][Mm]", "DatePart(month ,")//all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Yy][Ee][Aa][Rr] *[Ff][Rr][Oo][Mm]", "DatePart(year ,")//all
				;
		
		return res;
	}
	//select單純的置換
		public static String easyReplaceCreate(String sql) {	
			String res = sql;
			res = res
					//compress
					.replaceAll("COMPRESS[^\\r\\n]*", "")
					.replaceAll(RegexTool.getReg(",? NO FALLBACK ,?")+"[^\\r\\n]*\\r\\n", "")
					.replaceAll(RegexTool.getReg(",? NO BEFORE JOURNAL ,?")+"[^\\r\\n]*\\r\\n", "")
					.replaceAll(RegexTool.getReg(",? NO AFTER JOURNAL ,?")+"[^\\r\\n]*\\r\\n", "")
					.replaceAll(RegexTool.getReg(",? CHECKSUM = DEFAULT ,?")+"[^\\(]*\\r\\n", "")
					//CHARACTER SET
					.replaceAll(RegexTool.getReg("CHARACTER SET ")+"\\w+", "")
					//區分大小寫
					.replaceAll(RegexTool.getReg(" NOT CASESPECIFIC"), "")
					.replaceAll(RegexTool.getReg(" CASESPECIFIC"), " Collate Chinese_Taiwan_Stroke_CI_AS")
					//title
					.replaceAll(RegexTool.getReg("Title ")+"[^\\r\\n]*", "")
					//PARTITION BY RANGE_N
					.replaceAll(RegexTool.getReg("PARTITION BY RANGE_N\\([^\\)]*\\)"),"")
					//rank over
					.replaceAll("(?<!_|[A-Za-z0-9])[Rr][Aa][Nn][Kk]\\((?! |\\))", " RANK ( ) OVER ( order by ")//all
					//extract
					.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Dd][Aa][Yy] *[Ff][Rr][Oo][Mm]", "DatePart(day ,")//all
					.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Mm][Oo][Nn][Tt][Hh] *[Ff][Rr][Oo][Mm]", "DatePart(month ,")//all
					.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Yy][Ee][Aa][Rr] *[Ff][Rr][Oo][Mm]", "DatePart(year ,")//all
					;
			
			return res;
		}
	// 置換 group by 語法
	// 考慮到無法解決子查詢的轉換，暫時廢棄
	public static String changeGroupBy(String sql) {
		String res = "";
		// 沒有group by 就直接跳過
		if (!sql.matches("[\\S\\s]*[Gg][Rr][Oo][Uu][Pp] *[Bb][Yy][0-9, ]*[\\S\\s]*")) {
			return sql;
		}
		// 拆分每個詞
		List<String> lst = RegexTool.getSingleWord(sql);
		// 是否有子查詢
		if (sql.matches("[\\S\\s]*\\(\\s*[Ss][Ee][Ll][Ee][Cc][Tt][\\S\\s]*")) {
			boolean flag = false;
			String temp = "";
			int flag2 = 0;
			// 找尋子查詢
			for (int i = 0; i < lst.size(); i++) {
				String data = lst.get(i);
				String data2 = "";
				if(i+1 < lst.size()) {
					data2 = lst.get(i+1);
				}
				// 為子查詢時開始錄製
				if ((!flag) &&(data.matches(RegexTool.getReg("\\(select"))||(data.equals("(") && data2.matches(RegexTool.getReg("select"))))) {
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
		List<String> lst2 = RegexTool.getSingleWord(res);
		boolean flag3 = false;
		String strSelect = "";
		for(String str : lst2) {
			if(str.toUpperCase().equals("FROM")) {
				flag3 = false;
				break;
			}
			if(flag3) {
				strSelect += str+" ";
			}
			if(str.toUpperCase().equals("SELECT")) {
				strSelect = "";
				flag3 = true;
			}
		}
		String[] arColum = strSelect.split(",(?!('\\||(\\w|,)*\\)))");
		for(int i=0;i<arColum.length;i++) {
			arColum[i] = arColum[i].replaceAll(" *[Aa][Ss] *[\\w\\s]*", "");
		}
		//取得group by
		List<String> lstGroupBy = RegexTool.getRegexTarget2("[Gg][Rr][Oo][Uu][Pp] *[Bb][Yy] *[0-9]+[0-9, ]*",res);
		for(String gb : lstGroupBy) {
			String[] argb = gb.replaceAll(RegexTool.getReg("group by"),"").replaceAll(" ", "").split(",");
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
		/*20220613  去除分行符號? */
		//20220613 String res = sql.replaceAll("\\s+", " ");
		String res = sql.trim();
		
		//20220613 List<String> lst = RegexTool.getRegexTarget2("[Aa][Dd]{2}_[Mm][Oo][Nn][Tt][Hh][Ss]\\([^\\)]*\\) *,(-?[0-9]*) *\\)",res);
		List<String> lst = RegexTool.getRegexTarget2("[Aa][Dd][Dd]_[Mm][Oo][Nn][Tt][Hh][Ss]\\([^\\)]*\\)?,(-?[0-9]*) *\\)",res);
		for(String str : lst) {
			String[] param =str.replaceAll(RegexTool.getReg("add_Months\\(|\\)$"), "").split(",");
			res = RegexTool.encodeSQL(res);
			String oldstr = RegexTool.encodeSQL(str);
 			String newstr = RegexTool.encodeSQL("DateAdd(MONTH,"+param[1].trim()+","+param[0].trim()+")");
			res = res.replaceAll(oldstr,newstr);
			res = RegexTool.decodeSQL(res);
		}
		
		//20220613 return arrangeSQL(res);
		return res;
	}
	// sample
	public static String changeSample(String selectSQL) {
		String result = selectSQL;
		//取得sample
		List<String> lstSample = RegexTool.getRegexTarget("[Ss][Aa][Mm][Pp][Ll][Ee] +\\d+\\s*;",selectSQL);
		//是否存在sample
		if(lstSample.isEmpty()) {
			return selectSQL;
		}
		String sample = " SELECT TOP " + RegexTool.getRegexTarget("\\d+",lstSample.get(0)).get(0) +" ";
		result = result
				.replaceFirst("[Ss][Ee][Ll][Ee][Cc][Tt]", sample)
				.replaceAll("[Ss][Aa][Mm][Pp][Ll][Ee] +\\d+\\s*;", ";");
		return result;
	}
	// char index
	public static String changeCharindex(String selectSQL) {
		String result = RegexTool.encodeSQL(selectSQL);
		//取得sample
		List<String> lstSQL = RegexTool.getRegexTarget("[Ii][Nn][Dd][Ee][Xx]<encodingCode_ParentBracketLeft>[^,]+, *\\'[^\\']+\\'",result);
		for(String data : lstSQL) {
			String oldData = data;
			String param = data.replaceAll("[Ii][Nn][Dd][Ee][Xx]<encodingCode_ParentBracketLeft>","");
			String[] ar = param.split(",");
			String newData = "CHARINDEX<encodingCode_ParentBracketLeft>"+ar[1]+","+ar[0];
			result = result.replaceAll(oldData, newData);
		}
		return RegexTool.decodeSQL(result);
	}
	// zeroifnull
	public static String changeZeroifnull(String selectSQL) {
		String result = selectSQL;
		//取得sample
		result = result.replaceAll("(?<=zeroifnull\\(.{0,100})\\) +as ",",0) as ");
		result = result.replaceAll(RegexTool.getReg("zeroifnull \\("),"ISNULL(");
		return result;
	}
	// index
	public static String changeIndex(String sql) {
		String result = sql;
		//取得sample
		List<String> lstIndex = RegexTool.getRegexTarget("(?<=[, ])[Ii][Nn][Dd][Ee][Xx][^\\)]+",result);
		//是否存在sample
		if(lstIndex.isEmpty()) {
			return sql;
		}
		for(String data : lstIndex) {
			String upper = data.toUpperCase();
			if(upper.contains("COLLECT STATISTICS ON")
					||upper.contains("PRIMARY")
					||upper.contains("UNIQUE")) {
				continue;
			}
			List<String>lstP = RegexTool.getRegexTarget("(?<=[Ii][Nn][Dd][Ee][Xx]\\s{0,10}\\()[^\\)]+",data);
			if(lstP.isEmpty()) {
				continue;
			}
			String params = lstP.get(0);
			String[] arp = params.split(",");
			if(arp.length!=2) {
				continue;
			}
			String index = " CHARINDEX("+arp[1]+","+arp[0];
			String reg = RegexTool.encodeSQL(data);
			result = RegexTool.encodeSQL(result).replaceAll(reg,RegexTool.encodeSQL(index));
		}
		result = RegexTool.decodeSQL(result);
		return result;
	}
	//整理SQL
	//效果不好，已廢棄
	public static String arrangeSQL(String sql) {
		String res = "";
		String space = "";
		String spaceItem = "    ";
		List<String> lst = RegexTool.getSingleWord(sql);
		for(int i=0;i<lst.size();i++) {
			String str = lst.get(i);
			if(str.matches(RegexTool.getReg("select"))) {
				String tmp = "\r\n" + space + "select";
				i++;
				while(!lst.get(i).matches(RegexTool.getReg("from"))) {
					if(lst.get(i).substring(0,1).equals(",")) {
						tmp += "\r\n" + space + spaceItem + lst.get(i);
					}else{
						tmp += " "+lst.get(i);
					}
					i++;
					if(i>=lst.size()) {
						break;
					}
				}
				res += tmp + "\r\n" + space + "from";
			}else if(str.matches(RegexTool.getReg("where|and|on|group|order|union|sample"))) {
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
				if(lst.get(i+1).matches(RegexTool.getReg("join"))){
					if(str.matches(RegexTool.getReg("inner|left|right|full|cross|outer"))) {
						res += "\r\n" + space + str;
					}else {
						res += " " + str + "\r\n"; 
					}
				}
			}
		}
		res = res
				.replaceAll(" (?=#)", "\r\n")
				.replaceAll(";", ";\r\n")
				.replaceAll("# ", "#\r\n")
				;
		return res;
	}
	//去除註解
	public static String cleanSql(String fc) {
		String res = fc;
		//#
		System.out.println("cleanSql start");
		res = res.replaceAll("(?<='[^']{0,10})#(?=[^']{0,10}')", "<encodingCode_HashTag>");
		res = res.replaceAll("#.*","");
		res = res.replaceAll("<encodingCode_HashTag>","#");
		// //
		res = res.replaceAll("\\/\\/.*", "");
		// /**/
		res = res.replaceAll("\\/\\*.*\\*\\/","");
//		res = res.replaceAll("\\/\\*+([^\\/]|[^\\*]\\/)*\\*+\\/","");
//		System.out.println("/**/ s");
		// --
		res = res.replaceAll("--.*","");
		// /* \r\n*/
//		res = res.replaceAll("(#.*)|(\\/\\*.*\\*\\/)","");
//		res = res.replaceAll("'#'","QqAaZz").replaceAll("(#.*)|(\\/\\*.*\\*\\/)","");
//		res = res.replaceAll("QqAaZz","'#'");
		String sql = "";
		boolean es = false;
		for(String line : res.split("\r\n")) {
			if(line.trim().equals("")) {
				continue;
			}
			// /* \r\n */
			if(line.matches(".*\\/\\*.*")) {
				line = line.replaceAll("\\/\\*.*", "");
				es = true;
			}
			if(es) {
				if(line.matches(".*\\*\\/.*")) {
					line = line.replaceAll(".*\\*\\/", "");
					es = false;
				}else {
					continue;
				}
			}
//			if(line.trim().substring(0, 1).equals(".")) {
//				line = line + ";";
//			}
			sql += line+"\r\n";
		}
		res = sql;
		return res;
	}
	
	//將perl的參數置換到sql語句中
	public static String replaceParams(String fc) {
		String result = RegexTool.encodeSQL(fc);
		//列出參數清單
		List<String> paramList = RegexTool.getRegexTarget("(?<=my\\s{0,10}\\$)[^=\\s]+\\s*=\\s*\\$ENV[^;]+",fc);
		Map<String,String> paramMap = new HashMap<String,String>();
		//把參數加到map
		for(String param : paramList) {
			String[] arparam = param.split("=");
			String paramNm   = "${"+arparam[0].trim()+"}";
			String paramVal  = arparam[1].replaceAll("(ENV)|\"", "").trim();
			paramMap.put(RegexTool.encodeSQL(paramNm), RegexTool.encodeSQL(paramVal));
		}
		//置換參數
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			result = result.replaceAll(entry.getKey(), entry.getValue());
		}
		return RegexTool.decodeSQL(result);
	}
	
}
