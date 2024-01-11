package etec.sql.gp.translater;

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
	public static SQLTranslater sql = new SQLTranslater();
	
}
