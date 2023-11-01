REPLACE PROCEDURE PMART.FUNC_FACT_ORG_DIM_CHAR
(
   IN P_SHOWTYPE INTEGER,  
   IN P_ORGID VARCHAR(20) CHARACTER SET UNICODE
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000) ; 
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_ORG_DIM_CHAR');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_ORG_DIM_CHAR ( '
						 +'PARENT_ORG_ID VARCHAR(10), '
						 +'ORG_ID VARCHAR(11), '
	                     +'ORG_VAL VARCHAR(11), '
						 +'ORG_SNAME VARCHAR(27)   , '
						 +'ORG_NO VARCHAR(11), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'ORG_LEVEL SMALLINT, '
						 +'DRILL_ORG_LEVEL INTEGER, '
						 +'DRILL_ORG_ID VARCHAR(10) '						 
	                     +') UNIQUE PRIMARY INDEX(ORG_ID) ON COMMIT PRESERVE ROWS; ';
	EXECUTE IMMEDIATE SQLSTR; 
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_ORG_DIM_CHAR(PARENT_ORG_ID,ORG_ID,ORG_VAL,ORG_SNAME,ORG_NO,ALLOW_DRILL_DOWN,ORG_LEVEL,DRILL_ORG_LEVEL,DRILL_ORG_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +'  SELECT DAT.*,CASE WHEN ORG_LEVEL=1 THEN -1 ELSE (ORG_LEVEL-1) END AS DRILL_ORG_LEVEL,DAT.PARENT_ORG_ID AS DRILL_ORG_ID '
		                                    +'     FROM PMART.VW_ORG_DIM_CHAR DAT '
                                            +'  WHERE ORG_NO = TRIM(''' + P_ORGID + ''')'
                                            +'  UNION ALL ';
    ELSE
		   SET SQLSTR = SQLSTR +' SELECT 	  CAST(TRIM(-2) AS VARCHAR(10)) AS PARENT_ORG_ID '
		                                    +'                 ,CAST(-1   AS VARCHAR(10) ) AS ORG_ID '
		                                    +'                 ,CAST(-1   AS VARCHAR(10)) AS ORG_VAL '
		                                    +'                 ,CAST('''+'
		                                    +'                 ,CAST(''-1'' AS VARCHAR(11)) AS ORG_NO '
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS SMALLINT)  AS ORG_LEVEL '
		                                    +'                 ,CAST(-2 AS SMALLINT) AS  DRILL_ORG_LEVEL '
		                                    +'                 ,CAST(TRIM(''-2'') AS VARCHAR(10)) AS DRILL_ORG_ID '
											+'     FROM PMART.VW_ORG_DIM_CHAR DAT '
											+'  WHERE ORG_NO =  ''-1'' '
                                            +'  UNION ALL ';											
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,ORG_LEVEL AS DRILL_ORG_LEVEL,DAT.ORG_NO AS DRILL_ORG_ID '
                                     +'      FROM PMART.VW_ORG_DIM_CHAR DAT '
                                     +'   WHERE PARENT_ORG_ID = TRIM(''' + P_ORGID + ''')'
                                     +'  )DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;