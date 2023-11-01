REPLACE PROCEDURE PMART.FUNC_SHELF_FACT
(
   IN P_SHELF_LEVEL INTEGER,
   IN P_TIME_ID INTEGER,
   IN P_SHELF_ID INTEGER
)
SP:BEGIN
   DECLARE SQLSTR VARCHAR(10000); 
   DECLARE CON1WHERE VARCHAR(100); 
   DECLARE CON2WHERE VARCHAR(100); 
   IF P_SHELF_LEVEL = 0 THEN
            SET CON1WHERE = ' AND SHELF_ID=-1 AND SHELF_LEVEL='+P_SHELF_LEVEL;
            SET CON2WHERE = ' AND P_SHELF_ID=-1 AND SHELF_LEVEL='+P_SHELF_LEVEL+1;
   ELSEIF P_SHELF_LEVEL = 1 THEN
	    SET CON1WHERE = ' AND SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL;
            SET CON2WHERE = ' AND P_SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL+1;
   ELSEIF P_SHELF_LEVEL = 2 THEN
	    SET CON1WHERE = ' AND SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL;
            SET CON2WHERE = ' AND P_SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL+1;
   ELSEIF P_SHELF_LEVEL = 3 THEN
	    SET CON1WHERE = ' AND SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL;
            SET CON2WHERE = ' AND P_SHELF_ID='+P_SHELF_ID+' AND SHELF_LEVEL='+P_SHELF_LEVEL+1;
   ELSE
	    SET CON1WHERE = '';
   END IF;   
CALL PMART.P_DROP_TABLE ('#VT_FUNC_SHELF_FACT');
IF CON1WHERE<> '' THEN
LOCKING PMART.SHELF_FACT FOR ACCESS;
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FUNC_SHELF_FACT AS('                          										   
						   +'SELECT F.TIME_ID,F.ORG_ID,'
						   +'CASE WHEN F.SHELF_LEVEL<4 AND F.SHELF_LEVEL='+P_SHELF_LEVEL+' THEN -1 ELSE F.SHELF_ID END AS SHELF_ID, '
						   +'F.SHELF_LEVEL , '
						   +'F.ST_NUM , '
						   +'F.INPRD_CNT , '
						   +'F.INPRD_AMT , '
						   +'F.SALES_CNT , '
						   +'F.SALES_AMT , '
						   +'F.REAL_SALES_AMT,		 '	   
						   +' CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_CNT,F.ST_NUM) AS BIGINT) AS INPRD_CNT_PSD, '
						   +' CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_AMT,F.ST_NUM) AS BIGINT) AS INPRD_AMT_PSD, '
						   +' CAST(PMART.DIVIDE_BY_ZERO(F.SALES_CNT,F.ST_NUM) AS BIGINT) AS SALES_CNT_PSD, '
						   +' CAST(PMART.DIVIDE_BY_ZERO(F.SALES_AMT,F.ST_NUM) AS BIGINT) AS SALES_AMT_PSD, '
						   +' CAST(PMART.DIVIDE_BY_ZERO(F.REAL_SALES_AMT,F.ST_NUM) AS BIGINT) AS REAL_SALES_AMT_PSD'
                                                   +' FROM  '
                                                   +' ( '
                                                   +'   SELECT TIME_ID,ORG_ID,SHELF_ID,SHELF_LEVEL '
                                                   +'   ,SUM(ST_NUM)  AS ST_NUM '
                                                   +'   ,SUM(INPRD_CNT)  AS INPRD_CNT '
                                                   +'   ,SUM(INPRD_AMT)  AS INPRD_AMT '
                                                   +'   ,SUM(SALES_CNT)  AS SALES_CNT '
                                                   +'   ,SUM(SALES_AMT)  AS SALES_AMT '
                                                   +'   ,SUM(REAL_SALES_AMT)  AS REAL_SALES_AMT '
                                                   +'    FROM ( '
                                                   +' SELECT TIME_ID,ORG_ID,SHELF_ID,SHELF_LEVEL,ST_NUM,INPRD_CNT,INPRD_AMT,SALES_CNT,SALES_AMT,REAL_SALES_AMT '
                                                   +'   FROM PMART.SHELF_FACT '
                                                   +'  WHERE SHELF_ID2 IS NULL '
                                                   +' UNION ALL '
                                                   +' SELECT TIME_ID,ORG_ID,SHELF_ID2 AS SHELF_ID,SHELF_LEVEL,ST_NUM,INPRD_CNT,INPRD_AMT,SALES_CNT,SALES_AMT,REAL_SALES_AMT '
                                                   +'   FROM PMART.SHELF_FACT ' 
                                                   +'  WHERE SHELF_ID2 IS NOT NULL '
                                                   +' 	) E '
                                                   +' 	GROUP BY TIME_ID,ORG_ID,SHELF_ID,SHELF_LEVEL '
                                                   +' ) F JOIN ('
						   +'SELECT * FROM PMART.VW_SHELF_DIM WHERE STYLE=1 AND L_DAY_ID='+P_TIME_ID+CON1WHERE
						   +' UNION ALL '
						   +'SELECT * FROM PMART.VW_SHELF_DIM WHERE STYLE=1 AND L_DAY_ID='+P_TIME_ID+CON2WHERE+')  S ON F.SHELF_ID=S.SHELF_ID AND S.SHELF_LEVEL=F.SHELF_LEVEL '
						   +'JOIN (SELECT X.L_DAY_ID,CAST(TO_CHAR(C.CALENDAR_DATE, ''YYYYMMDD'') AS INTEGER)  AS  X_DAY FROM SYS_CALENDAR.CALENDAR C '
						   +'JOIN (SELECT DISTINCT L_DAY_ID,TIME_ID_S,TIME_ID_E FROM PMART.SHELF_DIM WHERE L_DAY_ID = '+P_TIME_ID+') X ON '
						   +'CAST(TO_CHAR(C.CALENDAR_DATE, ''YYYYMMDD'') AS INTEGER) >= X.TIME_ID_S '
						   +'AND CAST(TO_CHAR(C.CALENDAR_DATE, ''YYYYMMDD'') AS INTEGER) <= X.TIME_ID_E) D ON D.X_DAY = F.TIME_ID '
               + ' ) WITH DATA PRIMARY INDEX(TIME_ID,ORG_ID, SHELF_ID, SHELF_LEVEL) ON COMMIT PRESERVE ROWS;';
		EXECUTE IMMEDIATE SQLSTR;
		INSERT INTO #VT_FUNC_SHELF_FACT (TIME_ID, ORG_ID, SHELF_ID, SHELF_LEVEL, ST_NUM, INPRD_CNT, INPRD_AMT, SALES_CNT, SALES_AMT, REAL_SALES_AMT,
		INPRD_CNT_PSD, INPRD_AMT_PSD, SALES_CNT_PSD, SALES_AMT_PSD, REAL_SALES_AMT_PSD)
		SELECT -1 AS TIME_ID,ORG_ID,SHELF_ID,SHELF_LEVEL,SUM(ST_NUM),SUM(INPRD_CNT),SUM(INPRD_AMT),SUM(SALES_CNT),SUM(SALES_AMT),SUM(REAL_SALES_AMT),
		CAST(PMART.DIVIDE_BY_ZERO(SUM(INPRD_CNT),SUM(ST_NUM)) AS BIGINT) AS INPRD_CNT_PSD,
		CAST(PMART.DIVIDE_BY_ZERO(SUM(INPRD_AMT),SUM(ST_NUM)) AS BIGINT) AS INPRD_AMT_PSD,
		CAST(PMART.DIVIDE_BY_ZERO(SUM(SALES_CNT),SUM(ST_NUM)) AS BIGINT) AS SALES_CNT_PSD,
		CAST(PMART.DIVIDE_BY_ZERO(SUM(SALES_AMT),SUM(ST_NUM)) AS BIGINT) AS SALES_AMT_PSD,
		CAST(PMART.DIVIDE_BY_ZERO(SUM(REAL_SALES_AMT),SUM(ST_NUM)) AS BIGINT) AS REAL_SALES_AMT_PSD
		FROM #VT_FUNC_SHELF_FACT GROUP BY ORG_ID,SHELF_ID,SHELF_LEVEL;
END IF;
END SP;