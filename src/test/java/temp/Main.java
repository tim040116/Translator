package temp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.factory.TranslaterFactory;
import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.context.translater.exception.SQLFormatException;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.project.azure.fm.formal.service.FmSqlService;
import etec.src.translator.sql.gp.translater.GreenPlumTranslater;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class Main {

	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target\\show_procedure_2.txt";

	static String outputFile = "C:\\Users\\user\\Desktop\\Trans\\Assessment_Result\\test2\\"; 
	
	
	//轉全家UI的SP轉乘PG
	/**
	 * 把EXEC的語法做優化
	 * 
	 * */
	public static void main(String[] args) {
		try {
			String content = FileTool.readFile(folder);
			
			String regex = "(?i)(?:CREATE|REPLACE)\\s+PROCEDURE\\s+(\\w+\\.\\w+)[\\S\\s]+?END\\s+SP\\s*;";
			Matcher m = Pattern.compile(regex).matcher(content);
			while(m.find()) {
				String strsp = m.group();
				String tblNm = m.group(1);
				String newContent = strsp;
				newContent = ConvertRemarkSafely.savelyConvert(newContent,(t) -> {
					try {
						StringBuffer sb = new StringBuffer();
						String reg = "(#\\*s)?\\b(?:" + String.join("|",TranslaterFactory.getTitleList()) + ")\\b[^;]+?;";
						Matcher m2 = Pattern.compile(reg, Pattern.CASE_INSENSITIVE).matcher(t);
						while (m2.find()) {
							// 處理前後空白
							String sql = m2.group().trim();
							sql = GreenPlumTranslater.translate(sql);
							m2.appendReplacement(sb, Matcher.quoteReplacement(sql + "\r\n"));
						}
						m2.appendTail(sb);
						t = sb.toString();
						t = p_transSP(t);
						t = t //型態轉換
							.replaceAll("(?i)CALL\\s+PMART\\.P_DROP_TABLE\\s*\\('([^']+)'\\s*\\)\\s*;", "DROP TABLE IF EXISTS $1;")
							.replaceAll("(?i)CASESPECIFIC","")
							.replaceAll("(?i)\\bnumber\\b", "numeric")
							.replaceAll("(?i)\\bVARCHAR\\(\\8000\\)", "TEXT")
							.replaceAll("(?i)\\b(?:N)?(?:VAR)?CHAR", "character")
							.replaceAll("(?i)decode\\(([^,]+),(-?\\d+),null,\\1\\)","NULLIF\\($1,$2\\)")
							.replaceAll("(?i),\\s*NO\\s+JOURNAL","")
							.replaceAll("(?i),\\s*NO\\s+LOG","")
							.replaceAll("(?i)DECLARE\\s+(\\S+)\\s+character","DECLARE $1 TEXT")
							.replaceAll("(?i)CAST\\s*\\(\\s*([^()]+?)\\s+AS\\s+(\\w+)\\s*\\)", "$1::$2	")
						;
						t = t //清理
							.replaceAll(";\\s*'\\s*;",";';")
							.replaceAll("--\\s*'\\s*(.*)\\s*'\\s*\\|\\|", "'-- $1'||")
							.replaceAll("--'([^']+\\s*;\\s*')\\s*;", "--'--$1")
						;
						t = t //清理
							.replaceAll("bit_extract","bit_count")
							.replaceAll("bit_and\\(([^,]+),([^,]+)\\)", "$1 & $2")
							.replaceAll("bit_or\\(([^,]+),([^,]+)\\)", "$1 | $2")
						;
						t = p_transExcute(t);
						t = t //清理
							.replaceAll("(?i)\\s+(?=left\\s+join|distinct|from|on|and|where)\\b", "\r\n\t")
							.replaceAll("(?i)\\s*(=)\\s*", " $1 ")
							.replaceAll("(?i)\\s+ON\\s+COMMIT", " ON COMMIT")
							.replaceAll("[ \\t]+\\r?\\n", "\r\n")
							.replaceAll("    ", "\t")
							.replaceAll("(?i)\\bSET\\s+(\\w+)\\s*=\\s*'", "$1 := '")
							.replaceAll(": =", ":=")
							.replaceAll("(?i)MULTISET", "")
							.replaceAll("(?i)TEMP_TABLE\\.", "")
							.replaceAll("(\\r?\\n){3,}","\r\n\r\n")
						;
						t = FmSqlService.replaceAll(t);
						//t = t.replaceAll("dev\\.|tfm\\.|tfmds\\.","public.");
						t = GreenPlumTranslater.sql.easyReplase(t);
						t = GreenPlumTranslater.dql.changeMultAnalyze(t);
					} catch (SQLFormatException e) {
						e.printStackTrace();
					} catch (SQLTranslateException e) {
						e.printStackTrace();
					}
					return t;
				});
				
				FileTool.createFile(outputFile+"new\\"+tblNm+".sql", newContent);
				FileTool.createFile(outputFile+"old\\"+tblNm+".sql", strsp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("完成");
	}
	
	private static String p_transSP(String t) {
		List<String> lstD = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)\\bDECLARE\\s+(\\w+)\\s+([^;]+);").matcher(t);
		while(m.find()) {
			lstD.add(m.group(1)+" "+m.group(2));
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		String declare = "";
		if(!lstD.isEmpty()) {
			declare = "DECLARE " + String.join(";\r\n\t\t", lstD)+";";
			declare = declare.replaceAll("(?i)\\b(?:TEXT|VARCHAR|CHAR|character)\\b(?:\\s*\\([\\d ]+?\\))?", "TEXT");
		}
		String res = sb.toString() //SP語法
			.replaceAll("(?i)replace\\s+PROCEDURE\\s+([\\w.]+)", "CREATE OR REPLACE PROCEDURE $1")
			.replaceAll("(?i)SQL\\s+SECURITY\\s+INVOKER", "")
			.replaceAll("(?i)SP:BEGIN","LANGUAGE 'plpgsql'\r\nAS \\$BODY\\$\r\n\r\n"+declare+"\r\n\r\nBEGIN\r\n")
			.replaceAll("(?i)\\bEND\\s+SP\\s*;","END;\r\n\\$BODY\\$;")
			.replaceAll("(?i)EXECUTE\\s+IMMEDIATE\\s+", "EXECUTE ")
		;
		return res;
	}
	
	private static String p_transExcute(String t) {
		// 處理Excute語法
		StringBuffer sbSqlstr = new StringBuffer();
		Matcher mSqlstr = Pattern.compile("(?i)set\\s+SqlStr\\s*=\\s*'([\\S\\s]+?;)\\s*'\\s*;").matcher(t);
		while(mSqlstr.find()) {
			List<String> lstReplace = new ArrayList<String>();
			String str = mSqlstr.group();
			String subSql = mSqlstr.group(1);
			str = str
				.replaceAll("(?i)'\\s*\\|\\|\\s*'\\s*", "\r\n\t")
			;
			Matcher mcol = Pattern.compile("(?i)'\\s*\\|\\|\\s*(\\w+)\\s*\\|\\|\\s*'").matcher(subSql);
			while(mcol.find()) {
				if(!lstReplace.contains(mcol.group(1))) {
					lstReplace.add(mcol.group(1));
					str = str.replaceAll("\\Q"+mcol.group(0)+"\\E", "<"+mcol.group(1)+">");
				}
			}
			for(String replace : lstReplace) {
				str += "\r\n\tSqlStr := REPLACE(SqlStr,'<"+replace+">',"+replace+"::TEXT);";

			}
			mSqlstr.appendReplacement(sbSqlstr, Matcher.quoteReplacement(str));
		}
		mSqlstr.appendTail(sbSqlstr);
		return sbSqlstr.toString();
	}

}
