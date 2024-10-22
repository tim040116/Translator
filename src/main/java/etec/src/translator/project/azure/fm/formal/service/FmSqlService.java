package etec.src.translator.project.azure.fm.formal.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FmSqlService {

	private static List<String[]> lstrpl = new ArrayList<>();

	static {
		try (FileInputStream fis = new FileInputStream(new File("config\\replace_list\\fm\\replace_list.csv"));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {
			while (br.ready()) {
				String[] line = br.readLine().replace("\uFEFF","").split(",");
				lstrpl.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String preReplace(String content) {
		String res = content;
		res = removeBteq(res);
		res = res.replaceAll("CALL\\s+[^.]+\\.P_DROP_TABLE\\('[^']+'\\)\\s*;", "");
		
		return res;
	}
	
	public static String easyReplace(String content) {
		String res = content;
		res = replaceTempTableName(res);
		res = toLowerCase(res);
		res = dropCreate(res);
		res = cleanCode(res);
		res = addRerun(res);
		res = addSP(res);
		res = replaceAll(res);
		res = fdpUpt(res);
		res = cleanCode(res);
		return res;
	}
	
	//在程式的最後面加上droptable
	public static String addDropTempTable(String content,List<String> lstTempTable) {
		String res = "\r\n -- 刪除暫存檔";
		String regex = "(?i)IF\\s+OBJECT_ID\\('([^']+)','U'\\)\\s*IS\\s+NOT\\s+NULL"
				+ "\\s+BEGIN"
				+ "\\s+DROP\\s*TABLE\\s+\\1\\s*;"
				+ "\\s*END";
		Matcher m = Pattern.compile(regex).matcher(content);
		while(m.find()) {
			String str = m.group(0);
			if(lstTempTable.contains(m.group(1))) {
				str = "/*\r\n -- rerun\r\n" + str + "\r\n*/";
			}
			res += "\r\n" + str;
		}
		res = "\r\n" + res;
		return res;
	}
	
	/* rerun機制
	 * 1.找到所有target table
	 * 
	 * */
 	public static String addRerun(String content) {
		String res = content;
		String rerunNotNull = "IF  1=1";//準備rerun flag
		String rerunMerge = "";
		List<String> rerunSet = new ArrayList<String>();
		List<String> lstTempTable = new ArrayList<String>();
		List<String> lstTTable = new ArrayList<String>();
		List<String> lstSTable = new ArrayList<String>();
		
		//1.解析merge語法
		/**
		 * target	: target table
		 * tmpA		: 單一table
		 * tmpB		: sub query
		 * */
		String regMerge = "(?i)"
			+ "MERGE\\s+INTO\\s+(?<target>\\S+)[^;]*USING\\s*(?:"//target table
			+ "(?<tmpA>\\S+)\\s+\\w+"//單一table
			+ "|\\([^;]+?FROM\\s+(?<tmpB>[\\w.]+)[^;]+\\)\\s*\\w+)"//sub query
			+ "\\s*ON[^;]+?"//on 只是為了限制範圍
			+ "when\\s+(?:NOT\\s+)?MATCHED"//Match 只是為了限制範圍
			+ "[^;]+;";//到最底，為了組rerun
		Matcher mm = Pattern.compile(regMerge).matcher(res);
		while(mm.find()) {
			//1-1.取得target跟temp table
			String targetTable = mm.group("target");
			String tempTable = mm.group("tmpA")!=null?mm.group("tmpA"):mm.group("tmpB");
			lstTempTable.add(tempTable.toLowerCase().trim());
			//1-2.lstTTable 用來組註解
			if(!lstTTable.contains(targetTable.trim().toLowerCase())) {
				lstTTable.add(targetTable.trim().toLowerCase());
			}
			
			//1-3.if table Not Null 
			rerunNotNull += "\r\n\tAND OBJECT_ID('"+tempTable+"','U') IS NOT NULL";
			//1-4.set @check_rerun_dtl
			String strCnt = "\t\t\tSELECT COUNT(*) cnt"
					+ "\r\n\t\t\tFROM " + tempTable
					+ "\r\n\t\t\tWHERE fdp_upt = @tx_date"
			;
			rerunSet.add(strCnt);
			//1-5.rerun
			rerunMerge += 
				  "\r\n\t" 
				+ mm.group(0)
					.replaceAll("\r\n", "\r\n\t")
					.replaceAll("(?i)when\\s+not\\s+matched\\s+then\\s+[^;]+", "")
				+ "\r\n"
			;
			//1-6.將create跟insert加上fdp_upd
			res = res.replaceAll("(?i)"
				+ "(?:CREATE\\s+TABLE\\s*\\Q"+tempTable+"\\E\\s*WITH\\s*\\([\\S\\s]+?\\)\\s*AS"
				+ "|INSERT\\s+INTO\\s+\\Q"+tempTable+"\\E)"
				+ "\\s*SELECT\\s*"
				,"$0@tx_date as fdp_upt,\r\n"
			);
		}
		//Source table
		Matcher ms = Pattern.compile("(?i)(PDATA|PMART|\\$\\{DATA\\}|\\$\\{MART\\}|dev|tfm|tfmds)\\.\\w+").matcher(content);
		while(ms.find()) {
			String str = ms.group().toLowerCase();
			if(str.matches("(?i)dev\\.stg_\\w+")){
				continue;
			}
			else if(lstSTable.contains(str)) {
				continue;
			}
			else if(lstTTable.contains(str)) {
				continue;
			}
				
			lstSTable.add(str);
		}
		//2.組裝
		String rerun =  
			  "\r\n-- Rerun 機制 " + String.join(" , ", lstTTable)
			+ "\r\nDECLARE @check_rerun_dtl char(1);"
			+ "\r\n" + rerunNotNull
			+ "\r\nBEGIN"
			+ "\r\n\tSET @check_rerun_dtl = ("
			+ "\r\n\t\tSELECT CASE WHEN SUM(A.cnt) > 0 THEN 'Y' ELSE 'N' END"
			+ "\r\n\t\tFROM ("
			+ "\r\n" + String.join("\r\n\t\t\tUNION\r\n", rerunSet)
			+ "\r\n\t\t) A"
			+ "\r\n\t);"
			+ "\r\nEND"
			+ "\r\nELSE BEGIN"
			+ "\r\n\tSET @check_rerun_dtl = 'N';"
			+ "\r\nEND"
			+ "\r\n"
			+ "\r\nIF @check_rerun_dtl = 'Y'"
			+ "\r\nBEGIN"
			+ "\r\n" + rerunMerge
			+ "\r\nEND"
			+ "\r\n-- rerun end"
			+ "\r\n"
		;
		String txdate = "@tx_date";
		res = p_IOPTableLog(txdate,lstTTable,lstSTable)
			+ rerun
			+ res
			+ addDropTempTable(content,lstTempTable);
		return res;
	}
	//依開發規範轉換
	public static String designPattern(String content) {
		String res = content;
		res = res.replaceAll("(?i)\\bgetDate\\(\\)","dateadd\\(hour,8,getdate\\(\\)\\)");
		return res;
	}
	//依照參數檔批量轉換
	public static String replaceAll(String content) {
		String res = content;
		for(String[] arr : lstrpl) {
			String reg = "(?i)"+Pattern.quote(arr[0].trim())+"\\b";
			String rpm = Matcher.quoteReplacement(arr[1].trim());
			res = res.replaceAll(reg,rpm);
		}
		return res;
	}
	/**
	 * 加sp語法
	 */
	public static String addSP(String content) {
		String res = content;
		String txdate = "@tx_date";
		String txdate1 = "@v_tx_date";
		res = "CREATE PROCEDURE dev.sp__ldtf\r\n\t"
			+ txdate + " varchar(10)"
			+ "\r\nAS BEGIN"
			+ "\r\n\r\n\tDECLARE " + txdate1 + " int;"
			+ "\r\n\tSET " + txdate1 + " = cast(convert(varchar(8),cast(" + txdate + " as date),112) as int);\r\n"
			+ "\r\n\t" + res.trim().replaceAll("\n", "\n\t")
			+ "\r\n\r\nEND";
		//TXDATE處理
		res = res
			.replaceAll(Pattern.quote("${TXDATE1}"), txdate1)
			.replaceAll("(?i)CAST\\s*\\(\\s*'\\$\\{TXDATE\\}'\\s*AS\\s*DATE\\s*\\)",Matcher.quoteReplacement("cast("+txdate+" as date)"))
			.replaceAll("'\\$\\{TXDATE\\}'",txdate)
		;
		return res;
	}

	/**
	 * 關於temp table 的處理
	 *
	 * 依宇皇提供的開發規範(2024/09/02) table name一律小寫 存於dev. stg_開頭 若有複數temp table則編號放在最後
	 *
	 */
	public static String replaceTempTableName(String content) {
		String res = content;
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)(?:#|tempdb\\.\\.|TEMP_TABLE\\.)(?:TP(\\d+)?_)?(\\w+)").matcher(res);
		while (m.find()) {
//			String all = m.group(0);
			String tmpId = m.group(1);
			String tblNm = m.group(2);
			tmpId = tmpId == null ? "" : "_" + tmpId;
			tblNm = tblNm.toLowerCase();
			String rpm = "dev.stg_" + tblNm + tmpId;
			m.appendReplacement(sb, Matcher.quoteReplacement(rpm));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 關於temp table 的處理
	 *
	 * 依宇皇提供的開發規範(2024/09/02) table name及column name一律小寫
	 *
	 */
	public static String toLowerCase(String content) {
		String res = content;
		StringBuffer sbt = new StringBuffer();
		Matcher mt = Pattern.compile("(?i)([${}\\w]+)\\.(\\w+)").matcher(res);
		while (mt.find()) {
			String dbNm = mt.group(1);
			String tbNm = mt.group(2);
			if (!dbNm.matches("\\$\\{\\w+\\}")) {
				dbNm = dbNm.toLowerCase();
			}
			tbNm = tbNm.toLowerCase();
			String rpm = dbNm + "." + tbNm;
			mt.appendReplacement(sbt, Matcher.quoteReplacement(rpm));
		}
		mt.appendTail(sbt);
		Matcher mc = Pattern.compile("(?i)[0-Za-z]+(?:_[a-z0-Z]+)+|(?<=AS)\\s+\\w+").matcher(sbt.toString());
		StringBuffer sbc = new StringBuffer();
		while(mc.find()) {
			mc.appendReplacement(sbc,mc.group().toLowerCase());
		}
		mc.appendTail(sbc);
		res = sbc.toString();
		return res;
	}

	/**
	 * 依照全家提供drop create的語法規格 順便排除奇怪的SP語法
	 */
	public static String dropCreate(String content) {
		String res = content;
		res = res
				.replaceAll("(?i)(IF OBJECT_ID\\('[^']+')\\) IS NOT NULL (DROP TABLE \\S+)",
						"$1,'U'\\) IS NOT NULL \r\nBEGIN\r\n$2;\r\nEND")
				.replaceAll("(?i)EXEC\\s+[^.]+\\.p_drop_table\\s+'([^']+)'.*;",
						"IF OBJECT_ID\\('$1','U'\\) IS NOT NULL\r\nBEGIN\r\nDROP TABLE $1; \r\nEND");

		return res;
	}

	/**
	 * 移除bteq語法
	 **/
	public static String removeBteq(String content) {
		String res = content;
		res = res
			.replaceAll("(?i)^\\s*\\.IF\\s+ERRORCODE\\b[^;]+;", "")
			.replaceAll("(?i)^\\s*\\.SET\\s+ERROROUT\\s+STDOUT\\s*;?", "")
			.replaceAll("(?i)^\\s*\\.SET\\s+SESSION\\b[^;]+;", "")
			.replaceAll("(?i)^\\s*\\.LOGON\\s+[^,]+,\\s*\\S+\\s*", "")
			.replaceAll("(?i)^\\s*\\.LOGOFF\\s*;","")
			.replaceAll("(?i)^\\s*\\.QUIT\\s*0\\s*;","")
		;
		return res;
	}

	/**
	 * 清理跑版的語法
	 **/
	public static String cleanCode(String content) {
		String res = content;
		res = res
				.replaceAll("\uFEFF", "")
				.replaceAll("(?i)\r?\nDATABASE\\s*\\S+\\s*;", "")// database
				.replaceAll("(?i)\t+CASE", "CASE")// CASE
				.replaceAll("(?i),\\s+ROLLUP\\(", ",ROLLUP\\(")// ,\r\nROLLUP
				.replaceAll("    ","\t")
				.replaceAll("[ \\t]+\\r?\\n","\r\n")
				// DROP兩次
//				.replaceAll("(?i)IF OBJECT_ID\\('([^']+)','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END\\s+(IF OBJECT_ID\\('\\1','U'\\) IS NOT NULL\\s+BEGIN\\s+DROP TABLE \\1;\\s+END)","$2")
				//排版
				.replaceAll(" *, *",",")
				.replaceAll(",[ \t]*(\r?\n[ \t]++)(?!,)","$1,")
				.replaceAll("(?<! )(?==|<>)|(?<==|<>)(?! )"," ")
				.replaceAll("\\s*;",";")
				.replaceAll("(?!\\s|^);\\s*","\r\n\t;\r\n\r\n\t")
				.replaceAll("(?i)([ \t]+)(SELECT *(?:DISTINCT *)?)(\\S+)","$1$2\r\n$1\t$3")
				.replaceAll("(?i)\\s*(,fdp_upt)\\)", "$1\r\n\t\t\\)")
				.replaceAll("(?i)([ \t]+)(DROP TABLE \\S+)\\s*;\\s*END","$1$2;\r\n$1END")
				.replaceAll("(?i)\\s*?([\t ]+)(FROM|JOIN)\\s*\\(\\s*","\r\n$1$2 \\(\r\n$1\t")
				.replaceAll("(?i)\\s*([ \t]+-- GROUP BY)","\r\n$1")
				.replaceAll("(?i)\\s*?([\t ]+)(UNION(?:\\s*ALL)?)\\s*","\r\n$1\r\n$1$2\r\n$1")
				.replaceAll("(?i)(\\s*)([^\r\n]*)\\s*(,fdp_upt = dateadd)","$1$2$1$3")
				.replaceAll("(?i)(\n\\s*)(.*)\\s*(,dateadd\\(hour,8,getdate\\(\\)\\))","$1$2$1$3")
				.replaceAll("(\r?\n){3,}", "\r\n")
				.replaceAll("(?i)\\s*(OUTER|INNER|LEFT|RIGHT|CROSS)\\s+JOIN","\r\n$1 JOIN")
				.replaceAll("\tCLUSTERED", "\t CLUSTERED")
				.replaceAll("([\t ]+)WHEN\\s+(NOT\\s+)?MATCHED\\s*THEN\\s+","$1WHEN $2MATCHED THEN\r\n$1\t")
				.replaceAll("\tCLUSTERED", "\t CLUSTERED")
				.replaceAll("(INSERT|VALUES)\\s*\\([ \t]*\\b", "$1 \\(\r\n\t")
				.replaceAll("USING\\s*\\(", "USING \\(")
				.replaceAll("(?i)\\s+(ON|WHEN|END|AND|OR)\\s+", "\r\n\t$1 ")
				.replaceAll("\\s*([+\\-*/=])\\s*"," $1 ")
				.replaceAll("(?i)\\(\\s*SELECT", "\\(\r\n\tSELECT")
		;
		return res;
	}	

	/**
	 * Merge into語法增加fdp_upt欄位，是異動日期
	 **/
	public static String fdpUpt(String content) {
		String res = content;
		res = res
			.replaceAll("(?i)(when\\s+matched\\s+then\\s+[\\S\\s]+?)(\\s+when\\s*not\\s*matched|;)", "$1\r\n\t\t\t,fdp_upt = dateadd\\(hour,8,getdate\\(\\)\\)$2")
			.replaceAll("(?i)(when\\s*not\\s*matched\\s*then\\s*insert\\s*\\([^)]+?)(\\s*\\)\\s*values\\s*\\([^;]+?)(\\s*\\)\\s*;)", "$1\r\n\t\t\t,fdp_upt$2\r\n\t\t\t,dateadd\\(hour,8,getdate\\(\\)\\)$3")
		;
		return res;
	}
	
	public static String cleanLayout(String sql) {
		String res = sql;
		
		
		return res;
	}
	
	
	
	//input output的註解
	private static String p_IOPTableLog(String txdate,List<String> lstTTable,List<String> lstSTable) {
		String res = "\r\n/*--------------------------------------------------------------------------"
				+ "\r\n\tSP參數"
				+ "\r\n\t\t"+txdate+" = ${TXDATE}"
				+ "\r\n\tINPUT"
				+ "\r\n\t\t"
				+ String.join("\r\n\t\t", lstSTable)
				+ "\r\n\tOUTPUT"
				+ "\r\n\t\t"
				+ String.join("\r\n\t\t", lstTTable)
				+ "\r\n--------------------------------------------------------------------------*/"
				+ "\r\n";
		return res;
	}
}
