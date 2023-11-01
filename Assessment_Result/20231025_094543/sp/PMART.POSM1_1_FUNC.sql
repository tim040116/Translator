REPLACE PROCEDURE PMART.POSM1_1_FUNC(P_DATE_TYPE VARCHAR(2000),
									 P_L_DAY_ID VARCHAR(2000),
									 P_MR_ID VARCHAR(2000),
									 P_ORG_LEVEL VARCHAR(2000),
									 P_ORG_ID VARCHAR(2000),
									 P_ISTHOUSAND VARCHAR(2000))
SQL SECURITY INVOKER
SP:BEGIN
   DECLARE SQLSTR VARCHAR(64000);
   DECLARE V_SQL VARCHAR(20000);
   DECLARE V_STRING VARCHAR(20000);
   CALL PMART.P_DROP_TABLE ('#VT_POSM1_1_FUNC'); 
   IF (P_ISTHOUSAND='O') THEN
      SET V_SQL = 'SUM(M.ORDER_AMT) AS ORDER_AMT, '+
            'SUM(M.INPRD_AMT) AS INPRD_AMT, '+
            'SUM(M.SALES_AMT) AS SALES_AMT, '+
            'SUM(M.SALES_AMT)-(SUM(M.DIS_AMT)+SUM(M.SUB_AMT)) AS REAL_SALES_AMT, '+
            'SUM(M.DC_INPRD_AMT) AS DC_INPRD_AMT, '+
            'SUM(M.THROW_AMT) AS THROW_AMT, '+
            'SUM(M.DIS_AMT)+SUM(M.SUB_AMT)AS DIS_SUB_AMT ';
   END IF;
   IF (P_ISTHOUSAND='T') THEN
      SET V_SQL = 'CAST(SUM(M.ORDER_AMT) AS DECIMAL(18,6)) / 1000 AS ORDER_AMT, '+
            'CAST(SUM(M.INPRD_AMT) AS DECIMAL(18,6)) / 1000 AS INPRD_AMT, '+
            'CAST(SUM(M.SALES_AMT) AS DECIMAL(18,6)) / 1000 AS SALES_AMT, '+
            'CAST((SUM(M.SALES_AMT)-(SUM(M.DIS_AMT)+SUM(M.SUB_AMT))) AS DECIMAL(18,6))  / 1000 AS REAL_SALES_AMT, '+
            'CAST(SUM(M.DC_INPRD_AMT) AS DECIMAL(18,6))  / 1000 AS DC_INPRD_AMT, '+
            'CAST(SUM(M.THROW_AMT) AS DECIMAL(18,6))  AS THROW_AMT, '+
            'CAST((SUM(M.DIS_AMT)+SUM(M.SUB_AMT)) AS DECIMAL(18,6))  / 1000 AS DIS_SUB_AMT ';
   END IF;
   IF (P_DATE_TYPE='D') THEN
        IF (P_ORG_LEVEL='-99') THEN
            SET V_STRING = 'SELECT M.TIME_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M '+
            'WHERE  M.TIME_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY M.TIME_ID, M.MR_ID ';
        END IF;
		IF (P_ORG_LEVEL='0') THEN
            SET V_STRING = 'SELECT M.TIME_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN (SELECT DISTINCT PRD_ID,TOT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.TOT_ID = '+ P_ORG_ID +
            ' AND M.TIME_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY M.TIME_ID, M.MR_ID ';
        END IF;		
        IF (P_ORG_LEVEL='1') THEN
            SET V_STRING = 'SELECT M.TIME_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN (SELECT DISTINCT PRD_ID,DEPT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.DEPT_ID = '+ P_ORG_ID +
            ' AND M.TIME_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY M.TIME_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='2') THEN
            SET V_STRING = 'SELECT M.TIME_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN (SELECT DISTINCT PRD_ID,BRANCH_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.BRANCH_ID = '+ P_ORG_ID +
            ' AND M.TIME_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY M.TIME_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='3') THEN
            SET V_STRING = 'SELECT M.TIME_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN (SELECT DISTINCT PRD_ID,RESPON_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.RESPON_ID = '+ P_ORG_ID +
            ' AND M.TIME_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY M.TIME_ID, M.MR_ID ';
        END IF;
   END IF;
   IF (P_DATE_TYPE='W') THEN
        IF (P_ORG_LEVEL='-99') THEN
            SET V_STRING = 'SELECT TM.L_WEEK_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'WHERE TM.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_WEEK_ID, M.MR_ID ';
        END IF;
		 IF (P_ORG_LEVEL='0') THEN
            SET V_STRING = 'SELECT TM.L_WEEK_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,TOT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.TOT_ID = '+ P_ORG_ID +
            ' AND TM.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_WEEK_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='1') THEN
            SET V_STRING = 'SELECT TM.L_WEEK_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,DEPT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.DEPT_ID = '+ P_ORG_ID +
            ' AND TM.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_WEEK_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='2') THEN
            SET V_STRING = 'SELECT TM.L_WEEK_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,BRANCH_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.BRANCH_ID = '+ P_ORG_ID +
            ' AND TM.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_WEEK_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='3') THEN
            SET V_STRING = 'SELECT TM.L_WEEK_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,RESPON_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE  MD.RESPON_ID = '+ P_ORG_ID +
            ' AND TM.L_WEEK_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_WEEK_ID, M.MR_ID ';
        END IF;
   END IF;
   IF (P_DATE_TYPE='M') THEN
        IF (P_ORG_LEVEL='-99') THEN
            SET V_STRING = 'SELECT TM.L_MONTH_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'WHERE TM.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_MONTH_ID, M.MR_ID ';
        END IF;
		 IF (P_ORG_LEVEL='0') THEN
            SET V_STRING = 'SELECT TM.L_MONTH_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,TOT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.TOT_ID = '+ P_ORG_ID +
            ' AND TM.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_MONTH_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='1') THEN
            SET V_STRING = 'SELECT TM.L_MONTH_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,DEPT_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.DEPT_ID = '+ P_ORG_ID +
            ' AND TM.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_MONTH_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='2') THEN
            SET V_STRING = 'SELECT TM.L_MONTH_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,BRANCH_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.BRANCH_ID = '+ P_ORG_ID +
            ' AND TM.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_MONTH_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='3') THEN
            SET V_STRING = 'SELECT TM.L_MONTH_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,RESPON_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.RESPON_ID = '+ P_ORG_ID +
            ' AND TM.L_MONTH_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_MONTH_ID, M.MR_ID ';
        END IF;
   END IF;
   IF (P_DATE_TYPE='Y') THEN
        IF (P_ORG_LEVEL='-99') THEN
            SET V_STRING = 'SELECT TM.L_YEAR_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'WHERE TM.L_YEAR_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_YEAR_ID, M.MR_ID ';
        END IF;
		 IF (P_ORG_LEVEL='0') THEN
            SET V_STRING = 'SELECT TM.L_YEAR_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,TOT_ID FROM PMART.MR_DIM ) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.TOT_ID = '+ P_ORG_ID +
            ' AND TM.L_YEAR_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_YEAR_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='1') THEN
            SET V_STRING = 'SELECT TM.L_YEAR_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,DEPT_ID FROM PMART.MR_DIM ) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.DEPT_ID = '+ P_ORG_ID +
            ' AND TM.L_YEAR_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_YEAR_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='2') THEN
            SET V_STRING = 'SELECT TM.L_YEAR_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
             ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,BRANCH_ID FROM PMART.MR_DIM ) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.BRANCH_ID = '+ P_ORG_ID +
            ' AND TM.L_YEAR_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_YEAR_ID, M.MR_ID ';
        END IF;
        IF (P_ORG_LEVEL='3') THEN
            SET V_STRING = 'SELECT TM.L_YEAR_ID AS X_ID, M.MR_ID AS Y_ID, '+
            V_SQL+
            ' FROM PMART.BASIC_MFACT_MR M LEFT JOIN PMART.YMWD_TIME_W2 TM ON (M.TIME_ID = TM.L_DAY_ID) '+
            'LEFT JOIN (SELECT DISTINCT PRD_ID,RESPON_ID FROM PMART.MR_DIM) MD ON (M.PRD_ID=MD.PRD_ID) '+
            'WHERE MD.RESPON_ID = '+ P_ORG_ID +
            ' AND TM.L_YEAR_ID IN ('+ P_L_DAY_ID +') '+
            ' AND M.MR_ID IN ('+ P_MR_ID +') '+
            'GROUP BY TM.L_YEAR_ID, M.MR_ID ';
        END IF;
   END IF;
   SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_POSM1_1_FUNC  AS('+
        V_STRING +
   ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;
END SP;