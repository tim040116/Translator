REPLACE PROCEDURE PMART.POSI1_6_FUNC (P_L_DAY_ID VARCHAR(400), P_LEVEL VARCHAR(2), P_PRD_ID VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(8000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_6_FUNC'); 
   CALL PMART.P_DROP_TABLE ('#VT_POSI_FUNC');
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_6_FUNC_P'); 
   CALL PMART.POSI_FUNC(P_L_DAY_ID ,P_LEVEL,P_PRD_ID);   
   CALL PMART.POSI_SHORT_FUNC('MONTH_PRE',P_L_DAY_ID,P_LEVEL,P_PRD_ID);
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_6_FUNC_P  AS('+                   
                  ' SELECT '+
                  ' M.TIME_ID, '+
                  ' M.PRD_ID, '+
                  ' M.SALES_AMT, '+
                  ' M.SALES_AMT2, '+
                  ' M.DIS_AMT, '+
                  ' M.DIS_AMT2, '+
                  ' M.REAL_SALES_AMT, '+
                  ' M.REAL_SALES_AMT2, '+
                  ' M.REAL_SALES_AMT_PSD, '+
                  ' M.REAL_SALES_AMT_PSD2, '+
                  ' M.BUDGET_AMT, '+
                  ' M.BUDGET_AMT2, '+
                  ' M.BUDGET_AMT_PSD, '+
                  ' M.BUDGET_AMT_PSD2, '+
                  ' M.BUDGET_SALES_RATE, '+
                  ' M.BUDGET_SALES_PSD_RATE, '+
                  ' M.PSD_DIFF, '+
                  ' M.SALES_COST, '+
                  ' M.GP_SALES_AMT, '+
                  ' M.GP_SALES_AMT2, '+
                  ' M.GP_SALES_AMT_PSD, '+
                  ' M.GP_SALES_AMT_PSD2, '+
                  ' M.GP_BUDGET_AMT, '+
                  ' M.GP_BUDGET_AMT2, '+
                  ' M.GP_BUDGET_AMT_PSD, '+
                  ' M.GP_BUDGET_AMT_PSD2, '+
                  ' M.GP_RATE, '+
                  ' M.GP_PSD_RATE, '+
                  ' (M.GP_SALES_AMT-M.GP_BUDGET_AMT) AS GP_BUDGET_AMT_DIFF, '+
                  ' (M.GP_SALES_AMT2-M.GP_BUDGET_AMT2) AS GP_BUDGET_AMT_DIFF2, '+
                  ' M.BUDGET_AMT AS BUDGET_AMT_EDGE, '+
                  ' M.REAL_SALES_AMT AS REAL_SALES_AMT_EDGE, '+
                  ' 0 AS BUDGET_AMT_DIFF, '+
                  ' M.BUDGET_GPR, '+
                  ' M.SALES_GPR, '+
                  ' M.CS_DIFF, '+
                  ' M.SL2_DIFF, '+
                  ' M.INPRD_GPR, '+
                  ' M.GPR_DIFF, '+
                  ' P.REAL_SALES_AMT AS P_REAL_SALES_AMT, '+
                  ' P.REAL_SALES_AMT_PSD AS P_REAL_SALES_AMT_PSD, '+
                  ' P.INPRD_GPR AS P_INPRD_GPR '+                
                  ' FROM #VT_POSI_FUNC M '+
                  ' LEFT JOIN #VT_POSI_SHORT_FUNC P ON (M.TIME_ID=P.TIME_ID AND M.PRD_ID=P.PRD_ID) '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';  
	EXECUTE IMMEDIATE SQLSTR;   
   CALL PMART.P_DROP_TABLE ('#VT_POSI_SHORT_FUNC');
   CALL PMART.POSI_SHORT_FUNC('MONTH_L',P_L_DAY_ID,P_LEVEL,P_PRD_ID);
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_6_FUNC  AS('+
                  ' SELECT '+
                  ' S.TIME_ID, '+
                  ' S.PRD_ID, '+
                  ' S.SALES_AMT, '+
                  ' S.SALES_AMT2, '+
                  ' S.DIS_AMT, '+
                  ' S.DIS_AMT2, '+
                  ' S.REAL_SALES_AMT, '+
                  ' S.REAL_SALES_AMT2, '+
                  ' S.REAL_SALES_AMT_PSD, '+
                  ' S.REAL_SALES_AMT_PSD2, '+
                  ' S.BUDGET_AMT, '+
                  ' S.BUDGET_AMT2, '+
                  ' S.BUDGET_AMT_PSD, '+
                  ' S.BUDGET_AMT_PSD2, '+
                  ' S.BUDGET_SALES_RATE, '+
                  ' S.BUDGET_SALES_PSD_RATE, '+
                  ' S.PSD_DIFF, '+
                  ' CASE WHEN NVL(L.REAL_SALES_AMT_PSD,0)   = 0 THEN 0 ELSE S.REAL_SALES_AMT_PSD/L.REAL_SALES_AMT_PSD*100   END AS L_YEAR_PSD_DIFF, '+
                  ' CASE WHEN NVL(S.P_REAL_SALES_AMT_PSD,0) = 0 THEN 0 ELSE S.REAL_SALES_AMT_PSD/S.P_REAL_SALES_AMT_PSD*100 END AS L_MONTH_PSD_DIFF, '+
                  ' S.SALES_COST, '+
                  ' S.GP_SALES_AMT, '+
                  ' S.GP_SALES_AMT2, '+
                  ' S.GP_SALES_AMT_PSD, '+
                  ' S.GP_SALES_AMT_PSD2, '+
                  ' S.GP_BUDGET_AMT, '+
                  ' S.GP_BUDGET_AMT2, '+
                  ' S.GP_BUDGET_AMT_PSD, '+
                  ' S.GP_BUDGET_AMT_PSD2, '+
                  ' S.GP_RATE, '+
                  ' S.GP_PSD_RATE, '+
                  ' (S.GP_SALES_AMT-S.GP_BUDGET_AMT) AS GP_BUDGET_AMT_DIFF, '+
                  ' (S.GP_SALES_AMT2-S.GP_BUDGET_AMT2) AS GP_BUDGET_AMT_DIFF2, '+
                  ' S.BUDGET_AMT AS BUDGET_AMT_EDGE, '+
                  ' S.REAL_SALES_AMT AS REAL_SALES_AMT_EDGE, '+
                  ' 0 AS BUDGET_AMT_DIFF, '+
                  ' L.REAL_SALES_AMT AS L_YEAR_DIFF, '+
                  ' S.P_REAL_SALES_AMT AS L_MONTH_DIFF, '+
                  ' S.BUDGET_GPR, '+
                  ' S.SALES_GPR AS MONTH_SALES_GPR, '+
                  ' S.CS_DIFF, '+
                  ' S.SL2_DIFF, '+
                  ' S.INPRD_GPR AS MONTH_INPRD_GPR, '+
                  ' S.GPR_DIFF, '+
                  ' (S.INPRD_GPR-L.INPRD_GPR  ) AS GPR_L_YEAR_DIFF, '+
                  ' (S.INPRD_GPR-S.P_INPRD_GPR) AS GPR_L_MONTH_DIFF '+
                  ' FROM #VT_POSI1_6_FUNC_P S '+
                  ' LEFT JOIN #VT_POSI_SHORT_FUNC L ON (S.TIME_ID=L.TIME_ID AND S.PRD_ID=L.PRD_ID) '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';    
	EXECUTE IMMEDIATE SQLSTR;
END SP;