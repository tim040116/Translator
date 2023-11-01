REPLACE PROCEDURE PMART.NEW_TOX_MMA_MFACT_SUM_1
(FP_AMT_TYPE CHAR(1),
FP_TIME_TYPE CHAR(1),FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_ORG_LEVEL NUMBER,FP_ORG_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR(1),FP_PRD_LEVEL NUMBER,FP_PRD_ID NUMBER,FP_MMA_LIST VARCHAR(2000))
 SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000);    
CALL PMART.P_DROP_TABLE ('#VT_NEW_TOX_MMA_MFACT_SUM_1'); 
CALL PMART.NEW_TOX_MMA_MFACT_1(FP_AMT_TYPE,FP_TIME_TYPE,FP_TIME_LIST,FP_LTIME_LIST,FP_ORG_LEVEL,FP_ORG_LIST,FP_PRD_TYPE,FP_PRD_LEVEL,FP_PRD_ID,FP_MMA_LIST);
SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TOX_MMA_MFACT_SUM_1  AS('+   
'SELECT '+
'-2 AS MMA_ID, '+
'ORG_ID AS ORG_ID, '+
'TIME_ID AS TIME_ID, '+
'SUM(SALES_CNT) AS SALES_CNT, '+
'SUM(SALES_AMT) AS SALES_AMT, '+
'SUM(ORDER_CNT)AS ORDER_CNT, '+
'SUM(ORDER_AMT) AS ORDER_AMT, '+
'SUM(THROW_CNT) AS THROW_CNT, '+
'SUM(THROW_AMT) AS THROW_AMT, '+
'SUM(LSALES_CNT) AS LSALES_CNT, '+
'SUM(LSALES_AMT) AS LSALES_AMT, '+
'SUM(LORDER_CNT) AS LORDER_CNT, '+
'SUM(LORDER_AMT) AS LORDER_AMT, '+
'SUM(LTHROW_CNT) AS LTHROW_CNT, '+
'SUM(LTHROW_AMT) AS LTHROW_AMT, '+
'SUM(CUST_NUM) AS CUST_NUM, '+
'SUM(ITMRECEIVED_EXTEND_AMT) AS ITMRECEIVED_EXTEND_AMT, '+
'SUM(ITMRECEIVED_EXTEND_AMT_AVG) AS ITMRECEIVED_EXTEND_AMT_AVG, '+
'SUM(ITMRECEIVED_EXTEND_PRE_AMT) AS ITMRECEIVED_EXTEND_PRE_AMT, '+
'SUM(ITMRETURN_EXTEND_AMT) AS ITMRETURN_EXTEND_AMT, '+
'SUM(ITMRETURN_EXTEND_PRE_AMT) AS ITMRETURN_EXTEND_PRE_AMT, '+
'SUM(ITMTRANSFER_EXTEND_AMT) AS ITMTRANSFER_EXTEND_AMT, '+
'SUM(ITMTRANSFER_EXTEND_PRE_AMT) AS ITMTRANSFER_EXTEND_PRE_AMT, '+
'SUM(DISCOUNT_EXTEND_AMT) AS DISCOUNT_EXTEND_AMT, '+
'SUM(LET_EXTEND_AMT) AS LET_EXTEND_AMT, '+
'SUM(ITMREFOUND_EXTEND_AMT) AS ITMREFOUND_EXTEND_AMT, '+
'SUM(ITMRECEIVED_EXTEND_NUM) AS ITMRECEIVED_EXTEND_NUM, '+
'SUM(ITMRECEIVED_EXTEND_NUM_AVG) AS ITMRECEIVED_EXTEND_NUM_AVG, '+
'SUM(ITMRECEIVED_EXTEND_PRE_NUM) AS ITMRECEIVED_EXTEND_PRE_NUM, '+
'SUM(ITMRETURN_EXTEND_NUM) AS ITMRETURN_EXTEND_NUM, '+
'SUM(ITMRETURN_EXTEND_PRE_NUM) AS ITMRETURN_EXTEND_PRE_NUM, '+
'SUM(ITMTRANSFER_EXTEND_NUM) AS ITMTRANSFER_EXTEND_NUM, '+
'SUM(ITMTRANSFER_EXTEND_PRE_NUM) AS ITMTRANSFER_EXTEND_PRE_NUM '+
'FROM #VT_NEW_TOX_MMA_MFACT_1 '+
'GROUP BY TIME_ID,ORG_ID  ' +	  
') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END SP;