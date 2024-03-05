package etec.src.sql.gp.translater;

import java.util.Arrays;

import etec.common.exception.sql.SQLFormatException;
import etec.common.exception.sql.SQLTransduceException;
import etec.common.exception.sql.UnknowSQLTypeException;

/**
 * <h1>GreenPlumn轉換</h1>
 * 
 * 統整
 * 
 * @author	Tim
 * @since	4.0.0.0
 * @version	4.0.0.0
 * */
public class GreemPlumTranslater {
	/**
	 * <h1>ddl : 資料定義語言轉換</h1>
	 * <br>提供語法轉換：
	 * <li>REPLACE VIEW
	 * <li>Create table
	 * <li>不同型態間的比較有時會出現ERROR，要人工判斷
	 * <li>CHAR($1)若$1為DATE要改成TO_CHAR，若為CHAR則改成LENGTH
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public static DDLTranslater ddl = new DDLTranslater();
	
	public static DMLTranslater dml = new DMLTranslater();

	public static DQLTranslater dql = new DQLTranslater();
	
	public static OtherTranslater other = new OtherTranslater();
	
	public static SQLTranslater sql = new SQLTranslater();
	/**
	 * <h1>sql : 轉換通用語句</h1>
	 * <br>下列為需要人工處理之語法：
	 * <li>日期格式之乘除運算
	 * <li>DATE_TRUNC語法需確保裡面的參數為日期
	 * <li>不同型態間的比較有時會出現ERROR，要人工判斷
	 * <li>CHAR($1)若$1為DATE要改成TO_CHAR，若為CHAR則改成LENGTH
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public static String[] arrDQL = {
			 "WITH"
			,"SELECT"
	};
	public static String[] arrDML = {
			 "INSERT"
			,"DELETE"
			,"UPDATE"
	};
	public static String[] arrDDL = {
			 "CREATE"
			,"TRUNCAT"
			,
	};
	
	
	/**
	 * <h1>區分並轉換</h1>
	 * <p>將SQL分類後轉換</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月1日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	
	 * @see
	 * @return	
	 * @throws SQLFormatException 
	 * @throws UnknowSQLTypeException 
			 */
	public static String translate(String script) throws SQLTransduceException {
		String res = "";
		String title = script.trim().replaceAll("^(\\S+)[\\S\\s]+","$1").toUpperCase();
		
		if(Arrays.asList(arrDQL).contains(title)) {
			res = dql.easyReplace(script);
		}else if(Arrays.asList(arrDML).contains(title)) {
			res = dml.easyReplace(script);
		}else if(Arrays.asList(arrDDL).contains(title)) {
			res = ddl.easyReplace(script);
		}else {
			res = sql.easyReplase(script);
		}
		return res;
	}
	
}
