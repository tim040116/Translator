REPLACE PROCEDURE PMART.SHOP_ST_NUM_FUNC
(I_TIME_ID1 NUMBER, I_TIME_ID2 NUMBER, I_ORG_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000) Collate Chinese_Taiwan_Stroke_CI_AS; 
DECLARE V_ORG_ID NUMBER;
  CALL PMART.P_DROP_TABLE ('#VT_SHOP_ST_NUM_FUNC');  
  IF I_ORG_ID IS NULL THEN 
     SET V_ORG_ID = -1;
  ELSE
     SET V_ORG_ID = I_ORG_ID;
  END IF;
   IF (I_TIME_ID1>19000000) THEN
           SET SQLSTR =  'CREATE MULTISET VOLATILE TABLE #VT_SHOP_ST_NUM_FUNC  AS('+      
                    'SELECT * FROM ('+
                    '    SELECT S.TIME_ID,'+
                    '           S.MMA_ID,'+
                    '           T.PARENT_NAME AS MMA_NM,'+
                    '           BIT_EXTRACT(BIT_AND(S.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                    '      FROM PMART.BASIC_MAST_FACT_SHOP S, (SELECT DISTINCT MMA_ID, PARENT_NAME FROM PMART.MSTV_SHOP_LIST) T,'+
                    '           (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                    '     WHERE S.TIME_ID >= '+I_TIME_ID1+
                    '       AND S.TIME_ID <='+ I_TIME_ID2+
                    '       AND S.MMA_ID = T.MMA_ID  '+
                    '     UNION '+
                    '     SELECT B.TIME_ID,'+
                    '            19999999 AS MMA_ID,'+
                    '            '''+'全單位'+'''  AS MMA_NM,'+
                    '            BIT_EXTRACT(BIT_AND(B.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                    '       FROM PMART.BASIC_MAST_FACT B, (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                    '      WHERE B.TIME_ID >='+ I_TIME_ID1+
                    '        AND B.TIME_ID <='+ I_TIME_ID2+
                    ' ) X  '+ 
               ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
               	EXECUTE IMMEDIATE SQLSTR; 
   END IF ;
   IF (I_TIME_ID1>190000 AND I_TIME_ID1<999999 AND TO_NUMBER(SUBSTRING(TO_CHAR(I_TIME_ID1),5))<=12 ) THEN
           SET SQLSTR = 
               'CREATE MULTISET VOLATILE TABLE #VT_SHOP_ST_NUM_FUNC  AS('+      
                     'SELECT * FROM  ('
                     '    SELECT S.TIME_ID,'+
                     '           S.MMA_ID,'+
                     '           T.PARENT_NAME AS MMA_NM,'+
                     '           BIT_EXTRACT(BIT_AND(S.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                     '      FROM PMART.BASIC_MAST_FACT_SHOP S, (SELECT DISTINCT MMA_ID, PARENT_NAME FROM PMART.MSTV_SHOP_LIST) T,'+
                     '           (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                     '     WHERE S.TIME_ID >= '+I_TIME_ID1+
                     '       AND S.TIME_ID <= '+I_TIME_ID2+
                     '       AND TO_NUMBER(SUBSTRING(TO_CHAR(S.TIME_ID),5))<=12'+
                     '       AND S.MMA_ID = T.MMA_ID'+
                     '     UNION '+
                     '     SELECT B.TIME_ID,'+
                     '            19999999 AS MMA_ID,'+
                     '             '''+'全單位'+''' AS MMA_NM,'+
                     '            BIT_EXTRACT(BIT_AND(B.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                     '       FROM PMART.BASIC_MAST_FACT B, (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                     '      WHERE B.TIME_ID >= '+I_TIME_ID1+
                     '        AND B.TIME_ID <= '+I_TIME_ID2+
                     '        AND TO_NUMBER(SUBSTRING(TO_CHAR(B.TIME_ID),5))<=12'+
                     ') X '+
               ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
               	EXECUTE IMMEDIATE SQLSTR; 
   END IF ;
   IF (I_TIME_ID1>190000 AND I_TIME_ID1<999999 AND TO_NUMBER(SUBSTRING(TO_CHAR(I_TIME_ID1),5))>12 ) THEN
           SET SQLSTR = 
               'CREATE MULTISET VOLATILE TABLE #VT_SHOP_ST_NUM_FUNC  AS('+      
                      'SELECT * FROM ('+
                      '    SELECT S.TIME_ID,'+
                      '           S.MMA_ID,'+
                      '           T.PARENT_NAME AS MMA_NM,'+
                      '           BIT_EXTRACT(BIT_AND(S.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                      '      FROM PMART.BASIC_MAST_FACT_SHOP S, (SELECT DISTINCT MMA_ID, PARENT_NAME FROM PMART.MSTV_SHOP_LIST) T,'+
                      '           (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                      '     WHERE S.TIME_ID >= '+I_TIME_ID1+
                      '       AND S.TIME_ID <= '+I_TIME_ID2+
                      '       AND TO_NUMBER(SUBSTRING(TO_CHAR(S.TIME_ID),5))>12'+
                      '       AND S.MMA_ID = T.MMA_ID'+
                      '     UNION'+
                      '     SELECT B.TIME_ID,'+
                      '            19999999 AS MMA_ID,'+
                      '            '''+'全單位'+''' AS MMA_NM,'+
                      '            BIT_EXTRACT(BIT_AND(B.MAST_STORE_NUM,D.MASK)) AS STORE_NUM'+
                      '       FROM PMART.BASIC_MAST_FACT B, (SELECT MASK FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID='+V_ORG_ID+') D'+
                      '      WHERE B.TIME_ID >= '+I_TIME_ID1+
                      '        AND B.TIME_ID <= '+I_TIME_ID2+
                      '        AND TO_NUMBER(SUBSTRING(TO_CHAR(B.TIME_ID),5))>12'+
                      ')  X '+ 
               ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
               	EXECUTE IMMEDIATE SQLSTR; 
   END IF ; 
END SP;