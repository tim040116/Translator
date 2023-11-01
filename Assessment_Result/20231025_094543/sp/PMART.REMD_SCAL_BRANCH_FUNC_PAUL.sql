REPLACE PROCEDURE PMART.REMD_SCAL_BRANCH_FUNC_PAUL(P_DAY_ID NUMBER,P_DEPT_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_SCAL_BRANCH_FUNC_PAUL'); 
 CALL PMART.REMD_ST_BRANCH_FUNC_PAUL(P_DAY_ID,P_DEPT_ID); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_SCAL_BRANCH_FUNC_PAUL AS('+            
			   'SELECT '+
               'TOT_ID, '+
               'ORG_ID, '+
               'MAST_STORE_NUM AS R1, '+
               'AMT/DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R2, '+
               'CUST_NUM/DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R3, '+
               'AMT/DECODE(CUST_NUM,0,NULL,CUST_NUM) AS R4, '+
               '(ACCU_AMT/DECODE(ACCU_PLAN_STNUM,0,NULL,ACCU_PLAN_STNUM)) AS R5, '+
               'ACCU_AMT*0.9561/ '+
               'DECODE(ACCU_BUDGET,0,NULL,ACCU_BUDGET)*100 AS R6, '+
               '(REMD_ADD_TKSL('+P_DAY_ID+',''Y'',EX_ACCU_AMT,EX_ACCU_TKSL)/DECODE(EX_ACCU_PLAN_STNUM,0,NULL,EX_ACCU_PLAN_STNUM))/ '+
               '   (REMD_ADD_TKSL('+P_DAY_ID+',''Y'',EX_TOT_AMT_LAST_YEAR,EX_TOT_TKSL_LAST_YEAR)/ '+
               '   DECODE(EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
               'AS R7, '+
               '(REMD_ADD_TKSL('+P_DAY_ID+',''M'',ACCU_AMT,ACCU_TKSL)/DECODE(ACCU_PLAN_STNUM,0,NULL,ACCU_PLAN_STNUM))/ '+
               '   DECODE(REMD_ADD_TKSL('+P_DAY_ID+',''M'',TOT_AMT_LAST_MONTH,TOT_TKSL_LAST_MONTH)/ '+
               '   DECODE(TOT_PLAN_STNUM_LAST_MONTH,0,NULL,TOT_PLAN_STNUM_LAST_MONTH), '+
               '   0,NULL, REMD_ADD_TKSL('+P_DAY_ID+',''M'',TOT_AMT_LAST_MONTH,TOT_TKSL_LAST_MONTH)/ '+
               '   DECODE(TOT_PLAN_STNUM_LAST_MONTH,0,NULL,TOT_PLAN_STNUM_LAST_MONTH))*100 '+
               'AS R8, '+
               '(ACCU_CUST_NUM/DECODE(ACCU_PLAN_STNUM,0,NULL,ACCU_PLAN_STNUM)) AS R9, '+
               '(EX_ACCU_CUST_NUM/DECODE(EX_ACCU_PLAN_STNUM,0,NULL,EX_ACCU_PLAN_STNUM))/ '+
               'DECODE( '+
               'EX_TOT_CUST_NUM_LAST_YEAR/ '+
               'DECODE(EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR) '+
               ',0,NULL, '+
               'EX_TOT_CUST_NUM_LAST_YEAR/ '+
               'DECODE(EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR) '+
               ')*100 AS R10, '+
               '(ACCU_AMT/DECODE(ACCU_CUST_NUM,0,NULL,ACCU_CUST_NUM)) AS R11, '+
               '(REMD_ADD_TKSL('+P_DAY_ID+',''Y'',ACCU_AMT,ACCU_TKSL)/DECODE(ACCU_CUST_NUM,0,NULL,ACCU_CUST_NUM))- '+
               '   (REMD_ADD_TKSL('+P_DAY_ID+',''Y'',EX_TOT_AMT_LAST_YEAR,EX_TOT_TKSL_LAST_YEAR)/ '+
               '   DECODE(EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,EX_TOT_CUST_NUM_LAST_YEAR)) '+
               'AS R12, '+
               '(ACCU_AMT*0.9561/DECODE(BUDGET_ST_TOT_AMT,0,NULL,BUDGET_ST_TOT_AMT))*100 AS R13 '+
               'FROM #VT_REMD_ST_BRANCH_FUNC_PAUL T1 '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;