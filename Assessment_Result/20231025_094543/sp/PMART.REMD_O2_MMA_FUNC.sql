REPLACE PROCEDURE PMART.REMD_O2_MMA_FUNC
(P_DAY_ID NUMBER,P_MMA_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_O2_MMA_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_O2_MMA_FUNC AS('+            
               'SELECT '+
               'A.OSTORE_ID AS OSTORE_ID, '+
               'A.TOT_AMT AS TOT_AMT_LAST_MONTH, '+
               'A.TOT_PLAN_STNUM AS TOT_PLAN_STNUM_LAST_MONTH, '+
               'A.TOT_TKSL AS TOT_TKSL_LAST_MONTH '+
               'FROM '+
               '(SELECT   S1.* '+
               'FROM PMART.REMD_FACT S1, PMART.YMWD_TIME S2 '+
               'WHERE S2.L_DAY_ID='+P_DAY_ID+' '+
               'AND S1.L_DAY_ID=S2.L_DAY_LAST_DAY_LAST_MONTH) A, '+
               '(SELECT DISTINCT OSTORE_ID FROM PMART.STORE_TYPE_DIM WHERE MMA_ID='+P_MMA_ID+') B '+
               'WHERE A.OSTORE_ID=B.OSTORE_ID '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;