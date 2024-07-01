package etec.src.sql.az.translater;

import java.util.Arrays;

import etec.common.model.SQLTypeModel;
import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.common.utils.convert_safely.ConvertVarcharSafely;
import etec.common.utils.log.Log;
import etec.framework.translater.exception.SQLFormatException;
import etec.framework.translater.exception.SQLTranslateException;
import etec.framework.translater.exception.UnknowSQLTypeException;
import etec.framework.translater.interfaces.TranslaterFactory;

/**
 * <h1>Azsure轉換</h1>
 * 
 * 統整
 * 
 * @author Tim
 * @since 4.0.0.0
 * @version 4.0.0.0
 */
public class AzTranslater extends TranslaterFactory{
	/**
	 * <h1>ddl : 資料定義語言轉換</h1> <br>
	 * 提供語法轉換：
	 * <li>REPLACE VIEW
	 * <li>Create table
	 * <li>不同型態間的比較有時會出現ERROR，要人工判斷
	 * <li>CHAR($1)若$1為DATE要改成TO_CHAR，若為CHAR則改成LENGTH
	 * 
	 * @author Tim
	 * @since 4.0.0.0
	 */
	public static DDLTranslater ddl = new DDLTranslater();

	public static DMLTranslater dml = new DMLTranslater();

	public static DQLTranslater dql = new DQLTranslater();

	public static OtherTranslater other = new OtherTranslater();

	public static SQLTranslater sql = new SQLTranslater();
	/**
	 * <h1>sql : 轉換通用語句</h1> <br>
	 * 下列為需要人工處理之語法：
	 * <li>日期格式之乘除運算
	 * <li>DATE_TRUNC語法需確保裡面的參數為日期
	 * <li>不同型態間的比較有時會出現ERROR，要人工判斷
	 * <li>CHAR($1)若$1為DATE要改成TO_CHAR，若為CHAR則改成LENGTH
	 * 
	 * @author Tim
	 * @since 4.0.0.0
	 */
	public static String[] arrDQL = { "WITH", "SELECT" };
	public static String[] arrDML = { "INSERT", "DELETE", "UPDATE","MERGE"};
	public static String[] arrDDL = { "CREATE", "TRUNCAT", "DROP", "RENAME", "REPLACE" };
	public static String[] arrOther = { "CALL", "COLLECT", "LOCKING","EXEC" };

	/**
	 * <h1>區分並轉換</h1>
	 * <p>
	 * 將SQL分類後轉換
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * <h2>異動紀錄</h2> <br>
	 * 2024年3月1日 Tim 建立功能 <br>
	 * 2024年5月2日 Tim 增加Other類別
	 * 
	 * @author Tim
	 * @since 4.0.0.0
	 * @param
	 * @throws
	 * @see
	 * @return
	 * @throws SQLFormatException
	 * @throws UnknowSQLTypeException
	 */
	public static String translate(String script) throws SQLTranslateException {
		if(script.matches("\\s*")) {
			return script;
		}
		String res = "";
		script = script
				.replaceAll("([${}\\w]+)\\s*\\.\\s*([\\w-]+)", "$1.$2")
				.replaceAll("(?i)\\bFOR\\s+ACCESS(?!\\s*;)", "$0;")
		;
		script = regular(script);
		SQLTypeModel m = getType(script);
 		switch(m.getType()) {
			case DQL:
				Log.debug("\t分類：DQL");
				res = dql.easyReplace(script);
				break;
			case DML:
				Log.debug("\t分類：DML");
				res = dml.easyReplace(m.getTitle(),script);
				break;
			case DDL:
				Log.debug("\t分類：DDL");
				res = ddl.easyReplace(script);
				break;
			case OTHER:
				Log.debug("\t分類：OTHER");
				res = other.easyReplace(script);
				break;
			default:
				Log.debug("\t分類：其他");
				res = script;
		}
 		//額外處理
 		res = ConvertVarcharSafely.savelyConvert(res, (t)->{
 			return t.replaceAll("#", "TEMP_TABLE.");
 		});
		Log.debug("轉換完成");
		return res;
	}
	
	
	public static String regular(String script) {
		String res = script;
		res = res
				.replaceAll("(?i)\\bSEL\\b", "SELECT")
//				.replaceAll("(?i)\\bINS\\b", "INSERT")
				
			;
		return res;
	}
}
