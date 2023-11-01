REPLACE PROCEDURE PMART.POSI1_1_FUNC(P_L_DAY_ID VARCHAR(400), P_LEVEL VARCHAR(1), P_PRD_ID VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(4000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   DECLARE V_L_MONTH_ID NUMBER;
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_1_FUNC'); 
   CASE P_LEVEL 
         WHEN '0' THEN   
               SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_1_FUNC  AS('+
                   'SELECT T.TIME_ID,-1 AS PRD_ID, '+
                   ' SUM(T.SALES_AMT) AS SALES_AMT, '+
                   ' SUM(T.DIS_AMT+T.SUB_AMT) AS DIS_AMT, '+
                   ' SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)) AS REAL_SALES_AMT, '+
                   ' CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                   ' SUM(T.BUDGET_AMT) AS BUDGET_AMT, '+
                   ' SUM(T.BUDGET_PSD) AS BUDGET_AMT_PSD, '+
                   ' CASE WHEN SUM(T.BUDGET_AMT) = 0 THEN 0 '+
                   ' ELSE SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT))/SUM(CAST(T.BUDGET_AMT AS DECIMAL(16,6)))*100 END AS BUDGET_SALES_RATE, '+
                   ' CASE WHEN (SUM(T.BUDGET_PSD)) > 0 THEN  (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)/(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8))))*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                   ' (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)-(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8)))) AS PSD_DIFF '+
                   ' FROM PMART.BASIC_MFACT_BUDGET T , (SELECT DISTINCT GRP_ID ,KND_ID, TOT_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1  AND GRP_NO NOT IN (SELECT KIND_CODE+GROUP_CODE FROM PDATA.PBMKGRP WHERE FMDP_CODE=''0'')  ) A , PMART.PRD_DIM_POSI1 B  '+
                   ' WHERE TIME_ID IN('+ P_L_DAY_ID +') '+
                   ' AND T.PRD_ID IN('+ P_PRD_ID +') '+
                   ' AND T.PRD_ID = A.GRP_ID '+
				   ' AND A.KND_ID = B.KND_ID '+
                   ' GROUP BY T.TIME_ID '+ 
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';  
        WHEN  '1' THEN   
               SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_1_FUNC  AS('+
                   ' SELECT T.TIME_ID,B.PRD_ID, '+
                   ' SUM(T.SALES_AMT) AS SALES_AMT, '+
                   ' SUM(T.DIS_AMT+T.SUB_AMT) AS DIS_AMT, '+
                   ' SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)) AS REAL_SALES_AMT, '+
                   ' CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM  AS DECIMAL(16,6))) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                   ' SUM(T.BUDGET_AMT) AS BUDGET_AMT, '+
                   ' SUM(T.BUDGET_PSD) AS BUDGET_AMT_PSD, '+
                   ' CASE WHEN SUM(T.BUDGET_AMT) = 0 THEN 0 '+
                   ' ELSE SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT))/SUM(CAST(T.BUDGET_AMT AS DECIMAL(16,6)))*100 END AS BUDGET_SALES_RATE, '+
                   ' CASE WHEN (SUM(T.BUDGET_PSD)) > 0 THEN  (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)/(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8))))*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                   ' (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)-(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8)))) AS PSD_DIFF '+
                   ' FROM PMART.BASIC_MFACT_BUDGET T , (SELECT DISTINCT GRP_ID ,KND_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1) A , PMART.PRD_DIM_POSI1 B '+
                   ' WHERE TIME_ID IN('+ P_L_DAY_ID +') '+
                   ' AND T.PRD_ID IN('+ P_PRD_ID +') '+
                   ' AND T.PRD_ID = A.GRP_ID '+
                   ' AND A.KND_ID = B.KND_ID '+
                   ' GROUP BY T.TIME_ID,B.PRD_ID '+                
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
        WHEN  '2' THEN   
               SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_1_FUNC  AS('+
                   ' SELECT T.TIME_ID,A.KND_ID AS PRD_ID, '+
                   ' SUM(T.SALES_AMT) AS SALES_AMT, '+
                   ' SUM(T.DIS_AMT+T.SUB_AMT) AS DIS_AMT, '+
                   ' SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)) AS REAL_SALES_AMT, '+
                   ' CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM  AS DECIMAL(16,6))) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                   ' SUM(T.BUDGET_AMT) AS BUDGET_AMT, '+
                   ' SUM(T.BUDGET_PSD) AS BUDGET_AMT_PSD, '+
                   ' CASE WHEN SUM(T.BUDGET_AMT) = 0 THEN 0 '+
                   ' ELSE SUM((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT))/SUM(CAST(T.BUDGET_AMT AS DECIMAL(16,6)))*100 END AS BUDGET_SALES_RATE, '+
                   ' CASE WHEN (SUM(T.BUDGET_PSD)) > 0 THEN  (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)/(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8))))*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                   ' (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)-(SUM(CAST(T.BUDGET_PSD AS DECIMAL(18,8)))) AS PSD_DIFF '+
                   ' FROM PMART.BASIC_MFACT_BUDGET T , (SELECT DISTINCT GRP_ID ,KND_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1) A '+
                   ' WHERE TIME_ID IN('+ P_L_DAY_ID +') '+
                   ' AND T.PRD_ID IN('+ P_PRD_ID +') '+
                   ' AND T.PRD_ID = A.GRP_ID '+
                   ' GROUP BY T.TIME_ID,A.KND_ID '+                 
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
        WHEN  '3'    THEN    
               SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_1_FUNC  AS('+
                   ' SELECT T.TIME_ID,T.PRD_ID, '+
                   ' T.SALES_AMT AS SALES_AMT, '+
                   ' (T.DIS_AMT+T.SUB_AMT) AS DIS_AMT, '+
                   ' (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT) AS REAL_SALES_AMT, '+
                   ' CASE WHEN T.UPLOAD_STNUM > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.UPLOAD_STNUM  AS DECIMAL(16,6)) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                   ' T.BUDGET_AMT AS BUDGET_AMT, '+
                   ' T.BUDGET_PSD AS BUDGET_AMT_PSD, '+
                   ' CASE WHEN T.BUDGET_AMT = 0 THEN 0 '+
                   ' ELSE (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.BUDGET_AMT AS DECIMAL(16,6))*100 END AS BUDGET_SALES_RATE, '+
                   ' CASE WHEN (T.BUDGET_PSD) > 0 THEN  (CASE WHEN T.UPLOAD_STNUM > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.UPLOAD_STNUM AS DECIMAL(18,8)) ELSE 0 END)/(CAST(T.BUDGET_PSD AS DECIMAL(18,8)))*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                   ' (CASE WHEN T.UPLOAD_STNUM > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.UPLOAD_STNUM AS DECIMAL(18,8)) ELSE 0 END)-(CAST(T.BUDGET_PSD AS DECIMAL(18,8))) AS PSD_DIFF '+
                   ' FROM PMART.BASIC_MFACT_BUDGET T '+
                   ' WHERE TIME_ID IN('+ P_L_DAY_ID +') '+
                   ' AND PRD_ID IN('+ P_PRD_ID +') '+                 
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
        END CASE ;      
	EXECUTE IMMEDIATE SQLSTR;
END SP;