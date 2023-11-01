REPLACE PROCEDURE PMART.NEW_TOP_MFACT_1
(FP_AMT_TYPE CHAR(1),FP_TIME_TYPE CHAR(1),FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_ORG_LEVEL NUMBER,FP_ORG_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR(1),FP_PRD_LEVEL NUMBER,FP_PRD_ID VARCHAR(7))
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR1  VARCHAR(10000); 	
   DECLARE SQLSTR2  VARCHAR(10000); 	
   DECLARE SQLSTR3  VARCHAR(30000); 	
   DECLARE SQLSTRFINAL VARCHAR(64000); 	
   DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TOP_MFACT_1'); 
   CASE
      WHEN FP_TIME_TYPE='D' AND FP_ORG_LEVEL=3 AND FP_PRD_TYPE='P' AND FP_PRD_LEVEL=3  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';             
      WHEN FP_TIME_TYPE='D' AND FP_ORG_LEVEL=3 AND FP_PRD_TYPE='R' AND FP_PRD_LEVEL=2  THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_MFACT_DETAIL';
      WHEN FP_PRD_TYPE='L' THEN 
            SET V_TABLE_NAME = 'PMART.BASIC_MLFACT';
      ELSE
             SET V_TABLE_NAME = 'PMART.BASIC_MFACT';
   END CASE;
   SET SQLSTR1 = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TOP_MFACT_1  AS('+
                'SELECT '+
                'F1.TIME_ID AS TIME_ID, '+
                'F1.ORG_ID AS ORG_ID, ';
				 IF FP_AMT_TYPE='T' THEN
				SET SQLSTR2 =  'CAST(F1.SALES_CNT AS DECIMAL(18,6)) AS SALES_CNT, '+
                'CAST(F1.SALES_AMT AS DECIMAL(18,6)) / 1000 AS SALES_AMT, '+
                'CAST(F1.ORDER_CNT AS DECIMAL(18,6)) AS ORDER_CNT, '+
                'CAST(F1.ORDER_AMT AS DECIMAL(18,6)) / 1000 AS ORDER_AMT, '+
                'CAST(F1.THROW_CNT AS DECIMAL(18,6)) AS THROW_CNT, '+
                'CAST(F1.THROW_AMT AS DECIMAL(18,6)) / 1000 AS THROW_AMT, '+
                'CAST(F2.SALES_CNT AS DECIMAL(18,6)) AS LSALES_CNT, '+
                'CAST(F2.SALES_AMT AS DECIMAL(18,6)) / 1000 AS LSALES_AMT, '+
                'CAST(F2.ORDER_CNT AS DECIMAL(18,6)) AS LORDER_CNT, '+
                'CAST(F2.ORDER_AMT AS DECIMAL(18,6)) / 1000 AS LORDER_AMT, '+
                'CAST(F2.THROW_CNT AS DECIMAL(18,6)) AS LTHROW_CNT, '+
                'CAST(F2.THROW_AMT AS DECIMAL(18,6)) / 1000 AS LTHROW_AMT, ';
				 ELSE
				 SET SQLSTR2 =  'CAST(F1.SALES_CNT AS DECIMAL(18,6)) AS SALES_CNT, '+
                'CAST(F1.SALES_AMT AS DECIMAL(18,6)) AS SALES_AMT, '+
                'CAST(F1.ORDER_CNT AS DECIMAL(18,6)) AS ORDER_CNT, '+
                'CAST(F1.ORDER_AMT AS DECIMAL(18,6)) AS ORDER_AMT, '+
                'CAST(F1.THROW_CNT AS DECIMAL(18,6)) AS THROW_CNT, '+
                'CAST(F1.THROW_AMT AS DECIMAL(18,6)) AS THROW_AMT, '+
                'CAST(F2.SALES_CNT AS DECIMAL(18,6)) AS LSALES_CNT, '+
                'CAST(F2.SALES_AMT AS DECIMAL(18,6)) AS LSALES_AMT, '+
                'CAST(F2.ORDER_CNT AS DECIMAL(18,6)) AS LORDER_CNT, '+
                'CAST(F2.ORDER_AMT AS DECIMAL(18,6)) AS LORDER_AMT, '+
                'CAST(F2.THROW_CNT AS DECIMAL(18,6)) AS LTHROW_CNT, '+
                'CAST(F2.THROW_AMT AS DECIMAL(18,6)) AS LTHROW_AMT, ';
				 END IF;
                SET SQLSTR3 = 'CAST(F3.CUST_NUM AS DECIMAL(18,6)) AS CUST_NUM '+
                'FROM '+
                '( '+
                'SELECT DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
                'TIME_ID,ORG_ID,SALES_CNT,SALES_AMT,ORDER_CNT,ORDER_AMT,'+
                'THROW_CNT,THROW_AMT '+
                'FROM '+ V_TABLE_NAME + ' '+
                'WHERE '+
                'TIME_ID IN ('+ FP_TIME_LIST +') '+
                ' AND ORG_ID IN  ('+ FP_ORG_LIST +') '+
                ' AND PRD_ID='''+ FP_PRD_ID +''' '+
                ')F1 LEFT JOIN '+
                '( '+
                'SELECT  DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY, '+
                'TIME_ID,ORG_ID,PRD_ID,SALES_CNT,SALES_AMT,ORDER_CNT,ORDER_AMT,'+
                'THROW_CNT,THROW_AMT '+
                'FROM '+ V_TABLE_NAME + ' '  +
                'WHERE '+
                'TIME_ID IN ('+ FP_LTIME_LIST +') '+
                ' AND ORG_ID IN  ('+ FP_ORG_LIST +') '+
                ' AND PRD_ID='''+ FP_PRD_ID +''' '+
                ') F2 '+
                'ON ' +
                '(F1.JOIN_KEY=F2.JOIN_KEY AND F1.ORG_ID=F2.ORG_ID) '+
                'LEFT JOIN '+
                '( '+
                'SELECT  '+
                'TIME_ID,ORG_ID,CUST_NUM '+
                'FROM PMART.BASIC_SFACT '+
                'WHERE TIME_ID IN ('+ FP_TIME_LIST + ') '+
                'AND ORG_ID IN ('+ FP_ORG_LIST + ') '+
                ') F3 ON (F1.TIME_ID=F3.TIME_ID AND F1.ORG_ID=F3.ORG_ID) '+
              ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';      
  	SET SQLSTRFINAL = SQLSTR1+SQLSTR2+SQLSTR3;
	EXECUTE IMMEDIATE SQLSTRFINAL;
END SP;