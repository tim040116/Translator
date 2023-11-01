REPLACE PROCEDURE PMART.NEW_TMX_MMA_MFACT
(FP_AMT_TYPE CHAR,FP_TIME_TYPE CHAR,
FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR,FP_PRD_LEVEL NUMBER,FP_PRD_ID NUMBER,
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER,FP_MMA_LIST VARCHAR(2000))
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR1  VARCHAR(10000); 	
   DECLARE SQLSTR2  VARCHAR(10000); 	
   DECLARE SQLSTR3  VARCHAR(30000); 	
   DECLARE SQLSTRFINAL VARCHAR(64000); 	
   DECLARE V_TABLE_NAME VARCHAR(60);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TMX_MMA_MFACT'); 
   CASE
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='P' AND FP_PRD_LEVEL=2  AND FP_ORG_LEVEL=3  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_SHOP';             
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='R' AND FP_PRD_LEVEL=1  AND FP_ORG_LEVEL=3  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_SHOP';
      WHEN FP_PRD_TYPE='L' THEN 
            SET V_TABLE_NAME = 'PMART.BASIC_MLFACT_SHOP';
      ELSE
             SET V_TABLE_NAME = 'PMART.BASIC_MFACT_SHOP';
   END CASE;
   SET SQLSTR1 = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TMX_MMA_MFACT  AS('+
     'SELECT '+
     'F1.TIME_ID AS TIME_ID, '+
     'F1.MMA_ID AS MMA_ID, '+
     'CAST(F1.SALES_CNT AS DECIMAL(18,6)) AS SALES_CNT, ';
	   IF FP_AMT_TYPE='T' THEN
	   SET SQLSTR2 = 'CAST(F1.SALES_AMT AS DECIMAL(18,6))/1000 AS SALES_AMT, '+
	   'CAST(F1.ORDER_CNT AS DECIMAL(18,6)) AS ORDER_CNT, '+
	   'CAST(F1.ORDER_AMT AS DECIMAL(18,6))/1000 AS ORDER_AMT, '+
	   'CAST(F1.THROW_CNT AS DECIMAL(18,6)) AS THROW_CNT, '+
	   'CAST(F1.THROW_AMT AS DECIMAL(18,6))/1000 AS THROW_AMT, '+
	   'CAST(F2.SALES_CNT AS DECIMAL(18,6)) AS LSALES_CNT, '+
	   'CAST(F2.SALES_AMT AS DECIMAL(18,6))/1000 AS LSALES_AMT, '+
	   'CAST(F2.ORDER_CNT AS DECIMAL(18,6)) AS LORDER_CNT, '+
	   'CAST(F2.ORDER_AMT AS DECIMAL(18,6))/1000 AS LORDER_AMT, '+
	   'CAST(F2.THROW_CNT AS DECIMAL(18,6)) AS LTHROW_CNT, '+
	   'CAST(F2.THROW_AMT AS DECIMAL(18,6))/1000 AS LTHROW_AMT, '+
	   'CAST(F3.CUST_NUM AS DECIMAL(18,6)) AS CUST_NUM, '+
	   'CAST(F1.INPRD_AMT AS DECIMAL(18,6))/1000 AS ITMRECEIVED_EXTEND_AMT, '+
	   'NULL AS ITMRECEIVED_EXTEND_AMT_AVG, '+  
	   'CAST(F2.INPRD_AMT AS DECIMAL(18,6))/1000 AS ITMRECEIVED_EXTEND_PRE_AMT, '+
	   'CAST(F1.RETPRD_AMT AS DECIMAL(18,6))/1000 AS ITMRETURN_EXTEND_AMT, '+
	   'CAST(F2.RETPRD_AMT AS DECIMAL(18,6))/1000 AS ITMRETURN_EXTEND_PRE_AMT, '+
	   'CAST(F1.TRANSPRD_AMT AS DECIMAL(18,6))/1000 AS ITMTRANSFER_EXTEND_AMT, '+
	   'CAST(F2.TRANSPRD_AMT AS DECIMAL(18,6))/1000 AS ITMTRANSFER_EXTEND_PRE_AMT, '+
	   'CAST(F1.DIS_AMT AS DECIMAL(18,6))/1000 AS DISCOUNT_EXTEND_AMT, '+
	   'CAST(F1.SUB_AMT AS DECIMAL(18,6))/1000 AS LET_EXTEND_AMT, '+
	   '(CAST((F1.DIS_AMT+F1.SUB_AMT) AS DECIMAL(18,6))/1000) AS ITMREFOUND_EXTEND_AMT, '+	   
	   'CAST(F1.SALES_DISSUB_AMT AS DECIMAL(18,6))/1000 AS SALES_DISSUB_EXTEND_AMT, '+
	   'CAST(F2.SALES_DISSUB_AMT AS DECIMAL(18,6))/1000 AS LSALES_DISSUB_EXTEND_AMT, ' ;
	   ELSE
	   SET SQLSTR2 = 'CAST(F1.SALES_AMT AS DECIMAL(18,6)) AS SALES_AMT, '+
	   'CAST(F1.ORDER_CNT AS DECIMAL(18,6)) AS ORDER_CNT, '+
	   'CAST(F1.ORDER_AMT AS DECIMAL(18,6)) AS ORDER_AMT, '+
	   'CAST(F1.THROW_CNT AS DECIMAL(18,6)) AS THROW_CNT, '+
	   'CAST(F1.THROW_AMT AS DECIMAL(18,6)) AS THROW_AMT, '+
	   'CAST(F2.SALES_CNT AS DECIMAL(18,6)) AS LSALES_CNT, '+
	   'CAST(F2.SALES_AMT AS DECIMAL(18,6)) AS LSALES_AMT, '+
	   'CAST(F2.ORDER_CNT AS DECIMAL(18,6)) AS LORDER_CNT, '+
	   'CAST(F2.ORDER_AMT AS DECIMAL(18,6)) AS LORDER_AMT, '+
	   'CAST(F2.THROW_CNT AS DECIMAL(18,6)) AS LTHROW_CNT, '+
	   'CAST(F2.THROW_AMT AS DECIMAL(18,6)) AS LTHROW_AMT, '+
	   'CAST(F3.CUST_NUM AS DECIMAL(18,6)) AS CUST_NUM, '+
	   'CAST(F1.INPRD_AMT AS DECIMAL(18,6)) AS ITMRECEIVED_EXTEND_AMT, '+
	   'NULL AS ITMRECEIVED_EXTEND_AMT_AVG, '+  
	   'CAST(F2.INPRD_AMT AS DECIMAL(18,6)) AS ITMRECEIVED_EXTEND_PRE_AMT, '+
	   'CAST(F1.RETPRD_AMT AS DECIMAL(18,6)) AS ITMRETURN_EXTEND_AMT, '+
	   'CAST(F2.RETPRD_AMT AS DECIMAL(18,6)) AS ITMRETURN_EXTEND_PRE_AMT, '+
	   'CAST(F1.TRANSPRD_AMT AS DECIMAL(18,6)) AS ITMTRANSFER_EXTEND_AMT, '+
	   'CAST(F2.TRANSPRD_AMT AS DECIMAL(18,6)) AS ITMTRANSFER_EXTEND_PRE_AMT, '+
	   'CAST(F1.DIS_AMT AS DECIMAL(18,6)) AS DISCOUNT_EXTEND_AMT, '+
	   'CAST(F1.SUB_AMT AS DECIMAL(18,6)) AS LET_EXTEND_AMT, '+
	   '(CAST((F1.DIS_AMT+F1.SUB_AMT) AS DECIMAL(18,6))) AS ITMREFOUND_EXTEND_AMT, '+
	   'CAST(F1.SALES_DISSUB_AMT AS DECIMAL(18,6)) AS SALES_DISSUB_EXTEND_AMT, '+
	   'CAST(F2.SALES_DISSUB_AMT AS DECIMAL(18,6)) AS LSALES_DISSUB_EXTEND_AMT, ' ;
	   END IF;
	   SET SQLSTR3 = 'CAST(F1.INPRD_CNT AS DECIMAL(18,6)) AS ITMRECEIVED_EXTEND_NUM, '+
	   'NULL AS ITMRECEIVED_EXTEND_NUM_AVG, '+ 
	   'CAST(F2.INPRD_CNT AS DECIMAL(18,6)) AS ITMRECEIVED_EXTEND_PRE_NUM , '+
	   'CAST(F1.RETPRD_CNT AS DECIMAL(18,6)) AS ITMRETURN_EXTEND_NUM, '+
	   'CAST(F2.RETPRD_CNT AS DECIMAL(18,6)) AS ITMRETURN_EXTEND_PRE_NUM , '+
	   'CAST(F1.TRANSPRD_CNT AS DECIMAL(18,6)) AS ITMTRANSFER_EXTEND_NUM, '+
	   'CAST(F2.TRANSPRD_CNT AS DECIMAL(18,6)) AS ITMTRANSFER_EXTEND_PRE_NUM '+
     'FROM '+
     '( '+
     'SELECT  DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
     'TIME_ID AS TIME_ID, '+
     'MMA_ID AS MMA_ID, '+
     'PRD_ID AS PRD_ID, '+
     'SALES_CNT AS SALES_CNT, '+
     'SALES_AMT AS SALES_AMT, '+
     'ORDER_CNT AS ORDER_CNT, '+
     'ORDER_AMT AS ORDER_AMT, '+
     'THROW_CNT AS THROW_CNT, '+
     'THROW_AMT AS THROW_AMT, '+
     'INPRD_CNT AS INPRD_CNT, '+
     'INPRD_AMT AS INPRD_AMT, '+
     'RETPRD_CNT AS RETPRD_CNT, '+
     'RETPRD_AMT AS RETPRD_AMT, '+
     'TRANSPRD_CNT AS TRANSPRD_CNT, '+
     'TRANSPRD_AMT AS TRANSPRD_AMT, '+
     'DIS_AMT AS DIS_AMT, '+
     'SUB_AMT AS SUB_AMT, '+
     'SALES_AMT - DIS_AMT - SUB_AMT AS SALES_DISSUB_AMT '+
     'FROM '+ V_TABLE_NAME +' '+
     'WHERE TIME_ID IN ('+ FP_TIME_LIST +') '+
     'AND MMA_ID IN ('+ FP_MMA_LIST +') '+
     'AND PRD_ID='+ FP_PRD_ID +' '+
     'AND ORG_ID='+ FP_ORG_ID +''+
     ') F1 LEFT JOIN '+
     '( '+
     'SELECT  DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
     'TIME_ID AS TIME_ID, '+
     'MMA_ID AS MMA_ID, '+
     'PRD_ID AS PRD_ID, '+
     'SALES_CNT AS SALES_CNT, '+
     'SALES_AMT AS SALES_AMT, '+
     'ORDER_CNT AS ORDER_CNT, '+
     'ORDER_AMT AS ORDER_AMT, '+
     'THROW_CNT AS THROW_CNT, '+
     'THROW_AMT AS THROW_AMT, '+
     'INPRD_CNT AS INPRD_CNT, '+
     'INPRD_AMT AS INPRD_AMT, '+
     'RETPRD_CNT AS RETPRD_CNT, '+
     'RETPRD_AMT AS RETPRD_AMT, '+
     'TRANSPRD_CNT AS TRANSPRD_CNT, '+
     'TRANSPRD_AMT AS TRANSPRD_AMT, '+
     'DIS_AMT AS DIS_AMT, '+
     'SUB_AMT AS SUB_AMT, '+
     'SALES_AMT - DIS_AMT - SUB_AMT AS SALES_DISSUB_AMT '+      
     'FROM '+ V_TABLE_NAME +' '+
     'WHERE TIME_ID IN ('+ FP_LTIME_LIST +') '+
	 'AND MMA_ID IN ('+ FP_MMA_LIST +') '+
     'AND PRD_ID='+ FP_PRD_ID +' '+
     'AND ORG_ID='+ FP_ORG_ID +' '+
     ') F2 ON(F1.JOIN_KEY=F2.JOIN_KEY AND F1.PRD_ID=F2.PRD_ID AND F1.MMA_ID = F2.MMA_ID) '+
     'LEFT JOIN '+
     '( '+
     'SELECT TIME_ID,CUST_NUM '+
     'FROM PMART.BASIC_SFACT '+
     'WHERE TIME_ID IN ('+ FP_TIME_LIST +') ' +
     'AND ORG_ID='+ FP_ORG_ID +' '+
     ') F3 ON(F1.TIME_ID=F3.TIME_ID) '+     
     ') WITH DATA UNIQUE PRIMARY  CHARINDEX(TIME_ID , MMA_ID) ON COMMIT PRESERVE ROWS;';       
	SET SQLSTRFINAL = SQLSTR1+SQLSTR2+SQLSTR3;
	EXECUTE IMMEDIATE SQLSTRFINAL;
END SP;