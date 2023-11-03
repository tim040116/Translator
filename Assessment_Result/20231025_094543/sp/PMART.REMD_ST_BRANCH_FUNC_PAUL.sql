REPLACE PROCEDURE PMART.REMD_ST_BRANCH_FUNC_PAUL(P_DAY_ID NUMBER,P_DEPT_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_ST_BRANCH_FUNC_PAUL'); 
  CALL PMART.REMD_S1_BRANCH_FUNC_PAUL(P_DAY_ID,P_DEPT_ID);
  CALL PMART.REMD_S2_BRANCH_FUNC_PAUL(P_DAY_ID,P_DEPT_ID);
  CALL PMART.REMD_S3_BRANCH_FUNC_PAUL(P_DAY_ID,P_DEPT_ID);
  CALL PMART.REMD_S4_BRANCH_FUNC_PAUL(P_DAY_ID,P_DEPT_ID);
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_ST_BRANCH_FUNC_PAUL AS('+            
                 'SELECT '+
                 '-1 AS TOT_ID, '+
                 'A.ORG_ID AS ORG_ID, '+
                 'CAST(A.AMT AS NUMBER) AS AMT, '+
                 'A.UPLOAD_STNUM AS UPLOAD_STNUM, '+
                 'CAST(A.CUST_NUM AS NUMBER) AS CUST_NUM, '+
                 'CAST(A.ACCU_AMT AS NUMBER) AS ACCU_AMT, '+
                 'CAST(A.ACCU_PLAN_STNUM AS NUMBER) AS ACCU_PLAN_STNUM, '+  
                 'CAST(A.ACCU_CUST_NUM AS NUMBER) AS ACCU_CUST_NUM, '+
                 'A.BUDGET_ST_AVG_AMT AS BUDGET_ST_AVG_AMT, '+
                 'A.BUDGET_ST_TOT_AMT AS BUDGET_ST_TOT_AMT, '+
                 'CAST(A.BUDGET_DEPT_AVG_AMT AS NUMBER) AS BUDGET_DEPT_AVG_AMT, '+                 
                 'A.BUDGET_DEPT_TOT_AMT AS BUDGET_DEPT_TOT_AMT, '+
                 'A.ACCU_BUDGET AS ACCU_BUDGET, '+
                 'A.EX_ACCU_AMT, '+ 
                 'CAST(A.EX_ACCU_PLAN_STNUM AS NUMBER) AS EX_ACCU_PLAN_STNUM, '+                
                 'CAST(A.EX_ACCU_CUST_NUM AS NUMBER) AS EX_ACCU_CUST_NUM, '+
                 'A.EX_TOT_AMT_LAST_YEAR AS EX_TOT_AMT_LAST_YEAR, '+                 
                 'CAST(A.EX_TOT_PLAN_STNUM_LAST_YEAR AS NUMBER) AS EX_TOT_PLAN_STNUM_LAST_YEAR, '+
                 'CAST(A.EX_TOT_CUST_NUM_LAST_YEAR AS NUMBER) AS EX_TOT_CUST_NUM_LAST_YEAR, '+
                 'B.TOT_AMT_LAST_MONTH AS TOT_AMT_LAST_MONTH, '+
                 'B.TOT_PLAN_STNUM_LAST_MONTH, '+ 
                 'C.TOT_AMT_LAST_YEAR AS TOT_AMT_LAST_YEAR, '+
                 'C.TOT_CUST_NUM_LAST_YEAR AS TOT_CUST_NUM_LAST_YEAR, '+
                 'D.MAST_STORE_NUM AS MAST_STORE_NUM, '+
                 'A.TKSL, A.TOT_TKSL, A.ACCU_TKSL, A.EX_TOT_TKSL_LAST_YEAR, '+
                 'B.TOT_TKSL_LAST_MONTH, C.TOT_TKSL_LAST_YEAR, A.EX_ACCU_TKSL '+
                 'FROM '+
                 '#VT_REMD_S1_BRANCH_FUNC_PAUL A '+
                 ' LEFT JOIN '+
                 '#VT_REMD_S2_BRANCH_FUNC_PAUL B '+
                 '   ON (A.ORG_ID=B.ORG_ID) '+
                 ' LEFT JOIN '+
                 '#VT_REMD_S3_BRANCH_FUNC_PAUL C '+
                 '   ON (A.ORG_ID=C.ORG_ID) '+
                 ' LEFT JOIN '+
                 '#VT_REMD_S4_BRANCH_FUNC_PAUL D '+
                 '   ON (A.ORG_ID=D.ORG_ID) '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;