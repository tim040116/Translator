package etec.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

import etec.common.enums.SQLTypeEnum;
import etec.sql.az.translater.DQLTranslater;
import etec.sql.az.translater.OtherTranslater;

/**
 * @author	Tim
 * @since	2023年11月13日
 * 
 * 轉換SQL的邏輯 * 應盡速搬遷至Transducer層
 * 
 * */
public class TransduceTool {
	
	public static final String SPLIT_CHAR_RED =  "🀄";
	public static final String SPLIT_CHAR_WHITE =  "🀆";
	public static final String SPLIT_CHAR_GREEN =  "🀅";
	public static final String SPLIT_CHAR_BLACK =  "🀫";
	public static final String SPLIT_CHAR_CH_01 =  "蛬";
	//select單純的置換
	@Deprecated
	public static String easyReplaceCreate(String sql) {	
		String res = sql;
		res = res
				//compress
				.replaceAll("COMPRESS[^\\r\\n]*", "")
				.replaceAll(",?\\s*NO FALLBACK", "")
				.replaceAll(",?\\s*NO JOURNAL", "")
				.replaceAll(",?\\s*NO LOG", "")
				.replaceAll(",?\\s*DEFAULT MERGEBLOCKRATIO", "")
				.replaceAll(",?\\s*NO BEFORE JOURNAL", "")
				.replaceAll(",?\\s*NO AFTER JOURNAL", "")
				.replaceAll(",?\\s*CHECKSUM = DEFAULT", "")
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
	@Deprecated
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
	
	// sample
	@Deprecated
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
	@Deprecated
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
	
	//去除註解
	public static String cleanSql(String fc) {
		String res = fc;
		//#
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
	
	/**
	 * @author	Tim
	 * @since	2023年10月4日
	 *   將perl的參數置換到sql語句中
	 * */
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
	
	/**
	 * @author	Tim
	 * @since	2023年10月4日
	 * 
	 * 清除註解
	 * */
	public static String cleanRemark(String sql) {
		String res = sql.replaceAll("\\/\\*", "🀄")
				.replaceAll("\\*\\/", "🀄")
				.replaceAll("🀄[^🀄]*🀄", "")
				.replaceAll("\\-\\-.*", "")
				.replaceAll("\\/\\/.*", "")
				.replaceAll("(\r\n)+", "\r\n")
				.trim();
		return res;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	public static String saveTranslateFunction(String script,Function<String, String> function) {
		String res = "";
		int cntBracket = 0;
		int maxCnt = 0;
		//encode
		for(String c : script.split("")) {
			if( "(".equals(c)) {
				cntBracket++;
				c = "<saveTranslateFunctionMark_leftquater_"+cntBracket+">";
				
			}else if(")".equals(c)) {
				
				c = "<saveTranslateFunctionMark_rightquater_"+cntBracket+">";
				cntBracket--;
			}else if(",".equals(c)) {
				c = "<saveTranslateFunctionMark_comma_"+cntBracket+">";
			}
			if(cntBracket>maxCnt) {
				maxCnt = cntBracket;
			}
			res+=c;
		}
		//decode
		for(int i = 0;i<=maxCnt+1;i++) {
			String leftQuaterMark = "<saveTranslateFunctionMark_leftquater_"+i+">";
			String rightQuaterMark = "<saveTranslateFunctionMark_rightquater_"+i+">";
			String commaMark = "<saveTranslateFunctionMark_comma_"+i+">";
			res = function.apply(res);
			res = res
					.replaceAll(leftQuaterMark, "(")
					.replaceAll(rightQuaterMark, ")")
					.replaceAll(commaMark, ",")
					;
			
		}
		return res;
	}
	
}
