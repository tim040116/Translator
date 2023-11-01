REPLACE PROCEDURE PMART.FUNC_FACT_EXCL_PRD_SUGORDER_D
(
   IN P_TIMEID VARCHAR(500),
   IN P_ORGID VARCHAR(10)
)
SP:BEGIN
	DECLARE SQLSTR VARCHAR(20000);
	DECLARE SELECT_X VARCHAR(500);
	DECLARE SELECT_Y VARCHAR(500);
	SET SELECT_X = ' ,SUM(T.PRD_QTY) AS TOTPRD_NUM '
						    + ' ,SUM(T.UCPRD_QTY) AS NCHG_CNT '
						    + ' ,CAST(CASE SUM(T.PRD_QTY) WHEN 0 THEN 0 ELSE CAST(SUM(T.UCPRD_QTY) AS DECIMAL(14,4))/CAST(SUM(T.PRD_QTY) AS DECIMAL(14,4))*100 END AS DECIMAL(14,2)) AS PRDUS_RATE '
						    + ' ,SUM(T.STORD_QTY) AS STORD_NUM '
						    + ' ,SUM(T.SYSORD_QTY) AS SYORD_NUM '
						    + ' ,CAST(CASE SUM(T.STORD_QTY) WHEN 0 THEN 0 ELSE CAST(SUM(T.STORD_QTY) AS DECIMAL(14,4))/CAST(SUM(T.SYSORD_QTY) AS DECIMAL(14,4))*100 END AS DECIMAL(14,2)) AS ORDD_RATE ';
	CALL PMART.P_DROP_TABLE ('#VT_FACT_EXCL_PRD_SUGORDER_D');
	SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_EXCL_PRD_SUGORDER_D AS( '
						 + ' SELECT T.TIME_ID AS TIME_ID '
						 + '       ,O.STORE_NO AS STORE_NO '
						 + '       ,O.STORE_NM AS STORE_NAME '
						 + SELECT_X
						 + ' FROM PMART.FACT_PBOA_EXCL_PRD T '
						 + ' INNER JOIN ( '
						 + ' SELECT STORE_NO, STORE_ID, STORE_NM '
						 + '    FROM PMART.LATEST_ORG_DIM '
						 + ' WHERE BRANCH_ID = '+ P_ORGID +' OR RESPON_ID = '+ P_ORGID +' OR STORE_ID = '+ P_ORGID +' '
						 + ' ) O ON T.ORG_ID = O.STORE_ID '
						 + ' WHERE T.TIME_ID IN ('+  P_TIMEID +') '
						 + ' GROUP BY T.TIME_ID, O.STORE_NO, O.STORE_NM '
						 + ' ) WITH DATA PRIMARY  CHARINDEX(STORE_NO,TIME_ID) ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;  
END SP;