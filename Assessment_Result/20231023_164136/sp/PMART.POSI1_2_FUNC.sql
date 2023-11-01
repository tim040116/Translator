REPLACE PROCEDURE PMART.POSI1_2_FUNC(P_L_DAY_ID VARCHAR(400),P_DAY_TYPE VARCHAR(1), P_LEVEL VARCHAR(1), P_PRD_ID VARCHAR(400) )
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(8000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   DECLARE LDAY NUMBER;
   DECLARE V_SELECT VARCHAR(2000); 
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_2_FUNC');    
   SET V_SELECT  =' SUM(T.SALES_AMT) AS SALES_AMT, '+
                  ' SUM(T.DIS_AMT+T.SUB_AMT) AS DIS_AMT, '+
                  ' SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT) AS REAL_SALES_AMT, '+
                  ' CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                  ' SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) AS LSALES_AMT, '+
                  ' CASE WHEN MAX(TL.UPLOAD_STNUM) > 0 THEN  SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)/MAX(CAST(TL.UPLOAD_STNUM AS DECIMAL(16,6))) ELSE 0 END AS LSALES_AMT_PSD, '+
		  ' SUM(T.BUDGET_AMT) AS BUDGET_AMT, '+
                  ' SUM(T.BUDGET_PSD) AS BUDGET_AMT_PSD, '+
                  ' CASE WHEN SUM(T.BUDGET_AMT) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/SUM(CAST(T.BUDGET_AMT AS DECIMAL(16,6)))*100 ELSE 0 END AS BUDGET_SALES_RATE, '+
                  ' CASE WHEN (SUM(T.BUDGET_PSD)) > 0 THEN  (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN  SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(T.UPLOAD_STNUM) ELSE 0 END)/(SUM(CAST(T.BUDGET_PSD AS DECIMAL(16,6))))*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                  ' (CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(18,8))) ELSE 0 END)-(CASE WHEN MAX(T.UPLOAD_STNUM) > 0 THEN SUM(T.BUDGET_PSD) ELSE 0 END) AS PSD_DIFF, '+
                  ' CASE WHEN SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) = 0 OR SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) IS NULL OR SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)=0 THEN 0 ELSE SUM(CAST(T.SALES_AMT AS DECIMAL(16,6))-T.DIS_AMT-T.SUB_AMT)/SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)*100 END AS LSALES_RATE, '+
                  ' CASE WHEN SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) = 0 OR MAX(T.UPLOAD_STNUM)=0 OR MAX(TL.UPLOAD_STNUM)=0 OR SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) IS NULL OR SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)=0 THEN 0 ELSE (SUM(T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/MAX(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))))/(SUM(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)/MAX(CAST(TL.UPLOAD_STNUM AS DECIMAL(16,6))))*100 END AS L_YEAR_PSD_DIFF ';
   IF (P_DAY_TYPE='D') THEN  
        SET LDAY =10000;
   ELSEIF (P_DAY_TYPE='W') THEN 
        SET LDAY =100;
   ELSEIF (P_DAY_TYPE='M') THEN 
        SET LDAY =100;
   ELSE 
         SET LDAY =0;
   END IF;  
   CASE P_LEVEL
         WHEN '6' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     '  SELECT T.TIME_ID,T.PRD_ID, '+
                     '  T.SALES_AMT AS SALES_AMT, '+
                     '  T.DIS_AMT+T.SUB_AMT AS DIS_AMT, '+
                     '  T.SALES_AMT-T.DIS_AMT-T.SUB_AMT AS REAL_SALES_AMT, '+
                     '  CASE WHEN T.UPLOAD_STNUM > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.UPLOAD_STNUM AS DECIMAL(16,6)) ELSE 0 END AS REAL_SALES_AMT_PSD, '+
                     '  (TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT) AS LSALES_AMT, '+
                     '  CASE WHEN TL.UPLOAD_STNUM > 0 THEN  (TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)/CAST(TL.UPLOAD_STNUM AS DECIMAL(16,6)) ELSE 0 END AS LSALES_AMT_PSD, '+
                     '  T.BUDGET_AMT AS BUDGET_AMT, '+
                     '  T.BUDGET_PSD AS BUDGET_AMT_PSD, '+
                     '  CASE WHEN T.BUDGET_AMT > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.BUDGET_AMT AS DECIMAL(16,6))*100 ELSE 0 END AS BUDGET_SALES_RATE, '+
                     '  CASE WHEN (T.BUDGET_PSD) > 0 THEN  (CASE WHEN T.UPLOAD_STNUM > 0 THEN  (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/T.UPLOAD_STNUM ELSE 0 END)/(T.BUDGET_PSD)*100 ELSE 0 END AS BUDGET_SALES_PSD_RATE, '+
                     '  (CASE WHEN T.UPLOAD_STNUM > 0 THEN (T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/CAST(T.UPLOAD_STNUM AS DECIMAL(18,8)) ELSE 0 END)-(T.BUDGET_PSD) AS PSD_DIFF, '+
                     '  CASE WHEN (TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)  = 0 THEN 0 ELSE (CAST(T.SALES_AMT AS DECIMAL(16,6))-T.DIS_AMT-T.SUB_AMT)/(TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)*100 END AS LSALES_RATE, '+
                     '  CASE WHEN (TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)  = 0 OR T.UPLOAD_STNUM=0 OR TL.UPLOAD_STNUM=0 THEN 0 ELSE ((T.SALES_AMT-T.DIS_AMT-T.SUB_AMT)/(CAST(T.UPLOAD_STNUM AS DECIMAL(16,6))))/((TL.SALES_AMT-TL.DIS_AMT-TL.SUB_AMT)/(CAST(TL.UPLOAD_STNUM AS DECIMAL(16,6))))*100 END AS L_YEAR_PSD_DIFF '+
                     '  FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '    ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '   AND T.PRD_ID = TL.PRD_ID '+
                     ' WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '   AND T.PRD_ID  IN('+ P_PRD_ID +') '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';  
         WHEN '5' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     ' SELECT T.TIME_ID,A.KND_ID AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+
                     '                                    INNER JOIN (SELECT DISTINCT GRP_ID ,KND_ID FROM PMART.PRD_DIM WHERE KND_ID  NOT IN (''X1'')  ) A '+
                     '     ON T.PRD_ID  = A.GRP_ID '+
                     '  WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '    AND T.PRD_ID  IN('+ P_PRD_ID +') '+
                     '  GROUP BY T.TIME_ID,A.KND_ID '       
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
         WHEN '4' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+  
                     ' SELECT T.TIME_ID,B.RESPON_ID AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+                     
                     '                                    INNER JOIN (SELECT DISTINCT RESPON_ID,GRP_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO = 1  AND KND_ID  NOT IN (''X1'')  ) B '+
                     '     ON T.PRD_ID  = B.GRP_ID '+                   
                     '   WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '     AND T.PRD_ID  IN('+ P_PRD_ID +') '+
                     '  GROUP BY T.TIME_ID,B.RESPON_ID '
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';         
         WHEN '3' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     ' SELECT T.TIME_ID,B.BRANCH_ID AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+
                     '                                    INNER JOIN (SELECT DISTINCT BRANCH_ID,GRP_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1 AND KND_ID  NOT IN (''X1'')  ) B '+
                     '     ON T.PRD_ID  = B.GRP_ID '+ 
                     '  WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '    AND T.PRD_ID  IN('+ P_PRD_ID +') '+                     
                     '  GROUP BY T.TIME_ID,B.BRANCH_ID '
             ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';         
         WHEN '2' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     ' SELECT T.TIME_ID,B.DEPT_ID AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+
                     '                                    INNER JOIN (SELECT DISTINCT DEPT_ID,GRP_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1 AND KND_ID  NOT IN (''X1'')  ) B '+
                     '     ON T.PRD_ID  = B.GRP_ID '+                  
                     '  WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '    AND T.PRD_ID  IN('+ P_PRD_ID +') '+
                     '  GROUP BY T.TIME_ID,B.DEPT_ID '
                ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';         
         WHEN '1' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     ' SELECT T.TIME_ID,B.TOT_ID AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+
                     '                                   INNER JOIN (SELECT DISTINCT TOT_ID,GRP_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1 AND KND_ID  NOT IN (''X1'')  ) B '+
                     '     ON T.PRD_ID  = B.GRP_ID '+
                     '  WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '    AND T.PRD_ID  IN('+ P_PRD_ID +') '+                    
                     '  GROUP BY T.TIME_ID,B.TOT_ID '
                ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	  WHEN '0' THEN 
         SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_FUNC  AS('+
                     ' SELECT T.TIME_ID,-99 AS PRD_ID, '+
                     ' '+V_SELECT+' '+
                     '   FROM PMART.BASIC_MFACT_BUDGET T LEFT OUTER JOIN  PMART.BASIC_MFACT_BUDGET TL '+
                     '     ON T.TIME_ID - '+ LDAY +' = TL.TIME_ID '+
                     '    AND T.PRD_ID  = TL.PRD_ID '+
                     '                                   INNER JOIN (SELECT DISTINCT TOT_ID,GRP_ID FROM PMART.ORG_DIM_POSI1 WHERE SEQNO=1 AND KND_ID  NOT IN (''X1'')  ) B '+
                     '     ON T.PRD_ID  = B.GRP_ID '+
                     '  WHERE T.TIME_ID IN('+ P_L_DAY_ID +') '+
                     '    AND T.PRD_ID  IN('+ P_PRD_ID +') '+                    
                     '  GROUP BY T.TIME_ID '
                ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
      ELSE
	SET SQLSTR ='';                
      END CASE ;      
   DELETE FROM PMART.T1 WHERE F1 = 72;
   INSERT INTO PMART.T1(F1,F2) SELECT 72,SQLSTR;	
	EXECUTE IMMEDIATE SQLSTR;
END SP;