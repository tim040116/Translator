package etec.src.sql.gp.translater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.framework.translater.exception.SQLFormatException;
import etec.framework.translater.exception.UnknowSQLTypeException;

public class DDLTranslater {
	
	public String easyReplace(String sql) throws UnknowSQLTypeException, SQLFormatException {
		sql = sql.replaceAll("(?i)IF\\s+NOT\\s+EXISTS\\s+", "");//解決已經有IF NOT EXISTS的問題
		sql = changeCreateVolaTileTable(sql);//temp table
		if(sql.matches("(?i)\\s*CREATE(?:\\s+(?:TEMP|SET|MULTISET))?\\s+[\\S\\s]+")) {
			sql = changePrimaryIndex(sql);
			if(sql.matches("(?i)\\s*Create\\s+Table\\s+\\S+\\s+As\\s*[\\S\\s]+")) {
				Log.debug("\t\t細分：CTAS");
				sql = easyReplaceCTAS(sql);
			}else if(sql.matches("(?i)\\s*Create\\s+TEMP\\s+Table\\s+\\S+\\s+As\\s*[\\S\\s]+")) {
				Log.debug("\t\t細分：CTAS TEMP TABLE");
				sql = easyReplaceCTASTemp(sql);
			}else{
				sql = easyReplaceCreateTable(sql);
			}
		}
		sql = changeReplaceView(sql);
		sql = changeDropTableIfExist(sql);
		sql = changeRenameTable(sql);
		sql = changeIntegerGeneratedAlwaysAsIdentity(sql);
		return sql;
	}
	/**
	 * 
	 * @author	Tim
	 *
	 * @since	4.0.0.0
	 * */
	/**
	 * <h1>EPLACE VIEW</h1>
	 * <p>在GP中如果使用REPLACE VIEW，確定轉換前後的欄位有沒有變化
	 * <br>否則推薦DROP後再CREATE</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月2日	Tim	建立功能
	 * <br>2024年5月2日	增加處理後面的select語法
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sql
	 * @throws	SQLFormatException 
	 * @throws	UnknowSQLTypeException 
	 * @see		
	 * @return	String
			 */
	public String changeReplaceView(String sql) throws UnknowSQLTypeException, SQLFormatException {
		if(sql.matches("(?i)\\s*REPLACE\\s+VIEW[^;]*;?")) {
			String res = "";
			Pattern p = Pattern.compile("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS\\s+([^;]+;?)");
			Matcher m = p.matcher(sql);
			while(m.find()) {
				String select = m.group(2);
				select = GreenPlumTranslater.other.easyReplace("",select);
				select = GreenPlumTranslater.dql.easyReplace(select);
				res = "DROP VIEW IF EXISTS "+m.group(1)+";"
						+ "\r\nCREATE VIEW "+m.group(1)+" AS \r\n"
						+ select
				;
			}
//			String res = sql
//					.replaceAll("(?i)REPLACE\\s+VIEW\\s+(\\S+)\\s+AS", "DROP VIEW IF EXISTS $1;\r\nCREATE VIEW $1 AS")
//					;
			return res;
		}
		return sql;
	}
	/**
	 * <h1>Create Table 的轉換</h1>
	 * <li>if not exist的功能
	 * <li>date format
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String easyReplaceCreateTable(String sql) {
		String res = sql
//				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
			;
		//清除Create table 的設定
		res = res
			.replaceAll("(?i)\\s*,\\s*NO\\s*FALLBACK\\s*", " ")
			.replaceAll("(?i)\\s*,\\s*NO\\s*[A-Za-z]+\\s*JOURNAL\\s*", " ")
			.replaceAll("(?i)\\s*,\\s*CHECKSUM\\s*=\\s*[A-Za-z]+\\s*", " ")
			.replaceAll("(?i)\\s*,\\s*DEFAULT\\s*MERGEBLOCKRATIO\\s*", " ")
			.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
			.replaceAll("(?i)(NOT\\s+)?CASESPECIFIC", " ")
			.replaceAll("(?i)TITLE\\s+'[^']+'", " ")
			.replaceAll("(?i)\\bNO\\s+PRIMARY\\s+INDEX","")
		;
		res = res
			.replaceAll("(?i)CHARACTER\\s+SET\\s+\\S+", " ")
			.replaceAll("(?i)NOT\\s+CASESPECIFIC", " ")
			.replaceAll("(?i)TITLE\\s+'[^']+'", " ")
			.replaceAll("(?i)\\s*FORMAT\\s+'[^']+'", " ")
			.replaceAll("(?i)TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
			.replaceAll("(?i)VARBYTE", "VARBINARY")
//			.replaceAll(" +,", ",")
		;
		res = res
			.replaceAll("(?i)CREATE(\\s+TEMP)?\\s+TABLE\\s+", "CREATE$1 TABLE IF NOT EXISTS ")
			.replaceAll("(?i)\\bDATE\\s+FORMAT\\s+'[^']+'", "DATE")
		;
//		res = changeTypeConversion(res); 
		return res;
	}
	/**
	 * <h1>轉換CTAS</h1>
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * <h2>異動紀錄</h2> <br>
	 * <br>2024年5月27日 Tim 建立功能
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
	public String easyReplaceCTAS(String sql) throws UnknowSQLTypeException, SQLFormatException {
		String res = sql;
		
		/**
		 * <p>功能 ：CTAS</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從 CREATE 到 with data</p>
		 * <h2>群組 ：</h2>
		 * <br>	1.create table
		 * <br>	2.select
		 * <br> 3.with no data
		 * <br> 4.other
		 * <h2>備註 ：</h2>
		 * <p>
		 * <br>1.要加上 if not exist
		 * <br>--2.with no data的情況下要加like ????
		 * <br>postgress定義了with no data 的功能，Greenplum数据库当前未实现此功能
		 * </p>
		 * {@link : https://docs-cn.greenplum.org/v6/ref_guide/sql_commands/CREATE_TABLE_AS.html}
		 * <h2>異動紀錄 ：</h2>
		 * <br>2024年5月16日	Tim	建立邏輯
		 * <br>2024年5月20日	Tim	經Jason測試，like不能用，limit可以，但是not exist 跟CTAS衝突
		 * <br>					改用判別
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)CREATE\\s+TABLE\\s+(\\S+)\\s+AS\\s*\\(\\s*+([\\S\\s]+)\\)\\s*(WITH\\s+(?:NO\\s+)?DATA)?(?=\\b)([^;]+)";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(res);
		while (m.find()) {
			String table = m.group(1);
			String dbNm = null;
			String tblNm = "";
			String[] arrTbl = table.split("\\.");
			if(arrTbl.length==2) {
				dbNm  = arrTbl[0];
				tblNm = arrTbl[1];
			}else {
				tblNm = table;
			}
			String sel   = m.group(2);
			String withData = m.group(3)!=null?m.group(3):"";
			boolean noData = withData.matches("(?i)with\\s+no\\s+data");
			String other = m.group(4);
			String ctas = "";
			String select = GreenPlumTranslater.dql.easyReplace(sel);
			
			/**2024年5月20日	Tim	CTAS 與IF NOT EXIST 不相容
			 * */
			String title = "CREATE TABLE " + table + " AS ( \r\n\t";
			ctas = title
					+select+"\r\n"+ (noData?" LIMIT 0 \r\n":"")
					+ ")\r\n"+other+"\r\n;";
			/**
			 * <p>功能 ：將sql 語法轉成varchar</p>
			 * <p>類型 ：取代</p>
			 * <p>修飾詞：</p>
			 * <h2>群組 ：</h2>
			 * <h2>備註 ：</h2>
			 * <br>1.sql加上 limit 0
			 * <br>2.單引號跳脫
			 * <br>3.加上'' || 
			 * <br>4.把最後一個 || 改成 ;
			 * <h2>異動紀錄 ：</h2>
			 * 2024年5月20日	Tim	建立邏輯
			 * */
			String excute = "\t\t\t\tEXECUTE "
					+(ctas.replaceAll(";\\s*$","")+"\r\n")
						.replaceAll("'", "''")
						.replaceAll(".++", "'$0' || ")
						.replaceAll("\\|\\|\\s*$", ";")
						.replaceAll("\\s*\\|\\| '' \\|\\|\\s*", " \\|\\| ")
				;
			String script =    "DO $$\r\n"
					+ "BEGIN\r\n"
					+ "    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = '"+tblNm+"'"
					+ (dbNm != null ? " AND schemaname = '"+dbNm+"' " : "")
					+ " ) THEN \r\n"
					+ excute + "\r\n"
					+ "    END IF;\r\n"
					+ "END $$";
			m.appendReplacement(sb, Matcher.quoteReplacement(script));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
	}
	/**
	 * <h1>轉換CTAS temp table</h1>
	 * <p>CTAS temp table 不需要加if not exists
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
	public String easyReplaceCTASTemp(String sql) throws UnknowSQLTypeException, SQLFormatException {
		String res = sql;
		
		/**
		 * <p>功能 ：CTAS</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從 CREATE 到 with data</p>
		 * <h2>群組 ：</h2>
		 * <br>	1.create table name
		 * <br>	2.select
		 * <br> 3.with no data
		 * <br> 4.other
		 * <h2>備註 ：</h2>
		 * <p>
		 * <br>1.要加上 if not exist
		 * <br>--2.with no data的情況下要加like ????
		 * <br>postgress定義了with no data 的功能，Greenplum数据库当前未实现此功能
		 * </p>
		 * {@link : https://docs-cn.greenplum.org/v6/ref_guide/sql_commands/CREATE_TABLE_AS.html}
		 * <h2>異動紀錄 ：</h2>
		 * <br>2024年5月16日	Tim	建立邏輯
		 * <br>2024年5月20日	Tim	經Jason測試，like不能用，limit可以，但是not exist 跟CTAS衝突
		 * <br>					改用判別
		 * */
		StringBuffer sb = new StringBuffer();
		String reg = "(?i)CREATE\\s+TEMP\\s+TABLE\\s+(\\S+)\\s+AS\\s*\\(\\s*+([\\S\\s]+)\\)\\s*(WITH\\s+(?:NO\\s+)?DATA)?(?=\\b)([^;]+)";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(res);
		while (m.find()) {
			String table = m.group(1).replaceAll("#", "");
			String sel   = m.group(2);
			String withData = m.group(3)!=null?m.group(3):"";
			boolean noData = withData.matches("(?i)with\\s+no\\s+data");
			String other = m.group(4);
			String ctas = "";
			String select = GreenPlumTranslater.dql.easyReplace(sel);
			
			/**2024年5月20日	Tim	CTAS 與IF NOT EXIST 不相容
			 * */
			String title = "CREATE TEMP TABLE " + table + " AS ( \r\n\t";
			ctas = title
					+select+"\r\n"+ (noData?" LIMIT 0 \r\n":"")
					+ ")\r\n"+other+"\r\n";
			m.appendReplacement(sb, Matcher.quoteReplacement(ctas));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
	}
	
	/**
	 * DROP TABLE 要加上 if exist
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String changeDropTableIfExist(String sql) {
		String res = sql
			.replaceAll("(?i)DROP\\s+(TABLE|VIEW)\\s+(\\S+)\\s*;", "DROP $1 IF EXISTS $2"+(Params.gp.IS_CASCADE?" CASCADE":"")+";")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * RENAME TABLE 改成 ALTER TABLE 加上 RENAME TO
	 * 
	 * */
	public String changeRenameTable(String sql) {
		String res = sql
			.replaceAll("(?i)RENAME\\s+TABLE\\s+(\\S+)\\s+TO\\s+(?:[^\\.]+\\.)?(\\S+)\\s*;", "ALTER TABLE $1\r\nRENAME TO $2;")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * CREATE VOLATILE TABLE 改成 CREATE temp TABLE
	 * 
	 * */
	public String changeCreateVolaTileTable(String sql) {
		String res = sql
			.replaceAll("(?i)\\bVOLATILE\\b", "temp")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * INTEGER GENERATED ALWAYS AS IDENTITY (CYCLE) 改成 SERIAL
	 * 
	 * */
	public String changeIntegerGeneratedAlwaysAsIdentity(String sql) {
		String res = sql
			.replaceAll("(?i)\\bINTEGER\\s+GENERATED\\s+ALWAYS\\s+AS\\s+IDENTITY\\s*\\([^\\)]+\\)", "SERIAL")
			;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * PRIMARY INDEX 要改成 DISTRIBUTED
	 * 
	 * */
	public String changePrimaryIndex(String sql) {
		String res = sql
			.replaceAll("(?i)PRIMARY\\s+INDEX\\s*\\(", "DISTRIBUTED BY \\(")
			;
		return res;
	}
}
