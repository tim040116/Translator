package etec.sql.td.classifier;

import etec.common.enums.SQLTypeEnum;
import etec.common.utils.Log;
import etec.common.utils.TransduceTool;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * teradata 的sql 語法分類器
 * */
public class TeradataClassifier {

	/**
	 * @author	Tim
	 * @since	2023年10月4日
	 * 分辨SQL的類型
	 * 
	 * */
	public static SQLTypeEnum getSQLType(String sql) {
		sql = TransduceTool.cleanRemark(sql).toUpperCase().trim();
		SQLTypeEnum res = SQLTypeEnum.OTHER;
		
		if(sql.matches("\\s*;?\\s*")) {
			res = SQLTypeEnum.EMPTY;
		}
		else if (sql.matches("(?i)CREATE\\s*(MULTISET|SET)?(\\s+VOLATILE)?\\s+TABLE\\s+[\\S\\s]+")) {
			if(sql.matches("[\\S\\s]*\\s+SELECT\\s+[\\S\\s]*")) {
				if(sql.matches("(?i)CREATE\\s+TABLE\\s+\\S+\\s+WITH\\s*\\([\\S\\s]+")) {
					res = SQLTypeEnum.CTAS;
				}else {
					res = SQLTypeEnum.CREATE_INSERT;
				}
			}else {
				res = SQLTypeEnum.CREATE_TABLE;
			}
		}
		else if(sql.matches("(?i)RENAME\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.RENAME_TABLE;
		}
		else if (sql.matches("(?i)DROP\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DROP_TABLE;
		}
		
		
		else if(sql.matches("(?i)LOCK\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.LOCKING;
		}
		else if(sql.matches("(?i)TRUNCATE\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.TRUNCATE_TABLE;
		}
		else if (sql.matches("(?i)MERGE\\s+INTO\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.MERGE_INTO;
		}
		else if (sql.matches("(?i)UPDATE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.UPDATE_TABLE;
		}
		else if (sql.matches("(?i)SELECT\\s+[\\S\\s]+")) {
			if(sql.matches("(?i)[\\S\\s]+INTO\\s+\\S+\\s+FROM\\s*\\([\\S\\s]+")) {
				res = SQLTypeEnum.SELECT_INTO;
			}else {
				res = SQLTypeEnum.SELECT_TABLE;
			}
		}
		else if (sql.matches("(?i)DELETE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DELETE_TABLE;
		}
		else if(sql.matches("(?i)REPLACE\\s+VIEW\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.REPLACE_VIEW; 	
		}
		else if(sql.matches("(?i)COLLECT\\s+STATISTICS\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.COLLECT_STATISTICS;
		}
		else if(sql.matches("(?i)COMMENT\\s+ON\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.COMMENT_ON;
		}
		else if(sql.matches("(?i)INSERT\\s+INTO\\s+[\\S\\s]+")) {
			res = sql.matches("(?i)[\\S\\s]*\\s+SELECT\\s+[\\S\\s]*")?SQLTypeEnum.INSERT_SELECT:SQLTypeEnum.INSERT_TABLE;
		}
		else if(sql.matches("(?i)DROP\\s+VIEW\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DROP_VIEW;
		}
		else if(sql.matches("(?i)DATABASE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DATABASE;
		}
		else if(sql.matches("(?i)LOCKING\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.LOCKING;
		}
		else if(sql.matches("(?i)CALL\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.CALL;
		}
		else if(sql.matches("(?i)COMMIT\\s*;")) {
			res = SQLTypeEnum.COMMIT;
		}
		else if(sql.matches("(?i)BT\\s*;")) {
			res = SQLTypeEnum.BT;
		}
		else if(sql.matches("(?i)ET\\s*;")) {
			res = SQLTypeEnum.ET;
		}
		else if(sql.matches("(?i)EXIT\\s*;")) {
			res = SQLTypeEnum.EXIT;
		}
		else if(sql.matches("(?i)WITH\\s+\\S+\\s+AS\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.WITH;
		}
		else {
			res = SQLTypeEnum.OTHER;
		}
		if(res.equals(SQLTypeEnum.OTHER)) {
			Log.warn("出現無法處理的SQL語句");
			System.out.println(sql+"\r\n");
		}
		return res;
		
	}
}
