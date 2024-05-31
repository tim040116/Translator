package etec.src.sql.gp.translater;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.sql.SQLFormatException;
import etec.common.exception.sql.SQLTransduceException;
import etec.common.exception.sql.UnknowSQLTypeException;
import etec.common.utils.convert_safely.SplitCommaSafely;
import etec.common.utils.log.Log;

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
	public String easyReplace(String sql) throws SQLTransduceException {
		if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+\\S+\\s+VALUES\\b[\\S\\s]+")) {
			Log.debug("\t\t細分：INSERT  INTO");
		}else if(sql.matches("(?i)\\s*INSERT\\s+(?:INTO\\s+)?[\\S\\s]+")) {
			Log.debug("\t\t細分：INSERT  SELECT");
			sql = changeInsertSelect(sql);
		}else if(sql.matches("(?i)\\s*DELETE\\s+[\\S\\s]+")) {
			Log.debug("\t\t細分：DELETE TABLE");
			sql = changeDeleteTableUsing(sql);
		}else if(sql.matches("(?i)\\s*UPDATE\\s+[\\S\\s]+")) {
			Log.debug("\t\t細分：UPDATE TABLE");
			sql = changeUpdateTable(sql);
		}else {
		}
  		return sql;
	}
	/**
	 * 
	 * DELETE TABLE 要加上 USING
	 * 
	 * @author	Tim
	 * @throws SQLFormatException 
	 * @since	4.0.0.0 
	 * */
	public String changeDeleteTableUsing(String sql) throws SQLFormatException {
		String res = sql;
		/**
		 * <p>功能 ：DELETE TABLE</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從 DELETE 到 ;</p>
		 * <h2>群組 ：</h2>
		 * <br>	1.table name
		 * <br> 2.usingTable
		 * <br>	3.where
		 * <h2>備註 ：</h2>
		 * <p>index 還沒處理
		 * </p>with data 的部分也要再確認
		 * <h2>異動紀錄 ：</h2>
		 * <br>2024年5月16日	Tim	建立邏輯
		 * <br>2024年5月23日	Tim	
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)DELETE\\s+FROM\\s+(?<tableNm>\\S+(?:\\s+\\w+)?)(?:\\s*,(?<usingTable>[^;]+))?\\s*WHERE(?<where>[^;]+)";
		Matcher m = Pattern.compile(reg).matcher(res);
		while (m.find()) {
			String table   = m.group("tableNm") != null ? m.group("tableNm") : "";
			String using   = m.group("usingTable") != null ? m.group("usingTable") : "";
			String where   = m.group("where") != null ? m.group("where") : "";
			String delete  = "DELETE FROM "+table.trim()+"\r\nUSING "+using.trim()
					+"\r\nWHERE\r\n\t"
					+GreenPlumTranslater.sql.easyReplase(where).trim().replaceAll("\\s*;$","")
					+"\r\n;"
					; 
			m.appendReplacement(sb, Matcher.quoteReplacement(delete));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
	}
	
	/**
	 * <h1>INSERT SELECT語法轉換</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2023年12月26日	Tim	建立功能
	 * <br>2024年 4月22日	Tim	修正insert語法有欄位名稱時會錯位的問題
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	SQLTransduceException
	 * @see		
	 * @return	String
	 */
	public String changeInsertSelect(String sql) throws SQLTransduceException {
		String res = "";
		String[] arr = sql.split("(?i)\\bSELECT\\b", 2);
		String insert = arr[0];
		String select = sql.replace(arr[0],"");
		select = GreenPlumTranslater.dql.easyReplace(select);
		select = select.replaceAll("(?i)^\\s*SELECT(?:\\s+DISTINCT\\b)?", "SELECT DISTINCT\r\n");
 		res = insert+select;
		return res;
	}
	
	/**
	 * <h1>UPDATE SELECT語法轉換</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月31日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	SQLTransduceException
	 * @see		
	 * @return	String
	 * @throws UnknowSQLTypeException 
	 */
	public String changeUpdateTable(String sql) throws SQLFormatException, UnknowSQLTypeException {
		String res = "";
		/**
		 * <p>功能 ：</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：is</p>
		 * <p>範圍 ：從  到 </p>
		 * <h2>群組 ：</h2>
		 * 1. table name
		 * 2. from
		 * 3. from alias name
		 * 4. set 
		 * <h2>備註 ：</h2>
		 * <p>
		 * </p>
		 * <h2>異動紀錄 ：</h2>
		 * 2024年5月31日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?is)UPDATE\\s+(?<tableNm>[^()\\s]+)\\s+(FROM\\s*(?<from>.*))?(?<set>\\bSET\\b[^;]*)";
		Matcher m = Pattern.compile(reg).matcher(sql);
		while (m.find()) {
			String update = "";
			
			String tableNm = m.group("tableNm");
			String from = "";
			String set = m.group("set");
			
			
			if(m.group("from")!=null) {
				List<String> lst = SplitCommaSafely.splitComma(m.group("from"),(t)->{
					/**
					 * <p>功能 ：</p>
					 * <p>類型 ：搜尋</p>
					 * <p>修飾詞：i</p>
					 * <p>範圍 ：從  到 </p>
					 * <h2>群組 ：</h2>
					 * 	1.
					 * <h2>備註 ：</h2>
					 * <p>
					 * </p>
					 * <h2>異動紀錄 ：</h2>
					 * 2024年5月31日	Tim	建立邏輯
					 * */
					StringBuffer sbsel = new StringBuffer();
					String regsel = "(?is)\\s*\\(\\s*(.*)(\\)\\s*\\S+\\s*)";
					Matcher msel = Pattern.compile(regsel).matcher(t);
					while (msel.find()) {
						String subselect = msel.group(1);
						String subalias  = msel.group(2);
						
						try {
							subselect = GreenPlumTranslater.dql.easyReplace(subselect);
						} catch (UnknowSQLTypeException | SQLFormatException e) {
							e.printStackTrace();
						}
						
						msel.appendReplacement(sbsel, Matcher.quoteReplacement("("+subselect+subalias));
					}
					msel.appendTail(sbsel);
					return t;
				});
				from = "\r\nFROM "+String.join("\r\n,", lst);
			}
			
			set = GreenPlumTranslater.sql.easyReplase(set).trim();
			update = "UPDATE "+tableNm
					+from
					+"\r\n"+set;
			m.appendReplacement(sb,Matcher.quoteReplacement(update));
		}
		m.appendTail(sb);
		
		res = sb.toString();
		
		return res;
	}
}
