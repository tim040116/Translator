REPLACE PROCEDURE PMART.UING_CAL2_FUNC
(P_YEAR_ID NUMBER,P_ORG_ID NUMBER,P_OORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(8000) DEFAULT ''; 
  CALL PMART.P_DROP_TABLE ('#VT_UING_CAL2_FUNC'); 
      SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_UING_CAL2_FUNC AS('+            
               'SELECT '+
               '-1 AS TOT_ID, '+
               'A.L_MONTH_ID AS L_MONTH_ID, '+
               'B.AVG_AMT AS AVG_AMT, '+
               'B.AVG_AMT_RATE_LAST_MONTH  AS AVG_AMT_RATE_LAST_MONTH, '+
               'B.AVG_AMT_RATE_LAST_YEAR AS AVG_AMT_REATE_LAST_YEAR, '+
               'B.AVG_CUST_NUM AS AVG_CUST_NUM, '+
               'B.AVG_CUST_NUM_RATE_LAST_MONTH AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
               'B.AVG_CUST_NUM_RATE_LAST_YEAR AS AVG_CUST_NUM_RATE_LAST_YEAR, '+
               'B.CUST_PRICE AS CUST_PRICE, '+
               'B.CUST_PRICE_RATE_LAST_MONTH AS CUST_PRICE_RATE_LAST_MONTH, '+
               'B.CUST_PRICE_RATE_LAST_YEAR AS CUST_PRICE_RATE_LAST_YEAR, '+
               'C.THROW_RATE AS THROW_RATE, '+
               'C.RETURN_RATE AS RETURN_RATE, '+
               'C.LOSS_RATE AS LOSS_RATE, '+
               'C.EARN_RATE AS EARN_RATE, '+
               'C.EARN_AMT AS EARN_AMT, '+
               'C.PROCS AS PROCS, '+
               'C.LOSSCS AS LOSSCS, '+
               'C.PRCCHGSL2 AS PRCCHGSL2, '+
               'C.GAINS AS GAINS, '+
               'C.GAINP AS GAINP, '+
               'C.SVALUE AS SVALUE, '+
               'D.INAMT8 AS INAMT8, '+
               'D.SALAMT8 AS SALAMT8, '+
               'D.INSALD8 AS INSALD8, '+
               'D.ACCTAMT8 AS ACCTAMT8, '+
               'D.INAMT11 AS INAMT11, '+
               'D.SALAMT11 AS SALAMT11, '+
               'D.INSALD11 AS INSALD11, '+
               'D.ACCTAMT11 AS ACCTAMT11, '+
               'D.INAMT12 AS INAMT12, '+
               'D.SALAMT12 AS SALAMT12, '+
               'D.INSALD12 AS INSALD12, '+
               'D.ACCTAMT12 AS ACCTAMT12, '+
               'NULL AS ACCTAMT90 '+
               'FROM '+
               '( '+
               'SELECT 999999 AS L_MONTH_ID '+
               ') A '+
               'LEFT JOIN '+
               '( '+
               '     SELECT 999999 AS L_MONTH_ID, '+
               '     SUM(CAST(TOT_AMT AS DECIMAL(20,4)))/SUM(TOT_PLAN_STNUM)  AS AVG_AMT, '+
               '     NULL AS AVG_AMT_RATE_LAST_MONTH, '+
               '     NULL AS AVG_AMT_RATE_LAST_YEAR, '+
               '     SUM(CAST(TOT_CUST_NUM AS DECIMAL(20,4)))/SUM(TOT_PLAN_STNUM) AS AVG_CUST_NUM, '+
               '     NULL AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
               '     NULL AS AVG_CUST_NUM_RATE_LAST_YEAR, '+
               '     SUM(CAST(TOT_AMT AS DECIMAL(20,4)))/SUM(TOT_CUST_NUM) AS CUST_PRICE, '+
               '     NULL AS CUST_PRICE_RATE_LAST_MONTH, '+
               '     NULL AS CUST_PRICE_RATE_LAST_YEAR '+
               '     FROM PMART.REMD_FACT_MONTH_SUM '+
               '     WHERE  L_MONTH_ID IN (SELECT DISTINCT L_MONTH_ID FROM PMART.YMWD_TIME WHERE L_YEAR_ID='+P_YEAR_ID +')  '+
               '     AND ORG_ID='+P_OORG_ID+
               ')B  '+
               '   ON (A.L_MONTH_ID=B.L_MONTH_ID) '+
               'LEFT JOIN '+
               '( '+
               '     SELECT 999999 AS L_MONTH_ID, '+
               '     SUM(CAST(LOSSCS AS DECIMAL(20,4)))/DECODE(SUM(SVALUE),0,NULL,SUM(SVALUE))*100 AS THROW_RATE, '+
               '     SUM(CAST(SVALUE AS DECIMAL(20,4)))/(DECODE(SUM(BGNSL2+ENDSL2),0,NULL,SUM(BGNSL2+ENDSL2))/2) AS RETURN_RATE, '+
               '     NULL AS LOSS_RATE, '+
               '     SUM(CAST(SALEPROFIT AS DECIMAL(20,4)))/DECODE(SUM(SVALUE),0,NULL,SUM(SVALUE))*100 AS EARN_RATE, '+
               '     AVG(CAST(GAINS AS DECIMAL(20,4))-CAST(GAINP AS DECIMAL(20,4))) AS EARN_AMT, '+
               '     NULL AS PROCS, '+
               '     AVG(CAST(LOSSCS AS DECIMAL(20,4))) AS LOSSCS, '+
               '     AVG(CAST(PRCCHGSL2 AS DECIMAL(20,4))) AS PRCCHGSL2, '+
               '     AVG(CAST(GAINS AS DECIMAL(20,4))) AS GAINS, '+
               '     AVG(CAST(GAINP AS DECIMAL(20,4))) AS GAINP, '+
               '     AVG(CAST(SVALUE AS DECIMAL(20,4))) AS SVALUE '+
               '     FROM PMART.ACCT4420M_SUM WHERE  L_MONTH_ID IN (SELECT DISTINCT L_MONTH_ID FROM PMART.YMWD_TIME WHERE L_YEAR_ID='+P_YEAR_ID+') '+
               '     AND ORG_ID='+P_ORG_ID+
               ')C  '+
               '    ON (A.L_MONTH_ID=C.L_MONTH_ID) '+
               'LEFT JOIN '+
               '( '+
               '     SELECT 999999 AS L_MONTH_ID, '+
               '     AVG(CAST(INAMT8    AS DECIMAL(20,4))) AS INAMT8,   '+
               '     AVG(CAST(SALAMT8   AS DECIMAL(20,4))) AS SALAMT8,  '+
               '     AVG(CAST(INSALD8   AS DECIMAL(20,4))) AS INSALD8,  '+
               '     AVG(CAST(ACCTAMT8  AS DECIMAL(20,4))) AS ACCTAMT8, '+
               '     AVG(CAST(INAMT11   AS DECIMAL(20,4))) AS INAMT11,  '+
               '     AVG(CAST(SALAMT11  AS DECIMAL(20,4))) AS SALAMT11, '+
               '     AVG(CAST(INSALD11  AS DECIMAL(20,4))) AS INSALD11, '+
               '     AVG(CAST(ACCTAMT11 AS DECIMAL(20,4))) AS ACCTAMT11,'+
               '     AVG(CAST(INAMT12   AS DECIMAL(20,4))) AS INAMT12,  '+
               '     AVG(CAST(SALAMT12  AS DECIMAL(20,4))) AS SALAMT12, '+
               '     AVG(CAST(INSALD12  AS DECIMAL(20,4))) AS INSALD12, '+
               '     AVG(CAST(ACCTAMT12 AS DECIMAL(20,4))) AS ACCTAMT12 '+
               '     FROM PMART.PICL3090M_SUM '+
               '     WHERE  L_MONTH_ID IN (SELECT DISTINCT L_MONTH_ID FROM PMART.YMWD_TIME WHERE L_YEAR_ID='+P_YEAR_ID+') '+
               '     AND ORG_ID='+P_ORG_ID+
               ')D  '+
               '    ON (A.L_MONTH_ID=D.L_MONTH_ID) '+
           ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;