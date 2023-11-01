REPLACE PROCEDURE PMART.REMD_OCAL_SAGE_FUNC(P_DAY_ID NUMBER,P_SAGE_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(10000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_REMD_OCAL_SAGE_FUNC'); 
  CALL PMART.REMD_OT_SAGE_FUNC(P_DAY_ID,P_SAGE_ID);
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_REMD_OCAL_SAGE_FUNC AS('+            
                'SELECT '+
                'TOT_ID, '+
                'OSTORE_ID, '+
                'MAST_STORE_NUM AS R1, '+
                'AMT/DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R2, '+
                'CUST_NUM/DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R3, '+
                'AMT/DECODE(CUST_NUM,0,NULL,CUST_NUM) AS R4, '+
                '(T1.ACCU_AMT/DECODE(ACCU_PLAN_STNUM,0,NULL,ACCU_PLAN_STNUM)) AS R5, '+
                'T1.ACCU_AMT*0.9561/ '+
                'DECODE(ACCU_BUDGET,0,NULL,ACCU_BUDGET)*100 AS R6, '+
				' CASE WHEN  (REMD_ADD_TKSL('+P_DAY_ID+',''Y'',CAST(EX_TOT_AMT_LAST_YEAR AS NUMBER),CAST(T1.EX_TOT_TKSL_LAST_YEAR AS NUMBER))/ DECODE(T1.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR))=0    THEN 0 ELSE 
				((REMD_ADD_TKSL('+P_DAY_ID+',''Y'',CAST(T1.EX_ACCU_AMT AS NUMBER),CAST(T1.EX_ACCU_TKSL AS NUMBER))+0)/DECODE(T1.EX_ACCU_PLAN_STNUM,0,NULL,T1.EX_ACCU_PLAN_STNUM))/ '+
                '   (  REMD_ADD_TKSL('+P_DAY_ID+',''Y'',CAST(EX_TOT_AMT_LAST_YEAR AS NUMBER),CAST(T1.EX_TOT_TKSL_LAST_YEAR AS NUMBER))/ '+
                '   DECODE(T1.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR)  )*100 END '+
                ' AS R7, '+
                '((REMD_ADD_TKSL('+P_DAY_ID+',''M'',CAST(T1.ACCU_AMT AS NUMBER),CAST(ACCU_TKSL AS NUMBER))+0)/DECODE(T1.ACCU_PLAN_STNUM,0,NULL,T1.ACCU_PLAN_STNUM))/ '+
                '   DECODE(REMD_ADD_TKSL('+P_DAY_ID+',''M'',CAST(T1.TOT_AMT_LAST_MONTH AS NUMBER),CAST(TOT_TKSL_LAST_MONTH AS NUMBER))/ '+
                '   DECODE(T1.TOT_PLAN_STNUM_LAST_MONTH,0,NULL,T1.TOT_PLAN_STNUM_LAST_MONTH), '+
                '   0,NULL, REMD_ADD_TKSL('+P_DAY_ID+',''M'',CAST(T1.TOT_AMT_LAST_MONTH AS NUMBER),CAST(T1.TOT_TKSL_LAST_MONTH AS NUMBER))/ '+
                '   DECODE(T1.TOT_PLAN_STNUM_LAST_MONTH,0,NULL,T1.TOT_PLAN_STNUM_LAST_MONTH))*100 '+
                'AS R8, '+
                '(ACCU_CUST_NUM/DECODE(ACCU_PLAN_STNUM,0,NULL,ACCU_PLAN_STNUM)) AS R9, '+
                '((T1.EX_ACCU_CUST_NUM+0)/DECODE(T1.EX_ACCU_PLAN_STNUM,0,NULL,T1.EX_ACCU_PLAN_STNUM))/ '+
                'DECODE( '+
                'EX_TOT_CUST_NUM_LAST_YEAR/ '+
                'DECODE(T1.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR) '+
                ',0,NULL, '+
                'EX_TOT_CUST_NUM_LAST_YEAR/ '+
                'DECODE(T1.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                'AS R10, '+
                '(T1.ACCU_AMT/DECODE(ACCU_CUST_NUM,0,NULL,ACCU_CUST_NUM)) AS R11, '+
                '(REMD_ADD_TKSL('+P_DAY_ID+',''Y'',CAST(T1.ACCU_AMT AS NUMBER),CAST(ACCU_TKSL AS NUMBER))/DECODE(T1.ACCU_CUST_NUM,0,NULL,T1.ACCU_CUST_NUM))- '+
                '   (REMD_ADD_TKSL('+P_DAY_ID+',''Y'',CAST(EX_TOT_AMT_LAST_YEAR AS NUMBER),CAST(EX_TOT_TKSL_LAST_YEAR AS NUMBER))/ '+
                '   DECODE(EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,EX_TOT_CUST_NUM_LAST_YEAR)) '+
                'AS R12, '+
                '(T1.ACCU_AMT*0.9561/DECODE(BUDGET_ST_TOT_AMT,0,NULL,BUDGET_ST_TOT_AMT))*100 AS R13 '+
                'FROM #VT_REMD_OT_SAGE_FUNC T1 '+
         ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;