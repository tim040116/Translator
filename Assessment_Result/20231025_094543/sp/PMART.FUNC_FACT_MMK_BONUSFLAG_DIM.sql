REPLACE PROCEDURE PMART.FUNC_FACT_MMK_BONUSFLAG_DIM
(
   IN P_SHOWTYPE INTEGER,  
   IN P_MMKID VARCHAR(10)
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(20000) ; 
	 DECLARE CONDWHERE VARCHAR(100);
    IF CAST(LENGTH(TRIM(P_MMKID)) AS CHAR(2)) = '8' THEN
	    SET CONDWHERE = '   WHERE PARENT_MMK_ID+MMK_ID =  ''' + TRIM(P_MMKID)+''' ';	
	ELSE
	    SET CONDWHERE = '   WHERE PARENT_MMK_ID =  ''' + TRIM(P_MMKID)+''' ';
	END IF;	 
    CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_MMK_BONUSFLAG_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_MMK_BONUSFLAG_DIM ( '
						 +'PARENT_MMK_ID VARCHAR(10), '
						 +'MMK_ID VARCHAR(10), '
	                     +'MMK_VAL VARCHAR(10), '
						 +'MMK_NAME VARCHAR(50)   , '
						 +'MMK_NO VARCHAR(10), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'MMK_LEVEL SMALLINT, '
						 +'DRILL_MMK_LEVEL SMALLINT, '
						 +'DRILL_MMK_ID VARCHAR(10) '						 
	                     +') UNIQUE PRIMARY INDEX(MMK_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_FACT_MMK_BONUSFLAG_DIM(PARENT_MMK_ID,MMK_ID,MMK_VAL,MMK_NAME,MMK_NO,ALLOW_DRILL_DOWN,MMK_LEVEL,DRILL_MMK_LEVEL,DRILL_MMK_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +'  SELECT  CAST(DAT.PARENT_MMK_ID AS VARCHAR(10)) AS PARENT_MMK_ID  '
		                                    +'                 ,CAST(DAT.MMK_ID AS VARCHAR(10))  AS MMK_ID '
											+'                 ,CAST(DAT.MMK_VAL AS VARCHAR(10))  AS MMK_VAL '
											+'                 ,DAT.MMK_NAME  '
											+'                 ,CAST(DAT.MMK_NO AS VARCHAR(10))  AS MMK_NO '
											+'                 ,DAT.ALLOW_DRILL_DOWN '
											+'                 ,DAT.MMK_LEVEL '
		                                    +'                 ,CASE WHEN MMK_LEVEL=1 THEN -1 ELSE (MMK_LEVEL-1) END AS DRILL_MMK_LEVEL '
											+'                 ,CAST(DAT.PARENT_MMK_ID AS VARCHAR(10)) AS DRILL_MMK_ID '
		                                    +'     FROM PMART.VW_MMK_BONUSFLAG_DIM DAT '
                                            +'  WHERE TRIM(MMK_ID) = ''' + TRIM(P_MMKID) +''' '
                                            +'  UNION ALL ';
    ELSE
		   SET SQLSTR = SQLSTR +' SELECT   CAST(''-2'' AS VARCHAR(10))  AS PARENT_MMK_ID '
		                                    +'                 ,CAST(''-1'' AS VARCHAR(10))  AS MMK_ID '
		                                    +'                 ,CAST(''-1'' AS VARCHAR(10))  AS MMK_VAL '
		                                    +'                 ,CAST(''' +'
		                                    +'                 ,CAST(''-1'' AS VARCHAR(10)) AS MMK_NO '
		                                    +'                 ,CAST(''N'' AS VARCHAR(1)) AS ALLOW_DRILL_DOWN '
		                                    +'                 ,CAST(-1 AS INTEGER)  AS MMK_LEVEL '
		                                    +'                 ,CAST(-2 AS INTEGER)  AS DRILL_MMK_LEVEL '
		                                    +'                 ,CAST(''-2'' AS VARCHAR(10))  AS DRILL_MMK_ID '
											+'     FROM PMART.VW_MMK_BONUSFLAG_DIM DAT '
											+'  WHERE MMK_ID =  ''-1'' '
                                            +'  UNION ALL ';
	END IF;	
    SET SQLSTR = SQLSTR +'   SELECT  DAT.PARENT_MMK_ID '
	                                 +'                  ,CASE WHEN DAT.MMK_LEVEL=2 THEN DAT.PARENT_MMK_ID+DAT.MMK_ID ELSE DAT.MMK_ID END AS MMK_ID '
									 +'                  ,CASE WHEN DAT.MMK_LEVEL=2 THEN DAT.PARENT_MMK_ID+DAT.MMK_VAL ELSE DAT.MMK_VAL END AS MMK_VAL '
									 +'                  ,DAT.MMK_NAME AS MMK_NAME '
									 +'                  ,CASE WHEN DAT.MMK_LEVEL=2 THEN DAT.PARENT_MMK_ID+DAT.MMK_NO ELSE DAT.MMK_NO END AS MMK_NO '
									 +'                  ,DAT.ALLOW_DRILL_DOWN AS ALLOW_DRILL_DOWN '
									 +'                  ,DAT.MMK_LEVEL AS MMK_LEVEL '
	                                 +'                  ,DAT.MMK_LEVEL AS DRILL_MMK_LEVEL '
									 +'                  ,CASE WHEN DAT.MMK_LEVEL=2 THEN DAT.PARENT_MMK_ID+DAT.MMK_ID ELSE DAT.MMK_ID END AS DRILL_MMK_ID '
                                     +'      FROM PMART.VW_MMK_BONUSFLAG_DIM DAT '
									 + CONDWHERE
                                     +'  )DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;