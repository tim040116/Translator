REPLACE PROCEDURE PMART.BI_1_5_FUNC(P_DAY_ID VARCHAR(2000),
									 P_ORG_ID VARCHAR(2000),
									 P_PRD_LIST VARCHAR(2000))
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(64000);
   DECLARE V_STRING VARCHAR(20000);
   CALL PMART.P_DROP_TABLE ('#VT_BI_1_5_FUNC'); 
   CALL PMART.BI_1_5_S1_FUNC(P_DAY_ID, P_ORG_ID, P_PRD_LIST);
 SET V_STRING = 'SELECT TIME_ID, PRD_ID, ' +
   ' CAST(SALES_CNT AS DECIMAL(18,6)) AS SALES_CNT, ' +
   ' CAST(ORDER_CNT AS DECIMAL(18,6)) AS ORDER_CNT FROM #VT_BI_1_5_S1_FUNC ' +
   ' UNION ' +
   'SELECT  TIME_ID, PRD_ID, ' +
	'PMART.DIVIDE_BY_ZERO(CAST(SUM(SALES_CNT) AS DECIMAL(18,6)) , SUM(S_CNT)) AS SAL_PSD, ' +
	'PMART.DIVIDE_BY_ZERO(CAST(SUM(ORDER_CNT)  AS DECIMAL(18,6)) , SUM(O_CNT)) AS ORD_PSD ' +
	'FROM ( ' +
	'	SELECT 99991231 AS TIME_ID, PRD_ID,  ' +
	'	SALES_CNT, CASE WHEN SALES_CNT <> 0 THEN 1 ELSE 0 END AS S_CNT, ' +
	'	ORDER_CNT, CASE WHEN ORDER_CNT <> 0 THEN 1 ELSE 0 END AS O_CNT ' +
	'	FROM #VT_BI_1_5_S1_FUNC ' +
	')  AS T ' +
	'GROUP BY TIME_ID, PRD_ID ' ;
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_BI_1_5_FUNC  AS('+
        V_STRING +
   ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;
END SP;