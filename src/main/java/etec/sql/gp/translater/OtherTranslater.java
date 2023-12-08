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
}
