REPLACE  PROCEDURE PMART.FUNC_FACT_MMK_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_SYS_ID VARCHAR(2),
   IN P_CLASS_ID VARCHAR(2),
   IN P_MMK_ID VARCHAR(12)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000) ;
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_MMK_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_MMK_DIM ( '
						 +'PARENT_MMK_ID VARCHAR(12), '
						 +'MMK_ID VARCHAR(12), '
	                     +'MMK_VAL VARCHAR(12), '
						 +'MMK_NAME VARCHAR(100)   , '
						 +'MMK_NO VARCHAR(12), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'MMK_LEVEL SMALLINT, '
						 +'DRILL_MMK_LEVEL SMALLINT, '
						 +'DRILL_MMK_ID VARCHAR(12) '						 
	                     +') UNIQUE PRIMARY INDEX(MMK_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_MMK_DIM(PARENT_MMK_ID,MMK_ID,MMK_VAL,MMK_NAME,MMK_NO,ALLOW_DRILL_DOWN,MMK_LEVEL,DRILL_MMK_LEVEL,DRILL_MMK_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 		
		   SET SQLSTR = SQLSTR + '  SELECT PARENT_MMK_ID,MMK_ID,MMK_VAL,MMK_NAME,MMK_NO,ALLOW_DRILL_DOWN,MMK_LEVEL, '
				                                   + ' CASE WHEN MMK_LEVEL=1 THEN -1 ELSE (MMK_LEVEL-1) END AS DRILL_MMK_LEVEL, '
												   + ' TRIM(PARENT_MMK_ID) AS DRILL_MMK_ID '
		                                           + '      FROM PMART.VW_MMK_DIM '
		                                           + ' WHERE SYS_ID = ''' + TRIM(P_SYS_ID)+''''
												   + ' AND CLASS_ID = ''' + TRIM(P_CLASS_ID)+''''
												   + ' AND MMK_ID = ''' + TRIM(P_MMK_ID)+''''
		                                           + ' UNION ALL ';													
    END IF;	
    SET SQLSTR = SQLSTR + ' SELECT PARENT_MMK_ID,MMK_ID,MMK_VAL,MMK_NAME,MMK_NO,ALLOW_DRILL_DOWN,MMK_LEVEL, '
	                                 + ' MMK_LEVEL AS DRILL_MMK_LEVEL, '
									 + ' MMK_ID AS DRILL_MMK_ID '
                                     + '      FROM PMART.VW_MMK_DIM '
									 + ' WHERE SYS_ID = ''' + TRIM(P_SYS_ID)+''''
									 + ' AND CLASS_ID = ''' + TRIM(P_CLASS_ID)+''''
                                     + ' AND PARENT_MMK_ID = ''' + TRIM(P_MMK_ID)+''''
                                     + ' ) DAT_Y; ';			 						 
    EXECUTE IMMEDIATE SQLSTR;
	IF P_SHOWTYPE = 1 THEN 
		 SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_MMK_DIM VALUES '
							   ' (''-2'',''-1'', ''-1'', ''
		 EXECUTE IMMEDIATE SQLSTR; 
	END IF;
END SP;