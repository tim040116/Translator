REPLACE PROCEDURE PMART.POSI1_5_RPT1_FUNC(P_L_DAY_ID VARCHAR(400),P_RPT VARCHAR(400),P_UNIT VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(8000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_POSI1_5_RPT1_FUNC'); 
  CALL PMART.POSI1_5_RPT1_M_FUNC(P_L_DAY_ID,P_RPT,P_UNIT);
  CALL PMART.POSI1_5_RPT1_PRE_FUNC(P_L_DAY_ID,P_RPT,P_UNIT);
  CALL PMART.POSI1_5_RPT1_L_FUNC(P_L_DAY_ID,P_RPT,P_UNIT);
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_POSI1_5_RPT1_FUNC AS('+            
                  'SELECT '+
                  'T.TIME_ID, '+
                  'T.PRD_ID, '+
                  'T.RESPON_ID, '+
                  'T.SALES_AMT_PSD, '+
                  'T.BUDGET_AMT_PSD, '+
                  'CASE WHEN T.BUDGET_AMT_PSD = 0 THEN 0 ELSE T.SALES_AMT_PSD/T.BUDGET_AMT_PSD*100 END AS PSD_RATE, '+
                  '(T.SALES_AMT_PSD-T.BUDGET_AMT_PSD) AS PSD_DIFF, '+
                  'CASE WHEN C.L_REAL_SALES_PSD = 0 THEN 0 ELSE T.SALES_AMT_PSD/C.L_REAL_SALES_PSD*100 END AS L_PSD_RATE, '+
                  'CASE WHEN B.P_REAL_SALES_PSD = 0 THEN 0 ELSE T.SALES_AMT_PSD/B.P_REAL_SALES_PSD*100 END AS P_PSD_RATE, '+
                  'T.INPRD_RATE, '+
                  'T.BUDGET_RATE, '+
                  'T.INPRD_RATE-T.BUDGET_RATE AS RATE_DIFF, '+
                  'T.INPRD_RATE-C.L_INPRD_RATE AS L_RATE, '+
                  'T.INPRD_RATE-B.P_INPRD_RATE AS P_RATE, '+
                  'T.GPR, '+
                  'T.BUDGET_GRP, '+
                  'CASE WHEN T.BUDGET_GRP = 0 THEN 0 ELSE T.GPR/T.BUDGET_GRP*100 END AS GPR_RATE, '+
                  '(T.GPR-T.BUDGET_GRP) AS GRP_DIFF, '+
                  'T.REAL_SALES_AMT, '+
                  'T.BUDGET_AMT, '+
                  'C.L_REAL_SALES_AMT, '+
                  'B.P_REAL_SALES_AMT '+
                  'FROM #VT_POSI1_5_RPT1_M_FUNC T '+
                  'LEFT JOIN #VT_POSI1_5_RPT1_PRE_FUNC B ON (T.TIME_ID=B.TIME_ID AND T.PRD_ID=B.PRD_ID AND T.RESPON_ID=B.RESPON_ID) '+
                  'LEFT JOIN #VT_POSI1_5_RPT1_L_FUNC C   ON (T.TIME_ID=C.TIME_ID AND T.PRD_ID=C.PRD_ID AND T.RESPON_ID=C.RESPON_ID) '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;