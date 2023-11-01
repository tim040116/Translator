REPLACE PROCEDURE PMART.BASIC_EXTEND_T
(FP_TIME_TYPE CHAR(1) CASESPECIFIC,FP_TIME_ID NUMBER, FP_ORG_ID NUMBER, FP_MMA_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000); 
DECLARE V_TIME_ID1 NUMBER;
DECLARE V_TIME_ID2 NUMBER;
  CALL PMART.P_DROP_TABLE ('#VT_BASIC_EXTEND_T');
   IF ( (FP_MMA_ID=0 OR FP_MMA_ID=-1) AND FP_ORG_ID<>0 ) THEN
           CALL PMART.BASIC_TIME(FP_TIME_TYPE,FP_TIME_ID,FP_ORG_ID);
           SET SQLSTR = 
               'CREATE MULTISET VOLATILE TABLE #VT_BASIC_EXTEND_T  AS ('+ 
                   ' SELECT TIME_ID, '+
                   '     TIME_NM, '+
                   '     LTIME_ID, '+
                   '     LTIME_NM, '+
                   '     MAST_STORE_NUM, '+
                   '     WEATHER,'+ 
                   '     LWEATHER,'+ 
                   '     WEATH_NAME,'+ 
                   '     LWEATH_NAME '+ 
                   'FROM #VT_BASIC_TIME '+            
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
              	EXECUTE IMMEDIATE SQLSTR;                
   END IF;
   IF (FP_MMA_ID>0 AND (FP_ORG_ID=0 OR FP_ORG_ID=-1)) THEN
        CALL PMART.BASIC_TIME(FP_TIME_TYPE,FP_TIME_ID,FP_ORG_ID);
        SELECT MIN(TIME_ID), MAX(TIME_ID) INTO V_TIME_ID1, V_TIME_ID2  FROM #VT_BASIC_TIME;
        CALL PMART.SHOP_ST_NUM_FUNC(V_TIME_ID1, V_TIME_ID2, FP_ORG_ID);
        SET SQLSTR = 
               'CREATE MULTISET VOLATILE TABLE #VT_BASIC_EXTEND_T  AS('+ 
               'SELECT B.TIME_ID,'+
               '       B.TIME_NM,'+
               '       B.LTIME_ID,'+
               '       B.LTIME_NM,'+
               '       NVL(A.STORE_NUM,0) AS MAST_STORE_NUM,'+
               '       B.WEATHER,'+
               '       B.LWEATHER,'+
               '       B.WEATH_NAME,'+
               '       B.LWEATH_NAME'+
               '  FROM (SELECT TIME_ID, STORE_NUM'+
               '          FROM #VT_SHOP_ST_NUM_FUNC '+
               '         WHERE MMA_ID = '+FP_MMA_ID +' ) A '+
               '       RIGHT JOIN (SELECT * FROM #VT_BASIC_TIME) B ON A.TIME_ID = B.TIME_ID '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
              	EXECUTE IMMEDIATE SQLSTR;           
   END IF;
   IF (FP_MMA_ID>0 AND FP_ORG_ID>0) THEN
        CALL PMART.BASIC_TIME(FP_TIME_TYPE,FP_TIME_ID,-1);
         SELECT MIN(TIME_ID), MAX(TIME_ID) INTO V_TIME_ID1, V_TIME_ID2  FROM #VT_BASIC_TIME;
        CALL PMART.SHOP_ST_NUM_FUNC(V_TIME_ID1, V_TIME_ID2, -1);
        SET SQLSTR = 
               'CREATE MULTISET VOLATILE TABLE #VT_BASIC_EXTEND_T  AS('+ 
               'SELECT B.TIME_ID,'+
               '       B.TIME_NM,'+
               '       B.LTIME_ID,'+
               '       B.LTIME_NM,'+
               '       NVL(A.STORE_NUM,0) AS MAST_STORE_NUM,'+
               '       B.WEATHER,'+
               '       B.LWEATHER,'+
               '       B.WEATH_NAME,'+
               '       B.LWEATH_NAME'+
               '  FROM (SELECT TIME_ID, STORE_NUM'+
               '          FROM #VT_SHOP_ST_NUM_FUNC '+
                '         WHERE MMA_ID = '+FP_MMA_ID +' ) A '+
               '       RIGHT JOIN (SELECT * FROM #VT_BASIC_TIME) B ON A.TIME_ID = B.TIME_ID '+
               ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
              	EXECUTE IMMEDIATE SQLSTR;    
   END IF;
END SP;