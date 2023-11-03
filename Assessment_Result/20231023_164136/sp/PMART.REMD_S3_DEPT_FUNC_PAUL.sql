REPLACE PROCEDURE PMART.REMD_S3_DEPT_FUNC_PAUL(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_S3_DEPT_FUNC_PAUL'); 
  LOCKING PMART.REMD_FACT_SUM FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM FOR ACCESS;
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_S3_DEPT_FUNC_PAUL AS('+            
               ' SELECT '+
               ' B.DEPT_ID AS ORG_ID, '+
               ' A.TOT_AMT  AS TOT_AMT_LAST_YEAR, '+
               ' A.TOT_CUST_NUM AS TOT_CUST_NUM_LAST_YEAR, '+
               ' A.TOT_TKSL AS TOT_TKSL_LAST_YEAR '+
               ' FROM PMART.REMD_FACT_SUM A, '+
               ' (SELECT DISTINCT DEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1) B '+
               ' WHERE A.ORG_ID=B.DEPT_ID AND '+
               ' A.L_DAY_ID=ROUND('+P_DAY_ID+'/100)*100+1-10000 '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;