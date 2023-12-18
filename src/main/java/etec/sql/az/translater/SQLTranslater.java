package etec.sql.az.translater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import etec.common.exception.SQLFormatException;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;

public class SQLTranslater {
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
	 * 簡單轉換
	 * 
	 * @author	Tim
	 * @since	2023年10月17日
	 * @param	String	SQL語句
	 * @return	String	轉換後的SQL語句
	 * @throws SQLFormatException 
	 * */
	public static String easyReplaceSelect(String sql) throws IOException {
		String res = sql;
		res = res
				// SEL
				.replaceAll("\\bSEL\\vb", "SELECT")
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
				//TO_NUMBER
				.replaceAll("TO_NUMBER\\s*\\(\\s*(.*?)\\s*\\)", "CAST($1 AS INTEGER)")
				//TO_DATE
				.replaceAll("TO_DATE\\s*\\(\\s*(.*?)\\s*,\\s*\\S+\\s*\\)", "CAST($1 AS DATETIME)")
				//TO_CHAR
//				.replaceAll("TO_CHAR\\s*\\(\\s*([^,\\(\\)]+)\\s*\\)", "CAST($1 AS VARCHAR)")
				// rank over
				.replaceAll("(?<!_|[A-Za-z0-9])[Rr][Aa][Nn][Kk]\\((?! |\\))", " RANK ( ) OVER ( order by ")// all
				// extract
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Dd][Aa][Yy] *[Ff][Rr][Oo][Mm]", "DatePart(day ,")// all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Mm][Oo][Nn][Tt][Hh] *[Ff][Rr][Oo][Mm]",
						"DatePart(month ,")// all
				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Yy][Ee][Aa][Rr] *[Ff][Rr][Oo][Mm]", "DatePart(year ,")// all
				.replaceAll("[Ww][Ii][Tt][Hh] *[Cc][Oo][Uu][Nn][Tt]\\(\\*\\) *[Bb][Yy] *\\w*", "")
				.replaceAll(RegexTool.getReg("[Dd][Aa][Tt][Ee] [Ff][Oo][Rr][Mm][Aa][Tt] '[YyMmDdHhSs/\\-]*'"), "DATE")
				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll(RegexTool.getReg("length \\("), "LEN(")//all
				.replaceAll(RegexTool.getReg("Character \\("), "LEN(")//all
				.replaceAll(RegexTool.getReg(" MINUS "), " EXCEPT ")//all
				.replaceAll("(?i)INSTR\\s*\\(([@A-Za-z0-9_'\\(\\)]+),('[^']+'+)(,[0-9]+)?\\)", "CHARINDEX($2,$1 $3)")
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
	 * @throws SQLFormatException 
	 * */
	public static String convertDecode(String sql) throws SQLFormatException {
		String res = "";
		String[] arr = sql.toUpperCase()
				.replaceAll("\\)", " "+TransduceTool.SPLIT_CHAR_CH_01+"\\)"+TransduceTool.SPLIT_CHAR_CH_01+" ")
				.replaceAll(",", " "+TransduceTool.SPLIT_CHAR_CH_01+","+TransduceTool.SPLIT_CHAR_CH_01+" ")
				.split("\\b");
		boolean isDecode = false;
		int cntBrackets = 0;
		String temp = "";
		List<String> lstParam = new ArrayList<String>();
		for(String str : arr) {
			if("DECODE".equals(str)) {
				isDecode = true;
				continue;
			}else if(TransduceTool.SPLIT_CHAR_CH_01.equals(str)) {
				continue;
			}
			if(!isDecode) {
				res+=str;
				continue;
			}
			
			//計算括號
			cntBrackets+="(".equals(str)?1:")".equals(str)?-1:0;
			
			if(cntBrackets==1&&",".equals(str)) {
				lstParam.add(temp);
				temp = "";
			}else {
				temp+=str;
			}
			if(isDecode&&cntBrackets==0) {
				lstParam.add(temp);
				temp = "";
				isDecode = false;
				if(lstParam.size()!=4) {
					throw SQLFormatException.wrongParam("DECODE", 4,lstParam.size());
				}
				res+=" IIF"+lstParam.get(0)+"="+lstParam.get(1)+","+lstParam.get(2)+","+lstParam.get(3)+" ";
//				String strDecode = "CASE";
//				for(int i = 1;i<lstParam.size();i+=2) {
//					strDecode+=
//						  " WHEN "+lstParam.get(0)
//						+ " = "+lstParam.get(i)+" THEN "
//					;
//				}
			}
		}
		/*String res = sql.toUpperCase();
		List<String> lst = RegexTool.getRegexTarget("(?i)DECODE\\s*\\([^\\)]+\\)", res);
		for(String decode : lst) {
			String strcase = "CASE";
			String[] arrcol =  decode.replaceAll("(?i)DECODE\\s*\\(", "").replace(")", "").split(",");
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
		}*/
		return res;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年10月19日
	 * @version	3.3.0.0
	 * @param	String	sql
	 * 
	 * */
	public static String replaceToChar(String sql) {
		String res = sql;
		RegexTool.getRegexTarget("TO_CHAR\\s*\\(\\s*([^,]+\\))\\s*,\\s*([^\\)]+)\\s*\\)", "");
		return res;
	}
	
	
}