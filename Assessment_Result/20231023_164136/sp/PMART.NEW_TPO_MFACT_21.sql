REPLACE PROCEDURE PMART.NEW_TPO_MFACT_21 
(FP_AMT_TYPE CHAR(1) CASESPECIFIC,FP_TIME_TYPE CHAR(1) CASESPECIFIC,
FP_TIME_LIST VARCHAR(2000) CASESPECIFIC,FP_LTIME_LIST VARCHAR(2000) CASESPECIFIC,
FP_PRD_TYPE CHAR(1) CASESPECIFIC,FP_PRD_LEVEL NUMBER,FP_PRD_LIST VARCHAR(8000),
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000); 
DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TPO_MFACT_21'); 
   CASE
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='P' AND FP_PRD_LEVEL=2  AND FP_ORG_LEVEL=4   THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';             
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='R' AND FP_PRD_LEVEL=1    AND FP_ORG_LEVEL=4  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';
      WHEN FP_PRD_TYPE='L' THEN 
            SET V_TABLE_NAME = 'PMART.BASIC_MLFACT';
      ELSE
             SET V_TABLE_NAME = 'PMART.BASIC_MFACT';
   END CASE;
   SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TPO_MFACT_21  AS('+
     'SELECT '+
     'F1.TIME_ID AS TIME_ID, '+
     'F1.PRD_ID AS PRD_ID, '+
     'CAST(F1.SALES_CNT AS DECIMAL(18,6)) AS SALES_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.SALES_AMT AS DECIMAL(18,6)) /1000,CAST(F1.SALES_AMT AS DECIMAL(18,6)) ) AS SALES_AMT, '+
     'CAST(F1.ORDER_CNT  AS DECIMAL(18,6)) AS ORDER_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.ORDER_AMT AS DECIMAL(18,6)) /1000,CAST(F1.ORDER_AMT AS DECIMAL(18,6)) ) AS ORDER_AMT, '+
     'CAST(F1.THROW_CNT  AS DECIMAL(18,6)) AS THROW_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.THROW_AMT AS DECIMAL(18,6)) /1000,CAST(F1.THROW_AMT AS DECIMAL(18,6)) ) AS THROW_AMT, '+
     'CAST(F1.INPRD_CNT  AS DECIMAL(18,6)) AS INPRD_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.INPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F1.INPRD_AMT AS DECIMAL(18,6)) ) AS INPRD_AMT, '+
     'CAST(F1.RETPRD_CNT  AS DECIMAL(18,6)) AS RETPRD_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.RETPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F1.RETPRD_AMT AS DECIMAL(18,6)) ) AS RETPRD_AMT, '+
     'CAST(F1.TRANSPRD_CNT  AS DECIMAL(18,6)) AS TRANSPRD_CNT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.TRANSPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F1.TRANSPRD_AMT AS DECIMAL(18,6)) ) AS TRANSPRD_AMT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.DIS_AMT AS DECIMAL(18,6)) /1000,CAST(F1.DIS_AMT AS DECIMAL(18,6)) ) AS DIS_AMT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.SUB_AMT AS DECIMAL(18,6)) /1000,CAST(F1.SUB_AMT AS DECIMAL(18,6)) ) AS SUB_AMT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.DISSUB_AMT AS DECIMAL(18,6)) /1000,CAST(F1.DISSUB_AMT AS DECIMAL(18,6)) ) AS DISSUB_AMT, '+
     'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.SALES_DISSUB_AMT AS DECIMAL(18,6)) /1000,CAST(F1.SALES_DISSUB_AMT AS DECIMAL(18,6)) ) AS SALES_DISSUB_AMT, '+
     'CAST(NVL(F2.SALES_CNT,0)  AS DECIMAL(18,6)) AS LSALES_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.SALES_AMT AS DECIMAL(18,6)) /1000,CAST(F2.SALES_AMT AS DECIMAL(18,6)) ),0) AS LSALES_AMT, '+
     'CAST(NVL(F2.ORDER_CNT,0)  AS DECIMAL(18,6)) AS LORDER_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.ORDER_AMT AS DECIMAL(18,6)) /1000,CAST(F2.ORDER_AMT AS DECIMAL(18,6)) ),0) AS LORDER_AMT, '+
     'CAST(NVL(F2.THROW_CNT,0)  AS DECIMAL(18,6)) AS LTHROW_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.THROW_AMT AS DECIMAL(18,6)) /1000,CAST(F2.THROW_AMT AS DECIMAL(18,6)) ),0) AS LTHROW_AMT, '+
     'CAST(NVL(F2.INPRD_CNT,0)  AS DECIMAL(18,6)) AS LINPRD_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.INPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F2.INPRD_AMT AS DECIMAL(18,6)) ),0) AS LINPRD_AMT, '+
     'CAST(NVL(F2.RETPRD_CNT,0)  AS DECIMAL(18,6)) AS LRETPRD_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.RETPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F2.RETPRD_AMT AS DECIMAL(18,6)) ),0) AS LRETPRD_AMT, '+
     'CAST(NVL(F2.TRANSPRD_CNT,0)  AS DECIMAL(18,6)) AS LTRANSPRD_CNT, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.TRANSPRD_AMT AS DECIMAL(18,6)) /1000,CAST(F2.TRANSPRD_AMT AS DECIMAL(18,6)) ),0) AS LTRANSPRD_AMT, '+
     'CAST(NVL(F3.CUST_NUM,0)  AS DECIMAL(18,6)) AS CUST_NUM, '+
     'NVL(DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.SALES_DISSUB_AMT AS DECIMAL(18,6)) /1000,CAST(F2.SALES_DISSUB_AMT AS DECIMAL(18,6)) ),0) AS LSALES_DISSUB_AMT '+
     'FROM '+
     '( '+
     'SELECT  '+
     'DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
     'TIME_ID AS TIME_ID, '+
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
     'DIS_AMT + SUB_AMT AS DISSUB_AMT, '+
     'SALES_AMT - DIS_AMT - SUB_AMT AS SALES_DISSUB_AMT '+
     'FROM '+ V_TABLE_NAME +' '+
     'WHERE TIME_ID IN ('+ FP_TIME_LIST +') '+
     'AND PRD_ID IN ('+ FP_PRD_LIST+') '+
     'AND ORG_ID='+ FP_ORG_ID +' '+
     ') F1 LEFT JOIN '+
     '( '+
     'SELECT  '+
     'DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
     'TIME_ID AS TIME_ID, '+
     'PRD_ID AS PRD_ID, '+
     'SALES_CNT AS SALES_CNT, '+
     'SALES_AMT AS SALES_AMT, '+
     'ORDER_CNT AS ORDER_CNT, '+
     'ORDER_AMT AS ORDER_AMT, '+
     'THROW_CNT AS THROW_CNT, '+
     'THROW_AMT AS THROW_AMT,  '+
     'INPRD_CNT AS INPRD_CNT, '+
     'INPRD_AMT AS INPRD_AMT,  '+
     'RETPRD_CNT AS RETPRD_CNT, '+
     'RETPRD_AMT AS RETPRD_AMT,  '+
     'TRANSPRD_CNT AS TRANSPRD_CNT, '+
     'TRANSPRD_AMT AS TRANSPRD_AMT,  '+
     'SALES_AMT - DIS_AMT - SUB_AMT AS SALES_DISSUB_AMT '+
     'FROM '+ V_TABLE_NAME +' '+
     'WHERE TIME_ID IN ('+ FP_LTIME_LIST +') '+
     'AND PRD_ID IN ('+ FP_PRD_LIST +') '+
     'AND ORG_ID='+ FP_ORG_ID +' '+
     ') F2 ON(F1.JOIN_KEY=F2.JOIN_KEY AND F1.PRD_ID=F2.PRD_ID) '+
     'LEFT JOIN '+
     '( '+
     'SELECT  TIME_ID,CUST_NUM '+
     'FROM PMART.BASIC_SFACT '+
     'WHERE TIME_ID IN ('+ FP_TIME_LIST +') ' +
     'AND ORG_ID='+ FP_ORG_ID +' '+
     ') F3 ON(F1.TIME_ID=F3.TIME_ID) '+
     ') WITH DATA UNIQUE PRIMARY  CHARINDEX(PRD_ID , TIME_ID) ON COMMIT PRESERVE ROWS;';  
	EXECUTE IMMEDIATE SQLSTR;
END SP;