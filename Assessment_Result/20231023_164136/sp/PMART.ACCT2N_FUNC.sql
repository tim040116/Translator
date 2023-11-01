CREATE PROCEDURE PMART.ACCT2N_FUNC
(P_L_MONTH_ID NUMBER,P_ORG_ID NUMBER,P_LEVEL NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(4000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   DECLARE V_L_MONTH_ID NUMBER;
   CALL PMART.P_DROP_TABLE ('#VT_ACCT2N_FUNC'); 
   CASE P_LEVEL
      WHEN -1 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_ACCT2N_FUNC  AS('+
                   'SELECT '+
                   '-1 AS TOT_ID, '+
                   'ORG_ID AS ORG_ID, '+
                   'CAST(LOSSCS AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS THROW_RATE, '+
                   'CAST(SVALUE AS DECIMAL(14,4))/(DECODE(BGNSL2+ENDSL2,0,NULL,BGNSL2+ENDSL2)/2) AS RETURN_RATE, '+
                   'CAST(PROCS AS DECIMAL(16,6))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS LOSS_RATE, '+
                   'CAST(SALEPROFIT AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS EARN_RATE, '+
                   'GAINS-DECODE(GAINS,0,NULL,GAINP) AS EARN_AMT, '+
                   'PROCS, '+
                   'LOSSCS, '+
                   'PRCCHGSL2, '+
                   'GAINS, '+
                   'GAINP '+
                   'FROM '+
                   'PMART.ACCT4420M_SUM '+
                   'WHERE L_MONTH_ID='+P_L_MONTH_ID+' AND '+
                   'ORG_ID IN (SELECT DISTINCT PDEPT_ID FROM PMART.LATEST_ORG_DIM WHERE TOT_ID=-1) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';      
        WHEN 0 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_ACCT2N_FUNC  AS('+
                   'SELECT '+
                   '-1 AS TOT_ID, '+
                   'ORG_ID AS ORG_ID, '+
                   'CAST(LOSSCS AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS THROW_RATE, '+
                   'CAST(SVALUE AS DECIMAL(14,4))/(DECODE(BGNSL2+ENDSL2,0,NULL,BGNSL2+ENDSL2)/2) AS RETURN_RATE, '+
                   'CAST(PROCS AS DECIMAL(16,6))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS LOSS_RATE, '+
                   'CAST(SALEPROFIT AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS EARN_RATE, '+
                   'GAINS-DECODE(GAINS,0,NULL,GAINP) AS EARN_AMT, '+
                   'PROCS, '+
                   'LOSSCS, '+
                   'PRCCHGSL2, '+
                   'GAINS, '+
                   'GAINP '+
                   'FROM '+
                   'PMART.ACCT4420M_SUM '+
                   'WHERE L_MONTH_ID='+P_L_MONTH_ID+' AND '+
                   'ORG_ID IN (SELECT DISTINCT DEPT_ID FROM PMART.LATEST_ORG_DIM WHERE PDEPT_ID='+P_ORG_ID+') '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';      
	   WHEN 1 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_ACCT2N_FUNC  AS('+
                     'SELECT '+
                     '-1 AS TOT_ID, '+
                     'ORG_ID AS ORG_ID, '+
                     'CAST(LOSSCS AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS THROW_RATE, '+
                     'CAST(SVALUE AS DECIMAL(14,4))/(DECODE(BGNSL2+ENDSL2,0,NULL,BGNSL2+ENDSL2)/2) AS RETURN_RATE, '+
                     'CAST(PROCS AS DECIMAL(16,6))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS LOSS_RATE, '+
                     'CAST(SALEPROFIT AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS EARN_RATE, '+
                     'GAINS-DECODE(GAINS,0,NULL,GAINP) AS EARN_AMT, '+
                     'PROCS, '+
                     'LOSSCS, '+
                     'PRCCHGSL2, '+
                     'GAINS, '+
                     'GAINP '+
                     'FROM '+
                     'PMART.ACCT4420M_SUM '+
                     'WHERE L_MONTH_ID='+P_L_MONTH_ID+' AND '+
                     'ORG_ID IN '+
                     '(SELECT DISTINCT BRANCH_ID FROM PMART.LATEST_ORG_DIM WHERE DEPT_ID='+P_ORG_ID+') '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';       
      WHEN 2 THEN 
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_ACCT2N_FUNC  AS('+
                   'SELECT '+
                   '-1 AS TOT_ID, '+
                   'STORE_ID AS ORG_ID, '+
                   'CAST(LOSSCS AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS THROW_RATE, '+
                   'CAST(SVALUE AS DECIMAL(14,4))/(DECODE(BGNSL2+ENDSL2,0,NULL,BGNSL2+ENDSL2)/2) AS RETURN_RATE, '+
                   'CAST(PROCS AS DECIMAL(16,6))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS LOSS_RATE, '+
                   'CAST(SALEPROFIT AS DECIMAL(14,4))/DECODE(SVALUE,0,NULL,SVALUE)*100 AS EARN_RATE, '+
                   'GAINS-DECODE(GAINS,0,NULL,GAINP) AS EARN_AMT, '+
                   'PROCS, '+
                   'LOSSCS, '+
                   'PRCCHGSL2, '+
                   'GAINS, '+
                   'GAINP '+
                   'FROM '+
                   'PMART.ACCT4420M '+
                   'WHERE L_MONTH_ID='+P_L_MONTH_ID+' AND '+
                   'STORE_ID IN '+
                   '(SELECT DISTINCT STORE_ID FROM PMART.LATEST_ORG_DIM WHERE BRANCH_ID='+P_ORG_ID+') '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';       
   END CASE;
	EXECUTE IMMEDIATE SQLSTR;
END SP;