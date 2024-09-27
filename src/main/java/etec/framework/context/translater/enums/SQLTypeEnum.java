package etec.framework.context.translater.enums;

/**
 * @author	Tim
 * @since	2023年10月4日
 * @version	3.3.0.0
 *
 * SQL的類型
 * */
public enum SQLTypeEnum {
	//table
		//DDL
		 CREATE_TABLE
		,CREATE_INSERT
		,CREATE_VIEW
		,CTAS
		,SELECT_INTO
		,RENAME_TABLE
		,DROP_TABLE
		,TRUNCATE_TABLE
		,WITH
		//DML
		,INSERT_TABLE
		,INSERT_SELECT
		,UPDATE
		,DELETE
		,MERGE_INTO
		//DQL
		,SELECT
	//view
	,DROP_VIEW
	,REPLACE_VIEW
	//other
	,EMPTY
	,DATABASE
	,LOCKING
	,CALL
	,COLLECT_STATISTICS
	,COMMENT_ON
	,COMMIT
	,BT
	,ET
	,EXIT
	,EXEC
	,DQL
	,DDL
	,DML
	,OTHER
	,ELSE
}
