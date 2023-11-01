REPLACE PROCEDURE PMART.REMD_T3_NCIG_OSTORE_FUNC
(P_DAY_ID NUMBER,P_RESPON_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_T3_NCIG_OSTORE_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_T3_NCIG_OSTORE_FUNC AS('+            
               'SELECT '+
               'B.OSTORE_ID AS ORG_ID, '+
               'A.TOT_AMT  AS TOT_AMT_LAST_YEAR, '+
               'A.TOT_CUST_NUM AS TOT_CUST_NUM_LAST_YEAR, '+
               'A.TOT_TKSL AS TOT_TKSL_LAST_YEAR '+
               'FROM PMART.REMD_FACT_NCIG A, '+
               '(SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM '+
               'WHERE RESPON_ID='+P_RESPON_ID+') B '+
               'WHERE A.OSTORE_ID=B.OSTORE_ID AND '+
               'A.L_DAY_ID=ROUND('+P_DAY_ID+'/100)*100+1-10000 '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;