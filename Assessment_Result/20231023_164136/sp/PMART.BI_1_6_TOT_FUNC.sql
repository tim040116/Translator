REPLACE PROCEDURE PMART.BI_1_6_TOT_FUNC
(
	P_WEEK_ID NUMBER
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000);
	CALL PMART.P_DROP_TABLE ('#VT_BI_1_6_TOT_FUNC');
	CALL PMART.BI_1_6_TOT_S_FUNC(P_WEEK_ID);
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_BI_1_6_TOT_FUNC  AS( ' 
				+ ' SELECT * FROM #VT_BI_1_6_TOT_S_FUNC '
				+ ' UNION '
				+ ' SELECT ''9999999'' AS PRD_ID, ORG_ID, '
				+ ' SUM(ORD_AMT) ORD_AMT, '
				+ ' SUM(SALES_AMT) SALES_AMT, '
				+ ' SUM(BUG_SAL_ALL_AMT) BUG_SAL_ALL_AMT, '
				+ ' SUM(ORDER_AMT_AVG) / COUNT(0) ORDER_AMT_AVG, '
				+ ' SUM(SALES_AMT_AVG) / COUNT(0) SALES_AMT_AVG, '
				+ ' SUM(BUG_SAL_AMT) / COUNT(0) BUG_SAL_AMT, '
				+ ' CASE WHEN SUM(ORD_AMT) = 0 THEN 100 ELSE CAST(SUM(SALES_AMT) AS DECIMAL(12, 2)) * 100 / SUM(ORD_AMT) END AS ORDSAL_M, '
				+ ' CASE WHEN SUM(BUG_SAL_ALL_AMT)=0 THEN 100 ELSE CAST(SUM(SALES_AMT) AS DECIMAL(12, 2)) * 100 / SUM(BUG_SAL_ALL_AMT) END AS ACCU, '
				+ ' SUM(ORDER_CNT) ORDER_CNT, '
				+ ' SUM(SALES_CNT) SALES_CNT, '
				+ ' SUM(ORDER_CNT_AVG) / COUNT(0) ORDER_CNT_AVG, '
				+ ' SUM(SALES_CNT_AVG) / COUNT(0) SALES_CNT_AVG '
				+ ' FROM #VT_BI_1_6_TOT_S_FUNC '
				+ ' GROUP BY ORG_ID '
				+ ' ) WITH DATA PRIMARY  CHARINDEX( ORG_ID,PRD_ID) ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;  
END SP;