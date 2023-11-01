REPLACE PROCEDURE PMART.NEW_TPO_MFACT 
(FP_AMT_TYPE CHAR(1),FP_TIME_TYPE CHAR(1),
FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR(1),FP_PRD_LEVEL NUMBER,FP_PRD_LIST VARCHAR(5000),
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(64000);
   DECLARE SQLSTR_F1 VARCHAR(7000);
   DECLARE SQLSTR_F2 VARCHAR(7000);
   DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TPO_MFACT'); 
   CASE
      WHEN FP_TIME_TYPE='D' AND FP_PRD_TYPE='P' AND FP_PRD_LEVEL=2  AND FP_ORG_LEVEL=3  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';             
      WHEN FP_TIME_TYPE='D' AND FP_PRD_TYPE='R' AND FP_PRD_LEVEL=1  AND FP_ORG_LEVEL=3  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';
      WHEN FP_PRD_TYPE='L' THEN 
            SET V_TABLE_NAME = 'PMART.BASIC_MLFACT';
      ELSE
             SET V_TABLE_NAME = 'PMART.BASIC_MFACT';
   END CASE;
   IF FP_TIME_TYPE='D' THEN
   			SET SQLSTR_F1 = 'SELECT A.JOIN_KEY AS JOIN_KEY, '+
						                  'TIME_ID AS TIME_ID, '+
						                  'PRD_ID AS PRD_ID, '+
						                  'SALES_CNT AS SALES_CNT, '+
						                  'SALES_AMT AS SALES_AMT, '+
						                  'ORDER_CNT AS ORDER_CNT, '+
						                  'ORDER_AMT AS ORDER_AMT, '+
						                  'THROW_CNT AS THROW_CNT, '+
						                  'THROW_AMT AS THROW_AMT '+
										  'FROM (SELECT DENSE_RANK() OVER(ORDER BY L_DAY_ID) AS JOIN_KEY, L_DAY_ID FROM PMART.YMWD_TIME WHERE L_DAY_ID IN ('+ FP_TIME_LIST +')) A '+ 
						                  'LEFT JOIN  '+ V_TABLE_NAME +' B ON A.L_DAY_ID = B.TIME_ID ';
			SET SQLSTR_F2 = 'SELECT A.JOIN_KEY AS JOIN_KEY, '+
						                  'TIME_ID AS TIME_ID, '+
						                  'PRD_ID AS PRD_ID, '+
						                  'SALES_CNT AS SALES_CNT, '+
						                  'SALES_AMT AS SALES_AMT, '+
						                  'ORDER_CNT AS ORDER_CNT, '+
						                  'ORDER_AMT AS ORDER_AMT, '+
						                  'THROW_CNT AS THROW_CNT, '+
						                  'THROW_AMT AS THROW_AMT  '+
										  'FROM (SELECT DENSE_RANK() OVER(ORDER BY L_DAY_ID) AS JOIN_KEY, L_DAY_ID FROM PMART.YMWD_TIME WHERE L_DAY_ID IN ('+ FP_LTIME_LIST +')) A '+ 
						                  'LEFT JOIN  '+ V_TABLE_NAME +' B ON A.L_DAY_ID = B.TIME_ID ';
   ELSE
   			SET SQLSTR_F1 =  'SELECT DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
						                  'TIME_ID AS TIME_ID, '+
						                  'PRD_ID AS PRD_ID, '+
						                  'SALES_CNT AS SALES_CNT, '+
						                  'SALES_AMT AS SALES_AMT, '+
						                  'ORDER_CNT AS ORDER_CNT, '+
						                  'ORDER_AMT AS ORDER_AMT, '+
						                  'THROW_CNT AS THROW_CNT, '+
						                  'THROW_AMT AS THROW_AMT '+
						                  'FROM '+ V_TABLE_NAME +' ';
			SET SQLSTR_F2 =  'SELECT DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
						                  'TIME_ID AS TIME_ID, '+
						                  'PRD_ID AS PRD_ID, '+
						                  'SALES_CNT AS SALES_CNT, '+
						                  'SALES_AMT AS SALES_AMT, '+
						                  'ORDER_CNT AS ORDER_CNT, '+
						                  'ORDER_AMT AS ORDER_AMT, '+
						                  'THROW_CNT AS THROW_CNT, '+
						                  'THROW_AMT AS THROW_AMT '+
						                  'FROM '+ V_TABLE_NAME +' ';
   END IF;
   SET SQLSTR = 
          'CREATE MULTISET VOLATILE TABLE #VT_NEW_TPO_MFACT  AS('+
                  'SELECT '+
                  'F1.TIME_ID AS TIME_ID, '+
                  'F1.PRD_ID AS PRD_ID, '+
                  'CAST(F1.SALES_CNT  AS DECIMAL(18,6)) AS SALES_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.SALES_AMT AS DECIMAL(18,6)) /1000,CAST(F1.SALES_AMT AS DECIMAL(18,6))) AS SALES_AMT, '+      
                  'CAST(F1.ORDER_CNT  AS DECIMAL(18,6)) AS ORDER_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.ORDER_AMT AS DECIMAL(18,6)) /1000,CAST(F1.ORDER_AMT AS DECIMAL(18,6))) AS ORDER_AMT, '+
                  'CAST(F1.THROW_CNT  AS DECIMAL(18,6)) AS THROW_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F1.THROW_AMT AS DECIMAL(18,6)) /1000,CAST(F1.THROW_AMT AS DECIMAL(18,6))) AS THROW_AMT, '+
                  'CAST(F2.SALES_CNT  AS DECIMAL(18,6)) AS LSALES_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.SALES_AMT AS DECIMAL(18,6)) /1000, CAST(F2.SALES_AMT AS DECIMAL(18,6))) AS LSALES_AMT, '+
                  'CAST(F2.ORDER_CNT  AS DECIMAL(18,6)) AS LORDER_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.ORDER_AMT AS DECIMAL(18,6))/1000,CAST(F2.ORDER_AMT AS DECIMAL(18,6))) AS LORDER_AMT, '+
                  'CAST(F2.THROW_CNT  AS DECIMAL(18,6)) AS LTHROW_CNT, '+                  
				  'DECODE('''+FP_AMT_TYPE+''',''T'',CAST(F2.THROW_AMT AS DECIMAL(18,6)) /1000, CAST(F2.THROW_AMT AS DECIMAL(18,6))) AS LTHROW_AMT, '+
                  'CAST(F3.CUST_NUM  AS DECIMAL(18,6)) AS CUST_NUM '+
                  'FROM '+
                  '( '+ SQLSTR_F1 + ' ' +             				  
                  'WHERE TIME_ID IN ('+ FP_TIME_LIST +') '+
                  'AND PRD_ID IN ('+ FP_PRD_LIST +') '+
                  'AND ORG_ID='+ FP_ORG_ID +''+
                  ') F1 LEFT JOIN '+
                  '(  '+ SQLSTR_F2 + ' ' +		  
                  'WHERE TIME_ID IN ('+ FP_LTIME_LIST +') '+
                  'AND PRD_ID IN ('+ FP_PRD_LIST +') '+
                  'AND ORG_ID='+ FP_ORG_ID +' '+
                  ') F2 ON(F1.JOIN_KEY=F2.JOIN_KEY AND F1.PRD_ID=F2.PRD_ID) '+
                  'LEFT JOIN '+
                  '( '+
                  'SELECT TIME_ID,CUST_NUM '+
                  'FROM PMART.BASIC_SFACT '+
                  'WHERE TIME_ID IN ('+ FP_TIME_LIST +') ' +
                  'AND ORG_ID='+ FP_ORG_ID +' '+
                  ') F3 ON(F1.TIME_ID=F3.TIME_ID)'+                     
         ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';        
	EXECUTE IMMEDIATE SQLSTR;
END SP;