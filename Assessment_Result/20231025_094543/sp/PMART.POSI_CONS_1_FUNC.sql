REPLACE PROCEDURE PMART.POSI_CONS_1_FUNC (P_L_DAY_ID VARCHAR(400), P_LEVEL VARCHAR(400), P_PRD_ID VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(8000); 
   DECLARE V_SELECT  VARCHAR(4000); 
   CALL PMART.P_DROP_TABLE ('#VT_POSI_CONS_1_FUNC'); 
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI_CONS_1_FUNC  '+
               '( TIME_ID            INTEGER, '+
               '  PRD_ID              VARCHAR(7), '+
               '  SALES_AMT           NUMBER, '+
               '  SALES_AMT_PSD       DECIMAL(16,6), '+
               '  FEE                 NUMBER, '+
               '  FEE_PSD             DECIMAL(16,6), '+
               '  BUDGET_FEE          NUMBER, '+
               '  BUDGET_FEE_PSD      NUMBER, '+
               '  BUDGET_FEE_RATE     NUMBER, '+
               '  BUDGET_FEE_PSD_RATE NUMBER, '+
               '  PSD_DIFF            NUMBER ) '+
               ' NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
   EXECUTE IMMEDIATE SQLSTR;
   CASE P_LEVEL 
        WHEN '0' THEN
            SET SQLSTR ='INSERT INTO #VT_POSI_CONS_1_FUNC(TIME_ID,PRD_ID,SALES_AMT,SALES_AMT_PSD,FEE,FEE_PSD,BUDGET_FEE,BUDGET_FEE_PSD,BUDGET_FEE_RATE,BUDGET_FEE_PSD_RATE,PSD_DIFF)  '+                        
                        ' SELECT  T.TIME_ID,''-1'' AS PRD_ID, '+
                        ' SUM(T.SALES_AMT) AS SALES_AMT, '+ 
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(SALES_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS SALES_AMT_PSD , '+
                        ' SUM(T.FEE) AS FEE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(T.FEE)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS FEE_PSD , '+
                        ' SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM) AS BUDGET_FEE, '+
                        ' SUM(T.BUDGET_PSD) AS BUDGET_FEE_PSD, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM))*100 END AS BUDGET_FEE_RATE, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(MAX(T.UPLOAD_STNUM)*SUM(T.BUDGET_PSD))*100 END AS BUDGET_FEE_PSD_RATE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(T.FEE)/MAX(T.UPLOAD_STNUM) - SUM(T.BUDGET_PSD) END AS PSD_DIFF '+
                        ' FROM PMART.BASIC_MFACT_BUDGET_CONS T JOIN (SELECT DISTINCT GRP_ID ,KND_ID, TOT_ID FROM PMART.ORG_DIM_POSI_CONS WHERE SEQNO=1) A ON T.PRD_ID = A.GRP_ID '+
						' JOIN PMART.PRD_DIM_POSI_CONS B ON A.KND_ID = B.KND_ID  '+
                        ' WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                        ' AND T.PRD_ID IN('+ P_PRD_ID +') '+
                        ' GROUP BY T.TIME_ID ';
        WHEN '1' THEN
            SET SQLSTR ='INSERT INTO #VT_POSI_CONS_1_FUNC(TIME_ID,PRD_ID,SALES_AMT,SALES_AMT_PSD,FEE,FEE_PSD,BUDGET_FEE,BUDGET_FEE_PSD,BUDGET_FEE_RATE,BUDGET_FEE_PSD_RATE,PSD_DIFF)  '+
                        ' SELECT   T.TIME_ID,B.PRD_ID, '+
                        ' SUM(T.SALES_AMT) AS SALES_AMT, '+ 
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(SALES_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS SALES_AMT_PSD , '+
                        ' SUM(T.FEE) AS FEE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(T.FEE)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS FEE_PSD , '+
                        ' SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM) AS BUDGET_FEE, '+
                        ' SUM(T.BUDGET_PSD) AS BUDGET_FEE_PSD, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM))*100 END AS BUDGET_FEE_RATE, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(MAX(T.UPLOAD_STNUM)*SUM(T.BUDGET_PSD))*100 END AS BUDGET_FEE_PSD_RATE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(T.FEE)/MAX(T.UPLOAD_STNUM) - SUM(T.BUDGET_PSD) END AS PSD_DIFF '+
                        ' FROM PMART.BASIC_MFACT_BUDGET_CONS T JOIN (SELECT DISTINCT GRP_ID ,KND_ID FROM PMART.ORG_DIM_POSI_CONS WHERE SEQNO=1) A ON T.PRD_ID = A.GRP_ID '+
                        ' JOIN PMART.PRD_DIM_POSI_CONS B ON A.KND_ID = B.KND_ID '+
                        ' WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                        ' AND T.PRD_ID IN('+ P_PRD_ID +') '+        
                        ' GROUP BY T.TIME_ID,B.PRD_ID ';                               
        WHEN  '2' THEN
            SET SQLSTR ='INSERT INTO #VT_POSI_CONS_1_FUNC(TIME_ID,PRD_ID,SALES_AMT,SALES_AMT_PSD,FEE,FEE_PSD,BUDGET_FEE,BUDGET_FEE_PSD,BUDGET_FEE_RATE,BUDGET_FEE_PSD_RATE,PSD_DIFF)  '+
                        ' SELECT  T.TIME_ID,A.KND_ID AS PRD_ID, '+
                        ' SUM(T.SALES_AMT) AS SALES_AMT, '+ 
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(SALES_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS SALES_AMT_PSD , '+
                        ' SUM(T.FEE) AS FEE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0 ELSE SUM(T.FEE)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) END AS FEE_PSD , '+
                        ' SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM) AS BUDGET_FEE, '+
                        ' SUM(T.BUDGET_PSD) AS BUDGET_FEE_PSD, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(SUM(T.BUDGET_PSD)*MAX(T.PLAN_STNUM))*100 END AS BUDGET_FEE_RATE, '+
                        ' CASE WHEN SUM(T.BUDGET_PSD)*MAX(T.UPLOAD_STNUM) = 0 THEN 0 ELSE '+
                        ' SUM(CAST(T.FEE AS DECIMAL(16,6)))/(MAX(T.UPLOAD_STNUM)* SUM(T.BUDGET_PSD))*100 END AS BUDGET_FEE_PSD_RATE, '+
                        ' CASE WHEN MAX(T.UPLOAD_STNUM) =0 THEN 0- SUM(T.BUDGET_PSD) ELSE SUM(T.FEE)/MAX(T.UPLOAD_STNUM) - SUM(T.BUDGET_PSD) END AS PSD_DIFF '+
                        ' FROM PMART.BASIC_MFACT_BUDGET_CONS T JOIN (SELECT DISTINCT GRP_ID ,KND_ID FROM PMART.ORG_DIM_POSI_CONS WHERE SEQNO=1) A ON T.PRD_ID = A.GRP_ID '+
                        ' WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                        ' AND T.PRD_ID IN('+ P_PRD_ID +') '+
                        ' GROUP BY T.TIME_ID,A.KND_ID ';
        WHEN  '3' THEN
            SET SQLSTR ='INSERT INTO #VT_POSI_CONS_1_FUNC(TIME_ID,PRD_ID,SALES_AMT,SALES_AMT_PSD,FEE,FEE_PSD,BUDGET_FEE,BUDGET_FEE_PSD,BUDGET_FEE_RATE,BUDGET_FEE_PSD_RATE,PSD_DIFF)  '+
                        ' SELECT  T.TIME_ID,T.PRD_ID, '+
                        ' T.SALES_AMT, '+ 
                        ' CASE WHEN T.UPLOAD_STNUM =0 THEN 0 ELSE SALES_AMT/CAST(T.UPLOAD_STNUM AS DECIMAL(16,6)) END AS SALES_AMT_PSD , '+
                        ' T.FEE , '+
                        ' CASE WHEN T.UPLOAD_STNUM =0 THEN 0 ELSE T.FEE/CAST(T.UPLOAD_STNUM AS DECIMAL(16,6)) END AS FEE_PSD , '+
                        ' T.BUDGET_PSD * T.UPLOAD_STNUM AS BUDGET_FEE, '+
                        ' T.BUDGET_PSD AS BUDGET_FEE_PSD, '+
                        ' CASE WHEN T.BUDGET_PSD *  T.PLAN_STNUM = 0 THEN 0 ELSE '+
                        ' CAST(T.FEE AS DECIMAL(16,6))/(T.BUDGET_PSD * T.PLAN_STNUM)*100 END AS BUDGET_FEE_RATE, '+
                        ' CASE WHEN T.BUDGET_PSD * T.UPLOAD_STNUM =0 THEN 0 ELSE '+
                        ' CAST(T.FEE AS DECIMAL(16,6))/(T.UPLOAD_STNUM*T.BUDGET_PSD) *100 END AS BUDGET_FEE_PSD_RATE, '+
                        ' CASE WHEN T.UPLOAD_STNUM =0 THEN 0- T.BUDGET_PSD ELSE T.FEE/T.UPLOAD_STNUM - T.BUDGET_PSD END AS PSD_DIFF '+
                        ' FROM PMART.BASIC_MFACT_BUDGET_CONS T '+
                        ' WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                        ' AND T.PRD_ID IN('+ P_PRD_ID +') ';
        END CASE ;
   DELETE FROM PMART.T1 WHERE F1 = 71;
   INSERT INTO PMART.T1(F1,F2) SELECT 71,SQLSTR;	
	EXECUTE IMMEDIATE SQLSTR;
END SP;