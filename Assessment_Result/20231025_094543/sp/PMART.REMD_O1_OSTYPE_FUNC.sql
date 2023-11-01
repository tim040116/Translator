REPLACE PROCEDURE PMART.REMD_O1_OSTYPE_FUNC(P_DAY_ID NUMBER,P_OSTYPE_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_O1_OSTYPE_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_O1_OSTYPE_FUNC AS('+ 
               'SELECT  '+
               'A.OSTORE_ID AS OSTORE_ID, '+
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
               'A.EX_ACCU_AMT, '+ 
               'A.EX_ACCU_PLAN_STNUM AS EX_ACCU_PLAN_STNUM, '+
               'A.EX_ACCU_CUST_NUM AS EX_ACCU_CUST_NUM, '+
               'A.EX_TOT_AMT_LAST_YEAR AS EX_TOT_AMT_LAST_YEAR, '+
               'A.EX_TOT_PLAN_STNUM_LAST_YEAR AS EX_TOT_PLAN_STNUM_LAST_YEAR, '+
               'A.EX_TOT_CUST_NUM_LAST_YEAR AS EX_TOT_CUST_NUM_LAST_YEAR, '+
               'A.TKSL AS TKSL, '+
               'A.TOT_TKSL AS TOT_TKSL, '+
               'A.ACCU_TKSL AS ACCU_TKSL, '+
               'A.EX_TOT_TKSL_LAST_YEAR AS EX_TOT_TKSL_LAST_YEAR, '+
               'A.EX_ACCU_TKSL AS EX_ACCU_TKSL '+
               'FROM '+
               '(SELECT * '+
               'FROM PMART.REMD_FACT WHERE L_DAY_ID= '+P_DAY_ID+') A, '+
               '(SELECT DISTINCT OSTORE_ID FROM PMART.STORE_TYPE_DIM WHERE OSTYPE_ID='+P_OSTYPE_ID+') B '+
               'WHERE A.OSTORE_ID=B.OSTORE_ID '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;