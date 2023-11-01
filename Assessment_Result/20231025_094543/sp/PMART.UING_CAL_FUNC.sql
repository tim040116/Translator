REPLACE PROCEDURE PMART.UING_CAL_FUNC
(P_YEAR_ID NUMBER,P_ORG_ID NUMBER,P_OORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_UING_CAL_FUNC'); 
  CALL PMART.REMD1_FUNC (P_YEAR_ID,P_OORG_ID); 
  CALL PMART.ACCT1_FUNC (P_YEAR_ID,P_ORG_ID); 
  CALL PMART.PICL1_FUNC (P_YEAR_ID,P_ORG_ID); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_UING_CAL_FUNC AS('+    
               ' SELECT '+
               ' -1 AS TOT_ID, '+
               ' A.L_MONTH_ID AS L_MONTH_ID, '+
               ' B.AVG_AMT AS AVG_AMT, '+
               ' B.AVG_AMT_RATE_LAST_MONTH  AS AVG_AMT_RATE_LAST_MONTH, '+
               ' B.AVG_AMT_RATE_LAST_YEAR AS AVG_AMT_RATE_LAST_YEAR, '+
               ' B.AVG_CUST_NUM AS AVG_CUST_NUM, '+
               ' B.AVG_CUST_NUM_RATE_LAST_MONTH AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
               ' B.AVG_CUST_NUM_RATE_LAST_YEAR AS AVG_CUST_NUM_RATE_LAST_YEAR, '+
               ' B.CUST_PRICE AS CUST_PRICE, '+
               ' B.CUST_PRICE_RATE_LAST_MONTH AS CUST_PRICE_RATE_LAST_MONTH, '+
               ' B.CUST_PRICE_RATE_LAST_YEAR AS CUST_PRICE_RATE_LAST_YEAR, '+
               ' C.THROW_RATE AS THROW_RATE, '+
               ' C.RETURN_RATE AS RETURN_RATE, '+
               ' C.LOSS_RATE AS LOSS_RATE, '+
               ' C.EARN_RATE AS EARN_RATE, '+
               ' C.EARN_AMT AS EARN_AMT, '+
               ' C.PROCS AS PROCS, '+
               ' C.LOSSCS AS LOSSCS, '+
               ' C.PRCCHGSL2 AS PRCCHGSL2, '+
               ' C.GAINS AS GAINS, '+
               ' C.GAINP AS GAINP, '+
               ' C.SVALUE AS SVALUE, '+
               ' D.INAMT8 AS INAMT8, '+
               ' D.SALAMT8 AS SALAMT8, '+
               ' D.INSALD8 AS INSALD8, '+
               ' D.ACCTAMT8 AS ACCTAMT8, '+
               ' D.INAMT11 AS INAMT11, '+
               ' D.SALAMT11 AS SALAMT11, '+
               ' D.INSALD11 AS INSALD11, '+
               ' D.ACCTAMT11 AS ACCTAMT11, '+
               ' D.INAMT12 AS INAMT12, '+
               ' D.SALAMT12 AS SALAMT12, '+
               ' D.INSALD12 AS INSALD12, '+
               ' D.ACCTAMT12 AS ACCTAMT12, '+
               ' D.ACCTAMT90 AS ACCTAMT90 '+
               ' FROM '+
               ' (SELECT DISTINCT L_MONTH_ID FROM PMART.YMWD_TIME WHERE L_YEAR_ID= '+P_YEAR_ID+') A '+
               ' LEFT JOIN '+
               ' (SELECT * FROM #VT_REMD1_FUNC ) B '+
               ' ON (A.L_MONTH_ID=B.L_MONTH_ID) '+
               ' LEFT JOIN '+
               ' (SELECT * FROM #VT_ACCT1_FUNC ) C '+
               ' ON (A.L_MONTH_ID=C.L_MONTH_ID) '+
               ' LEFT JOIN '+
               ' (SELECT * FROM #VT_PICL1_FUNC ) D '+
               ' ON (A.L_MONTH_ID=D.L_MONTH_ID) '+
          ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;