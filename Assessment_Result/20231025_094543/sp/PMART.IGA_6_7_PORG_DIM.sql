REPLACE PROCEDURE PMART.IGA_6_7_PORG_DIM
(
	IN TIME_ID_S INTEGER,
	IN TIME_ID_E INTEGER,
	IN P_SHOWTYPE INTEGER,  
	IN P_ORGID INT
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000) ; 
	 DECLARE SQLSTR_UNION VARCHAR(2000) ; 
    CALL PMART.P_DROP_TABLE ('#VT_IGA_6_7_PORG_DIM');
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_IGA_6_7_PORG_DIM ( '
						 +'PARENT_ORG_ID INTEGER, '
						 +'ORG_ID INTEGER, '
	                     +'ORG_VAL INTEGER, '
						 +'ORG_SNAME VARCHAR(27)   , '
						 +'ORG_NO VARCHAR(11), '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'ORG_LEVEL SMALLINT, '
						 +'DRILL_ORG_LEVEL SMALLINT, '
						 +'DRILL_ORG_ID INTEGER '		
	                     +') UNIQUE PRIMARY  CHARINDEX( ORG_NO,ORG_ID) ON COMMIT PRESERVE ROWS; ';
    EXECUTE IMMEDIATE SQLSTR;   
	SET SQLSTR_UNION = ' SELECT DISTINCT O.RESPON_ID AS PARENT_ORG_ID,O.STORE_ID AS ORG_ID, O.STORE_ID AS ORG_VAL, (O.STORE_NO+''_''+O.STORE_NM) AS ORG_SNAME,O.STORE_NO (VARCHAR(11)) AS ORG_NO,''N'' AS ALLOW_DRILL_DOWN,4 AS ORG_LEVEL '
							   +' FROM PMART.LATEST_ORG_DIM O '
							   +' INNER JOIN (SELECT * FROM PDATA.OA_EXCL_STOR WHERE TIME_ID BETWEEN '+ TIME_ID_S +' AND '+ TIME_ID_E +' ) C '
							   +' ON O.STORE_ID = C.STORE_ID '
							   +' UNION '
							   +' SELECT BRANCH_ID AS PARENT_ORG_ID,RESPON_ID AS ORG_ID,RESPON_ID AS ORG_VAL,RESPON_NAME AS ORG_SNAME,RESPON_NO (VARCHAR(11)) AS ORG_NO,''Y'' AS ALLOW_DRILL_DOWN,3 AS ORG_LEVEL FROM PMART.ORG_RESPON '
							   +' UNION '
							   +' SELECT DEPT_ID AS PARENT_ORG_ID,BRANCH_ID AS ORG_ID,BRANCH_ID AS ORG_VAL,BRANCH_SNAME AS ORG_SNAME,BRANCH_NO (VARCHAR(11)) AS ORG_NO,''Y'' AS ALLOW_DRILL_DOWN,2 AS ORG_LEVEL FROM PMART.ORG_BRANCH '
							   +' UNION '
							   +' SELECT PDEPT_ID AS PARENT_ORG_ID,DEPT_ID AS ORG_ID,DEPT_ID AS ORG_VAL, DEPT_SNAME AS ORG_SNAME,DEPT_NO (VARCHAR(11)) AS ORG_NO,''Y'' AS ALLOW_DRILL_DOWN,1 AS ORG_LEVEL FROM PMART.ORG_DEPT '
							   +' UNION '
							   +' SELECT -1 (INTEGER) AS PARENT_ORG_ID,PDEPT_ID AS ORG_ID,PDEPT_ID AS ORG_VAL, PDEPT_SNAME AS ORG_SNAME,PDEPT_NO (VARCHAR(11)) AS ORG_NO,''Y'' AS ALLOW_DRILL_DOWN,0 AS ORG_LEVEL FROM PMART.ORG_PDEPT '
							   +' UNION '
							   +' SELECT DISTINCT -2  PARENT_ORG_ID, -1 AS ORG_ID, -1 AS ORG_VAL,CAST('''+'全公司'+''' AS VARCHAR(27)) AS ORG_SNAME, ''-1'' AS ORG_NO,''N'' AS ALLOW_DRILL_DOWN, -1 AS ORG_LEVEL FROM PMART.ORG_PDEPT ';
    SET SQLSTR = 'INSERT INTO #VT_IGA_6_7_PORG_DIM(PARENT_ORG_ID,ORG_ID,ORG_VAL,ORG_SNAME,ORG_NO,ALLOW_DRILL_DOWN,ORG_LEVEL,DRILL_ORG_LEVEL,DRILL_ORG_ID) '
	                     +'SELECT * FROM( ';
    IF P_SHOWTYPE = 2 THEN 	
		   SET SQLSTR = SQLSTR +' SELECT DAT.*,CASE WHEN ORG_LEVEL=1 THEN -1 ELSE (ORG_LEVEL-1) END AS DRILL_ORG_LEVEL,DAT.PARENT_ORG_ID AS DRILL_ORG_ID '
		                                    +' FROM ( '
											+ SQLSTR_UNION
											+' ) DAT '
                                            +' WHERE ORG_ID = ' + P_ORGID 
                                            +' UNION ALL ';
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
											+'     FROM ( '
											+ SQLSTR_UNION
											+' ) DAT '
											+'  WHERE ORG_ID =  -1 '
                                            +'  UNION ALL ';											
	END IF;	
    SET SQLSTR = SQLSTR +'  SELECT DAT.*,ORG_LEVEL AS DRILL_ORG_LEVEL,DAT.ORG_ID AS DRILL_ORG_ID '
                                     +'      FROM ( '
									 + SQLSTR_UNION
									 +' ) DAT '
                                     +'   WHERE PARENT_ORG_ID =  ' + P_ORGID
                                     +'  )DAT_Y ';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;