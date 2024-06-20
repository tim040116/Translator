package etec.src.sql.az.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.log.Log;
import etec.framework.translater.enums.SQLTypeEnum;
import etec.framework.translater.exception.SQLTranslateException;
import etec.src.sql.gp.translater.GreenPlumTranslater;
import etec.src.sql.gp.translater.service.MergeIntoService;
import etec.src.sql.td.classifier.TeradataClassifier;

public class DMLTranslater {
	
	/**
	 * <h1>轉換DML</h1>
	 * <p>
	 * <br>轉換 INSERT INTO
	 * <br>轉換 INSERT SELECT
	 * <br>轉換 DELETE TABLE
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月15日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public String easyReplace(String title,String sql) throws SQLTranslateException {
		SQLTypeEnum type = TeradataClassifier.getSQLType(sql);
		switch(type) {
		case INSERT_TABLE:
			Log.debug("\t細分：INSERT  INTO");
			sql = runInsertInto(sql);
			break;
		case INSERT_SELECT:
			Log.debug("\t細分：INSERT  SELECT");
			sql = runInsertSelect(sql);
			break;
		case DELETE:
			Log.debug("\t細分：DELETE TABLE");
//			sql = changeDeleteTableUsing(sql);
			break;
		case UPDATE:
			Log.debug("\t細分：UPDATE TABLE");
//			sql = changeUpdateTable(sql);
			break;
		case MERGE_INTO:
			Log.debug("\t細分：MERGE INTO");
			sql = runMergeInto(sql);
			break;
		default:
			break;
		}
  		return sql;
	}
	//insert into
	public static String runInsertInto(String sql) throws SQLTranslateException {
		String res = sql;
//		res = DQLTranslater.changeSample(res);
//		res = DQLTranslater.easyReplace(res);
		return res+"\r\n;";
	}
	
	//insert select
	public static String runInsertSelect(String sql) throws SQLTranslateException {
//		String res = "";
//		String insert = RegexTool.getRegexTarget("INSERT\\s+(?:INTO\\s+)?\\S+\\s+", sql).get(0).trim();
//		String select = RegexTool.decodeSQL(RegexTool.encodeSQL(sql).replaceAll(RegexTool.encodeSQL(insert), ""));
//		res = insert+"\r\n"+DQLTranslater.easyReplace(select);
		
		String res = "";
		String[] arr = sql.split("(?i)\\bSELECT\\b", 2);
		String insert = arr[0];
		String select = sql.replace(arr[0],"");
		select = DQLTranslater.easyReplace(select);
 		res = insert+select;
		return res;
	}
	
	/**
	 * <h1>merge into 轉換</h1>
	 * <p>
	 * <br>Az 支援merge into 語法，但經測試後發現效能太差，
	 * <br>決定改比照GP語法，將語法拆分成insert跟update 兩段語法
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月20日	Tim	改使用gp語法
	 * 
	 * @author	Tim
	 * @since	4.1.0.0.0
	 * @param	sql
	 * @throws	e
	 * @see		{@link etec.src.sql.gp.translater.DMLTranslater#changeMergeInto(String)}
	 * @return	return_type
			 */
	public static String runMergeInto(String sql) throws SQLTranslateException {
		String res = "";
		/**
		 * <p>功能 ：第一步拆解merge into 語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：is</p>
		 * <p>範圍 ：從 merge 到 ;</p>
		 * <h2>群組 ：</h2>
		 * tableNm. table name
		 * using. 	using
		 * when1. 	第一段是否MATCH
		 * sql1. 	第一段SQL
		 * when2. 	第二段when
		 * sql2.	第二段SQL
		 * <h2>備註 ：</h2>
		 * <p>
		 * </p>
		 * <h2>異動紀錄 ：</h2>
		 * 2024年5月31日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?is)"
				+ "MERGE\\s+INTO\\s+(?<tableNm>.*?)"
				+ "USING(?<using>.*?)"
				+ "WHEN\\s+(?<when1>(?:NOT\\s+)?MATCHED)\\s+THEN\\s+(?<type1>\\w+)(?<sql1>[^;]+?)"
				+ "WHEN\\s+(?<when2>(?:NOT\\s+)?MATCHED)\\s+THEN\\s+(?<type2>\\w+)(?<sql2>[^;]+);?";
		Matcher m = Pattern.compile(reg).matcher(sql);
		while (m.find()) {
			String merge = "";
			String tableNm = m.group("tableNm").trim().replaceAll("^#", "");
			String using = m.group("using").trim();
			String sql1  = m.group("sql1");
			String sql2  = m.group("sql2");
			//處理WHEN 1
			sql1 = MergeIntoService.convert(m.group("type1"), tableNm, using, m.group("sql1").trim());
			sql1 = GreenPlumTranslater.translate(sql1);
			//處理WHEN 2
			sql2 = MergeIntoService.convert(m.group("type2"), tableNm, using, m.group("sql2").trim());
			sql2 = GreenPlumTranslater.translate(sql2);
			
			merge = sql1+"\r\n\r\n"+sql2;
			m.appendReplacement(sb,Matcher.quoteReplacement(merge));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
		
//		String res = "";
//		String mergeInto = sql.replaceAll("\\s*USING\\s*\\([^;]+","");
//		String using = "";
//		String when = "";
//		String temp = "";
//		String status = "BEGIN";
//		int	bracketCnt = -1;
//		for(String c : sql.split("")) {
//			if("BEGIN".equals(status)) {
//				temp+=c;
//				if(temp.toUpperCase().replaceAll("\\s+", " ").contains(("USING"+" (").toUpperCase().replaceAll("\\s+", " "))) {
//					status = "USING";
//					bracketCnt = 1;
//				}
//			}
//			else if("USING".equals(status)) {
//				if(c.equals("(")) {
//					bracketCnt++;
//				}else if(c.equals(")")) {
//					bracketCnt--;
//				}
//				if(bracketCnt==0) {
//					status = "AFTER";
//					continue;
//				}
//				using+=c;
//			}
//			else if("AFTER".equals(status)){
//				when+=c;
//			}
//			
//		}
//		res = mergeInto+"\r\nUSING (\r\n"+using.trim()+"\r\n)"+when+"\r\n";
//		return res;
	}
}
