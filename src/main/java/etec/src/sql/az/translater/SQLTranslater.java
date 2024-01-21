package etec.src.sql.az.translater;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.SQLFormatException;
import etec.common.utils.RegexTool;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.convert_safely.ConvertRemarkSafely;

public class SQLTranslater {
	

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
				.replaceAll("(?i)\\bSEL\\b", "SELECT")// SEL
				.replaceAll("\\|\\|", "+")// ||
				.replaceAll("(?i)\\bSUBSTR\\b", "SUBSTRING")// SUBSTR
				.replaceAll("(?i)OREPLACE\\s*\\(", "REPLACE(")// oreplace
				.replaceAll("(?i)STRTOK\\s*\\(", "STRING_SPLIT(")// strtok
				.replaceAll("(?i)NVL\\s*\\(", "ISNULL(")//NVL
				.replaceAll("(?i)Character\\s*\\(", "LEN(")//Character
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				//TO_DATE
				.replaceAll("(?i)TO_DATE\\s*\\(\\s*(.*?)\\s*,\\s*\\S+\\s*\\)", "CAST($1 AS DATETIME)")
				//TO_CHAR
//				.replaceAll("TO_CHAR\\s*\\(\\s*([^,\\(\\)]+)\\s*\\)", "CAST($1 AS VARCHAR)")
				// rank over
				.replaceAll("(?i)\\bRANK\\(?:(?! |\\))", "RANK ( ) OVER ( order by ")// all
				// extract
				.replaceAll("(?i)EXTRACT\\s*\\(\\s*(DAY|MONTH|YEAR)\\s*FROM", "DatePart($1,")// all
				.replaceAll("(?i)WITH\\s*COUNT\\s*\\(\\*\\)\\s*BY\\s*\\w*", "")
				.replaceAll("(?i)DATE\\s*FORMAT\\s+'[YMDHS/\\-]*'", "DATE")
//				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll("(?i)length\\s*\\(", "LEN(")//length
				
		;
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		cfs.savelyConvert(res, (t)->{
			String rt = t
				//truncCAST(A.TIME_RANGE/10000 AS INTEGER)
				.replaceAll("(?i)TRUNC\\((.*?)(?:,\\s*0)?\\)", "CAST($1 AS INTEGER)")
				//TO_NUMBER
				.replaceAll("(?i)TO_NUMBER\\s*\\(\\s*(.*?)\\s*\\)", "CAST($1 AS INTEGER)")
				//INSTR
				.replaceAll("(?i)INSTR\\s*\\(([@\\w'\\(\\)]+),('[^']+'+)(,\\d+)?\\)", "CHARINDEX($2,$1 $3)")

			;
			return rt;
		});
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
		String res = sql;
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		ConvertRemarkSafely crs = new ConvertRemarkSafely();
		res = crs.savelyConvert(res, (t0)->{
			String rescrs = t0;
			rescrs = cfs.savelyConvert(rescrs, (t1)->{
				String r = t1;
//				r = t
//					//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
//					.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*(\\d+)\\s*,\\s*NULL\\s*,\\s*\\1\\s*\\)", "NULLIF\\($1,$2\\)")
//					//DECODE($1,null,\d,$1) -> COALESCE($1,\d)
//					.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*NULL\\s*,\\s*(\\d+)\\s*,\\s*\\1\\s*\\)","COALESCE\\($1,$2\\)")
//					//other
//					.replaceAll("(?i)DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*,\\s*([^,]+)\\s*\\)","CASE WHEN $1 = $2 THEN $3 ELSE $4 END")
//				;
				Pattern p = Pattern.compile("(?i)DECODE\\s*\\(\\s*([^\\(\\),]+)\\s*,\\s*([^,\\(\\)]+)\\s*,\\s*([^,\\(\\)]+)\\s*,\\s*([^,\\(\\)]+)\\s*\\)",Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(r);
				while (m.find()) {
					String p0 = m.group(0);
					String p1 = m.group(1);
					String p2 = m.group(2);
					String p3 = m.group(3);
					String p4 = m.group(4);
					String rpm = "";
					//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
					boolean ismatch = ConvertRemarkSafely.equals(p1,p4,(eq)->{
						return eq.replaceAll("\\w+\\.(\\w+)", "$1");
					});
					if(ismatch&&ConvertRemarkSafely.match("\\d+",p2)&&ConvertRemarkSafely.match("(?i)NULL",p3)) {//DECODE($1,\d,null,$1) -> NULLIF($1,\d)
						rpm = "NULLIF("+p1+","+p2+")";
					}else if(ismatch&&ConvertRemarkSafely.match("(?i)NULL",p2)&&ConvertRemarkSafely.match("\\d+",p3)) {//DECODE($1,null,\d,$1) -> COALESCE($1,\d)
						rpm = "COALESCE("+p1+","+p3+")";
					}else {
						rpm = "CASE WHEN "+p1+" = "+p2+" THEN "+p3+" ELSE "+p4+" END";
					}
					r = t1
						.replace(p0,rpm)
					;
				}
				return r;
			});
			return rescrs;
		});
		
		return res;
	}
	
	/**
	 * @author	Tim
	 * @since	3.3.0.0
	 * @param	String	sql
	 * 
	 * */
	public static String replaceToChar(String sql) {
		String res = sql;
		RegexTool.getRegexTarget("(?i)TO_CHAR\\s*\\(\\s*([^,]+\\))\\s*,\\s*([^\\)]+)\\s*\\)", "");
		return res;
	}
	
}
