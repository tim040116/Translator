package etec.sql.gp.translater;

import etec.common.utils.ConvertFunctionsSafely;

/**
 * @author	Tim
 * @since	2023年12月19日
 * @version	4.0.0.0
 * 
 * <h1>查詢語法轉換</h1>
 * 
 * <br>此類別並不是用來轉換SQL裡的語法
 * <br>若需要轉換語法請使用
 * <br>{@link etec.sql.gp.translater.SQLTranslater}
 * <br>
 * <br>此類別著重於SQL語句的拆解，
 * <br>負責將sub query,join,union,with...等
 * <br>複合型語句拆解成單純的查詢語句
 * */
public class DQLTranslater {
	/**
	 * @author	Tim
	 * @since	2023年12月19日
	 * 
	 * <h1>QUALIFY ROW_NUMBER()語法轉換</h1>
	 * 
	 * */
	public String changeQualifaRank(String sql) {
		String res = sql;
		//先排除沒有QUALIFY語法的語句
		if(!sql.toUpperCase().contains("QUALIFY")) {
			return sql;
		}
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		cfs.saveTranslateFunction(res, (String t)->{
			
			return t;
		});
		return res;
	}


}
