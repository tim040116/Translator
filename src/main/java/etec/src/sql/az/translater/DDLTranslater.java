package etec.src.sql.az.translater;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;
import etec.common.utils.log.Log;
import etec.framework.translater.exception.SQLTranslateException;

public class DDLTranslater {
	
	public static String easyReplace(String title,String sql) throws SQLTranslateException {
		String res = sql;
		if(sql.matches("(?i)\\s*CREATE(?:\\s+(?:TEMP|SET|MULTISET))?\\s+[\\S\\s]+")) {
			
			if(sql.matches("(?i)\\s*Create\\s+Table\\s+\\S+\\s+As\\s*[\\S\\s]+")) {
				Log.debug("\t\t細分：CTAS");
				sql = runCTAS(sql);
			}else if(sql.matches("(?i)\\s*Create\\s+TEMP\\s+Table\\s+\\S+\\s+As\\s*[\\S\\s]+")) {
				Log.debug("\t\t細分：CTAS TEMP TABLE");
				sql = runCreateTable(sql);
			}else{
				sql = runCreateTable(sql);
			}
		}else if("DROP".equals(title)){
			res = runDropTable(res);
		}else if("RENAME".equals(title)){
			res = runRenameTable(res);
		}else if("REPLACE".equals(title)){
			res = runReplaceView(res);
		}	
		return res;
	}
	// create table
	public static String runCreateTable(String sql) throws SQLTranslateException {
		// create語法轉換
		String res = sql;
		res = replaceCreateTitle(res);
		res = replaceColumn(res);
		// Index語法轉換
		res = addWith(res);
		// drop if exist
		String drop = "";
		res = drop + res;
		return res;
	}
	
	/**
	 * <h1>CTAS</h1>
	 * <p>
	 * <br>先清除註解並處理index
	 * <br>拆開sub query 
	 * <br>加上 if not exists
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月20日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String runCTAS(String sql) throws SQLTranslateException {
		String res = sql;
		
		res = replaceCreateTitle(res);
		res = addWith(res);
		
		/**
		 * <p>功能 ：CTAS拆解</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：is</p>
		 * <p>範圍 ：從  create 到 with</p>
		 * <h2>群組 ：</h2>
		 * 	1.setting 	: VOLATILE,MULTISET...等修飾詞
		 *  2.tableName : 如題
		 *  3.select	: CTAS的sub query
		 * <h2>備註 ：</h2>
		 * <p>
		 * </p>
		 * <h2>異動紀錄 ：</h2>
		 * 2024年6月20日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?is)"
				+ "CREATE\\s+(?<setting>.*?)\\s+"
				+ "TABLE\\s+(?<tableName>[^,\\s()]+)\\s+"
				+ "AS\\s*\\(\\s*(?<select>SELECT.*)?"
				+ "\\)(?=.*WITH\\s*\\()";
		Matcher m = Pattern.compile(reg).matcher(res);
		while (m.find()) {
			String newSql = "";
			String setting = m.group("setting");
			String tableName = m.group("tableName");
			String select = m.group("select");
			
			select = DQLTranslater.easyReplace(select);
			
			
			newSql = "CREATE TABLE IF NOT EXISTS "+tableName + " (\r\n"
					+ select+"\r\n)";
			
			m.appendReplacement(sb, Matcher.quoteReplacement(newSql));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * 可用於drop table跟drop view
	 * 會加上if exist的語法
	 * 
	 * */
	public static String runDropTable(String sql){
		String res = sql
				.replaceAll("(?i)DROP\\s+TABLE\\s+([^;]+)", "IF OBJECT_ID(N'$1') IS NOT NULL\r\nDROP TABLE $1");
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * rename table 改成rename object
	 * 新名稱不要加DBname
	 * */
	public static String runRenameTable(String sql) throws SQLTranslateException {
		String result = sql;
		result = result.replaceAll("(?i)RENAME\\s+TABLE\\s+(\\S+)\\s+TO\\s+([^;]+)", "RENAME OBJECT $1 TO $2");
		return result;
	}
	
	/**
	 * <h1>replace view</h1>
	 * <p>replace view ... </p>
	 * <p>ALTER VIEW ...</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月19日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String runReplaceView(String sql){
		String res = "";
		res = sql.replaceAll("(?i)REPLACE\\s+VIEW", "ALTER VIEW")+ "\r\n;";
		return res;
	}
	
	// 轉換create table
	public static String replaceCreateTitle(String sql) {
		String result = sql.replaceAll("(?i)\\s*,\\s*NO\\s*FALLBACK\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*NO\\s*[A-Za-z]+\\s*JOURNAL\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*CHECKSUM\\s*=\\s*[A-Za-z]+\\s*", " ")
				.replaceAll("(?i)\\s*,\\s*DEFAULT\\s*MERGEBLOCKRATIO\\s*", " ")
				.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
				.replaceAll("(?i)(NOT\\s+)?CASESPECIFIC", " ")
				.replaceAll("(?i)\\bON\\s+COMMIT\\b", "") // on commit
				.replaceAll("(?i)\\bPRESERVE\\s+ROWS\\b","")//PRESERVE ROWS
				.replaceAll("(?i)TITLE\\s+'[^']+'", " ");
		return result;
	}
	/**
	 * <h1>with處理</h1>
	 * <p>
	 * <br>不一定要整段SQL，可以只提供 最後段的設定
	 * <br>首先要清除所有index
	 * <br>在SQL尾端加上with語法
	 * <br>如果原本有INDEX,要用HASH
	 * </p>
	 * <p>
	 * with(
	 * 	CLUSTERED COLUMNSTORE INDEX,
	 * 	DISTRIBUTION = [ REPLICATE | HASH(...) ]
	 * )
	 * </p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月19日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	index 的處理
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String addWith(String sql) {
		String result = sql;
		/**
		 * <p>功能 ：取得index</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：gmi</p>
		 * <p>範圍 ：從  到 </p>
		 * <h2>群組 ：</h2>
		 * 	1.修飾詞
		 * 	2.欄位
		 * <h2>備註 ：</h2>
		 * 	
		 * <h2>異動紀錄 ：</h2>
		 * 2024年6月19日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		List<String> lst = new ArrayList<String>();
		String reg = "(?i)((?:(?:UNIQUE|PRIMARY)\\s+)*)INDEX\\s*\\(([^()]+)\\)";
		Matcher m = Pattern.compile(reg).matcher(result);
		while(m.find()) {
			lst.add(m.group(2));
			m.appendReplacement(sb, "");
		}
		String distribution = "\r\nWITH (" 
				+ "\r\n\tCLUSTERED COLUMNSTORE INDEX,"
				+ "\r\n\tDISTRIBUTION = "
				+ (lst.isEmpty()?"REPLICATE":"HASH( " + String.join(",", lst) + " )")
				+ "\r\n)\r\n;"
				;
//		sb.append(distribution);
		m.appendTail(sb);
		
		result = sb.toString().replaceAll("\\s*;\\s*$","") + distribution;
//		
//		// 取得欄位
//		List<String> lstPrimaryIndex = RegexTool.getRegexTarget("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
//		temp = temp.replaceAll("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", "");
//		List<String> lstIndex = RegexTool.getRegexTarget("(?i)PRIMARY\\s+INDEX\\s+\\([^\\)]+\\)", temp);
//		// 添加欄位
//		String column = "";
//		if (!lstPrimaryIndex.isEmpty()) {
//			String indexCol = lstPrimaryIndex.get(0).replaceAll("(?i)UNIQUE\\s+PRIMARY\\s+INDEX\\s+\\(", "")
//					.replaceAll("\\)", "").replaceAll("\\s", "").trim();
//			column += indexCol;
//		}
//		if (!lstIndex.isEmpty()) {
//			if (!lstPrimaryIndex.isEmpty()) {
//				column += ",";
//			}
//			String indexCol = lstIndex.get(0).replaceAll("(?i)PRIMARY\\s+INDEX\\s+\\(", "").replaceAll("\\)", "")
//					.replaceAll("\\s", "").trim();
//			column += indexCol;
//		}
//		String hash = "\r\n\tDISTRIBUTION = " + ("".equals(column) ? "REPLICATE" : "HASH(" + column + ")");
//		result += hash + "\r\n)";
		return result;
	}
	
	// 清除TD特有的語法
	@Deprecated
	public
	static String replaceTDsql(String sql) {
		String result = sql
				.replaceAll("(?i)\\bVOLATILE\\b", "")// VOLATILE
				.replaceAll("(?i)\\bMULTISET\\b", "")// MULTISET
				.replaceAll("(?i)SET\\s+TABLE", "TABLE")// SET TABLE
				.replaceAll("(?i)(UNIQUE\\s+)?(PRIMARY\\s+)?INDEX\\s+\\([^\\)]+\\)", " ")// UNIQUE PRIMARY INDEX
				.replaceAll("(?i)NO\\s+PRIMARY\\s+INDEX", "")
				.replaceAll("(?i)RANGE_N" + "\\s*\\([^\\)]+\\)", " ")// PARTITION BY
				.replaceAll("(?i)PARTITION\\s+BY\\s*(\\s*\\([^\\)]*\\))?", " ")// PARTITION BY
		;
		return result;
	}
	
	// 清除欄位的多餘設定
	public static String replaceColumn(String sql) {
		String result = sql.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
				.replaceAll("(?i)NOT\\s+CASESPECIFIC", " ")
				.replaceAll("(?i)TITLE\\s+'[^']+'", " ")
				.replaceAll("(?i)\\s*FORMAT\\s+'[^']+'\\s*", " ")
				.replaceAll("(?i)TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
				.replaceAll("(?i)VARBYTE", "VARBINARY")
				.replaceAll(" +,", ",");
		return result;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年11月17日
	 * 
	 * 解決ms sql 不支援CTAS語法問題，將CTAS轉為select into語法
	 * */
	public static String runCTASToSelectInto(String sql) {
		String res = "";
		String tableNm = sql.replaceAll("(?i)\\s*CREATE\\s+TABLE\\s+(\\S+)\\s+[\\S\\s]+", "$1");
		String selectSrc = sql.replaceAll("(?i)[\\S\\s]+\\s+AS\\s+SELECT", "SELECT");
		String[] arrFrom = selectSrc.split("(?i)FROM");
		for(String str: arrFrom) {
			res+="".equals(res)?(str.trim()+"\r\nINTO "+tableNm+"\r\n"):("FROM"+str);
		}
		res = res.trim()
//				.replaceAll(regex, replacement)
				;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月24日
	 * 
	 * 解決ms sql 不支援CTAS語法問題，將CTAS轉為select into語法
	 * */
	public static String runSelectIntoToCTAS(String sql) {
		String res = "";
		String tableNm = sql.replaceAll("(?i)[\\S\\s]+INTO\\s+(\\S+)\\s+FROM[\\S\\s]+", "$1");
		String selectSrc = sql.replaceAll("(?i)INTO\\s+(\\S+)\\s+", "");
		res = "CREATE TABLE " + tableNm + "\r\n"
				+ "WITH ( \r\n"
				+ "\tCLUSTERED COLUMNSTORE INDEX,\r\n"
				+ "\tDISTRIBUTION = REPLICATE\r\n"
				+ ")\r\nAS\r\n"+selectSrc
				;
		return res;
	}
}
