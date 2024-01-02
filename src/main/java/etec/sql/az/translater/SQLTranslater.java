package etec.sql.az.translater;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.SQLFormatException;
import etec.common.utils.ConvertFunctionsSafely;
import etec.common.utils.RegexTool;

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
				.replaceAll("(?i)\\bSEL\\vb", "SELECT")
				// ||
				.replaceAll("\\|\\|", "+")
				// SUBSTR
				.replaceAll("(?i)SUBSTR\\s*\\(", "SUBSTRING(")
				// oreplace
				.replaceAll("(?i)OREPLACE\\s*\\(", "REPLACE(")
				// strtok
				.replaceAll("(?i)STRTOK\\s*\\(", "STRING_SPLIT(")
				//NVL
				.replaceAll("(?i)NVL\\s*\\(", "ISNULL(")
				//truncCAST(A.TIME_RANGE/10000 AS INTEGER)
				.replaceAll("(?i)TRUNC\\((.*?)(,\\s*0)?\\)", "CAST($1 AS INTEGER)")
				//TO_NUMBER
				.replaceAll("(?i)TO_NUMBER\\s*\\(\\s*(.*?)\\s*\\)", "CAST($1 AS INTEGER)")
				//TO_DATE
				.replaceAll("(?i)TO_DATE\\s*\\(\\s*(.*?)\\s*,\\s*\\S+\\s*\\)", "CAST($1 AS DATETIME)")
				//TO_CHAR
//				.replaceAll("TO_CHAR\\s*\\(\\s*([^,\\(\\)]+)\\s*\\)", "CAST($1 AS VARCHAR)")
				// rank over
				.replaceAll("(?i)\\bRANK\\((?! |\\))", "RANK ( ) OVER ( order by ")// all
				// extract
				.replaceAll("(?i)EXTRACT\\s*\\(\\s*(DAY|MONTH|YEAR)\\s*FROM", "DatePart($1,")// all
				.replaceAll("(?i)WITH\\s*COUNT\\s*\\(\\*\\)\\s*BY\\s*\\w*", "")
				.replaceAll("(?i)DATE\\s*FORMAT\\s+'[YMDHS/\\-]*'", "DATE")
				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll("(?i)length\\s*\\(", "LEN(")//all
				.replaceAll("(?i)Character\\s*\\(", "LEN(")//all
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//all
				.replaceAll("(?i)INSTR\\s*\\(([@A-Za-z0-9_'\\(\\)]+),('[^']+'+)(,[0-9]+)?\\)", "CHARINDEX($2,$1 $3)")
		;
		res = convertDecode(res);
		return res;
	}
	/**	
	 * <h1>轉換decode語法<h1>
	 * 
	 * <br>azure 不支援decode語法
	 * <br>於是要改成case when
	 * <br>
	 * <br>TD語法:
	 * <br>	decode(target_col,number,NULL,target_col)
	 * <br>az語法:
	 * <br> NULLIF(target_col,number)
	 * <br>
	 * <br>TD語法:
	 * <br>	decode(target_col,condition,col,def_col)
	 * <br>az語法:
	 * <br>	CASE WHEN target_col = condition THEN col ELSE def_col END
	 * <br>
	 * 
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	String	SQL語句
	 * @return	String	轉換後的SQL語句
	 * @throws SQLFormatException 
	 * 
	 * <br>2023/12/28	Tim	改使用 ConvertFunctionsSafely 處理
	 * <br>2024/01/02	Tim	解決空白造成的誤判，改用Matcher處理
	 * */
	public static String convertDecode(String sql){
		String res = "";
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		res =cfs.savelyConvert(sql, (t)->{
			String r = t;
//			r = t
//				//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
//				.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*(\\d+)\\s*,\\s*NULL\\s*,\\s*\\1\\s*\\)", "NULLIF\\($1,$2\\)")
//				//DECODE($1,null,\d,$1) -> COALESCE($1,\d)
//				.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*NULL\\s*,\\s*(\\d+)\\s*,\\s*\\1\\s*\\)","COALESCE\\($1,$2\\)")
//				//other
//				.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*\\)","CASE WHEN $1 = $2 THEN $3 ELSE $4 END")
//			;
			Pattern p = Pattern.compile("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,\\)]+)\\s*\\)",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(r);
			while (m.find()) {
				String p0 = m.group(0);
				String p1 = m.group(1);
				String p2 = m.group(2);
				String p3 = m.group(3);
				String p4 = m.group(4);
				String rpm = "";
				//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
				boolean ismatch = p1.toUpperCase().replaceAll("\\s+", "").equals(p4.toUpperCase().replaceAll("\\s+", ""));
				String tp1 = p1.replaceAll("\\w+\\.(\\w+)", "$1").trim().toUpperCase();
				String tp4 = p4.replaceAll("\\w+\\.(\\w+)", "$1").trim().toUpperCase();
				if(!ismatch) {
					
					ismatch = tp1.equals(tp4);
				}
				boolean ismatch2 = p2.matches("\\d+");
				boolean ismatch3 = p3.matches("(?i)NULL");
				if(ismatch&&p2.matches("\\d+")&&p3.matches("(?i)NULL")) {//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
					rpm = "NULLIF("+p1+","+p2+")";
				}else if(ismatch&&p2.matches("(?i)NULL")&&p3.matches("\\d+")) {//DECODE($1,null,\d,$1) -> COALESCE($1,\d)
					rpm = "COALESCE("+p1+","+p3+")";
				}else {
					rpm = "CASE WHEN "+p1+" = "+p2+" THEN "+p3+" ELSE "+p4+" END";
				}
				r = t.replace(p0,rpm);
			}
			return r;
		});
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
		RegexTool.getRegexTarget("(?i)TO_CHAR\\s*\\(\\s*([^,]+\\))\\s*,\\s*([^\\)]+)\\s*\\)", "");
		return res;
	}
	
}
