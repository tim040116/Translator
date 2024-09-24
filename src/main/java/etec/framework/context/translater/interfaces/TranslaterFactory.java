package etec.framework.context.translater.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import etec.common.model.SQLTypeModel;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.src.translator.sql.gp.translater.GreenPlumTranslater;

/**
 * <h1>Translater的父類別介面</h1>
 * <p></p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年6月17日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	2024年6月17日
 * @see		
 */
public abstract class TranslaterFactory {

	//
	public static final String[] AR_DQL = { "WITH", "SELECT" };
	
	public static final String[] AR_DML = { "INSERT", "DELETE", "UPDATE","MERGE"};
	
	public static final String[] AR_DDL = { "CREATE", "TRUNCAT", "DROP", "RENAME", "REPLACE" };
	
	public static final String[] AR_OTH = { "CALL", "COLLECT", "LOCKING" };
	
	private static List<String> listTitle = new ArrayList<String>();
	
	static {
		listTitle = new ArrayList<String>();
		listTitle.addAll(Arrays.asList(GreenPlumTranslater.arrDQL));
		listTitle.addAll(Arrays.asList(GreenPlumTranslater.arrDML));
		listTitle.addAll(Arrays.asList(GreenPlumTranslater.arrDDL));
		listTitle.addAll(Arrays.asList(GreenPlumTranslater.arrOther));
	}
	
	/**
	 * <h1>轉換主程序</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月17日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	2024年6月17日
	 * @param	script	單一段落程式，需先預處理
	 * @throws	SQLTranslateException
	 * @see		
	 * @return	return_type
			 */
//	public abstract String translate(String script) throws SQLTranslateException;
	
	/**
	 * <h1>取得該SQL的大類</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月17日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	2024年6月17日
	 * @param	script
	 * @throws	SQLTranslateException
	 * @see		
	 * @return	return_type
			 */
	protected static SQLTypeModel getType(String script) throws SQLTranslateException {
		SQLTypeModel m = new SQLTypeModel();
		if(script.matches("\\s*")) {
			return m;
		}
		String title = script.trim().replaceAll("^\\b([\\w]+)\\b[\\S\\s]+","$1").toUpperCase();
		m.setTitle(title);
 		if(Arrays.asList(AR_DQL).contains(title)) {
 			m.setType("DQL");
		}else if(Arrays.asList(AR_DML).contains(title)) {
			m.setType("DML");
		}else if(Arrays.asList(AR_DDL).contains(title)) {
			m.setType("DDL");
		}else if(Arrays.asList(AR_OTH).contains(title)) {
			m.setType("OTHER");
		}else {
			m.setType("ELSE");
		}
		return m;
	}
	
	/**
	 * <h1>取得該SQL的類型清單</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月17日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	2024年6月17日
	 * @throws	SQLTranslateException
	 * @see		
	 * @return	return_type
			 */
	public static List<String> getTitleList(){
		return listTitle;
	}
}
