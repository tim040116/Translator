REPLACE PROCEDURE PMART.REMD_S1_PDEPT_FUNC(P_DAY_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_S1_PDEPT_FUNC');   
  LOCKING PMART.REMD_FACT_SUM FOR ACCESS;
  LOCKING PMART.LAST_ORG_DIM  FOR ACCESS;      
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_S1_PDEPT_FUNC AS('+
          'SELECT '+
          'B.PDEPT_ID AS ORG_ID, '+
          'A.AMT AS AMT, '+
          'A.UPLOAD_STNUM AS UPLOAD_STNUM, '+
          'A.CUST_NUM AS CUST_NUM, '+
          'A.ACCU_AMT AS ACCU_AMT, '+
          'A.ACCU_PLAN_STNUM AS ACCU_PLAN_STNUM, '+
          'A.ACCU_CUST_NUM AS ACCU_CUST_NUM, '+
          'A.BUDGET_ST_AVG_AMT AS BUDGET_ST_AVG_AMT, '+
          'A.BUDGET_ST_TOT_AMT AS BUDGET_ST_TOT_AMT, '+
          'A.BUDGET_DEPT_AVG_AMT AS BUDGET_DEPT_AVG_AMT, '+
          'A.BUDGET_DEPT_TOT_AMT AS BUDGET_DEPT_TOT_AMT, '+
          'A.ACCU_BUDGET AS ACCU_BUDGET, '+
          'A.EX_ACCU_AMT AS EX_ACCU_AMT, '+
          'A.EX_ACCU_PLAN_STNUM AS EX_ACCU_PLAN_STNUM, '+
          'A.EX_ACCU_CUST_NUM AS EX_ACCU_CUST_NUM, '+
          'A.EX_TOT_AMT_LAST_YEAR AS EX_TOT_AMT_LAST_YEAR, '+
          'A.EX_TOT_PLAN_STNUM_LAST_YEAR AS EX_TOT_PLAN_STNUM_LAST_YEAR, '+
          'A.EX_TOT_CUST_NUM_LAST_YEAR AS EX_TOT_CUST_NUM_LAST_YEAR, '+
          'A.TKSL AS TKSL, '+
          'A.TOT_TKSL AS TOT_TKSL, '+
          'A.ACCU_TKSL AS ACCU_TKSL, '+
          'A.EX_TOT_TKSL_LAST_YEAR AS EX_TOT_TKSL_LAST_YEAR, '+
          'A.EX_ACCU_TKSL AS EX_ACCU_TKSL, '+
          'A.SCUST_NUM AS SCUST_NUM, '+
          'A.ACCU_SCUST_NUM AS ACCU_SCUST_NUM, '+
          'A.EX_ACCU_SCUST_NUM AS EX_ACCU_SCUST_NUM, '+
          'A.EX_TOT_SCUST_NUM_LAST_YEAR AS EX_TOT_SCUST_NUM_LAST_YEAR, '+
          'A.TOT_PLAN_STNUM AS TOT_PLAN_STNUM, '+
          'A.BUDGET_ST_TOT_PLAN_STNUM AS BUDGET_ST_TOT_PLAN_STNUM,  '+
		  'A.BUDGET_DEPT_TOT_PLAN_STNUM AS BUDGET_DEPT_TOT_PLAN_STNUM,  '+
          'A.BUDGET_PDEPT_AVG_AMT AS BUDGET_PDEPT_AVG_AMT, '+
          'A.BUDGET_PDEPT_TOT_AMT AS BUDGET_PDEPT_TOT_AMT, '+		  
          'A.BUDGET_PDEPT_TOT_PLAN_STNUM AS BUDGET_PDEPT_TOT_PLAN_STNUM  '+
          'FROM PMART.REMD_FACT_SUM A '+
          ' INNER JOIN  '+
          '(SELECT DISTINCT PDEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1) B '+
          ' ON A.ORG_ID=B.PDEPT_ID  '+
          'WHERE A.L_DAY_ID='+P_DAY_ID+
          ' ) WITH DATA ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;