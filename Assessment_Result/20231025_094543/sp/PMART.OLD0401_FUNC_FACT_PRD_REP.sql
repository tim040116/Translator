REPLACE PROCEDURE PMART.OLD0401_FUNC_FACT_PRD_REP
(
   IN P_SHOWTYPE INTEGER,  
   IN P_PRDID INT
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000) ; 
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_PRD_REP');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_PRD_REP ( '
						 +'PARENT_PRD_ID INTEGER, '
						 +'PRD_ID INTEGER, '
						 +'FM_NAME VARCHAR(27)   , '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_ID INTEGER '						 
	                     +') UNIQUE PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_PRD_REP(PARENT_PRD_ID,PRD_ID,FM_NAME,ALLOW_DRILL_DOWN,PRD_LEVEL,DRILL_PRD_LEVEL,DRILL_PRD_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +'  SELECT DAT.*,CASE WHEN PRD_LEVEL=1 THEN -1 ELSE (PRD_LEVEL-1) END AS DRILL_PRD_LEVEL,DAT.PARENT_PRD_ID AS DRILL_PRD_ID '
		                                    +'     FROM PMART.VW_PRD_REP DAT '
                                            +'  WHERE PRD_ID = ' + P_PRDID
                                            +'  UNION ALL ';
    ELSE
		   SET SQLSTR = SQLSTR +' SELECT 	CAST(-2 AS INTEGER) AS PARENT_PRD_ID '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS PRD_ID '
		                                    +'                 ,CAST('''+'
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS PRD_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_PRD_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_PRD_ID '
											+'     FROM PMART.VW_PRD_REP DAT '
											+'  WHERE PRD_ID =  -1 '
                                            +'  UNION ALL ';											
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,PRD_LEVEL AS DRILL_PRD_LEVEL,DAT.PRD_ID AS DRILL_PRD_ID '
                                     +'      FROM PMART.VW_PRD_REP DAT '
                                     +'   WHERE PARENT_PRD_ID =  ' + P_PRDID
                                     +'  )DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;