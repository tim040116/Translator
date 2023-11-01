REPLACE PROCEDURE PMART.REMD_S2_TOT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_S2_TOT_FUNC'); 
  LOCKING PMART.REMD_FACT_SUM FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM FOR ACCESS;
  LOCKING PMART.YMWD_TIME FOR ACCESS;
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_S2_TOT_FUNC AS('+            
          'SELECT '+
          '-1 AS ORG_ID, '+
          'A.TOT_AMT AS TOT_AMT_LAST_MONTH, '+
          'A.TOT_PLAN_STNUM AS TOT_PLAN_STNUM_LAST_MONTH, '+
          'A.TOT_TKSL AS TOT_TKSL_LAST_MONTH '+
          'FROM PMART.REMD_FACT_SUM A, '+
          'PMART.YMWD_TIME C '+
          'WHERE '+
          'A.ORG_ID=-1 AND '+
          'C.L_DAY_ID='+P_DAY_ID+' AND '+
          'A.L_DAY_ID=C.L_DAY_LAST_DAY_LAST_MONTH '+
          ' ) WITH DATA ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;