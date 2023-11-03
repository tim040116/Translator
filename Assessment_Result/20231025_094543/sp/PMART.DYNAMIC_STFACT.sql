REPLACE PROCEDURE PMART.DYNAMIC_STFACT
(
	FP_FACT_TYPE VARCHAR(100),
	FP_TIME_LIST VARCHAR(4000),
	FP_ORG_LIST  VARCHAR(4000),
	FP_PRD_LIST  VARCHAR(4000)
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000);
	DECLARE V_TABLE_NAME VARCHAR(400);
	CALL PMART.P_DROP_TABLE ('#VT_DYNAMIC_STFACT');
	IF FP_FACT_TYPE = 'D' THEN
		SET V_TABLE_NAME = ' PMART.BASIC_STFACT_DETAIL ';
	ELSE
		SET V_TABLE_NAME = ' PMART.BASIC_STFACT ';
	END IF;
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_DYNAMIC_STFACT  AS( ' 
				+ 'SELECT D.TIME_ID AS TIME_ID, '
				+ 'D.ORG_ID AS ORG_ID, '
				+ 'D.PRD_ID AS PRD_ID,'
				+ 'BIT_EXTRACT(BIT_AND(SALES_STORE_NUM,MASK)) AS SALES_STORE_NUM,'
				+ 'BIT_EXTRACT(BIT_AND(ORDER_STORE_NUM,MASK)) AS ORDER_STORE_NUM,'
				+ 'BIT_EXTRACT(BIT_AND(THROW_STORE_NUM,MASK)) AS THROW_STORE_NUM,'
				+ 'BIT_EXTRACT(BIT_AND(BIT_OR(SALES_STORE_NUM,ORDER_STORE_NUM),MASK)) AS SALES_ORDER_STORE_NUM, '
				+ 'BIT_EXTRACT(BIT_AND(INPRD_STORE_NUM,MASK)) AS INPRD_STORE_NUM,'
				+ 'BIT_EXTRACT(BIT_AND(RETPRD_STORE_NUM,MASK)) AS RETPRD_STORE_NUM,'
				+ 'BIT_EXTRACT(BIT_AND(TRANSPRD_STORE_NUM,MASK)) AS TRANSPRD_STORE_NUM '
				+ 'FROM  ('
				+ 'SELECT TIME_ID, ORG_ID, PRD_ID, MASK '
				+ 'FROM '
				+ '( '+ FP_TIME_LIST +' ) T, '
				+ '(SELECT A.ORG_ID AS ORG_ID, A.MASK AS MASK '
				+ 'FROM PMART.LAST_ORG_DIM_MASK A, ( '+ FP_ORG_LIST +' ) B '
				+ 'WHERE A.ORG_ID = B.ORG_ID) M, '
				+ '( '+ FP_PRD_LIST +' ) P '
				+ ') D, '+ V_TABLE_NAME +' F '
				+ 'WHERE F.TIME_ID = D.TIME_ID '
				+ 'AND F.PRD_ID = D.PRD_ID'
				+ ' ) WITH DATA PRIMARY INDEX (TIME_ID, ORG_ID, PRD_ID) ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;  
END SP;