package etec.src.sql.az.translater;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;
import etec.framework.convert_safely.ConvertFunctionsSafely;
import etec.framework.convert_safely.ConvertRemarkSafely;
import etec.framework.translater.exception.SQLFormatException;
import etec.framework.translater.exception.SQLTranslateException;

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
	public static String easyReplaceSelect(String sql) throws SQLTranslateException {
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
				// rank over
				.replaceAll("(?i)\\bRANK\\(?:(?! |\\))", "RANK ( ) OVER ( order by ")// all
				// extract
				.replaceAll("(?i)EXTRACT\\s*\\(\\s*(DAY|MONTH|YEAR)\\s*FROM", "DatePart($1,")// all
				.replaceAll("(?i)WITH\\s*COUNT\\s*\\(\\*\\)\\s*BY\\s*\\w*", "")
				.replaceAll("(?i)\\bDATE\\s*FORMAT\\s+'[YMDHS/\\-]*'", "DATE")
//				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll("(?i)length\\s*\\(", "LEN(")//length
				
		;
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		res = cfs.savelyConvert(res, (t)->{
			String rt = t
				//trunc CAST(A.TIME_RANGE/10000 AS INTEGER)
				.replaceAll("(?i)\\bTRUNC\\(([^(),]*?)(?:,\\s*0\\s*)?\\)", "CAST($1 AS INTEGER)")
				//TO_NUMBER
				.replaceAll("(?i)TO_NUMBER\\s*\\(\\s*([^(),]*?)\\s*\\)", "CAST($1 AS INTEGER)")
				//TO_DATE
				.replaceAll("(?i)TO_DATE\\s*\\(\\s*([^(),]*?)\\s*,\\s*\\S+\\s*\\)", "CAST($1 AS DATETIME)")
				//TO_CHAR
				.replaceAll("(?i)TO_CHAR\\s*\\(([^,()]+)(?:,[^()]+)?\\)", "CAST\\($1 AS VARCHAR\\)")
				//INSTR
				.replaceAll("(?i)INSTR\\s*\\(([@\\w'\\(\\)]+),('[^']+'+)(,\\d+)?\\)", "CHARINDEX($2,$1 $3)")
 			;
			rt = changeAddMonth(rt);
			rt = changeZeroifnull(rt);
			rt = changeCharindex(rt);
			return rt;
		});
		ConvertFunctionsSafely cfs2 = new ConvertFunctionsSafely();
		res = cfs2.savelyConvert(res, (t)->{
			String rt = convertDecode(t);
			return rt;
		});
		
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
		
		
		/**
		 * <p>功能 ：取得DECODE語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從 DECODE( 到 )</p>
		 * <h2>群組 ：</h2>
		 * 	1.參數1
		 * 	2.參數2
		 * 	3.參數3
		 *  4.參數4
		 * <h2>備註 ：</h2>
		 * 	
		 * <h2>異動紀錄 ：</h2>
		 * 2024年6月14日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)DECODE\\s*\\(\\s*([^\\(\\),]+)\\s*,\\s*([^,\\(\\)]+)\\s*,\\s*([^,\\(\\)]+)\\s*,\\s*([^,\\(\\)]+)\\s*\\)";
		Matcher m = (Pattern.compile(reg)).matcher(res);
		while (m.find()) {
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
			m.appendReplacement(sb, rpm);
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	
	// AddMonth修改
		public static String changeAddMonth(String sql) {
			String res = sql;
			//預處理
			res = res.replaceAll("(?i)\\bADD_MONTHS\\b","ADD_MONTH");
			//捕獲
			/**
			 * <p>功能 ：轉換ADD_MONTHS</p>
			 * <p>類型 ：取代</p>
			 * <p>修飾詞：i</p>
			 * <p>範圍 ：從  ADD_MONTH 到 )</p>
			 * <h2>群組 ：</h2>
			 * 	1.YEAR|MONTH|DAY
			 *  2.欄位
			 *  3.數字
			 * <h2>備註 ：</h2>
			 * 	ADD_MONTHS(COL_NM,-1)
			 *  DateAdd(MONTH,-1,COL_NM)
			 * <h2>異動紀錄 ：</h2>
			 * 2024年5月8日	Tim	建立邏輯
			 * */
			res = res.replaceAll(
					  "(?i)ADD_(YEAR|MONTH|DAY)\\s*\\(([^,]+)\\s*,\\s*([+-]?\\s*\\d+)\\s*\\)"
					, "DateAdd\\($1,$3,$2\\)");
			return res;
		}
		// zeroifnull
		public static String changeZeroifnull(String selectSQL) {
			String result = selectSQL;
			// 取得sample
//			result = result.replaceAll("(?<=zeroifnull\\(.{0,100})\\) +as ", ",0) as ");
//			result = result.replaceAll(RegexTool.getReg("zeroifnull \\("), "ISNULL(");
			result = result.replaceAll("(?i)zeroifnull\\s*\\(\\s*([^\\(\\)]+\\([^\\)]+\\))?\\)", "ISNULL($1,0)");
			return result;
		}
		
		// char index
		public static String changeCharindex(String sql) {
//			String result = RegexTool.encodeSQL(sql);
//			// 取得sample
//			List<String> lstSQL = RegexTool
//					.getRegexTarget("(?i)INDEX<encodingCode_ParentBracketLeft>[^,]+, *\\'[^\\']+\\'", result);
//			for (String data : lstSQL) {
//				String oldData = data;
//				String param = data.replaceAll("(?i)INDEX<encodingCode_ParentBracketLeft>", "");
//				String[] ar = param.split(",");
//				String newData = "CHARINDEX<encodingCode_ParentBracketLeft>" + ar[1] + "," + ar[0];
//				result = result.replaceAll(oldData, newData);
//			}
//			return RegexTool.decodeSQL(result);
			
			//20240618 Tim	優化
			String res = sql;
			res = res.replaceAll("(?i)\\bINDEX\\s*\\(([^,]+),([^()]+)\\)","CHARINDEX\\($2,$1\\)");
			return res;
		}
}
