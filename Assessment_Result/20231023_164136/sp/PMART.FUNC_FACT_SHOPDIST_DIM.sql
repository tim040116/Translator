REPLACE PROCEDURE PMART.FUNC_FACT_SHOPDIST_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_SHOPDIST_TYPE VARCHAR(36)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000) ;
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_SHOPDIST_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_SHOPDIST_DIM ( '
						 +'PARENT_SHOPDIST_ID VARCHAR(36), '
						 +'SHOPDIST_ID VARCHAR(36), '
	                     +'SHOPDIST_VAL VARCHAR(36), '
						 +'SHOPDIST_NAME VARCHAR(186)   , '
						 +'SHOPDIST_NO VARCHAR(36), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'SHOPDIST_LEVEL SMALLINT, '
						 +'DRILL_SHOPDIST_LEVEL SMALLINT, '
						 +'DRILL_SHOPDIST_ID VARCHAR(36) '						 
	                     +') UNIQUE PRIMARY INDEX(SHOPDIST_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_SHOPDIST_DIM(PARENT_SHOPDIST_ID,SHOPDIST_ID,SHOPDIST_VAL,SHOPDIST_NAME,SHOPDIST_NO,ALLOW_DRILL_DOWN,SHOPDIST_LEVEL,DRILL_SHOPDIST_LEVEL,DRILL_SHOPDIST_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 		
		   SET SQLSTR = SQLSTR +'  SELECT DAT.*,CASE WHEN SHOPDIST_LEVEL=1 THEN -1 ELSE (SHOPDIST_LEVEL-1) END AS DRILL_SHOPDIST_LEVEL,PARENT_SHOPDIST_ID AS DRILL_SHOPDIST_ID '
                                            +'      FROM PMART.VW_SHOPDIST_DIM DAT '
                                            +'  WHERE SHOPDIST_ID = ''' + TRIM(P_SHOPDIST_TYPE)+''''
                                            +'  UNION ALL ';
     ELSE
		   SET SQLSTR = SQLSTR +' SELECT 	CAST(-2 AS VARCHAR(12)) AS PARENT_SHOPDIST_ID '
		                                    +'                 ,CAST(-1 AS VARCHAR(36))  AS SHOPDIST_ID '
		                                    +'                 ,CAST(-1 AS VARCHAR(36))  AS SHOPDIST_VAL '
		                                    +'                 ,CAST('''+'
		                                    +'                 ,CAST(''-1'' AS VARCHAR(36)) AS SHOPDIST_NO '
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS SMALLINT)  AS SHOPDIST_LEVE '
		                                    +'                 ,CAST(-2 AS SMALLINT)  AS DRILL_SHOPDIST_LEVEL '
		                                    +'                 ,CAST(-2 AS VARCHAR(36))  AS DRILL_SHOPDIST_ID '
											+'     FROM PMART.VW_SHOPDIST_DIM DAT '
											+'  WHERE SHOPDIST_ID =  ''-1'' '
                                            +'  UNION ALL ';
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,SHOPDIST_LEVEL AS DRILL_SHOPDIST_LEVEL,SHOPDIST_ID AS DRILL_SHOPDIST_ID '
                                     +'      FROM PMART.VW_SHOPDIST_DIM DAT '
                                     +'   WHERE PARENT_SHOPDIST_ID = ''' + TRIM(P_SHOPDIST_TYPE)+''''
                                     +' ) DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;