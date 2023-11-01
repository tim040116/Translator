REPLACE PROCEDURE PMART.NEW_TPO_STFACT_2
(FP_TIME_TYPE CHAR(1),FP_TIME_LIST VARCHAR(2000),FP_LTIME_LIST VARCHAR(2000),
FP_PRD_TYPE CHAR(1),FP_PRD_LEVEL NUMBER,FP_PRD_LIST VARCHAR(8000),
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(64000);
DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_NEW_TPO_STFACT_2'); 
   CASE
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='P' AND FP_PRD_LEVEL=2 THEN  
            SET V_TABLE_NAME = 'PMART.BASIC_STFACT_DETAIL';
      WHEN (FP_TIME_TYPE='D' OR FP_TIME_TYPE='I' ) AND FP_PRD_TYPE='R' AND FP_PRD_LEVEL=1  THEN  
            SET V_TABLE_NAME = 'PMARTB.ASIC_STFACT_DETAIL';
      WHEN FP_PRD_TYPE='L' THEN 
            SET V_TABLE_NAME = 'PMART.BASIC_STLFACT';
      ELSE  
            SET V_TABLE_NAME = 'PMART.BASIC_STFACT';
    END CASE;
   SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_NEW_TPO_STFACT_2  AS('+
      'SELECT  '+
      'F1.TIME_ID AS TIME_ID, '+
      'F1.PRD_ID AS PRD_ID, '+
      'BIT_EXTRACT(BIT_AND(F1.SALES_STORE_NUM,Y.MASK)) AS SALES_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.ORDER_STORE_NUM,Y.MASK)) AS ORDER_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.THROW_STORE_NUM,Y.MASK)) AS THROW_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(BIT_OR(F1.SALES_STORE_NUM,F1.ORDER_STORE_NUM),'+
      'Y.MASK)) AS SALES_ORDER_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F2.MAST_STORE_NUM,Y.MASK)) AS MAST_STORE_NUM,  '+
      'BIT_EXTRACT(BIT_AND(F3.MAST_STORE_NUM,Y.MASK)) AS LMAST_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.INPRD_STORE_NUM,Y.MASK)) AS INPRD_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.RETPRD_STORE_NUM,Y.MASK)) AS RETPRD_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.TRANSPRD_STORE_NUM,Y.MASK)) AS TRANSPRD_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F1.TRANSPRD_IN_STORE_NUM,Y.MASK)) AS TRANSPRD_IN_STORE_NUM, '+
      'BIT_EXTRACT(BIT_AND(F2.STNUM_STORE_NUM,Y.MASK)) AS STNUM_STORE_NUM,  '+
      'BIT_EXTRACT(BIT_AND(F3.STNUM_STORE_NUM,Y.MASK)) AS LSTNUM_STORE_NUM  '+
      'FROM '+
      '( '+
      'SELECT '+
      'DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY,'+
      'TIME_ID AS TIME_ID, '+
      'PRD_ID AS PRD_ID, '+
      'SALES_STORE_NUM AS SALES_STORE_NUM, '+
      'ORDER_STORE_NUM AS ORDER_STORE_NUM, '+
      'THROW_STORE_NUM AS THROW_STORE_NUM, '+
      'INPRD_STORE_NUM AS INPRD_STORE_NUM, '+
      'RETPRD_STORE_NUM AS RETPRD_STORE_NUM, '+
      'TRANSPRD_STORE_NUM AS TRANSPRD_STORE_NUM, '+
      'TRANSPRD_IN_STORE_NUM AS TRANSPRD_IN_STORE_NUM  '+
      'FROM '+ V_TABLE_NAME +' '+
      'WHERE TIME_ID IN ('+ FP_TIME_LIST +') '+
      'AND PRD_ID IN ('+ FP_PRD_LIST +') '+
      ') F1, '+
      '( '+
      'SELECT TIME_ID,MAST_STORE_NUM,STNUM_STORE_NUM FROM '+
      'PMART.BASIC_MAST_FACT '+
      'WHERE TIME_ID IN ('+ FP_TIME_LIST +') '+
      ')F2, '+
      '( '+
      'SELECT '+
      'DENSE_RANK() OVER(ORDER BY TIME_ID) AS JOIN_KEY,'+
      'TIME_ID,MAST_STORE_NUM,STNUM_STORE_NUM FROM '+
      'PMART.BASIC_MAST_FACT '+
      'WHERE TIME_ID IN ('+ FP_LTIME_LIST +') '+
      ') F3, PMART.LAST_ORG_DIM_MASK Y '+
      'WHERE '+
      'F1.TIME_ID=F2.TIME_ID '+
      'AND F1.JOIN_KEY=F3.JOIN_KEY '+
      'AND Y.ORG_ID='+ FP_ORG_ID +' '+
     ') WITH DATA UNIQUE PRIMARY  CHARINDEX(PRD_ID , TIME_ID) ON COMMIT PRESERVE ROWS;';       
    DELETE FROM PMART.T1  WHERE F1=2;     
	INSERT INTO PMART.T1(2, SQLSTR );    	
	EXECUTE IMMEDIATE SQLSTR;
END SP;