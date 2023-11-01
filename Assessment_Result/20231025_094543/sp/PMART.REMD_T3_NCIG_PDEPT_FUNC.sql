REPLACE PROCEDURE PMART.REMD_T3_NCIG_PDEPT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_T3_NCIG_PDEPT_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_T3_NCIG_PDEPT_FUNC AS('+            
               ' SELECT '+
               ' B.PDEPT_ID AS ORG_ID, '+
               ' A.TOT_AMT  AS TOT_AMT_LAST_YEAR, '+
               ' A.TOT_CUST_NUM AS TOT_CUST_NUM_LAST_YEAR, '+
               ' A.TOT_TKSL AS TOT_TKSL_LAST_YEAR '+
               ' FROM PMART.REMD_FACT_SUM_NCIG A, '+
               ' (SELECT DISTINCT PDEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1) B '+
               ' WHERE A.ORG_ID=B.PDEPT_ID AND '+
               ' A.L_DAY_ID=ROUND('+P_DAY_ID+'/100)*100+1-10000 '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;