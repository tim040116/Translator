REPLACE PROCEDURE PMART.POSN_SFACT_1_4(
P_TIME_TYPE VARCHAR(400), 
P_TIME_LIST VARCHAR(400), 
P_ORG_LEVEL NUMBER, 
P_ORG_LIST  VARCHAR(400), 
P_MMA_LEVEL NUMBER, 
P_MMA_ID    VARCHAR(400) 
)									 
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(4000);
   DECLARE V_MAST_SQL VARCHAR(4000);
   DECLARE V_REMD_SQL VARCHAR(4000); 
   DECLARE V_LATEST_SHOPDIST VARCHAR(4000);
   CALL PMART.P_DROP_TABLE('#VT_POSN_SFACT_1_4');
   SET V_REMD_SQL = ' ';
   SET V_MAST_SQL = ' ';
   SET V_LATEST_SHOPDIST = 'SELECT DISTINCT OSTORE_NO AS ORG_ID, '+
                           'CASE '+
                           'WHEN LOCAL0=''A'' THEN 10000001 '+
                           'WHEN LOCAL0=''B'' THEN 10000002 '+
                           'WHEN LOCAL0=''C'' THEN 10000003 '+
                           'WHEN LOCAL0=''D'' THEN 10000004 '+
                           'WHEN LOCAL0=''E'' THEN 10000005 '+
                           'WHEN LOCAL0=''F'' THEN 10000006 '+
                           'WHEN LOCAL0=''G'' THEN 10000007 '+
                           'WHEN LOCAL0=''H'' THEN 10000008 '+
                           'WHEN LOCAL0=''I'' THEN 10000009 '+
                           'WHEN LOCAL0=''J'' THEN 10000010 '+
                           'END AS MMA_ID, '+
                           'CASE '+
                           'WHEN SEC_1_LOCAL=''A'' THEN 10000001 '+
                           'WHEN SEC_1_LOCAL=''B'' THEN 10000002 '+
                           'WHEN SEC_1_LOCAL=''C'' THEN 10000003 '+
                           'WHEN SEC_1_LOCAL=''D'' THEN 10000004 '+
                           'WHEN SEC_1_LOCAL=''E'' THEN 10000005 '+
                           'WHEN SEC_1_LOCAL=''F'' THEN 10000006 '+
                           'WHEN SEC_1_LOCAL=''G'' THEN 10000007 '+
                           'WHEN SEC_1_LOCAL=''H'' THEN 10000008 '+
                           'WHEN SEC_1_LOCAL=''I'' THEN 10000009 '+
                           'WHEN SEC_1_LOCAL=''J'' THEN 10000010 '+
                           'ELSE 0 '+
                           'END AS SUB_MMA_ID '+
                           ' FROM PMART.LAST_SHOPDIST L, PMART.LATEST_ORG_DIM O WHERE L.OSTORE_NO = O.OSTORE_ID ';
   IF P_ORG_LEVEL = 0 THEN SET V_LATEST_SHOPDIST = V_LATEST_SHOPDIST + ' AND O.TOT_ID = '+P_ORG_LIST; END IF;
   IF P_ORG_LEVEL = 1 THEN SET V_LATEST_SHOPDIST = V_LATEST_SHOPDIST + ' AND O.DEPT_ID = '+P_ORG_LIST; END IF;
   IF P_ORG_LEVEL = 2 THEN SET V_LATEST_SHOPDIST = V_LATEST_SHOPDIST + ' AND O.BRANCH_ID = '+P_ORG_LIST; END IF;
   IF P_ORG_LEVEL = 3 THEN SET V_LATEST_SHOPDIST = V_LATEST_SHOPDIST + ' AND O.STORE_ID = '+P_ORG_LIST; END IF;
   IF P_TIME_TYPE = 'D' THEN
		SET V_REMD_SQL =' SELECT B.L_DAY_ID AS TIME_ID,L.SUB_MMA_ID AS MMA_ID ,SUM(B.UPLOAD_STNUM) AS STNUM_STORE_NUM '+
                     ' FROM PMART.REMD_FACT B, ('+ V_LATEST_SHOPDIST +') L '+
                     ' WHERE B.L_DAY_ID IN (  '+ P_TIME_LIST +') ' +
                     ' AND B.OSTORE_ID = L.ORG_ID '+
                     ' AND L.MMA_ID = '+ P_MMA_ID +' '+
                     ' GROUP BY B.L_DAY_ID,L.SUB_MMA_ID';
   END IF;
   IF P_TIME_TYPE = 'W' THEN
		SET V_REMD_SQL =' SELECT Y.L_WEEK_ID AS TIME_ID,L.SUB_MMA_ID AS MMA_ID ,SUM(B.UPLOAD_STNUM) AS STNUM_STORE_NUM '+
                     ' FROM PMART.REMD_FACT B,PMART.YMWD_TIME_W2 Y, ('+ V_LATEST_SHOPDIST +') L '+
                     ' WHERE B.L_DAY_ID = Y.L_DAY_ID AND Y.L_WEEK_ID IN (  '+ P_TIME_LIST +') ' +
                     ' AND B.OSTORE_ID = L.ORG_ID '+
                     ' AND L.MMA_ID = '+ P_MMA_ID +' '+
                     ' AND B.L_DAY_ID <> TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                     ' GROUP BY Y.L_WEEK_ID,L.SUB_MMA_ID';
   END IF;
   IF P_TIME_TYPE = 'M' THEN
		SET V_REMD_SQL =
                     ' SELECT Y.L_MONTH_ID AS TIME_ID,L.SUB_MMA_ID AS MMA_ID ,SUM(B.UPLOAD_STNUM) AS STNUM_STORE_NUM '+
                     ' FROM PMART.REMD_FACT B,PMART.YMWD_TIME_W2 Y, ('+ V_LATEST_SHOPDIST +') L '+
                     ' WHERE B.L_DAY_ID = Y.L_DAY_ID AND Y.L_MONTH_ID IN (  '+ P_TIME_LIST +') ' +
                     ' AND B.OSTORE_ID = L.ORG_ID '+
                     ' AND L.MMA_ID = '+ P_MMA_ID +' '+
                     ' AND B.L_DAY_ID <> TO_CHAR(CURRENT_DATE,''YYYYMMDD'') '+
                     ' GROUP BY Y.L_MONTH_ID,L.SUB_MMA_ID';
   END IF;
   SET V_MAST_SQL =' SELECT A.TIME_ID AS TIME_ID, '+
                ' Y.SUB_MMA_ID AS MMA_ID, '+
                ' BIT_EXTRACT(BIT_AND(A.MAST_STORE_NUM,Y.MASK)) AS MAST_STORE_NUM '+
                ' FROM PMART.BASIC_MAST_FACT A , '+
                ' ( SELECT SUB_MMA_ID,BIT_GEN_AGGT(T2.OSTORE_BIT_SEQ) AS MASK FROM ('+ V_LATEST_SHOPDIST +') T1, PMART.ORG_BIT_MAPPING T2 '+
                ' WHERE T1.ORG_ID = T2.OSTORE_ID AND MMA_ID = '+ P_MMA_ID +' GROUP BY SUB_MMA_ID '+
                ' ) Y WHERE A.TIME_ID IN (  '+ P_TIME_LIST +'  )';
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSN_SFACT_1_4  AS('+
               ' SELECT '+
               ' A.TIME_ID AS TIME_ID,    '+
               ' 0 AS ORG_ID,    '+
               ' A.MMA_ID AS MMA_ID, '+
               ' A.MAST_STORE_NUM AS MAST_STORE_NUM,   '+
               ' B.STNUM_STORE_NUM AS STNUM_STORE_NUM '+
               ' FROM '+
               ' ('+ V_MAST_SQL +') A, ('+V_REMD_SQL+') B  '+
               '  WHERE '+
               '  A.TIME_ID = B.TIME_ID AND A.MMA_ID = B.MMA_ID '+
               ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;'; 
   EXECUTE IMMEDIATE SQLSTR;
END SP;