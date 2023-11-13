package etec.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import etec.common.enums.SQLTypeEnum;
import etec.src.transducer.DQLTransducer;
import etec.src.transducer.OtherTransducer;

/**
 * @author	Tim
 * @since	2023年11月13日
 * 
 * 轉換SQL的邏輯 * 應盡速搬遷至Transducer層
 * 
 * */
public class TransduceTool {
	

	
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
	public static final String SPLIT_CHAR_RED =  "🀄";
	public static final String SPLIT_CHAR_WHITE =  "🀆";
	public static final String SPLIT_CHAR_GREEN =  "🀅";
	public static final String SPLIT_CHAR_BLACK =  "🀫";
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
	 * @since	2023年10月4日
	 * 分辨SQL的類型
	 * 
	 * */
	public static SQLTypeEnum getSQLType(String sql) {
		sql = cleanRemark(sql).toUpperCase().trim();
		SQLTypeEnum res = SQLTypeEnum.OTHER;
		
		if(sql.matches("\\s*;?\\s*")) {
			res = SQLTypeEnum.EMPTY;
		}
		else if (sql.matches("(?i)CREATE\\s*(MULTISET|SET)?(\\s+VOLATILE)?\\s+TABLE\\s+[\\S\\s]+")) {
			res = sql.matches("[\\S\\s]*\\s+SELECT\\s+[\\S\\s]*")?SQLTypeEnum.CREATE_INSERT:SQLTypeEnum.CREATE_TABLE;
		}
		else if(sql.matches("(?i)RENAME\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.RENAME_TABLE;
		}
		else if (sql.matches("(?i)DROP\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DROP_TABLE;
		}
		
		
		else if(sql.matches("(?i)LOCK\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.LOCKING;
		}
		else if(sql.matches("(?i)TRUNCATE\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.TRUNCATE_TABLE;
		}
		else if (sql.matches("(?i)MERGE\\s+INTO\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.MERGE_INTO;
		}
		else if (sql.matches("(?i)UPDATE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.UPDATE_TABLE;
		}
		else if (sql.matches("(?i)SELECT\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.SELECT_TABLE;
		}
		else if (sql.matches("(?i)DELETE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DELETE_TABLE;
		}
		else if(sql.matches("(?i)REPLACE\\s+VIEW\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.REPLACE_VIEW; 	
		}
		else if(sql.matches("(?i)COLLECT\\s+STATISTICS\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.COLLECT_STATISTICS;
		}
		else if(sql.matches("(?i)COMMENT\\s+ON\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.COMMENT_ON;
		}
		else if(sql.matches("(?i)INSERT\\s+INTO\\s+[\\S\\s]+")) {
			res = sql.matches("(?i)[\\S\\s]*\\s+SELECT\\s+[\\S\\s]*")?SQLTypeEnum.INSERT_SELECT:SQLTypeEnum.INSERT_TABLE;
		}
		else if(sql.matches("(?i)DROP\\s+VIEW\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DROP_VIEW;
		}
		else if(sql.matches("(?i)DATABASE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DATABASE;
		}
		else if(sql.matches("(?i)LOCKING\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.LOCKING;
		}
		else if(sql.matches("(?i)CALL\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.CALL;
		}
		else if(sql.matches("(?i)COMMIT\\s*;")) {
			res = SQLTypeEnum.COMMIT;
		}
		else if(sql.matches("(?i)BT\\s*;")) {
			res = SQLTypeEnum.BT;
		}
		else if(sql.matches("(?i)ET\\s*;")) {
			res = SQLTypeEnum.ET;
		}
		else if(sql.matches("(?i)EXIT\\s*;")) {
			res = SQLTypeEnum.EXIT;
		}
		else if(sql.matches("(?i)WITH\\s+\\S+\\s+AS\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.WITH;
		}
		else if(sql.matches("(?i)SET\\s+@\\S+\\s*=\\s*'[\\S\\s]+")) {
			res = SQLTypeEnum.SET_EXECUTE;
		}
		else {
			res = SQLTypeEnum.OTHER;
		}
		if(res.equals(SQLTypeEnum.OTHER)) {
			Log.warn("出現無法處理的SQL語句");
			System.out.println(sql+"\r\n");
		}
		return res;
		
	}
	
}
