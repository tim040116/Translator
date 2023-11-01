REPLACE PROCEDURE PMART.POSN_SFACT_1_4_ALL(
FP_TIME_TYPE CHAR,         
FP_TIME_LIST VARCHAR(400), 
FP_MMA_LEVEL NUMBER,       
FP_MMA_ID    VARCHAR(400)  
)
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE V_MAST_SQL VARCHAR(2000);
   DECLARE V_REMD_SQL VARCHAR(2000); 
   DECLARE SQLSTR     VARCHAR(4000);
   CALL PMART.P_DROP_TABLE ('#VT_POSN_SFACT_1_4_ALL'); 
   SET V_REMD_SQL = ' ';
   IF FP_MMA_LEVEL = 0 THEN 
     IF FP_TIME_TYPE = 'D' THEN
        SET V_REMD_SQL =' SELECT A.TIME_ID AS TIME_ID, 0 AS MMA_ID, '+
                        ' BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK)) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.LAST_ORG_DIM_MASK Y '+
                        ' WHERE '+
                        ' A.TIME_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND Y.ORG_ID = -1 AND A.TIME_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') ';
     END IF;
     IF FP_TIME_TYPE = 'W' THEN
        SET V_REMD_SQL =' SELECT B.L_WEEK_ID AS TIME_ID, 0 AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.YMWD_TIME_W2 B, PMART.LAST_ORG_DIM_MASK Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_WEEK_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND Y.ORG_ID = -1 AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_WEEK_ID ';
     END IF;
     IF FP_TIME_TYPE = 'M' THEN
        SET V_REMD_SQL =' SELECT B.L_MONTH_ID AS TIME_ID, 0 AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.YMWD_TIME_W2 B, PMART.LAST_ORG_DIM_MASK Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_MONTH_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND Y.ORG_ID = -1 AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_MONTH_ID ';
     END IF;
   END IF;
   IF FP_MMA_LEVEL = 1 THEN 
     IF FP_TIME_TYPE = 'D' THEN
        SET V_REMD_SQL =' SELECT A.TIME_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK)) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A ,'+
                        ' ( SELECT MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID GROUP BY MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND A.TIME_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') ';
     END IF;
     IF FP_TIME_TYPE = 'W' THEN
        SET V_REMD_SQL =' SELECT B.L_WEEK_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , YMWD_TIME_W2 B, '+
                        ' ( SELECT MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID GROUP BY MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_WEEK_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_WEEK_ID,Y.MMA_ID ';
     END IF;
     IF FP_TIME_TYPE = 'M' THEN
        SET V_REMD_SQL =' SELECT B.L_MONTH_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.YMWD_TIME_W2 B, '+
                        ' ( SELECT MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID GROUP BY MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_MONTH_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_MONTH_ID,Y.MMA_ID ';
     END IF;
   END IF;
   IF FP_MMA_LEVEL = 2 THEN 
     IF FP_TIME_TYPE = 'D' THEN
        SET V_REMD_SQL =' SELECT A.TIME_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK)) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A ,'+
                        ' ( SELECT SUB_MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID AND MMA_ID = '+ FP_MMA_ID +' GROUP BY SUB_MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND A.TIME_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') ';
     END IF;
     IF FP_TIME_TYPE = 'W' THEN
        SET V_REMD_SQL =' SELECT B.L_WEEK_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.YMWD_TIME_W2 B, '+
                        ' ( SELECT SUB_MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID AND MMA_ID = '+ FP_MMA_ID +' GROUP BY SUB_MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_WEEK_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_WEEK_ID,Y.MMA_ID ';
     END IF;
     IF FP_TIME_TYPE = 'M' THEN
        SET V_REMD_SQL =' SELECT B.L_MONTH_ID AS TIME_ID, Y.MMA_ID AS MMA_ID, '+
                        ' SUM(BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,Y.MASK))) AS STNUM_STORE_NUM '+
                        ' FROM PMART.BASIC_MAST_FACT A , PMART.YMWD_TIME_W2 B, '+
                        ' ( SELECT SUB_MMA_ID AS MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                        ' WHERE T1.ORG_ID = T2.OSTORE_ID AND MMA_ID = '+ FP_MMA_ID +' GROUP BY SUB_MMA_ID ) Y '+
                        ' WHERE '+
                        ' A.TIME_ID = B.L_DAY_ID AND B.L_MONTH_ID IN ('+ FP_TIME_LIST +') '+
                        ' AND B.L_DAY_ID < TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                        ' GROUP BY B.L_MONTH_ID,Y.MMA_ID ';
     END IF;
   END IF;
   IF FP_MMA_LEVEL = 0 THEN 
     SET V_MAST_SQL =' SELECT A.TIME_ID AS TIME_ID, '+
                  ' 0 AS MMA_ID, '+
                  ' BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,Y.MASK)) AS MAST_STORE_NUM '+
                  ' FROM PMART.BASIC_MAST_FACT AS A , PMART.LAST_ORG_DIM_MASK Y '+
                  ' WHERE A.TIME_ID IN (  '+ FP_TIME_LIST +'  ) AND Y.ORG_ID = -1 ';
   END IF;
   IF FP_MMA_LEVEL = 1 THEN 
     SET V_MAST_SQL =' SELECT A.TIME_ID AS TIME_ID, '+
                  ' Y.MMA_ID AS MMA_ID, '+
                  ' BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,Y.MASK)) AS MAST_STORE_NUM '+
                  ' FROM PMART.BASIC_MAST_FACT A , '+
                  ' ( SELECT MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                  ' WHERE T1.ORG_ID = T2.OSTORE_ID GROUP BY MMA_ID '+
                  ' ) Y WHERE A.TIME_ID IN (  '+ FP_TIME_LIST +'  )';
   END IF;
   IF FP_MMA_LEVEL = 2 THEN
     SET V_MAST_SQL =' SELECT A.TIME_ID AS TIME_ID, '+
                  ' Y.SUB_MMA_ID AS MMA_ID, '+
                  ' BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,Y.MASK)) AS MAST_STORE_NUM '+
                  ' FROM PMART.BASIC_MAST_FACT A , '+
                  ' ( SELECT SUB_MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM PMART.POSN_1_4V_MMA T1, PMART.ORG_BIT_MAPPING T2 '+
                  ' WHERE T1.ORG_ID = T2.OSTORE_ID AND MMA_ID = '+ FP_MMA_ID +' GROUP BY SUB_MMA_ID '+
                  ' ) Y WHERE A.TIME_ID IN (  '+ FP_TIME_LIST +'  )';
   END IF;
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_SFACT_1_4_ALL  AS('+
      'SELECT '+
      'A.TIME_ID AS TIME_ID,    '+
      '0 AS ORG_ID,    '+
      'A.MMA_ID AS MMA_ID, '+
      'A.MAST_STORE_NUM AS MAST_STORE_NUM,   '+   
      'B.STNUM_STORE_NUM AS STNUM_STORE_NUM '+    
      'FROM '+
      '('+ V_MAST_SQL +') A, ('+V_REMD_SQL+') B  '+
      ' WHERE '+
      ' A.TIME_ID = B.TIME_ID AND A.MMA_ID = B.MMA_ID '+
      ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';    
  EXECUTE IMMEDIATE SQLSTR;
END SP;