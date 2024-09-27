package etec.src.translator.sql.td.classifier;

import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.context.translater.enums.SQLTypeEnum;
import etec.framework.security.log.service.Log;

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
 		sql = ConvertRemarkSafely.clean(sql).trim();
 		//清除修飾詞
 		sql = sql.replaceAll("(?i)\\b(MULTISET|SET|VOLATILE|TEMP)\\b", "");
		SQLTypeEnum res = SQLTypeEnum.OTHER;

		if(sql.matches("\\s*;?\\s*")) {
			res = SQLTypeEnum.EMPTY;
		}
		else if (sql.matches("(?i)CREATE\\s+TABLE\\s+[\\S\\s]+")) {
			if(sql.matches("(?i)[\\S\\s]*\\bSELECT\\b[\\S\\s]*")) {
				if(sql.matches("(?i)CREATE\\s+[\\S\\s]*?\\bTABLE\\s+[\\S\\s]*?\\b(?:WITH|AS)\\s*\\([\\S\\s]+")) {
					res = SQLTypeEnum.CTAS;
				}else {
					res = SQLTypeEnum.CREATE_INSERT;
				}
			}else {
				res = SQLTypeEnum.CREATE_TABLE;
			}
		}
		else if(sql.matches("(?i)\\bCREATE\\s+VIEW\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.CREATE_VIEW;
		}
		else if(sql.matches("(?i)RENAME\\s+(?:OBJECT|TABLE)\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.RENAME_TABLE;
		}
		else if (sql.matches("(?i)DROP\\s+(?:TABLE\\s+)?[\\S\\s]+")) {
			res = SQLTypeEnum.DROP_TABLE;
		}
		else if(sql.matches("(?i)LOCK\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.LOCKING;
		}
		else if(sql.matches("(?i)TRUNCATE\\s+TABLE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.TRUNCATE_TABLE;
		}
		else if (sql.matches("(?i)MERGE\\s+(?:INTO\\s+)?[\\S\\s]+")) {
			res = SQLTypeEnum.MERGE_INTO;
		}
		else if (sql.matches("(?i)UPDATE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.UPDATE;
		}
		else if (sql.matches("(?i)SELECT\\s+[\\S\\s]+")) {
			if(sql.matches("(?i)[\\S\\s]+INTO\\s+\\S+\\s+FROM\\s*\\([\\S\\s]+")) {
				res = SQLTypeEnum.SELECT_INTO;
			}else {
				res = SQLTypeEnum.SELECT;
			}
		}
		else if (sql.matches("(?i)DELETE\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.DELETE;
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
		else if(sql.matches("(?i)\\bINS(?:ERT)\\s+(?:INTO\\s+)?[\\S\\s]+")) {
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
		else if(sql.matches("(?i)\\bEXEC\\s*;")) {
			res = SQLTypeEnum.EXEC;
		}
		else if(sql.matches("(?i)WITH\\s+\\S+\\s+AS\\s+[\\S\\s]+")) {
			res = SQLTypeEnum.WITH;
		}
		else {
			res = SQLTypeEnum.OTHER;
		}
		if(res.equals(SQLTypeEnum.OTHER)) {
			System.out.println(sql+"\r\n");
 			Log.warn("出現無法辨識的SQL語句");
		}
		return res;

	}
}
