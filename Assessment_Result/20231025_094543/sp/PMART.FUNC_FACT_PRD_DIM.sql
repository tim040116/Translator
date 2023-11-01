REPLACE PROCEDURE PMART.FUNC_FACT_PRD_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_PRDID VARCHAR(7),
   IN P_AUTH_LEVEL INTEGER,
   IN P_AUTH_VALUE VARCHAR(20)
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000) ; 
	 DECLARE PRDWHERE VARCHAR(2000);  
	 SET PRDWHERE = ' ';
	IF (P_AUTH_LEVEL  = 6 OR P_AUTH_LEVEL  = 7) AND P_PRDID = -1 THEN
		SET PRDWHERE = ' AND DAT.PRD_ID IN (SELECT DISTINCT KND_ID FROM PMART.ORG_DIM_POSI1 WHERE DEPT_NO = '''+ P_AUTH_VALUE +''') ' ;
	ELSEIF P_AUTH_LEVEL  = 8 AND P_PRDID = -1 THEN
		SET PRDWHERE = ' AND DAT.PRD_ID IN (SELECT DISTINCT KND_ID FROM PMART.ORG_DIM_POSI1 WHERE BRANCH_NO = '''+ P_AUTH_VALUE +''') ' ;
	ELSEIF P_AUTH_LEVEL  = 9 AND P_PRDID = -1 THEN
		SET PRDWHERE = ' AND DAT.PRD_ID IN (SELECT DISTINCT KND_ID FROM PMART.ORG_DIM_POSI1 WHERE RESPON_NO = '''+ P_AUTH_VALUE +''') ' ;
	END IF;
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_PRD_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_PRD_DIM ( '
						 +'PARENT_PRD_ID VARCHAR(7), '
						 +'PRD_ID VARCHAR(7), '
						 +'FM_NAME VARCHAR(27)   , '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_ID VARCHAR(7) '						 
	                     +') UNIQUE PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_PRD_DIM(PARENT_PRD_ID,PRD_ID,FM_NAME,ALLOW_DRILL_DOWN,PRD_LEVEL,DRILL_PRD_LEVEL,DRILL_PRD_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +'  SELECT DAT.*,CASE WHEN PRD_LEVEL=1 THEN -1 ELSE (PRD_LEVEL-1) END AS DRILL_PRD_LEVEL,DAT.PARENT_PRD_ID AS DRILL_PRD_ID '
		                                    +'     FROM PMART.VW_PRD_DIM_NEW DAT '
                                            +'  WHERE PRD_ID = '''+ P_PRDID+''''
                                            +'  UNION ALL ';
    ELSE
		   SET SQLSTR = SQLSTR +' SELECT 	CAST(-2 AS VARCHAR(7)) AS PARENT_PRD_ID '
		                                    +'                 ,CAST(-1 AS VARCHAR(7))  AS PRD_ID '
		                                    +'                 ,CAST('''+'
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS PRD_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_PRD_LEVEL '
		                                    +'                 ,CAST(-2 AS VARCHAR(7))  AS DRILL_PRD_ID '
											+'     FROM PMART.VW_PRD_DIM_NEW DAT '
											+'  WHERE PRD_ID =  ''-1'' '
                                            +'  UNION ALL ';											
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,PRD_LEVEL AS DRILL_PRD_LEVEL,DAT.PRD_ID AS DRILL_PRD_ID '
                                     +'      FROM PMART.VW_PRD_DIM_NEW DAT '
                                     +'   WHERE PARENT_PRD_ID =  TRIM(''' + P_PRDID+''')' + PRDWHERE
                                     +'  )DAT_Y ';
	DELETE PTEMP.T1 WHERE F1 = 1;					 
   	INSERT INTO PTEMP.T1(F1,F2) SELECT 1,SQLSTR;
    EXECUTE IMMEDIATE SQLSTR;   
END SP;