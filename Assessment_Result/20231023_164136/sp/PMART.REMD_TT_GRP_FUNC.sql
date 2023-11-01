REPLACE PROCEDURE PMART.REMD_TT_GRP_FUNC
(P_L_DAY_ID NUMBER,P_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_TT_GRP_FUNC'); 
  CALL PMART.REMD_T1_GRP_FUNC(P_L_DAY_ID,P_ORG_ID);
  CALL PMART.REMD_T2_GRP_FUNC(P_L_DAY_ID,P_ORG_ID);
  CALL PMART.REMD_T3_GRP_FUNC(P_L_DAY_ID,P_ORG_ID);
  CALL PMART.REMD_T4_GRP_FUNC();
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_TT_GRP_FUNC AS('+            
                'SELECT '+
                '-1 AS TOT_ID, '+
                'A.STYPE_ID AS STYPE_ID, '+
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
                'B.TOT_PLAN_STNUM_LAST_MONTH AS TOT_PLAN_STNUM_LAST_MONTH, '+
                'C.TOT_AMT_LAST_YEAR AS TOT_AMT_LAST_YEAR, '+
                'C.TOT_CUST_NUM_LAST_YEAR AS TOT_CUST_NUM_LAST_YEAR, '+
                'D.MAST_STORE_NUM AS MAST_STORE_NUM, '+
                'A.TKSL, A.TOT_TKSL, A.ACCU_TKSL, A.EX_TOT_TKSL_LAST_YEAR, '+
                'B.TOT_TKSL_LAST_MONTH, C.TOT_TKSL_LAST_YEAR, A.EX_ACCU_TKSL '+
                'FROM '+
                   '#VT_REMD_T1_GRP_FUNC A '+
                'LEFT JOIN '+
                   '#VT_REMD_T2_GRP_FUNC B '+
                '   ON (A.STYPE_ID=B.STYPE_ID) '+
                'LEFT JOIN '+
                    '#VT_REMD_T3_GRP_FUNC C '+
                '   ON (A.STYPE_ID=C.STYPE_ID) '+
                'LEFT JOIN '+
                   '#VT_REMD_T4_GRP_FUNC  D '+
                '   ON (A.STYPE_ID=D.STYPE_ID) '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;