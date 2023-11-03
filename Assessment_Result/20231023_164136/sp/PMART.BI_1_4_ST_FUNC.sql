REPLACE PROCEDURE PMART.BI_1_4_ST_FUNC
(
   IN P_LEVEL NUMBER,
   IN P_ORG_ID NUMBER
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(1000);
	DECLARE SQLSTR_ORG VARCHAR(400);
	CALL PMART.P_DROP_TABLE ('#VT_BI_1_4_ST_FUNC');
	IF P_LEVEL = 0 THEN
		SET SQLSTR_ORG = ' AND L.TOT_ID= -1';
	ELSEIF P_LEVEL = 1 THEN
		SET SQLSTR_ORG = ' AND L.DEPT_ID=' + P_ORG_ID;
	ELSEIF P_LEVEL = 2 THEN
		SET SQLSTR_ORG = ' AND L.BRANCH_ID=' + P_ORG_ID;
	END IF;
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_BI_1_4_ST_FUNC  AS( ' 
						+ ' SELECT L.STORE_ID,L.STORE_NM,T.MMA_NM,L.DEPT_ID,L.DEPT_NM,L.BRANCH_ID,L.BRANCH_NM,L.RESPON_ID,L.RESPON_NM '
						+ ' FROM PMART.LAST_ORG_DIM L, PMART.ORG_TYPE_DIM T '
						+ ' WHERE L.STORE_ID = T.STORE_ID '+ SQLSTR_ORG
						+ ' ) WITH DATA PRIMARY INDEX (STORE_ID) ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;  
END SP;