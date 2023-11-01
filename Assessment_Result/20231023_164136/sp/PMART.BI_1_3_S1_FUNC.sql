REPLACE PROCEDURE PMART.BI_1_3_S1_FUNC(P_TIME_ID NUMBER,P_LEVEL NUMBER,P_ORG_ID NUMBER,P_PRD_ID NUMBER,P_PRD_VALUE NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(1000);
   DECLARE ORG_STR VARCHAR(400);
   DECLARE TAB_STR VARCHAR(100);
   CALL PMART.P_DROP_TABLE ('#VT_BI_1_3_S1_FUNC');          
   IF (P_LEVEL=0) THEN  
      SET ORG_STR = ' SELECT DISTINCT STORE_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=-1 ';
   END IF;
   IF (P_LEVEL=1) THEN  
      SET ORG_STR = ' SELECT DISTINCT STORE_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID= '+P_ORG_ID;
   END IF;
   IF (P_LEVEL=2) THEN  
	  SET ORG_STR = ' SELECT DISTINCT STORE_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID= '+P_ORG_ID;
   END IF;
   IF (P_PRD_VALUE=1) THEN  
      SET TAB_STR = ' PMART.BASIC_MFACT ';
   END IF;
   IF (P_PRD_VALUE=2) THEN  
      SET TAB_STR = ' PMART.BASIC_MLFACT ';
   END IF;
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_BI_1_3_S1_FUNC  AS('+
               'SELECT TIME_ID,ORG_ID,PRD_ID  '+
               ',CAST(ORDER_CNT AS NUMBER) AS ORDER_CNT  '+
               ',CAST(SALES_CNT AS NUMBER) AS SALES_CNT  '+
               ',CAST(THROW_CNT AS NUMBER) AS THROW_CNT  '+
               ',CAST(INPRD_CNT AS NUMBER) AS INPRD_CNT  '+
               ' FROM ' + TAB_STR +
               ' WHERE PRD_ID = '+P_PRD_ID+' '+
               ' AND ORG_ID IN ('+ORG_STR+') '+
               ' AND TIME_ID = '+P_TIME_ID +
      ') WITH DATA PRIMARY  CHARINDEX(ORG_ID , PRD_ID) ON COMMIT PRESERVE ROWS;'; 	  
	EXECUTE IMMEDIATE SQLSTR;
END SP;