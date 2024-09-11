package etec.src.translator.sql.az.translater;

import java.util.ArrayList;
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
			Log.debug("\tchangeRollUp");
			txt = changeRollUp(txt);
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
		if(!selectSQL.matches("(?i)[\\S\\s]*\\bSAMPLE\\s+\\d+\\b[\\S\\s]+")) {
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
		//整理語法
		res = res.replaceAll("(?i),\\s*ROLLUP\\s*\\(",",ROLLUP\\(");
		//將rollup排序到最後
		res = res.replaceAll("(?i)(,ROLLUP\\([^)]+\\))\\s*((?:,\\s*[\\w.]+\\s*)+)","$2$1");
		res = res.replaceAll("(?i)GROUP\\s+BY\\s+(ROLLUP\\([^)]+\\))\\s*,\\s*([\\w.]+\\s*(?:,\\s*[\\w.]+\\s*)+)(,ROLLUP\\([^);]+\\))","GROUP BY $2,$1$3");
		res = res.replaceAll("(?i)GROUP\\s+BY\\s+(ROLLUP\\([^)]+\\))\\s*,\\s*([\\w.]+\\s*(?:,\\s*[\\w.]+\\s*)+)","GROUP BY $2,$1");
		//將複數rollup拆分成union
		res = multiRollup(res);
		//轉換rollup
		res = singleRollup(res);
		//整理having
		res = res.replaceAll("(?i)(\\bhaving\\s+\\S+\\s*is not null(?:\\s*and\\s*\\S+\\s+is not null)+\\s*)having\\b", "$1   and");
		return res;
	}
	
	//單一rollup轉換
	private static String singleRollup(String scrippt) {
		//轉換rollup
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)([ \\t]*)"
			+ "GROUP\\s+BY\\s+([\\w.,\\s]+)"
			+ ",ROLLUP\\s*\\(([^)]+)\\)"
			+ "(\\s*,\\s*ROLLUP\\s*\\([^)]+\\))?"
		;
		Matcher m= Pattern.compile(reg).matcher(scrippt);
		while(m.find()) {
			String tab = m.group(1);
			String groupby = m.group(2).replaceAll("\\s+", "");
			String rollup = m.group(3).replaceAll("\\s+", "");	
			String rollup2 = m.group(4)==null?"":m.group(4);
			String having = "having " + groupby.trim().replaceAll("\\s*,\\s*"," is not null\r\n" + tab + "   and ") + " is not null";
			String rpm = tab+"GROUP BY ROLLUP("
				+ groupby + "," + rollup + ")"+rollup2
				+ "\r\n" + tab + having + "\r\n"
			;
			m.appendReplacement(sb, rpm);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	//將複數rollup拆分成union
	private static String multiRollup(String script) {
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)(?<head>^.*\\n"
				+ "(?<tab>[ \\t]+)GROUP\\s+BY[\\w.,\\s]+"
				+ "ROLLUP\\()(?<rollup1>[^)]+)\\)\\s*"
				+ "(,\\s*ROLLUP\\((?<rollup2>[^)]+)\\))"
				+ "(?<foot>.*$)";
		Matcher m= Pattern.compile(reg,Pattern.DOTALL).matcher(script);
		while(m.find()) {
			List<String> lst = new ArrayList<String>();
			//取出參數
			String head = m.group("head");
			String tab = m.group("tab");
			String rollup1 = m.group("rollup1");
			String rollup2 = m.group("rollup2");
			String foot = m.group("foot");
			//加工
			String tmpHaving = "";
			String tmpRollup2 = "";
			String[] arrRollup2 = rollup2.trim().split("\\s*,\\s*");
			//先製作最下層的語法
			String tmpFirstHead = head;
			for(String rplCol : arrRollup2) {
				tmpFirstHead = tmpFirstHead.replaceAll(Pattern.quote(rplCol), "null");
			}
			String rpm = tmpFirstHead
				+ rollup1 + ")"
				+ tab + foot
			;
			//迴圈疊union
			for(String col : arrRollup2) {
				lst.add(col);
				tmpRollup2 += col + ",";
				tmpHaving += 
						(tmpHaving.equals("")?tmpHaving+"having ":tmpHaving+"   and")
						+ col + " is not null\r\n"
				;
				String tmpHead = head;
				for(String rplCol : arrRollup2) {
					if(!lst.contains(rplCol)) {
						tmpHead = tmpHead.replaceAll(Pattern.quote(rplCol), "null");
					}
				}
				String tmpsql = tmpHead
					+ tmpRollup2 + rollup1 + ")"
					+ tab + tmpHaving
					+ tab + foot
					+ "\r\n"
				;
				rpm = tmpsql 
					+ "\r\nUNION"
					+ "\r\n" + rpm
				;
			}
			m.appendReplacement(sb,Matcher.quoteReplacement(rpm));	
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
