package etec.sql.gp.translater;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * <h1>GreenPlumn轉換</h1>

 * */
public class GreemPlumTranslater {
	
	public static DDLTranslater ddl = new DDLTranslater();
	
	public static DMLTranslater dml = new DMLTranslater();

	public static DQLTranslater dql = new DQLTranslater();
	
	public static OtherTranslater other = new OtherTranslater();
	/**
	 * @author Tim
	 * <h1>sql : 轉換通用語句</h1>
	 * <br>下列為需要人工處理之語法：
	 * <li>日期格式之乘除運算
	 * <li>DATE_TRUNC語法需確保裡面的參數為日期
	 * <li>不同型態間的比較有時會出現ERROR，要人工判斷
	 * <li>
	 * */
	public static SQLTranslater sql = new SQLTranslater();
	
}
