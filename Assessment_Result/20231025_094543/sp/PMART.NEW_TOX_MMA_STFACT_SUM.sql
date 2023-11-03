REPLACE PROCEDURE PMART.NEW_TOX_MMA_STFACT_SUM
(FP_TIME_TYPE CHAR(1),FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_ORG_LEVEL NUMBER,FP_ORG_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR(1),FP_PRD_LEVEL NUMBER,FP_PRD_ID VARCHAR(7),FP_MMA_LIST VARCHAR(2000))
 SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000);    
CALL PMART.P_DROP_TABLE ('#VT_NEW_TOX_MMA_STFACT_SUM'); 
CALL PMART.NEW_TOX_MMA_STFACT(FP_TIME_TYPE,FP_TIME_LIST,FP_LTIME_LIST,FP_ORG_LEVEL,FP_ORG_LIST,FP_PRD_TYPE,FP_PRD_LEVEL,FP_PRD_ID,FP_MMA_LIST);
SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TOX_MMA_STFACT_SUM  AS('+   
'SELECT '+
'-2 AS MMA_ID, '+
'ORG_ID AS ORG_ID, '+
'TIME_ID AS TIME_ID, '+
'SUM(SALES_STORE_NUM) AS SALES_STORE_NUM, '+
'SUM(ORDER_STORE_NUM) AS ORDER_STORE_NUM, '+
'SUM(THROW_STORE_NUM)  AS THROW_STORE_NUM, '+
'SUM(SALES_ORDER_STORE_NUM) AS SALES_ORDER_STORE_NUM, '+
'SUM(MAST_STORE_NUM)  AS MAST_STORE_NUM, '+
'SUM(LMAST_STORE_NUM) AS LMAST_STORE_NUM, '+
'SUM(SHOP_EXTEND_RECEIVED_NUM) AS SHOP_EXTEND_RECEIVED_NUM, '+
'SUM(SHOP_EXTEND_RECEIVED_RATE) AS SHOP_EXTEND_RECEIVED_RATE, '+
'SUM(SHOP_EXTEND_RETURN_NUM) AS SHOP_EXTEND_RETURN_NUM, '+
'SUM(SHOP_EXTEND_TRANSFER_NUM)  AS SHOP_EXTEND_TRANSFER_NUM, '+
'SUM(SHOP_EXTEND_TRANSFER_IN_NUM)  AS  SHOP_EXTEND_TRANSFER_IN_NUM '+
'FROM #VT_NEW_TOX_MMA_STFACT B '+
'GROUP BY TIME_ID,ORG_ID ' +	  
') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END SP;