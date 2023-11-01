REPLACE PROCEDURE PMART.BI_1_4_FUNC
(
   IN P_TIME_ID NUMBER,
   IN P_LEVEL NUMBER,
   IN P_ORG_ID NUMBER,
   IN P_PRD_LIST VARCHAR(200),
   IN P_SELECT_PRD_DIM_VALUE NUMBER
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000);
	DECLARE SQLSTR_TABLE VARCHAR(400);
	CALL PMART.P_DROP_TABLE ('#VT_BI_1_4_FUNC');
	CALL PMART.BI_1_4_S_FUNC(P_TIME_ID, P_LEVEL, P_ORG_ID, P_PRD_LIST, P_SELECT_PRD_DIM_VALUE);
	CALL PMART.BI_1_4_ST_FUNC(P_LEVEL, P_ORG_ID);
	CALL PMART.BI_1_4_S0_FUNC(P_TIME_ID);
	IF P_SELECT_PRD_DIM_VALUE = 1 THEN
		SET SQLSTR_TABLE = ' INNER JOIN PMART.PRD_DIM P ON P.PRD_ID = A.PRD_ID ';
	ELSE
		SET SQLSTR_TABLE = ' INNER JOIN PMART.PRD_LINK_DIM P ON P.LINK_ID = A.PRD_ID ';
	END IF;
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_BI_1_4_FUNC  AS( ' 
				+ 'SELECT A.TIME_ID, A.ORG_ID, S.STORE_NM, S.MMA_NM, A.PRD_ID, P.PRD_NM, '
				+ '(CASE WHEN F.STNUM <> 0 THEN A.ORDER_CNT / F.STNUM ELSE A.ORDER_CNT END) C_ORDER_CNT , '
			    + '(CASE WHEN F.STNUM <> 0 THEN A.SALES_CNT / F.STNUM ELSE A.SALES_CNT END) C_SALES_CNT , '
				+ '(CASE WHEN F.STNUM <> 0 THEN A.THROW_CNT / F.STNUM ELSE A.THROW_CNT END) C_THROW_CNT, '
			    + 'CASE WHEN A.ORDER_CNT IS NULL OR A.ORDER_CNT=0 THEN 0 ELSE (A.SALES_CNT)/(A.ORDER_CNT)*100 END AS C_RATE, '
			    + 'S.RESPON_NM, S.BRANCH_NM, S.DEPT_NM, S.DEPT_ID, S.BRANCH_ID, S.RESPON_ID '
				+ 'FROM #VT_BI_1_4_S_FUNC A '
			    + 'INNER JOIN #VT_BI_1_4_ST_FUNC S ON A.ORG_ID = S.STORE_ID  '
			    + SQLSTR_TABLE
				+ 'INNER JOIN #VT_BI_1_4_S0_FUNC F ON A.ORG_ID = F.OSTORE_ID '
				+ ' ) WITH DATA PRIMARY  CHARINDEX( ORG_ID,PRD_ID) ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;  
END SP;