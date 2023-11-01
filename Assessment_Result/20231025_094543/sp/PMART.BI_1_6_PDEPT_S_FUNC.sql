REPLACE PROCEDURE PMART.BI_1_6_PDEPT_S_FUNC
(
	P_WEEK_ID NUMBER
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000);
	CALL PMART.P_DROP_TABLE ('#VT_BI_1_6_PDEPT_S_FUNC');
	CALL PMART.BI_1_6_PDEPT_S0_FUNC(P_WEEK_ID);
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_BI_1_6_PDEPT_S_FUNC  AS( ' 
				+ ' SELECT A.PRD_ID, B.ORG_ID, '
				+ ' SUM(A.ORDER_AMT) AS ORD_AMT, '
				+ ' SUM(A.SALES_AMT) AS SALES_AMT, '
				+ ' SUM(A.BUG_SAL_AMT)*1.2*F.STNUM AS BUG_SAL_ALL_AMT, '
				+ ' CAST(SUM(A.ORDER_AMT) AS DECIMAL(12,2)) / F.STNUM AS ORDER_AMT_AVG, ' 
				+ ' CAST(SUM(A.SALES_AMT) AS DECIMAL(12,2)) / F.STNUM AS SALES_AMT_AVG,' 
				+ ' SUM(A.BUG_SAL_AMT)*1.2 AS BUG_SAL_AMT, ' 
				+ ' (CASE WHEN SUM(A.ORDER_AMT)=0 THEN 100 ELSE CAST(SUM(A.SALES_AMT) AS DECIMAL(12,2)) *100 / SUM(A.ORDER_AMT) END) AS ORDSAL_M, ' 
				+ ' (CASE WHEN SUM(A.BUG_SAL_AMT)*1.2*F.STNUM <> 0 THEN CAST(SUM(A.SALES_AMT) AS DECIMAL(12,2)) * 100  / SUM(A.BUG_SAL_AMT)*1.2*F.STNUM ELSE NULL END) AS ACCU, '
				+ ' SUM(A.ORDER_CNT) AS ORDER_CNT, '
				+ ' SUM(A.SALES_CNT) AS SALES_CNT, '
				+ ' CAST(SUM(A.ORDER_CNT) AS DECIMAL(12,2)) / F.STNUM AS ORDER_CNT_AVG, '
				+ ' CAST(SUM(A.SALES_CNT) AS DECIMAL(12,2)) / F.STNUM AS SALES_CNT_AVG '
				+ ' FROM '
       			+ ' 	(SELECT  ORG_ID , PRD_ID '
          		+ ' 	FROM '
				+' 		(SELECT DISTINCT PDEPT_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID = -1) ORG, '
				+ ' 		(SELECT PRD_ID FROM PMART.BI_CODE WHERE PRD_ID <> ''17'' '
				+ ' 		UNION '
				+ ' 		SELECT ''176''  FROM (SELECT 1 AS "DUMMY") AS "DUAL" ) PRD '
         		+ ' ) B, '
				+ ' PMART.BASIC_MFACT_ORD1 A, '
				+ ' #VT_BI_1_6_PDEPT_S0_FUNC F '
   				+ ' WHERE A.TIME_ID = ' + P_WEEK_ID
      			+ ' AND A.ORG_ID = B.ORG_ID '
      			+ ' AND A.PRD_ID = B.PRD_ID '
				+ ' AND A.ORG_ID = F.ORG_ID '
      			+ ' GROUP BY A.PRD_ID, B.ORG_ID, F.STNUM  '
				+ ' ) WITH DATA PRIMARY  CHARINDEX( ORG_ID,PRD_ID) ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;  
END SP;