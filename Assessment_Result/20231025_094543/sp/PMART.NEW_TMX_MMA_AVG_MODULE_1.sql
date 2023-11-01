REPLACE PROCEDURE PMART.NEW_TMX_MMA_AVG_MODULE_1
(FP_AMT_TYPE CHAR,FP_TIME_TYPE CHAR,
FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR,FP_PRD_LEVEL NUMBER,FP_PRD_ID NUMBER,
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER,FP_MMA_LIST VARCHAR(2000))
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TMX_MMA_AVG_MODULE_1'); 
   CALL PMART.NEW_TMX_MMA_MFACT_1(FP_AMT_TYPE ,FP_TIME_TYPE ,FP_TIME_LIST ,FP_LTIME_LIST ,FP_PRD_TYPE ,FP_PRD_LEVEL ,FP_PRD_ID ,FP_ORG_LEVEL ,FP_ORG_ID ,FP_MMA_LIST );
   CALL PMART.NEW_TMX_MMA_STFACT(FP_TIME_TYPE ,FP_TIME_LIST ,FP_LTIME_LIST ,FP_PRD_TYPE ,FP_PRD_LEVEL ,FP_PRD_ID ,FP_ORG_LEVEL ,FP_ORG_ID ,FP_MMA_LIST );
   SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TMX_MMA_AVG_MODULE_1  AS('+
                   'SELECT '+
                   'A.TIME_ID AS TIME_ID, '+
                   'A.MMA_ID AS MMA_ID, '+
                   'A.ORDER_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS ORDER_EXTEND_AMT, '+
                   'A.LORDER_AMT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LORDER_EXTEND_AMT, '+
                   'A.ORDER_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)- '+
                   'A.LORDER_AMT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) '+
                   'AS LORDER_EXTEND_AMT_DIFF, '+
                   'CASE WHEN '+
                   '((A.ORDER_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_AMT,0,NULL,A.LORDER_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)>=10000 THEN 9999.99 '+
                   'WHEN '+
                   '((A.ORDER_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_AMT,0,NULL,A.LORDER_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)<=-10000 THEN -9999.99 '+
                   'ELSE ((A.ORDER_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_AMT,0,NULL,A.LORDER_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100) END '+
                   'AS LORDER_EXTEND_AMT_RATE, '+
                   'A.SALES_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS SALES_EXTEND_AMT, '+
                   'A.LSALES_AMT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LSALES_EXTEND_AMT, '+
                   'A.SALES_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)- '+
                   'A.LSALES_AMT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) '+
                   'AS LSALES_EXTEND_AMT_DIFF, '+
                   'CASE WHEN '+
                   '((A.SALES_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_AMT,0,NULL,A.LSALES_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)>=10000 THEN 9999.99 '+
                   'WHEN '+
                   '((A.SALES_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_AMT,0,NULL,A.LSALES_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)<=-10000 THEN -9999.99 '+
                   'ELSE ((A.SALES_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_AMT,0,NULL,A.LSALES_AMT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100) END '+
                   'AS LSALES_EXTEND_AMT_RATE, '+
                   'A.THROW_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS THROW_EXTEND_AMT, '+
                   'A.LTHROW_AMT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LTHROW_EXTEND_AMT, '+
                   'PMART.DIVIDE_BY_ZERO((A.THROW_AMT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)), '+
                   '(DECODE(A.ORDER_AMT,0,NULL,A.ORDER_AMT)/ '+
                   'DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)))*100 AS THROW_EXTEND_AMT_RATE, '+
                   ' '+
                   'A.ORDER_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS ORDER_EXTEND_CNT, '+
                   'A.LORDER_CNT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LORDER_EXTEND_CNT, '+
                   'A.ORDER_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)- '+
                   'A.LORDER_CNT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) '+
                   'AS LORDER_EXTEND_CNT_DIFF, '+
                   'CASE WHEN '+
                   '((A.ORDER_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_CNT,0,NULL,A.LORDER_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)>=10000 THEN 9999.99 '+
                   'WHEN '+
                   '((A.ORDER_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_CNT,0,NULL,A.LORDER_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)<=-10000 THEN -9999.99 '+
                   'ELSE ((A.ORDER_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LORDER_CNT,0,NULL,A.LORDER_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100) END '+
                   'AS LORDER_EXTEND_CNT_RATE, '+
                   'A.SALES_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS SALES_EXTEND_CNT, '+
                   'A.LSALES_CNT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LSALES_EXTEND_CNT, '+
                   'A.SALES_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)- '+
                   'A.LSALES_CNT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) '+
                   'AS LSALES_EXTEND_CNT_DIFF, '+
                   'CASE WHEN '+
                   '((A.SALES_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_CNT,0,NULL,A.LSALES_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)>=10000 THEN 9999.99 '+
                   'WHEN '+
                   '((A.SALES_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_CNT,0,NULL,A.LSALES_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100)<=-10000 THEN -9999.99 '+
                   'ELSE ((A.SALES_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM))/ '+
                   'PMART.DIVIDE_BY_ZERO(DECODE(A.LSALES_CNT,0,NULL,A.LSALES_CNT), '+
                   'DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM))*100) END '+
                   'AS LSALES_EXTEND_CNT_RATE, '+
                   'A.THROW_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM) AS THROW_EXTEND_CNT, '+
                   'A.LTHROW_CNT/DECODE(B.LMAST_STORE_NUM,0,NULL,B.LMAST_STORE_NUM) AS LTHROW_EXTEND_CNT, '+
                   'PMART.DIVIDE_BY_ZERO((A.THROW_CNT/DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)), '+
                   '(DECODE(A.ORDER_CNT,0,NULL,A.ORDER_CNT)/ '+
                   'DECODE(B.MAST_STORE_NUM,0,NULL,B.MAST_STORE_NUM)))*100 AS THROW_EXTEND_CNT_RATE, '+
                   'B.SHOP_EXTEND_RECEIVED_NUM AS SHOP_EXTEND_RECEIVED_NUM, '+
                   'NULL  AS SHOP_EXTEND_RECEIVED_RATE,  '+
                   'B.SHOP_EXTEND_RETURN_NUM AS SHOP_EXTEND_RETURN_NUM, '+
                   'B.SHOP_EXTEND_TRANSFER_NUM AS SHOP_EXTEND_TRANSFER_NUM, '+
                   'B.SHOP_EXTEND_TRANSFER_IN_NUM AS SHOP_EXTEND_TRANSFER_IN_NUM, '+
                   'A.ITMRECEIVED_EXTEND_AMT AS ITMRECEIVED_EXTEND_AMT, '+
                   'A.ITMRECEIVED_EXTEND_PRE_AMT AS ITMRECEIVED_EXTEND_PRE_AMT, '+
                   'A.ITMRETURN_EXTEND_AMT AS ITMRETURN_EXTEND_AMT, '+
                   'A.ITMRETURN_EXTEND_PRE_AMT AS ITMRETURN_EXTEND_PRE_AMT, '+
                   'A.ITMTRANSFER_EXTEND_AMT AS ITMTRANSFER_EXTEND_AMT, '+
                   'A.ITMTRANSFER_EXTEND_PRE_AMT AS ITMTRANSFER_EXTEND_PRE_AMT, '+
                   'A.DISCOUNT_EXTEND_AMT AS DISCOUNT_EXTEND_AMT, '+
                   'A.LET_EXTEND_AMT AS LET_EXTEND_AMT, '+
                   'A.ITMREFOUND_EXTEND_AMT AS ITMREFOUND_EXTEND_AMT, '+
                   'A.ITMRECEIVED_EXTEND_NUM AS ITMRECEIVED_EXTEND_NUM, '+
                   'NULL AS ITMRECEIVED_EXTEND_NUM_AVG,  '+
                   'A.ITMRECEIVED_EXTEND_PRE_NUM AS ITMRECEIVED_EXTEND_PRE_NUM, '+
                   'A.ITMRETURN_EXTEND_NUM AS ITMRETURN_EXTEND_NUM, '+
                   'A.ITMRETURN_EXTEND_PRE_NUM AS ITMRETURN_EXTEND_PRE_NUM, '+
                   'A.ITMTRANSFER_EXTEND_NUM AS ITMTRANSFER_EXTEND_NUM, '+
                   'A.ITMTRANSFER_EXTEND_PRE_NUM AS ITMTRANSFER_EXTEND_PRE_NUM '+
                   'FROM'+
                   '  #VT_NEW_TMX_MMA_MFACT_1 A LEFT JOIN '+
                   '  #VT_NEW_TMX_MMA_STFACT B '+
                   '  ON (A.TIME_ID=B.TIME_ID AND A.MMA_ID=B.MMA_ID) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END SP;