CREATE PROCEDURE PMART.REMD2N_FUNC
(P_L_MONTH_ID NUMBER,P_ORG_ID NUMBER,P_LEVEL NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(4000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   DECLARE V_L_MONTH_ID NUMBER;
   DECLARE V_L_MONTH_LAST_MONTH NUMBER;
   CALL PMART.P_DROP_TABLE ('#VT_REMD2N_FUNC'); 
   SET V_L_MONTH_ID = P_L_MONTH_ID;
   SELECT  TO_NUMBER(TO_CHAR(ADD_MONTHS(TO_DATE(TO_CHAR(V_L_MONTH_ID)+'01','YYYYMMDD'),-1),'YYYYMM'))
   INTO V_L_MONTH_LAST_MONTH;
   CASE P_LEVEL
      WHEN -1 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_REMD2N_FUNC  AS('+
                      'SELECT  '+
                      '-1 AS TOT_ID, '+
                      'B.ORG_ID AS ORG_ID, '+
                      'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) AS AVG_AMT, '+
                      '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                      '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                      'AS AVG_AMT_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+
                      '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                      'AS AVG_AMT_RATE_LAST_YEAR, '+
                      'CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) '+
                      'AS AVG_CUST_NUM, '+
                      '(CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                      '(CAST(C.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                      'AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+                      
                      '(CAST(B.EX_TOT_CUST_NUM_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                      'AS AVG_CUST_NUM_LAST_YEAR, '+
                      'CAST(B.TOT_AMT AS DECIMAL(16,4))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM) AS CUST_PRICE, '+
                      '(CAST(B.TOT_AMT AS DECIMAL(16,4))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM))- '+
                      '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_CUST_NUM,0,NULL,C.TOT_CUST_NUM)) AS CUST_PRICE_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM,0,NULL,B.EX_TOT_CUST_NUM))- '+
                      '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,B.EX_TOT_CUST_NUM_LAST_YEAR)) '+
                      'AS CUST_PRICE_RATE_LAST_YEAR '+
                      'FROM '+
                      '   ( '+
                      '   SELECT  * '+
                      '   FROM PMART.REMD_FACT_MONTH_SUM '+
                      '   WHERE ORG_ID IN (SELECT DISTINCT PDEPT_ID FROM PMART.LATEST_ORG_DIM WHERE TOT_ID=-1) '+
                      '   AND L_MONTH_ID='+P_L_MONTH_ID +
                      '   ) B '+
                      'LEFT JOIN '+
                      '   ( '+
                      '   SELECT  * '+
                      '   FROM PMART.REMD_FACT_MONTH_SUM '+
                      '   WHERE ORG_ID IN (SELECT DISTINCT PDEPT_ID FROM PMART.LATEST_ORG_DIM WHERE TOT_ID=-1) '+
                      '   AND L_MONTH_ID='+V_L_MONTH_LAST_MONTH+
                      '   ) C '+
                      'ON(B.ORG_ID=C.ORG_ID) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';      
        WHEN 0 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_REMD2N_FUNC  AS('+
                      'SELECT  '+
                      '-1 AS TOT_ID, '+
                      'B.ORG_ID AS ORG_ID, '+
                      'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) AS AVG_AMT, '+
                      '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                      '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                      'AS AVG_AMT_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+
                      '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                      'AS AVG_AMT_RATE_LAST_YEAR, '+
                      'CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) '+
                      'AS AVG_CUST_NUM, '+
                      '(CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                      '(CAST(C.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                      'AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+                      
                      '(CAST(B.EX_TOT_CUST_NUM_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                      'AS AVG_CUST_NUM_LAST_YEAR, '+
                      'CAST(B.TOT_AMT AS DECIMAL(16,4))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM) AS CUST_PRICE, '+
                      '(CAST(B.TOT_AMT AS DECIMAL(16,4))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM))- '+
                      '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_CUST_NUM,0,NULL,C.TOT_CUST_NUM)) AS CUST_PRICE_RATE_LAST_MONTH, '+
                      '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM,0,NULL,B.EX_TOT_CUST_NUM))- '+
                      '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,B.EX_TOT_CUST_NUM_LAST_YEAR)) '+
                      'AS CUST_PRICE_RATE_LAST_YEAR '+
                      'FROM '+
                      '   ( '+
                      '   SELECT  * '+
                      '   FROM PMART.REMD_FACT_MONTH_SUM '+
                      '   WHERE ORG_ID IN (SELECT DISTINCT DEPT_ID FROM PMART.LATEST_ORG_DIM WHERE PDEPT_ID='+P_ORG_ID+') '+
                      '   AND L_MONTH_ID='+P_L_MONTH_ID +
                      '   ) B '+
                      'LEFT JOIN '+
                      '   ( '+
                      '   SELECT  * '+
                      '   FROM PMART.REMD_FACT_MONTH_SUM '+
                      '   WHERE ORG_ID IN (SELECT DISTINCT DEPT_ID FROM PMART.LATEST_ORG_DIM WHERE TOT_ID=-1) '+
                      '   AND L_MONTH_ID='+V_L_MONTH_LAST_MONTH+
                      '   ) C '+
                      'ON(B.ORG_ID=C.ORG_ID) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';      
	  WHEN 1 THEN  
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_REMD2N_FUNC  AS('+
                    'SELECT  '+
                    '-1 AS TOT_ID, '+
                    'B.ORG_ID AS ORG_ID, '+
                    'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) AS AVG_AMT, '+
                    '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                    '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                    'AS AVG_AMT_RATE_LAST_MONTH, '+
                    '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+
                    '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                    'AS AVG_AMT_RATE_LAST_YEAR, '+
                    'CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) '+
                    'AS AVG_CUST_NUM, '+
                    '(CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                    '(CAST(C.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                    'AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
                    '(CAST(B.EX_TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM,0,NULL,B.EX_TOT_PLAN_STNUM))/ '+
                    '(CAST(B.EX_TOT_CUST_NUM_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                    'AS AVG_CUST_NUM_LAST_YEAR, '+
                    'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM) AS CUST_PRICE, '+
                    '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM))- '+
                    '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_CUST_NUM,0,NULL,C.TOT_CUST_NUM)) AS CUST_PRICE_RATE_LAST_MONTH, '+
                    '(CAST(B.EX_TOT_AMT AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM,0,NULL,B.EX_TOT_CUST_NUM))- '+
                    '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,B.EX_TOT_CUST_NUM_LAST_YEAR)) '+
                    'AS CUST_PRICE_RATE_LAST_YEAR '+
                    'FROM '+
                    '   ( '+
                    '   SELECT * '+
                    '   FROM PMART.REMD_FACT_MONTH_SUM '+
                    '   WHERE ORG_ID IN (SELECT DISTINCT BRANCH_ID FROM PMART.LATEST_ORG_DIM '+
                    '   WHERE DEPT_ID='+P_ORG_ID+') '+
                    '   AND L_MONTH_ID='+P_L_MONTH_ID+
                    '   ) B '+
                    'LEFT JOIN '+
                    '   ( '+
                    '   SELECT * '+
                    '   FROM PMART.REMD_FACT_MONTH_SUM '+
                    '   WHERE ORG_ID IN (SELECT DISTINCT BRANCH_ID FROM PMART.LATEST_ORG_DIM '+
                    '   WHERE DEPT_ID='+P_ORG_ID+') '+
                    '   AND L_MONTH_ID='+V_L_MONTH_LAST_MONTH+
                    '   ) C '+
                    'ON(B.ORG_ID=C.ORG_ID) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';     
      WHEN 2 THEN 
           SET SQLSTR = 
              'CREATE MULTISET VOLATILE TABLE #VT_REMD2N_FUNC  AS('+
                   'SELECT '+
                   '-1 AS TOT_ID, '+
                   'D.STORE_ID AS ORG_ID, '+
                   'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) AS AVG_AMT, '+
                   '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                   '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                   'AS AVG_AMT_RATE_LAST_MONTH, '+
                   '(CAST(B.EX_ACCU_AMT AS DECIMAL(16,6))/DECODE(B.EX_ACCU_PLAN_STNUM,0,NULL,B.EX_ACCU_PLAN_STNUM))/ '+
                   '(CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                   'AS AVG_AMT_RATE_LAST_YEAR, '+
                   'CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM) '+
                   'AS AVG_CUST_NUM, '+
                   '(CAST(B.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(B.TOT_PLAN_STNUM,0,NULL,B.TOT_PLAN_STNUM))/ '+
                   '(CAST(C.TOT_CUST_NUM AS DECIMAL(16,6))/DECODE(C.TOT_PLAN_STNUM,0,NULL,C.TOT_PLAN_STNUM))*100 '+
                   'AS AVG_CUST_NUM_RATE_LAST_MONTH, '+
                   '(CAST(B.EX_ACCU_CUST_NUM AS DECIMAL(16,6))/DECODE(B.EX_ACCU_PLAN_STNUM,0,NULL,B.EX_ACCU_PLAN_STNUM))/ '+
                   '(CAST(B.EX_TOT_CUST_NUM_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_PLAN_STNUM_LAST_YEAR,0,NULL,B.EX_TOT_PLAN_STNUM_LAST_YEAR))*100 '+
                   'AS AVG_CUST_NUM_LAST_YEAR, '+
                   'CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM) AS CUST_PRICE, '+
                   '(CAST(B.TOT_AMT AS DECIMAL(16,6))/DECODE(B.TOT_CUST_NUM,0,NULL,B.TOT_CUST_NUM))- '+
                   '(CAST(C.TOT_AMT AS DECIMAL(16,6))/DECODE(C.TOT_CUST_NUM,0,NULL,C.TOT_CUST_NUM)) AS CUST_PRICE_RATE_LAST_MONTH, '+
                   '(CAST(B.EX_ACCU_AMT AS DECIMAL(16,6))/DECODE(B.EX_ACCU_CUST_NUM,0,NULL,B.EX_ACCU_CUST_NUM))- '+
                   '( CAST(B.EX_TOT_AMT_LAST_YEAR AS DECIMAL(16,6))/DECODE(B.EX_TOT_CUST_NUM_LAST_YEAR,0,NULL,B.EX_TOT_CUST_NUM_LAST_YEAR)) '+
                   'AS CUST_PRICE_RATE_LAST_YEAR '+
                   'FROM '+
                   '   ( '+
                   '   SELECT * FROM PMART.REMD_FACT_MONTH '+
                   '   WHERE OSTORE_ID IN (SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                   '   WHERE BRANCH_ID='+P_ORG_ID+') '+
                   '   AND L_MONTH_ID='+P_L_MONTH_ID+ 
                   '   ) B '+
                   'LEFT JOIN '+
                   '   ( '+
                   '   SELECT * FROM PMART.REMD_FACT_MONTH '+
                   '   WHERE OSTORE_ID IN (SELECT DISTINCT OSTORE_ID FROM PMART.LATEST_ORG_DIM '+
                   '   WHERE BRANCH_ID='+P_ORG_ID+') '+
                   '   AND L_MONTH_ID='+V_L_MONTH_LAST_MONTH+ 
                   '   ) C '+
                   'ON(B.OSTORE_ID=C.OSTORE_ID) '+
                   'INNER JOIN PMART.LATEST_ORG_DIM D '+
                   'ON (B.OSTORE_ID=D.OSTORE_ID) '+
                   'WHERE D.OPNDT<=('+P_L_MONTH_ID+ '*100+31) '+
                   '  AND D.ENDDT>=('+P_L_MONTH_ID+ '*100+ 1) '+                   
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';       
   END CASE;
	EXECUTE IMMEDIATE SQLSTR;
END SP;