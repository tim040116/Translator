package etec.src.translator.sql.az.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.convert_safely.service.ConvertFunctionsSafely;
import etec.framework.context.translater.exception.SQLFormatException;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.src.translator.sql.az.translater.service.DataTypeService;
import etec.src.translator.sql.az.translater.service.TxdateService;

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
		while(true) {//coalesce
			String temp = res;
			res = res.replaceAll("(?i)COALESCE\\s*\\(\\s*((?:[^,()]+,)+)\\s*COALESCE\\s*\\(\\s*+([^()]+?)\\s*+\\)\\)","COALESCE\\($1$2\\)");
			if(temp.equals(res)) {
				break;
			}
		}
		res = res
				.replaceAll("(?i)\\bCURRENT_DATE\\b", "getDate\\(\\)")//CURRENT_DATE
				.replaceAll("(?i)ADD_MONTHS", "ADD_MONTH")//ADD_MONTHS
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
				.replaceAll("(?i)EXTRACT\\s*\\(\\s*(HOUR|MINUTE|SECOND)\\s*FROM", "DatePart($1,")// all
				.replaceAll("(?i)WITH\\s*COUNT\\s*\\(\\*\\)\\s*BY\\s*\\w*", "")
				.replaceAll("(?i)\\bDATE\\s*FORMAT\\s+'[YMDHS/\\-]*'", "DATE")
//				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll("(?i)length\\s*\\(", "LEN(")//length
		;
		res = TxdateService.easyReplace(res);
//		res = DataTypeService.changeTypeConversion(res);
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		res = cfs.savelyConvert(res, (t)->{
			String rt = t
				//TO_NUMBER
				.replaceAll("(?i)TO_NUMBER\\s*\\(\\s*([^(),]*?)\\s*\\)", "CAST($1 AS NUMERIC)")
				//TO_DATE
				.replaceAll("(?i)TO_DATE\\s*\\(\\s*([^(),]*?)\\s*,\\s*\\S+\\s*\\)", "CAST($1 AS DATETIME)")
				//TO_CHAR
				.replaceAll("(?i)TO_CHAR\\s*\\(([^,()]+)(?:,[^()]+)?\\)", "CAST\\($1 AS VARCHAR\\)")
				//INSTR
				.replaceAll("(?i)INSTR\\s*\\(([@\\w'\\(\\)]+),('[^']+'+)(,\\d+)?\\)", "CHARINDEX($2,$1 $3)")
				.replaceAll(
						  "(?i)ADD_(YEAR|MONTH|DAY)\\s*\\(([^,]+)\\s*,\\s*([+-]?\\s*\\d+)\\s*\\)"
						, "DateAdd\\($1,$3,$2\\)")
 			;
			rt = DataTypeService.changeStrongConvert(rt);
			rt = changeTrunc(rt);
			rt = changeZeroifnull(rt);
			rt = changeCharindex(rt);
			return rt;
		});
		ConvertFunctionsSafely cfs2 = new ConvertFunctionsSafely();
		res = cfs2.savelyConvert(res, (t)->{
			String rt = convertDecode(t);
			rt = rt.replaceAll("(?i)\\s*=\\s*NULL", " IS NULL");
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
	 * <br>2024/08/21	Tim	增加超過4個參數的情境
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
//		res = sb.toString();
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)DECODE\\s*\\(([^(),]+),((?:,?[^,()]+,[^,()]+)+)(?:,([^(),]+))?\\)";
		Matcher m = (Pattern.compile(reg)).matcher(res);
		while (m.find()) {
			String rpm = "CASE";
			String targetCol = m.group(1);
			String[] arrParam = m.group(2).split("\\s*,\\s*");
			String strElse = m.group(3);
			for (int i = 0; i < arrParam.length; i+=2) {
				rpm += "\r\n\tWHEN "+targetCol+" = "+arrParam[i]+" THEN "+arrParam[i+1];
			}
			if(strElse!=null) {
				rpm += "\r\n\tELSE "+strElse;
			}
			rpm += "\r\nEND ";
			m.appendReplacement(sb,Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}


	// zeroifnull
	public static String changeZeroifnull(String selectSQL) {
		String result = selectSQL;
		// 取得sample
//			result = result.replaceAll("(?<=zeroifnull\\(.{0,100})\\) +as ", ",0) as ");
//			result = result.replaceAll(RegexTool.getReg("zeroifnull \\("), "ISNULL(");
		result = result.replaceAll("(?i)zeroifnull\\s*\\(([^()]+)?\\)", "ISNULL($1,0)");
		return result;
	}

	// char index
	public static String changeCharindex(String sql) {
		//20240618 Tim	優化
		String res = sql;
		res = res.replaceAll("(?i)\\bINDEX\\s*\\(([^,]+),([^()]+)\\)","CHARINDEX\\($2,$1\\)");
		return res;
	}

	/**
	 * <h1>TRUNC</h1>
	 * <p>功能說明
	 * <br>TRUNC負責處理截斷的功能，
	 * <br>有分日期截斷跟數字截斷
	 * </p>
	 * <p>數字截斷
	 * <br>TRUNC(A.BDATE,\d+) -> ROUND(COL_NM, \d+, 1)
	 * <br>若只有一個參數則\d+ = 0
	 * </p>
	 * <p>日期截斷
	 * <br>TRUNC(A.BDATE,\d+) -> ROUND(COL_NM, \d+, 1)
	 * <br>若只有一個參數則\d+ = 0
	 * </p>
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年8月26日	Tim	建立功能
	 *
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sql
	 * @throws	e
	 * @see
	 * @return	return_type
			 */
	public static String changeTrunc(String sql) {
		String res = sql;
		res = res
			//trunc CAST(A.TIME_RANGE/10000 AS INTEGER)
			//數字截斷
			.replaceAll("(?i)\\bTRUNC\\(([^(),]+)\\)", "ROUND\\($1,0,1\\)")
			.replaceAll("(?i)\\bTRUNC\\(([^(),]+),([\\s\\d.]+)\\)", "ROUND\\($1,$2,1\\)")
			//日期截斷
			.replaceAll("(?i)\\bTRUNC\\s*\\(([^,()]+),\\s*'(YEAR|MONTH|DAY)'\\s*\\)", "DATEADD\\($2, DATEDIFF\\(MONTH, 0, $1\\), 0\\)")
		;
		return res;
	}
}
