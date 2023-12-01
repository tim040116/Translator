package etec.sql.gp.translater;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * 整合
 * */
public class GreemPlumTranslater {
	
	public static DDLTranslater ddl = new DDLTranslater();
	
	public static DMLTranslater dml = new DMLTranslater();

	public static DQLTranslater dql = new DQLTranslater();
	
	public static OtherTranslater other = new OtherTranslater();
	
	public static SQLTranslater sql = new SQLTranslater();
	
}
