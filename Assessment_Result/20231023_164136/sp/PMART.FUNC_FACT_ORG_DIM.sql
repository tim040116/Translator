REPLACE PROCEDURE PMART.FUNC_FACT_ORG_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_ORGID INT
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000) ; 
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_ORG_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_ORG_DIM ( '
						 +'PARENT_ORG_ID INTEGER, '
						 +'ORG_ID INTEGER, '
	                     +'ORG_VAL INTEGER, '
						 +'ORG_SNAME VARCHAR(27)   , '
						 +'ORG_NO VARCHAR(11), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'ORG_LEVEL SMALLINT, '
						 +'DRILL_ORG_LEVEL SMALLINT, '
						 +'DRILL_ORG_ID INTEGER '						 
	                     +') UNIQUE PRIMARY INDEX(ORG_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_ORG_DIM(PARENT_ORG_ID,ORG_ID,ORG_VAL,ORG_SNAME,ORG_NO,ALLOW_DRILL_DOWN,ORG_LEVEL,DRILL_ORG_LEVEL,DRILL_ORG_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +'  SELECT DAT.*,CASE WHEN ORG_LEVEL=1 THEN -1 ELSE (ORG_LEVEL-1) END AS DRILL_ORG_LEVEL,DAT.PARENT_ORG_ID AS DRILL_ORG_ID '
		                                    +'     FROM PMART.VW_ORG_DIM_NEW DAT '
                                            +'  WHERE ORG_ID = ' + P_ORGID
                                            +'  UNION ALL ';
    ELSE
		   SET SQLSTR = SQLSTR +' SELECT 	CAST(-2 AS INTEGER) AS PARENT_ORG_ID '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS ORG_ID '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS ORG_VAL '
		                                    +'                 ,CAST('''+'
		                                    +'                 ,CAST(''-1'' AS VARCHAR(11)) AS ORG_NO '
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS ORG_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_ORG_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_ORG_ID '
											+'     FROM PMART.VW_ORG_DIM_NEW DAT '
											+'  WHERE ORG_ID =  -1 '
                                            +'  UNION ALL ';											
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,ORG_LEVEL AS DRILL_ORG_LEVEL,DAT.ORG_ID AS DRILL_ORG_ID '
                                     +'      FROM PMART.VW_ORG_DIM_NEW DAT '
                                     +'   WHERE PARENT_ORG_ID =  ' + P_ORGID
                                     +'  )DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;