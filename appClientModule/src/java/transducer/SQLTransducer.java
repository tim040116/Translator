package src.java.transducer;

import java.util.List;

import etec.common.utils.RegexTool;

public class SQLTransducer {
	// 去除註解
	public static String cleanSql(String fc) {
		String res = fc;
		// #
//			System.out.println("cleanSql start");
//			res = res.replaceAll("(?<='[^']{0,10})#(?=[^']{0,10}')", "<encodingCode_HashTag>");
//			res = res.replaceAll("#.*", "");
//			res = res.replaceAll("<encodingCode_HashTag>", "#");
		// //
//			res = res.replaceAll("\\/\\/.*", "");
		// /**/
		res = res.replaceAll("\\/\\*.*\\*\\/", "");
//				res = res.replaceAll("\\/\\*+([^\\/]|[^\\*]\\/)*\\*+\\/","");
//				System.out.println("/**/ s");
		// --
		res = res.replaceAll("--.*", "");
		// /* \r\n*/
//				res = res.replaceAll("(#.*)|(\\/\\*.*\\*\\/)","");
//				res = res.replaceAll("'#'","QqAaZz").replaceAll("(#.*)|(\\/\\*.*\\*\\/)","");
//				res = res.replaceAll("QqAaZz","'#'");
		String sql = "";
		boolean es = false;
		for (String line : res.split("\r\n")) {
			if (line.trim().equals("")) {
				continue;
			}
			// /* \r\n */
			if (line.matches(".*\\/\\*.*")) {
				line = line.replaceAll("\\/\\*.*", "");
				es = true;
			}
			if (es) {
				if (line.matches(".*\\*\\/.*")) {
					line = line.replaceAll(".*\\*\\/", "");
					es = false;
				} else {
					continue;
				}
			}
//					if(line.trim().substring(0, 1).equals(".")) {
//						line = line + ";";
//					}
			sql += line + "\r\n";
		}
		res = sql;
		return res;
	}

	/**
	 * 轉換decode語法
	 * 
	 * azure 不支援decode語法
	 *於是要改成case when
	 *
	 * TD語法:
	 * 	decode(target_col,condition,col,def_col)
	 * az語法:
	 * 	CASE WHEN target_col = condition THEN col ELSE def_col END
	 * 
	 * @author	Tim
	 * @since	2022/05/05
	 * @param	String	SQL語句
	 * @return	String	轉換後的SQL語句
	 * */
	public static String easyReplaceSelect(String sql) {
		String res = sql;
		res = res
				// ||
				.replaceAll("\\|\\|", "+")
				// SUBSTR
				.replaceAll("SUBSTR\\s*\\(", "SUBSTRING(")
				// oreplace
				.replaceAll("OREPLACE\\s*\\(", "REPLACE(")
				// strtok
				.replaceAll("STRTOK\\s*\\(", "STRING_SPLIT(")
				//NVL
				.replaceAll("NVL\\s*\\(", "ISNULL(")
				//truncCAST(A.TIME_RANGE/10000 AS INTEGER)
				.replaceAll("TRUNC\\((.*?)(,\\s*0)?\\)", "CAST($1 AS INTEGER)")
				//truncCAST(A.TIME_RANGE/10000 AS INTEGER)
				.replaceAll("TO_NUMBER\\((.*?)\\)", "CAST($1 AS INTEGER)")
				// rank over
				.replaceAll("(?<!_|[A-Za-z0-9])[Rr][Aa][Nn][Kk]\\((?! |\\))", " RANK ( ) OVER ( order by ")// all
				// extract
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Dd][Aa][Yy] *[Ff][Rr][Oo][Mm]", "DatePart(day ,")// all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Mm][Oo][Nn][Tt][Hh] *[Ff][Rr][Oo][Mm]",
						"DatePart(month ,")// all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Yy][Ee][Aa][Rr] *[Ff][Rr][Oo][Mm]", "DatePart(year ,")// all
		;
		res = convertDecode(res);
		return res;
	}
	/**
	 * 轉換decode語法
	 * 
	 * azure 不支援decode語法
	 *於是要改成case when
	 *
	 * TD語法:
	 * 	decode(target_col,condition,col,def_col)
	 * az語法:
	 * 	CASE WHEN target_col = condition THEN col ELSE def_col END
	 * 
	 * @author	Tim
	 * @since	2022/05/05
	 * @param	String	SQL語句
	 * @return	String	轉換後的SQL語句
	 * */
	public static String convertDecode(String sql) {
		String res = sql.toUpperCase();
		List<String> lst = RegexTool.getRegexTarget("DECODE\\s*\\([^\\)]+\\)", res);
		for(String decode : lst) {
			String strcase = "CASE";
			String[] arrcol =  decode.replaceAll("DECODE\\s*\\(", "").replace(")", "").split(",");
			int len = arrcol.length;
			boolean iswhen = true;
			String targetCol = arrcol[0];
			for(int i=1;i<=len-2;i++) {
				strcase+=iswhen
						?" WHEN "+targetCol+" = "+arrcol[i]
						:" THEN "+arrcol[i];
				iswhen = !iswhen;
			}
			strcase+=" ELSE "+(len%2==1?"NULL":arrcol[len-1])+" END";
			res = res.replace(decode,strcase);
		}
		return res;
	}
	
}
