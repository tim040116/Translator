package etec.sql.gp.translater;

public class DMLTranslater {
	/**
	 * @author	Tim
	 * @since	2023年12月5日
	 * 
	 * DELETE TABLE 要加上 USING
	 * 
	 * */
	public String changeDeleteTableUsing(String sql) {
		String res = sql
			.replaceAll("(?i)DELETE\\s+FROM\\s+(\\S+)(\\s+\\S+)?\\s*,", "DELETE FROM $1$2\r\n USING ")//Locking
			;
		return res;
	}
}
