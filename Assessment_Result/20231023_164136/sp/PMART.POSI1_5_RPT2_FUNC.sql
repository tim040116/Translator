REPLACE PROCEDURE PMART.POSI1_5_RPT2_FUNC(P_L_DAY_ID VARCHAR(400),P_RPT VARCHAR(400),P_UNIT VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(8000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_POSI1_5_RPT2_FUNC'); 
  CALL PMART.POSI1_5_RPT1_M_FUNC(P_L_DAY_ID,P_RPT,P_UNIT);
  CALL PMART.POSI1_5_RPT1_L_FUNC(P_L_DAY_ID,P_RPT,P_UNIT);
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_POSI1_5_RPT2_FUNC AS('+            
                 ' SELECT '+
                 ' T.TIME_ID, '+
                 ' T.PRD_ID, '+
                 ' T.RESPON_ID, '+
                 ' T.REAL_SALES_AMT/1000 AS SALES_AMT_PSD, '+
                 ' T.BUDGET_AMT/1000 AS BUDGET_AMT_PSD, '+
                 ' CASE WHEN T.BUDGET_AMT = 0 THEN 0 ELSE T.REAL_SALES_AMT/T.BUDGET_AMT*100 END AS PSD_RATE, '+
                 ' T.SALES_AMT_PSD-T.BUDGET_AMT_PSD AS PSD_DIFF, '+
                 ' CASE WHEN C.L_REAL_SALES_PSD = 0 THEN 0 ELSE T.SALES_AMT_PSD/C.L_REAL_SALES_PSD*100 END AS L_PSD_RATE, '+
                 ' 0 AS P_PSD_RATE, '+
                 ' T.INPRD_RATE, '+
                 ' T.BUDGET_RATE, '+
                 ' T.INPRD_RATE-T.BUDGET_RATE AS RATE_DIFF, '+
                 ' T.INPRD_RATE-C.L_INPRD_RATE AS L_RATE, '+
                 ' 0 AS P_RATE, '+
                 ' T.GPR , '+
                 ' T.BUDGET_GRP , '+
                 ' CASE WHEN T.BUDGET_GRP = 0 THEN 0 ELSE T.GPR/T.BUDGET_GRP*100 END AS GPR_RATE , '+
                 ' T.GPR-T.BUDGET_GRP AS GRP_DIFF, '+
                 ' T.REAL_SALES_AMT, '+
                 ' T.BUDGET_AMT, '+
                 ' C.L_REAL_SALES_AMT, '+
                 ' 0 AS P_REAL_SALES_AMT'+
                 ' FROM #VT_POSI1_5_RPT1_M_FUNC T '+
                 ' LEFT JOIN #VT_POSI1_5_RPT1_L_FUNC C '+
                 ' ON (T.TIME_ID=C.TIME_ID AND T.PRD_ID=C.PRD_ID ) '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';             
    EXECUTE IMMEDIATE SQLSTR; 
END SP;