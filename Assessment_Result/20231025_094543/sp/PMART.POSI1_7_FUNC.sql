REPLACE PROCEDURE PMART.POSI1_7_FUNC (P_L_DAY_ID VARCHAR(400), P_LEVEL VARCHAR(2), P_PRD_ID VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(4000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_7_FUNC'); 
   CALL PMART.P_DROP_TABLE ('#VT_POSI_SHORT_FUNC');
   CALL PMART.P_DROP_TABLE ('#VT_POSI_FUNC');
   CALL PMART.POSI_FUNC(P_L_DAY_ID ,P_LEVEL,P_PRD_ID);   
   CALL PMART.POSI_SHORT_FUNC('YEAR_L',P_L_DAY_ID ,P_LEVEL,P_PRD_ID);
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_7_FUNC  AS('+
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
                    ' M.BUDGET_SALES_RATE , '+
                    ' M.BUDGET_SALES_PSD_RATE , '+
                    ' M.REAL_SALES_AMT-M.BUDGET_AMT AS BUDGET_SALES_DIFF, '+
                    ' M.PSD_DIFF, '+
                    ' CASE WHEN L.REAL_SALES_AMT_PSD = 0 THEN 0 ELSE M.REAL_SALES_AMT_PSD/L.REAL_SALES_AMT_PSD*100 END  AS L_YEAR_PSD_DIFF, '+
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
                    ' L.REAL_SALES_AMT AS L_YEAR_DIFF, '+    
                    ' M.BUDGET_GPR AS BUDGET_GPR, '+
                    ' M.SALES_GPR AS YEAR_SALES_GPR, '+
                    ' M.CS_DIFF, '+
                    ' M.SL2_DIFF, '+
                    ' M.INPRD_GPR AS YEAR_INPRD_GPR, '+
                    ' M.GPR_DIFF , '+
                    ' (M.INPRD_GPR-L.INPRD_GPR) AS GPR_L_YEAR_DIFF'+
                    ' FROM #VT_POSI_FUNC M '+
                    ' LEFT JOIN #VT_POSI_SHORT_FUNC L ON (M.TIME_ID=L.TIME_ID AND M.PRD_ID=L.PRD_ID) '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';  
	EXECUTE IMMEDIATE SQLSTR;
END SP;