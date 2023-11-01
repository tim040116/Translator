REPLACE PROCEDURE PMART.REMD_T2_NCIG_PDEPT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_T2_NCIG_PDEPT_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_T2_NCIG_PDEPT_FUNC AS('+            
               'SELECT '+
               'B.PDEPT_ID AS ORG_ID, '+
               'A.TOT_AMT AS TOT_AMT_LAST_MONTH, '+
               'A.TOT_PLAN_STNUM AS TOT_PLAN_STNUM_LAST_MONTH, '+
               'A.TOT_TKSL AS TOT_TKSL_LAST_MONTH '+
               'FROM PMART.REMD_FACT_SUM_NCIG A, '+
               '(SELECT DISTINCT PDEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1) B, '+
               'PMART.YMWD_TIME C '+
               'WHERE '+
               'A.ORG_ID=B.PDEPT_ID AND '+
               'C.L_DAY_ID='+P_DAY_ID+' AND '+
               'A.L_DAY_ID=C.L_DAY_LAST_DAY_LAST_MONTH '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;