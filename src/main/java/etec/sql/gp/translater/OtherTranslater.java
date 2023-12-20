package etec.sql.gp.translater;

/**
 * @author	Tim
 * @since	2023年11月1日
 * @version	3.3.1.1
 * 
 * 處理不是單段語法的語句
 * 
 * */
public class OtherTranslater {
	
	/**
	 * @author	Tim
	 * @since	2023年12月20日
	 * 
	 * */
	public String easyReplase(String script) {
		String res = script;
		res = changeLockingTable(res);
		res = changeCollectStatistics(res);
		res = changeIndex(res);
		return res;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * LOCKING TABLE 要註解掉
	 * 
	 * */
	public String changeLockingTable(String sql) {
		String res = sql
			.replaceAll("(?i)LOCKING\\s+TABLE\\s+\\S+\\s+FOR\\s+ACCESS", "/*$0*/")//Locking
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月20日
	 * 
	 * COLLECT STATISTICS ON 改成 ANALYZE
	 * 
	 * */
	public String changeCollectStatistics(String sql) {
		String res = sql
			.replaceAll("(?i)COLLECT\\s+STATISTICS\\s+ON\\s+(\\S+)\\s*;", "ANALYZE $1;")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年12月20日
	 * 
	 * INDEX要註解掉，
	 * <br>UNIQUE 跟 PRIMARY 也要
	 * 
	 * */
	public String changeIndex(String sql) {
		String res = sql
			.replaceAll("(?i)((UNIQUE|PRIMARY)\\s+)*INDEX\\s*\\(\\s*[\\w\\.\\s,]+\\)\\s*;", "/*$0*/")
			;
		return res;
	}
}
