REPLACE PROCEDURE PMART.POSM1_3_FUNC(P_VENDOR VARCHAR(2000),
									 P_DATE_TYPE VARCHAR(2000),
									 P_L_DAY_ID VARCHAR(2000),
									 P_PRD_ID VARCHAR(2000),
									 P_ISTHOUSAND VARCHAR(2000),
									 P_IS_COUNT VARCHAR(2000))
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(10000);
   DECLARE V_SQL VARCHAR(5000);
   DECLARE V_STRING VARCHAR(6000);
   CALL PMART.P_DROP_TABLE ('#VT_POSM1_3_FUNC'); 
   SET V_SQL = 'SUM(M.INPRD_CNT) AS INPRD_CNT, '+
            'SUM(M.INPRD_AMT) AS INPRD_AMT, '+
            'SUM(M.DC_INPRD_CNT) AS DC_INPRD_CNT, '+
            'SUM(M.DC_INPRD_AMT) AS DC_INPRD_AMT, '+
            'SUM(M.RETPRD_CNT) AS RETPRD_CNT, '+
            'SUM(M.RETPRD_AMT) AS RETPRD_AMT, '+
            'SUM(M.DC_RETPRD_CNT) AS DC_RETPRD_CNT, '+
            'SUM(M.DC_RETPRD_AMT) AS DC_RETPRD_AMT ';
   IF (P_IS_COUNT='A') THEN
     IF (P_ISTHOUSAND='T') THEN
       SET V_SQL =  'SUM(M.INPRD_CNT) AS INPRD_CNT, '+
                 'SUM(M.INPRD_AMT)/1000 AS INPRD_AMT, '+
                 'SUM(M.DC_INPRD_CNT) AS DC_INPRD_CNT, '+
                 'SUM(M.DC_INPRD_AMT)/1000 AS DC_INPRD_AMT, '+
                 'SUM(M.RETPRD_CNT) AS RETPRD_CNT, '+
                 'SUM(M.RETPRD_AMT)/1000 AS RETPRD_AMT, '+
                 'SUM(M.DC_RETPRD_CNT) AS DC_RETPRD_CNT, '+
                 'SUM(M.DC_RETPRD_AMT)/1000 AS DC_RETPRD_AMT ';
     END IF;
   END IF;
   IF (P_DATE_TYPE='D') THEN
     SET V_STRING = 'SELECT T.L_DAY_ID AS TIME_ID, M.PRD_ID,MD.MRZN, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M, PMART.MR_DIM MD , (SELECT DISTINCT L_DAY_ID FROM PMART.YMWD_TIME_W2 ) T '+
            ' WHERE M.TIME_ID = T.L_DAY_ID '+
            '       AND M.MR_ID = MD.MRZN_ID '+
            '       AND M.PRD_ID = MD.PRD_ID '+
            '       AND T.L_DAY_ID IN ('+ P_L_DAY_ID +') '+
            '       AND M.PRD_ID IN ('+ PMART.CONVERT_STRING_LIST(P_PRD_ID) +') '+
            '       AND MD.MR_ID = '+ P_VENDOR +' '+
            ' GROUP BY T.L_DAY_ID, M.PRD_ID,MD.MRZN ';
   END IF;
   IF (P_DATE_TYPE='W') THEN
     SET V_STRING = 'SELECT T.L_WEEK_ID AS TIME_ID, M.PRD_ID,MD.MRZN, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M, PMART.MR_DIM MD , (SELECT DISTINCT L_DAY_ID,L_WEEK_ID FROM PMART.YMWD_TIME_W2 ) T '+
            ' WHERE M.TIME_ID = T.L_DAY_ID '+
            '       AND M.MR_ID = MD.MRZN_ID '+
            '       AND M.PRD_ID = MD.PRD_ID '+
            '       AND T.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            '       AND M.PRD_ID IN ('+ PMART.CONVERT_STRING_LIST(P_PRD_ID) +') '+
            '       AND MD.MR_ID = '+ P_VENDOR +' '+
            ' GROUP BY T.L_WEEK_ID, M.PRD_ID,MD.MRZN ';
   END IF;
   IF (P_DATE_TYPE='M') THEN
     SET V_STRING = 'SELECT T.L_MONTH_ID AS TIME_ID, M.PRD_ID,MD.MRZN, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M, PMART.MR_DIM MD , (SELECT DISTINCT L_DAY_ID,L_MONTH_ID FROM PMART.YMWD_TIME_W2 ) T '+
            ' WHERE M.TIME_ID = T.L_DAY_ID '+
            '       AND M.MR_ID = MD.MRZN_ID '+
            '       AND M.PRD_ID = MD.PRD_ID '+
            '       AND T.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            '       AND M.PRD_ID IN ('+ PMART.CONVERT_STRING_LIST(P_PRD_ID) +') '+
            '       AND MD.MR_ID = '+ P_VENDOR +' '+
            ' GROUP BY T.L_MONTH_ID, M.PRD_ID,MD.MRZN ';
   END IF;
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSM1_3_FUNC  AS('+
        V_STRING +
   ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;
END SP;