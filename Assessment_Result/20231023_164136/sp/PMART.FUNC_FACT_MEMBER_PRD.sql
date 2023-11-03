REPLACE PROCEDURE PMART.FUNC_FACT_MEMBER_PRD
(
   IN P_RPTID SMALLINT,
   IN P_TIMEID VARCHAR(1000),
   IN P_TIMETYPE VARCHAR(1),   
   IN P_ORGTYPE SMALLINT,
   IN P_ORGID VARCHAR(20),
   IN P_DISTRICTTYPE SMALLINT,
   IN P_DISTRICT VARCHAR(20),
   IN P_PRDTYPE SMALLINT,
   IN P_PRDID VARCHAR(20)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(20000); 
   DECLARE ORGWHERE VARCHAR(2000);
   DECLARE PRDWHERE VARCHAR(50);
   DECLARE PRD_FSELECT VARCHAR(50);
   DECLARE PRD_SELECT VARCHAR(50);
   DECLARE BASETABLE VARCHAR(50);
   SET ORGWHERE = '  ';
   SET PRDWHERE = ' WHERE 1=1 ';
	IF P_ORGTYPE <> -1 THEN
		SET ORGWHERE = ' AND B.ORG_ID  = ' + TRIM(P_ORGID); 
	ELSE
		SET ORGWHERE = ' AND B.ORG_ID  = -1 '; 
	END IF;
	IF P_DISTRICTTYPE <> -1 THEN
		SET ORGWHERE =ORGWHERE + ' AND B.SHOP_DIST = '''  + P_DISTRICT + ''' ';
	ELSE
		SET ORGWHERE = ORGWHERE + ' AND B.SHOP_DIST = '''  + TRIM(P_DISTRICTTYPE) + ''' ';
	END IF;
	IF P_PRDTYPE = 1 THEN
		SET PRDWHERE = ' WHERE P.KND_ID  = ''' + TRIM(P_PRDID)+'''';  
		SET PRD_FSELECT = ' P.KND_ID ';
		SET PRD_SELECT = ' P.GRP_ID ';
	ELSEIF P_PRDTYPE = 2 THEN
		SET PRDWHERE = ' WHERE P.GRP_ID = ''' + TRIM(P_PRDID)+'''';
		SET PRD_FSELECT = ' P.GRP_ID ';
		SET PRD_SELECT = ' P.PRD_ID ';
	ELSEIF P_PRDTYPE = 3 THEN
		SET PRDWHERE = ' WHERE P.PRD_ID = ''' + TRIM(P_PRDID)+'''';
		SET PRD_FSELECT = ' P.PRD_ID ';
		SET PRD_SELECT = ' P.PRD_ID ';
	ELSE
		SET PRDWHERE = PRDWHERE; 	
		SET PRD_FSELECT = '''-1''';
		SET PRD_SELECT = ' P.KND_ID ';
	END IF;  
   SET BASETABLE = ' PMART.FACT_MEMBER_ORG ';
	CALL PMART.P_DROP_TABLE ('#VT_FACT_MEMBER_TMP');
	CALL PMART.P_DROP_TABLE ('#VT_FACT_MEMBER_PRD');
	CALL PMART.P_DROP_TABLE ('#VT_YDIM');
	CALL PMART.P_DROP_TABLE ('#VT_PRD');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_PRD AS( '
                      +'     SELECT '+PRD_FSELECT+' AS PRD_ID,2 AS DATA_TYPE FROM PMART.PRD_DIM P ' + PRDWHERE 
					  +'     UNION '
					  +'     SELECT '+PRD_SELECT+' AS PRD_ID,4 AS DATA_TYPE FROM PMART.PRD_DIM P ' + PRDWHERE 
					  + '  ) WITH DATA PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS;';			  
EXECUTE IMMEDIATE SQLSTR;   	
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_MEMBER_TMP AS( '
						 + ' SELECT P.DATA_TYPE,B.TIME_ID ,B.PRD_ID AS Y_DIM '
						 + ' ,CAST(B.STORE_NUM AS DECIMAL(16,0)) AS STORE_NUM '
						 + ' ,CAST(B.ALL_CNT AS DECIMAL(16,0)) AS ALL_CNT '
						 + ' ,CAST(B.ALL_AMT AS DECIMAL(16,0)) AS ALL_AMT '
						 + ' ,CAST(B.ALL_NUM AS DECIMAL(16,0)) AS ALL_NUM '
						 + ' ,CAST(B.MEMBER_CNT AS DECIMAL(16,0)) AS MEMBER_CNT '
						 + ' ,CAST(B.MEMBER_AMT AS DECIMAL(16,0)) AS MEMBER_AMT '
						 + ' ,CAST(B.MEMBER_NUM AS DECIMAL(16,0)) AS MEMBER_NUM '
						 + ' ,CAST(B.MEMBER_REP AS DECIMAL(16,0)) AS MEMBER_REP '
						 + ' ,CAST(B.MEMBER_REP_CNT AS DECIMAL(16,0)) AS MEMBER_REP_CNT '
						 + ' FROM '+BASETABLE+' B '
						 + ' JOIN #VT_PRD P ON B.PRD_ID = P.PRD_ID '
						 + ' WHERE B.TIME_ID IN ( '+P_TIMEID+' ) '+ORGWHERE
						 + ' ) WITH DATA PRIMARY  CHARINDEX(Y_DIM,TIME_ID) ON COMMIT PRESERVE ROWS;';	
EXECUTE IMMEDIATE SQLSTR;   
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_MEMBER_PRD AS( '
						 + ' SELECT CASE WHEN DATA_TYPE = 2 THEN 1 ELSE 3 END AS DATA_TYPE'
						 + ' ,CAST(-1 AS INTEGER)  AS TIME_ID ,Y_DIM '
						 + ' ,SUM(STORE_NUM) AS STORE_NUM '
						 + ' ,SUM(ALL_CNT) AS ALL_CNT '
						 + ' ,SUM(ALL_AMT) AS ALL_AMT '
						 + ' ,SUM(ALL_NUM) AS ALL_NUM '
						 + ' ,CAST(SUM(ALL_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS ALL_NUM_PSD '
						 + ' ,CAST(SUM(ALL_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS ALL_AMT_PSD '
						 + ' ,SUM(MEMBER_CNT) AS MEMBER_CNT '
						 + ' ,SUM(MEMBER_AMT) AS MEMBER_AMT '
						 + ' ,SUM(MEMBER_NUM) AS MEMBER_NUM '
						 + ' ,CAST(SUM(MEMBER_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS MEMBER_NUM_PSD '
						 + ' ,CAST(SUM(MEMBER_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS MEMBER_AMT_PSD '
						 + ' ,CAST(SUM(MEMBER_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_AMT) = 0 THEN 1 ELSE SUM(ALL_AMT) END AS MEMBER_AMT_RATE '
						 + ' ,CAST(SUM(MEMBER_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_NUM) = 0 THEN 1 ELSE SUM(ALL_NUM) END AS MEMBER_NUM_RATE '
						 + ' ,CAST(SUM(MEMBER_CNT) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_CNT) = 0 THEN 1 ELSE SUM(ALL_CNT) END AS MEMBER_CNT_RATE '
						 + ' ,NULL AS MEMBER_REP '
						 + ' ,NULL AS MEMBER_REP_CNT '
						 + ' FROM #VT_FACT_MEMBER_TMP '
						 + ' GROUP BY Y_DIM,DATA_TYPE'
						 + ' UNION ALL '
						 + ' SELECT DATA_TYPE ,TIME_ID ,Y_DIM '
						 + ' ,SUM(CAST(STORE_NUM AS DECIMAL(16,0))) AS STORE_NUM '
						 + ' ,SUM(CAST(ALL_CNT AS DECIMAL(16,0))) AS ALL_CNT '
						 + ' ,SUM(CAST(ALL_AMT AS DECIMAL(16,0))) AS ALL_AMT '
						 + ' ,SUM(CAST(ALL_NUM AS DECIMAL(16,0))) AS ALL_NUM '
						 + ' ,CAST(SUM(ALL_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS ALL_NUM_PSD '
						 + ' ,CAST(SUM(ALL_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS ALL_AMT_PSD '
						 + ' ,SUM(CAST(MEMBER_CNT AS DECIMAL(16,0))) AS MEMBER_CNT '
						 + ' ,SUM(CAST(MEMBER_AMT AS DECIMAL(16,0))) AS MEMBER_AMT '
						 + ' ,SUM(CAST(MEMBER_NUM AS DECIMAL(16,0))) AS MEMBER_NUM '
						 + ' ,CAST(SUM(MEMBER_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS MEMBER_NUM_PSD '
						 + ' ,CAST(SUM(MEMBER_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(STORE_NUM) = 0 THEN 1 ELSE SUM(STORE_NUM) END AS MEMBER_AMT_PSD '
						 + ' ,CAST(SUM(MEMBER_AMT) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_AMT) = 0 THEN 1 ELSE SUM(ALL_AMT) END AS MEMBER_AMT_RATE '
						 + ' ,CAST(SUM(MEMBER_NUM) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_NUM) = 0 THEN 1 ELSE SUM(ALL_NUM) END AS MEMBER_NUM_RATE '
						 + ' ,CAST(SUM(MEMBER_CNT) AS DECIMAL(16,2))/CASE WHEN SUM(ALL_CNT) = 0 THEN 1 ELSE SUM(ALL_CNT) END AS MEMBER_CNT_RATE '
						 + ' ,SUM(CAST(MEMBER_REP AS DECIMAL(16,0))) AS MEMBER_REP '
						 + ' ,SUM(CAST(MEMBER_REP_CNT AS DECIMAL(16,0))) AS MEMBER_REP_CNT '
						 + ' FROM #VT_FACT_MEMBER_TMP '
						 + ' GROUP BY TIME_ID ,Y_DIM,DATA_TYPE '
						 + ' ) WITH DATA PRIMARY INDEX(DATA_TYPE,TIME_ID,Y_DIM) ON COMMIT PRESERVE ROWS;';	
EXECUTE IMMEDIATE SQLSTR;   
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_YDIM AS( '
					  +' SELECT DISTINCT S.*,CASE WHEN S.PRD_LEVEL=1 THEN -1 ELSE (S.PRD_LEVEL-1) END AS DRILL_PRD_LEVEL,S.PARENT_PRD_ID AS DRILL_PRD_ID ' 
	                  +' FROM #VT_FACT_MEMBER_PRD V  '
					  +' JOIN PMART.VW_PRD_DIM_NEW S ON V.Y_DIM = S.PRD_ID AND V.DATA_TYPE = 1 '
					  +' UNION ALL '
					  +' SELECT DISTINCT S.*,S.PRD_LEVEL AS DRILL_PRD_LEVEL,S.PRD_ID AS DRILL_PRD_ID ' 
	                  +' FROM #VT_FACT_MEMBER_PRD V  '
					  +' JOIN PMART.VW_PRD_DIM_NEW S ON V.Y_DIM = S.PRD_ID AND V.DATA_TYPE = 3 '
					  + ' ) WITH DATA PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS;';	
EXECUTE IMMEDIATE SQLSTR;   
END SP;