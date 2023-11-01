REPLACE PROCEDURE PMART.REMD_S3_DEPT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_S3_DEPT_FUNC'); 
  LOCKING PMART.REMD_FACT_SUM FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM FOR ACCESS;  
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_S3_DEPT_FUNC AS('+            
               ' SELECT '+
               ' B.DEPT_ID AS ORG_ID, '+
               'A.ACCU_CUST_NUM AS ACCU_CUST_NUM_LAST_YEAR, '+
               'A.ACCU_PLAN_STNUM AS ACCU_PLAN_STNUM_LAST_YEAR, '+
               'A.ACCU_SCUST_NUM AS ACCU_SCUST_NUM_LAST_YEAR '+ 
               'FROM PMART.REMD_FACT_SUM A INNER JOIN '+
              ' (SELECT DISTINCT DEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1) B '+
               ' ON A.ORG_ID=B.DEPT_ID '+
               ' INNER JOIN '+
               '(SELECT DISTINCT L_MONTH_END_DAY FROM PMART.YMWD_TIME '+
               'WHERE L_DAY_ID='+P_DAY_ID+') C '+
               ' ON A.L_DAY_ID=C.L_MONTH_END_DAY -10000 '+ 
           ' ) WITH DATA ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;