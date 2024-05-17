package etec.src.sql.gp.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.exception.sql.SQLFormatException;
import etec.common.exception.sql.SQLTransduceException;
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
		}else if(sql.matches("(?i)\\s*INSERT\\s+INTO\\s+[\\S\\s]+")) {
			Log.debug("\t\t細分：INSERT  SELECT");
			sql = changeInsertSelect(sql);
		}else if(sql.matches("(?i)\\s*DELETE\\s+[\\S\\s]+")) {
			Log.debug("\t\t細分：DELETE TABLE");
			sql = changeDeleteTableUsing(sql);
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
		 * <br> 2.alias
		 * <br>	3.where
		 * <h2>備註 ：</h2>
		 * <p>index 還沒處理
		 * </p>with data 的部分也要再確認
		 * <h2>異動紀錄 ：</h2>
		 * <br>2024年5月16日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile("(?i)DELETE\\s+FROM\\s+(\\S+)(\\s+\\S+)?\\s+WHERE\\s+([^;]+);?");
		Matcher m = p.matcher(res);
		while (m.find()) {
			String table = m.group(1);
			String alias   = m.group(2);
			String where   = m.group(3);
			String delete = "DELETE FROM "+table+" USING "+table+alias
					+"\r\nWHERE\r\n"+GreenPlumTranslater.sql.easyReplase(where)+"\r\n;"
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
 		res = insert+select;
		return res;
	}
}
