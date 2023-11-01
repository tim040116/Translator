REPLACE PROCEDURE PMART.POSI1_2_ADD_TKSL
(IN P_TIME_ID VARCHAR(400), 
 IN  P_PRD_ID VARCHAR(400), 
 IN  P_SALES_AMT NUMBER, 
 IN P_DAY_TYPE VARCHAR(1)
)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(8000); 	
   DECLARE V_SALES_AMT NUMBER DEFAULT -1;
   DECLARE V_TIME_ID  VARCHAR(400) ;
   DECLARE V_PRD_ID   VARCHAR(400) ;
   DECLARE V_DAY_TYPE VARCHAR(1) ;
   DECLARE STORE_CS CURSOR FOR STORE_SQL;
   CALL PMART.P_DROP_TABLE ('#VT_POSI1_2_ADD_TKSL');    
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSI1_2_ADD_TKSL  
                (TIME_ID NUMBER, PRD_ID NUMBER,DAY_TYPE VARCHAR(1), SALES_AMT NUMBER 
                ) NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
   EXECUTE IMMEDIATE SQLSTR;    
      SET SQLSTR ='  INSERT INTO #VT_POSI1_2_ADD_TKSL               ' +
                  'SELECT A.TIME_ID,B.PRD_ID,'''+P_DAY_TYPE+'''   ' +
                  ' , NULL  FROM (                                  ' +
                  'SELECT T1.TIME_ID                                ' +
                  '  FROM PMART.BASIC_MFACT_BUDGET T1               ' +
                  ' WHERE T1.TIME_ID IN(  '+P_TIME_ID+'           ' +
                  ' ) AND T1.PRD_ID  IN(  '+P_PRD_ID+'            ' +
                  ' )  GROUP BY T1.TIME_ID                          ' +
                  ')A,                                              ' +
                  '(                                                ' +
                  'SELECT T2.PRD_ID                                 ' +
                  '  FROM PMART.BASIC_MFACT_BUDGET T2               ' +
                  ' WHERE T2.TIME_ID IN(  '+P_TIME_ID+'           ' +
                  ' ) AND T2.PRD_ID  IN(  '+P_PRD_ID+'            ' +
                  ' )  GROUP BY T2.PRD_ID                           ' +
                  ')B                                               ' +
                  '; ' ;   
      EXECUTE IMMEDIATE SQLSTR;    
END SP;