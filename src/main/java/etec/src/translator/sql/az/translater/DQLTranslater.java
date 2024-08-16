package etec.src.translator.sql.az.translater;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;
import etec.framework.context.convert_safely.model.Mark;
import etec.framework.context.convert_safely.service.ConvertSubQuerySafely;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.framework.context.translater.exception.UnknowSQLTypeException;
import etec.framework.security.log.service.Log;

public class DQLTranslater {
	// select語句轉換
	public static String easyReplace(String sql) throws SQLTranslateException {
		Log.debug("transduceSelectSQL");
		String res  = SQLTranslater.easyReplaceSelect(sql);
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		res = csqs.savelyConvert(res, (t)->{
			String txt = t;
			// 轉換
			// txt = changeGroupBy(txt);
			
			// 整理 如果有註解會被Mark
			// txt = arrangeSQL(txt);
//					txt = changeGroupBy(txt);
			Log.debug("\tchangeSample");
			txt = changeSample(txt);
			Log.debug("\teasyReplaceSelect");
			txt = changeIndex(txt);
			Log.debug("\tchangeIndex");
			txt = txt.replaceAll("\\bINTO\\b", "=");
			return txt;
		});
		
		return res.trim();
	}

	// select單純的置換
	public static String easyReplaceSelect(String sql) {
		String res = sql;
		res = res.replaceAll("\\(\\s*([^\\(\\),\\s]+)\\s*,\\s*([^\\(\\),\\s]+)\\s*\\)", "\\($1,$2\\)")
				// ||
				.replaceAll("\\|\\|", "+")
				// SUBSTR
				.replaceAll("(?i)\\bSUBSTR\\s*\\(", "SUBSTRING(")
				// CAST(SUBSTR('${LAST01TX4YMB}',1,4)||'-01-01' AS DATE FORMAT 'YYYY-MM-DD')
				// .replaceAll("[Cc][Aa][Ss][Tt] *\\(|[Aa][Ss] *[Dd][Aa][Tt][Ee]
				// *[Ff][Oo][Rr][Mm][Aa][Tt] *'[YyMmDdHhSs-]*'\\)","")
				// oreplace
				.replaceAll("(?i)\\boreplace\\s*\\(", "Replace(")
				// strtok
				.replaceAll("(?i)\\bstrtok\\s*\\(", "STRING_SPLIT(")
				// rank over
				.replaceAll("(?i)(?<!_|[A-Za-z0-9])RANK\\((?! |\\))", " RANK ( ) OVER ( order by ")
				// extract
				.replaceAll("(?i)EXTRACT\\s*\\(\\s*(DAY|MONTH|YEAR)\\s+FROM", "DatePart($1 ,")
				/**
				 * @author Tim
				 * @since 2023年11月21日
				 * 
				 *        3.3.1.4 應Jason要求新增功能 TO_NUMBER要改成cast as但是TO_NUMBER裡面有substring的就不要轉
				 * 
				 */
				.replaceAll("(?i)TO_NUMBER\\(([^\\(]+)\\)", "CAST\\($1 AS NUMERIC\\)")
				/**
				 * @author Tim
				 * @since 2023年11月21日
				 * 
				 *        3.3.1.5 DECLARE DEFAULT語法分成兩段
				 * 
				 */
				.replaceAll("(?i)([ \\t]+)DECLARE\\s+(\\S+)\\s+(\\S+)\\s+DEFAULT\\s+([^;]+)\\s*;",
						"$1DECLARE $2 $3;\r\n$1SET $2 = $4;")

		;

		return res;
	}

	// sample
	public static String changeSample(String selectSQL) {
		String result = selectSQL;
		if(!selectSQL.matches("(?i)[\\S\\s]*\\bSAMPLE\\b[\\S\\s]+")) {
			return selectSQL;
		}
		// 取得sample
		List<String> lstSample = RegexTool.getRegexTarget("(?i)SAMPLE\\s+\\d+\\s*;", selectSQL);
		String sample = " SELECT TOP " + RegexTool.getRegexTarget("\\d+", lstSample.get(0)).get(0) + " ";
		result = result.replaceFirst("(?i)SELECT", sample)
				.replaceAll("(?i)SAMPLE\\s+\\d+\\s*;", ";");
		return result;
	}

	/**
	 * <h1>index</h1>
	 * <p>
	 * <br>轉換select語法的index
	 * <br>要避開create table的index
	 * <br>
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月20日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.1.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String changeIndex(String sql) {
		String result = sql;
		// 取得sample
		List<String> lstIndex = RegexTool.getRegexTarget("(?i)(?<=[, ])INDEX[^\\)]+", result);
		// 是否存在sample
		if (lstIndex.isEmpty()) {
			return sql;
		}
		for (String data : lstIndex) {
			String upper = data.toUpperCase();
			if (upper.contains("COLLECT STATISTICS ON") || upper.contains("PRIMARY") || upper.contains("UNIQUE")) {
				continue;
			}
			List<String> lstP = RegexTool.getRegexTarget("(?i)(?<=INDEX\\s{0,10}\\()[^\\)]+", data);
			if (lstP.isEmpty()) {
				continue;
			}
			String params = lstP.get(0);
			String[] arp = params.split(",");
			if (arp.length != 2) {
				continue;
			}
			String index = " CHARINDEX(" + arp[1] + "," + arp[0];
			String reg = RegexTool.encodeSQL(data);
			result = RegexTool.encodeSQL(result).replaceAll(reg, RegexTool.encodeSQL(index));
		}
		result = RegexTool.decodeSQL(result);
		return result;
	}

	/**
	 * <h1>rollup</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年7月31日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String changeRollUp(String sql) {
		String res = sql;
		//找到group by
		String temp = sql.replaceAll("(?i)\\b\\w+\\s+BY\\s", Mark.MAHJONG_GREEN+"$0");
		Matcher m = Pattern.compile("(?i)"+Mark.MAHJONG_GREEN+"\\bGROUP\\s+BY\\s+([^"+Mark.MAHJONG_GREEN+"]++)").matcher(sql);
		if(m.find()) {
			//確認rollup
			if(!m.group(1).toUpperCase().contains("ROLLUP(")) {
				return sql;
			}
			String groupBy = m.group(1);
			
		}
		
		return res;
	}
}
