REPLACE PROCEDURE PMART.POSN_MFACT_1_4_ALL
(
FP_TIME_TYPE CHAR,         
FP_TIME_LIST VARCHAR(400), 
FP_MMA_LEVEL NUMBER,       
FP_MMA_ID    VARCHAR(400), 
FP_PRD_LIST  VARCHAR(400), 
FP_AMT_TYPE  VARCHAR(400)  
)            
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR        VARCHAR(4000);
   DECLARE I             NUMBER;
   DECLARE BASE_AMTUNIT  NUMBER;        
   DECLARE V_SQL_DATA    VARCHAR(2000);  
   DECLARE V_TIME_SQL    VARCHAR(200);  
   CALL PMART.P_DROP_TABLE ('#VT_POSN_MFACT_1_4_ALL'); 
   SET V_TIME_SQL = ' ';
   IF FP_AMT_TYPE='T' THEN SET BASE_AMTUNIT = 1000; ELSE SET BASE_AMTUNIT = 1;  END IF;
   IF FP_TIME_TYPE = 'W' THEN
      SET V_TIME_SQL ='SELECT L_DAY_ID,L_WEEK_ID AS P_TIME_ID FROM PMART.YMWD_TIME_W2 WHERE L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') ';
   END IF;
   IF FP_TIME_TYPE = 'M' THEN
      SET V_TIME_SQL ='SELECT L_DAY_ID,L_MONTH_ID AS P_TIME_ID FROM PMART.YMWD_TIME_W2 WHERE L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') ';
   END IF;
   SET V_SQL_DATA =
   'SUM(SALES_CNT) AS SALES_CNT, '+
   'SUM(CAST(SALES_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS SALES_AMT, '+
   'SUM(ORDER_CNT) AS ORDER_CNT, '+
   'SUM(CAST(ORDER_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS ORDER_AMT, '+
   'SUM(THROW_CNT) AS THROW_CNT, '+
   'SUM(CAST(THROW_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS THROW_AMT, '+
   'SUM(INPRD_CNT) AS INPRD_CNT, '+
   'SUM(CAST(INPRD_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS INPRD_AMT, '+
   'SUM(RETPRD_CNT) AS RETPRD_CNT, '+
   'SUM(RETPRD_AMT)/'+BASE_AMTUNIT+' AS RETPRD_AMT, '+
   'SUM(TRANSPRD_CNT) AS TRANSPRD_CNT, '+
   'SUM(TRANSPRD_AMT)/'+BASE_AMTUNIT+' AS TRANSPRD_AMT, '+
   'SUM(DIS_AMT)/'+BASE_AMTUNIT+' AS DIS_AMT, '+
   'SUM(CAST(SUB_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS SUB_AMT, '+
   'SUM(SALES_UNTAX_COST)/'+BASE_AMTUNIT+' AS SALES_UNTAX_COST, '+
   'SUM(SALES_UNTAX_REAL_AMT)/'+BASE_AMTUNIT+' AS SALES_UNTAX_REAL_AMT, '+
   'SUM(ORDER_SALES_CNT) AS ORDER_SALES_CNT, '+
   'SUM(CAST(ORDER_SALES_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS ORDER_SALES_AMT, '+
   'SUM(INPRD_SALES_CNT) AS INPRD_SALES_CNT, '+
   'SUM(CAST(INPRD_SALES_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS INPRD_SALES_AMT, '+
   'SUM(Y_ORDER_SALES_CNT) AS Y_ORDER_SALES_CNT, '+
   'SUM(CAST(Y_ORDER_SALES_AMT AS DECIMAL(16,6)))/'+BASE_AMTUNIT+' AS Y_ORDER_SALES_AMT, '+
   '(SUM(CAST(SALES_AMT AS DECIMAL(16,6)))-SUM(CAST(DIS_AMT AS DECIMAL(16,6)))-SUM(CAST(SUB_AMT AS DECIMAL(16,6))))/'+BASE_AMTUNIT+' AS REAL_SALES_AMT '; 
   IF (FP_TIME_TYPE = 'W' OR FP_TIME_TYPE = 'M' ) THEN 
      IF(FP_MMA_LEVEL = 0) THEN 
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'TT.P_TIME_ID AS TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     '0 AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T ,('+ V_TIME_SQL +') TT '+
                     ' WHERE TT.P_TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.TIME_ID = TT.L_DAY_ID '+
                     ' AND T.ORG_ID =  -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' GROUP BY TT.P_TIME_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
      IF(FP_MMA_LEVEL = 1) THEN 
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'TT.P_TIME_ID AS TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     'T.MMA_ID AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T ,('+ V_TIME_SQL +') TT '+
                     ' WHERE TT.P_TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.TIME_ID = TT.L_DAY_ID '+
                     ' AND T.ORG_ID =  -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' GROUP BY TT.P_TIME_ID,T.MMA_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
      IF(FP_MMA_LEVEL = 2) THEN 
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'TT.P_TIME_ID AS TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     'T.SEC_MMA_ID AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T ,('+ V_TIME_SQL +') TT '+
                     ' WHERE TT.P_TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.TIME_ID = TT.L_DAY_ID '+
                     ' AND T.ORG_ID =  -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' AND T.MMA_ID = ('+ FP_MMA_ID +') '+
                     ' GROUP BY TT.P_TIME_ID,T.SEC_MMA_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
   ELSE
      IF(FP_MMA_LEVEL = 0) THEN 
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'T.TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     '0 AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T '+
                     ' WHERE '+
                     ' T.TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.ORG_ID = -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' GROUP BY T.TIME_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
      IF(FP_MMA_LEVEL = 1) THEN 
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'T.TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     'T.MMA_ID AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T '+
                     ' WHERE '+
                     ' T.TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.ORG_ID = -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' GROUP BY T.TIME_ID,T.MMA_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
      IF(FP_MMA_LEVEL = 2) THEN
	       SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_MFACT_1_4_ALL  AS('+
                     'SELECT  '+
                     'T.TIME_ID, '+
                     '0 AS ORG_ID, '+
                     '0 AS PRD_ID, '+
                     'T.SEC_MMA_ID AS MMA_ID, '+
                     ' '+V_SQL_DATA+' '+
                     ' FROM PMART.BASIC_MFACT_SHOP_SEC T '+
                     ' WHERE '+
                     ' T.TIME_ID IN ('+ FP_TIME_LIST +') '+
                     ' AND T.ORG_ID = -1 '+
                     ' AND T.PRD_ID IN ('+ FP_PRD_LIST +') '+
                     ' AND T.MMA_ID = ('+ FP_MMA_ID +') '+
                     ' GROUP BY T.TIME_ID,T.SEC_MMA_ID'+
                     ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      END IF;
   END IF;
  EXECUTE IMMEDIATE SQLSTR;
END SP;