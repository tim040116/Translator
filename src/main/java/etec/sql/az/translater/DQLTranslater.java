package etec.sql.az.translater;

import java.io.IOException;
import java.util.List;

import etec.common.utils.Log;
import etec.common.utils.RegexTool;

public class DQLTranslater {
	// select語句轉換
	public static String transduceSelectSQL(String sql) throws IOException {
		Log.debug("transduceSelectSQL");

		String txt = sql;
		// 轉換
		// txt = changeGroupBy(txt);
		txt = SQLTranslater.easyReplaceSelect(txt);
		// 整理 如果有註解會被Mark
		// txt = arrangeSQL(txt);
//			txt = changeGroupBy(txt);
		txt = changeAddMonth(txt);
		txt = changeSample(txt);
		txt = changeZeroifnull(txt);
		txt = changeCharindex(txt);
		txt = changeIndex(txt);
		txt = txt.replaceAll("\\bINTO\bb", "=");
		return txt.trim();
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
						"$1DECLARE $2 $3;\\r\\n$1SET $2 = $4;")

		;

		return res;
	}

	// AddMonth修改
	public static String changeAddMonth(String sql) {
		if (!sql.contains("ADD_MONTH")) {
			return sql;
		}
		/* 20220613 去除分行符號? */
		// 20220613 String res = sql.replaceAll("\\s+", " ");
		String res = sql.trim();

		// 20220613 List<String> lst =
		// RegexTool.getRegexTarget2("[Aa][Dd]{2}_[Mm][Oo][Nn][Tt][Hh][Ss]\\([^\\)]*\\)
		// *,(-?[0-9]*) *\\)",res);
		List<String> lst = RegexTool
				.getRegexTarget("ADD_MONTHS\\([^\\)]*\\)?,(-?[0-9]*) *\\)", res);
		for (String str : lst) {
			String[] param = str.replaceAll("(?i)add_Months\\(|\\)$", "").split(",");
			res = RegexTool.encodeSQL(res);
			String oldstr = RegexTool.encodeSQL(str);
			String newstr = RegexTool.encodeSQL("DateAdd(MONTH," + param[1].trim() + "," + param[0].trim() + ")");
			res = res.replaceAll(oldstr, newstr);
			res = RegexTool.decodeSQL(res);
		}

		// 20220613 return arrangeSQL(res);
		return res;
	}

	// sample
	public static String changeSample(String selectSQL) {
		String result = selectSQL;
		// 取得sample
		List<String> lstSample = RegexTool.getRegexTarget("(?i)SAMPLE\\s+\\d+\\s*;", selectSQL);
		// 是否存在sample
		if (lstSample.isEmpty()) {
			return selectSQL;
		}
		String sample = " SELECT TOP " + RegexTool.getRegexTarget("\\d+", lstSample.get(0)).get(0) + " ";
		result = result.replaceFirst("(?i)SELECT", sample)
				.replaceAll("(?i)SAMPLE\\s+\\d+\\s*;", ";");
		return result;
	}

	// zeroifnull
	public static String changeZeroifnull(String selectSQL) {
		String result = selectSQL;
		// 取得sample
//		result = result.replaceAll("(?<=zeroifnull\\(.{0,100})\\) +as ", ",0) as ");
//		result = result.replaceAll(RegexTool.getReg("zeroifnull \\("), "ISNULL(");
		result = result.replaceAll("(?i)zeroifnull\\s*\\(\\s*([^\\(\\)]+\\([^\\)]+\\))?\\)", "ISNULL($1,0)");
		return result;
	}

	// char index
	public static String changeCharindex(String selectSQL) {
		String result = RegexTool.encodeSQL(selectSQL);
		// 取得sample
		List<String> lstSQL = RegexTool
				.getRegexTarget("[Ii][Nn][Dd][Ee][Xx]<encodingCode_ParentBracketLeft>[^,]+, *\\'[^\\']+\\'", result);
		for (String data : lstSQL) {
			String oldData = data;
			String param = data.replaceAll("[Ii][Nn][Dd][Ee][Xx]<encodingCode_ParentBracketLeft>", "");
			String[] ar = param.split(",");
			String newData = "CHARINDEX<encodingCode_ParentBracketLeft>" + ar[1] + "," + ar[0];
			result = result.replaceAll(oldData, newData);
		}
		return RegexTool.decodeSQL(result);
	}

	// index
	public static String changeIndex(String sql) {
		String result = sql;
		// 取得sample
		List<String> lstIndex = RegexTool.getRegexTarget("(?<=[, ])[Ii][Nn][Dd][Ee][Xx][^\\)]+", result);
		// 是否存在sample
		if (lstIndex.isEmpty()) {
			return sql;
		}
		for (String data : lstIndex) {
			String upper = data.toUpperCase();
			if (upper.contains("COLLECT STATISTICS ON") || upper.contains("PRIMARY") || upper.contains("UNIQUE")) {
				continue;
			}
			List<String> lstP = RegexTool.getRegexTarget("(?<=[Ii][Nn][Dd][Ee][Xx]\\s{0,10}\\()[^\\)]+", data);
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

	//
	public static String convertRollup(String content) {
		String res = "";
		String sql = replaceMark(content);

		return res;
	}

	// 註解
	private static String replaceMark(String content) {
		String res = content;
		res = res.replaceAll("--.*", "").replaceAll("\r", "<encodingCode_r>").replaceAll("\n", "<encodingCode_n>")
				.replaceAll("\\/\\*(.*?)\\*\\/", "").replaceAll("<encodingCode_r>", "\r")
				.replaceAll("<encodingCode_n>", "\n");
		return res;
	}

}
