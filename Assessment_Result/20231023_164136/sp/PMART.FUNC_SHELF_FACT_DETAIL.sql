REPLACE PROCEDURE PMART.FUNC_SHELF_FACT_DETAIL(
   IN P_TIME_ID INTEGER,
   IN P_SHELF_ID INTEGER
)
SP:BEGIN
   DECLARE SQLSTR VARCHAR(10000); 
	CALL PMART.P_DROP_TABLE ('#VT_FUNC_SHELF_FACT_DETAIL');
LOCKING PMART.SHELF_DIM FOR ACCESS;
LOCKING PMART.SHELF_FACT FOR ACCESS;
LOCKING PMART.SHELF_FACT_DETAIL FOR ACCESS;
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_SHELF_FACT_DETAIL AS( '
			   +'SELECT 1 AS DATA_LEVEL '
			   +', F.TIME_ID '
			   +', CAST('''' AS CHAR(10)) AS SHELF_TYPE_NAME '
			   +', CAST('''' AS CHAR(10)) AS SHELF_GROUP_NAME '
			   +', D.SHELF_NAME '
			   +', CAST(''99'' AS INTEGER) AS LAYER_NO '
			   +', CAST('''' AS CHAR(10)) AS KND_NO '
			   +', CAST('''' AS CHAR(10)) AS GRP_NO '
			   +', CAST('''' AS CHAR(7)) AS PRD_NO '
			   +', CAST('''' AS CHAR(32)) AS PRD_NM '
			   +', CAST('''' AS CHAR(10)) AS PRD_CMBND '
			   +', CAST('''' AS DECIMAL(8,2)) AS PRD_CSUNT '
			   +', CAST('''' AS INTEGER) AS PRD_SLUNT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_LENGTH '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_HEIGHT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_WIDTH '
			   +', F.ST_NUM '
			   +', F.INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', F.SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_AMT, F.ST_NUM) AS DECIMAL(10,2))AS SALES_AMT_PSD '
			   +', F.REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.REAL_SALES_AMT, F.ST_NUM)AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', F.INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', F.SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +'FROM (SELECT TIME_ID , '+P_SHELF_ID+ 'AS SHELF_ID '
                           +'           ,SUM(ST_NUM) AS ST_NUM '
			   +'           ,SUM(INPRD_AMT) AS INPRD_AMT '
			   +'           ,SUM(SALES_AMT) AS SALES_AMT '
			   +'           ,SUM(REAL_SALES_AMT) AS REAL_SALES_AMT '
			   +'           ,SUM(INPRD_CNT) AS INPRD_CNT '
			   +'           ,SUM(SALES_CNT) AS SALES_CNT '
			   +'        FROM PMART.SHELF_FACT '
			   +'       WHERE SHELF_LEVEL = 4 '
			   +'         AND (SHELF_ID  = '+P_SHELF_ID 
			   +'          OR  SHELF_ID2 = '+P_SHELF_ID 
			   +'             ) GROUP BY TIME_ID '			   
			   +')F '
			   +'JOIN (SELECT DISTINCT SHELF_ID,SHELF_NAME,TIME_ID_S,TIME_ID_E FROM PMART.SHELF_DIM WHERE L_DAY_ID = '+P_TIME_ID+') D ON F.SHELF_ID = D.SHELF_ID AND F.TIME_ID >= D.TIME_ID_S AND F.TIME_ID <= D.TIME_ID_E '			   
			   +' UNION ALL '
			   +'SELECT 2 AS DATA_LEVEL '
			   +', A1.TIME_ID '
			   +', CAST('''' AS CHAR(10)) AS SHELF_TYPE_NAME '
			   +', CAST('''' AS CHAR(10)) AS SHELF_GROUP_NAME '
			   +', CAST('''+'第'+'''+CAST(A1.LAYER_NO AS CHAR(1))+'''+'層'+''' AS CHAR(10)) AS SHELF_NAME '
			   +', A1.LAYER_NO '
			   +', CAST('''' AS CHAR(10)) AS KND_NO '
			   +', CAST('''' AS CHAR(10)) AS GRP_NO '
			   +', CAST('''' AS CHAR(7)) AS PRD_NO '
			   +', CAST('''' AS CHAR(32)) AS PRD_NM '
			   +', CAST('''' AS CHAR(10)) AS PRD_CMBND '
			   +', CAST('''' AS DECIMAL(8,2)) AS PRD_CSUNT '
			   +', CAST('''' AS INTEGER) AS PRD_SLUNT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_LENGTH '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_HEIGHT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_WIDTH '
			   +', A1.ST_NUM AS ST_NUM '
			   +', SUM(A1.INPRD_AMT) AS INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A1.INPRD_AMT) , A1.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', SUM(A1.SALES_AMT) AS SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A1.SALES_AMT), A1.ST_NUM) AS DECIMAL(10,2)) AS SALES_AMT_PSD '
			   +', SUM(A1.REAL_SALES_AMT) AS REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A1.REAL_SALES_AMT), A1.ST_NUM) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', SUM(A1.INPRD_CNT) AS INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A1.INPRD_CNT), A1.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', SUM(A1.SALES_CNT) AS SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A1.SALES_CNT), A1.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +'FROM( SELECT F.TIME_ID, D.LAYER_NO2 AS LAYER_NO, F.ST_NUM, F.INPRD_AMT, F.SALES_AMT, F.REAL_SALES_AMT '
			   +'         , F.INPRD_CNT, F.SALES_CNT FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D '
			   +'  ON D.STYLE=2 AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID '
			   +' AND F.PRD_ID = D.PRD_ID '
			   +' AND F.TIME_ID >= D.TIME_ID_S '
			   +' AND F.TIME_ID <= D.TIME_ID_E '
			   +' UNION ALL '
			   +'SELECT F.TIME_ID '
			   +', D.LAYER_NO '
			   +', F.ST_NUM '
			   +', F.INPRD_AMT '
			   +', F.SALES_AMT '
			   +', F.REAL_SALES_AMT '
			   +', F.INPRD_CNT '
			   +', F.SALES_CNT '
			   +'FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D ' 
			   +'  ON D.STYLE=1  '
			   +' AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID  '
			   +' AND F.PRD_ID = D.PRD_ID  '
			   +' AND F.TIME_ID >= D.TIME_ID_S  '
			   +' AND F.TIME_ID <= D.TIME_ID_E) A1 '
			   +' GROUP BY A1.TIME_ID,A1.LAYER_NO,A1.ST_NUM '
			   +' UNION ALL '
			   +'SELECT 3 AS DATA_LEVEL '
			   +', A2.TIME_ID '
			   +', CAST('''' AS CHAR(10)) AS SHELF_TYPE_NAME '
			   +', CAST('''' AS CHAR(10)) AS SHELF_GROUP_NAME '
			   +', CAST('''+'一般貨架小計'+''' AS CHAR(10)) AS SHELF_NAME '
			   +', A2.LAYER_NO '
			   +', CAST('''' AS CHAR(10)) AS KND_NO '
			   +', CAST('''' AS CHAR(10)) AS GRP_NO '
			   +', CAST('''' AS CHAR(7)) AS PRD_NO '
			   +', CAST('''' AS CHAR(32)) AS PRD_NM '
			   +', CAST('''' AS CHAR(10)) AS PRD_CMBND '
			   +', CAST('''' AS DECIMAL(8,2)) AS PRD_CSUNT '
			   +', CAST('''' AS INTEGER) AS PRD_SLUNT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_LENGTH '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_HEIGHT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_WIDTH '
			   +', A2.ST_NUM AS ST_NUM '
			   +', SUM(A2.INPRD_AMT) AS INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.INPRD_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', SUM(A2.SALES_AMT) AS SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.SALES_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS SALES_AMT_PSD '
			   +', SUM(A2.REAL_SALES_AMT) AS REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.REAL_SALES_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', SUM(A2.INPRD_CNT) AS INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.INPRD_CNT), A2.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', SUM(A2.SALES_CNT) AS SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.SALES_CNT), A2.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +'FROM( SELECT F.TIME_ID, D.LAYER_NO, F.ST_NUM, F.INPRD_AMT, F.SALES_AMT, F.REAL_SALES_AMT '
			   +'           , F.INPRD_CNT, F.SALES_CNT FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D  '
			   +'  ON D.STYLE=1  '
			   +' AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID  '
			   +' AND F.PRD_ID = D.PRD_ID  '
			   +' AND F.TIME_ID >= D.TIME_ID_S  '
			   +' AND F.TIME_ID <= D.TIME_ID_E) A2 '
			   +'GROUP BY A2.TIME_ID,A2.LAYER_NO,A2.ST_NUM '
			   +' UNION ALL ' 
			   +'SELECT 4 AS DATA_LEVEL '
			   +', A2.TIME_ID '
			   +', CAST('''' AS CHAR(10)) AS SHELF_TYPE_NAME '
			   +', CAST('''' AS CHAR(10)) AS SHELF_GROUP_NAME '
			   +', CAST('''+'架上架小計'+''' AS CHAR(10)) AS SHELF_NAME '
			   +', A2.LAYER_NO '
			   +', CAST('''' AS CHAR(10)) AS KND_NO '
			   +', CAST('''' AS CHAR(10)) AS GRP_NO '
			   +', CAST('''' AS CHAR(7)) AS PRD_NO '
			   +', CAST('''' AS CHAR(32)) AS PRD_NM '
			   +', CAST('''' AS CHAR(10)) AS PRD_CMBND '
			   +', CAST('''' AS DECIMAL(8,2)) AS PRD_CSUNT '
			   +', CAST('''' AS DECIMAL(8,2)) AS PRD_SLUNT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_LENGTH '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_HEIGHT '
			   +', CAST('''' AS DECIMAL(6,1)) AS UT_WIDTH '
			   +', A2.ST_NUM AS ST_NUM '
			   +', SUM(A2.INPRD_AMT) AS INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.INPRD_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', SUM(A2.SALES_AMT) AS SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.SALES_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS SALES_AMT_PSD '
			   +', SUM(A2.REAL_SALES_AMT) AS REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.REAL_SALES_AMT), A2.ST_NUM) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', SUM(A2.INPRD_CNT) AS INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.INPRD_CNT), A2.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', SUM(A2.SALES_CNT) AS SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(SUM(A2.SALES_CNT), A2.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +' FROM( SELECT F.TIME_ID, D.LAYER_NO2 AS LAYER_NO, F.ST_NUM, F.INPRD_AMT, F.SALES_AMT, F.REAL_SALES_AMT '
			   +'          , F.INPRD_CNT, F.SALES_CNT FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D  '
			   +'  ON D.STYLE=2  '
			   +' AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID  '
			   +' AND F.PRD_ID = D.PRD_ID  '
			   +' AND F.TIME_ID >= D.TIME_ID_S  '
			   +' AND F.TIME_ID <= D.TIME_ID_E) A2 '
			   +'GROUP BY A2.TIME_ID,A2.LAYER_NO,A2.ST_NUM '
			   +' UNION ALL '
			   +'SELECT 5 AS DATA_LEVEL '
			   +', F.TIME_ID '
			   +', D.SHELF_TYPE_NAME '
			   +', D.SHELF_GROUP_NAME '
			   +', D.SHELF_NAME2 AS SHELF_NAME '
			   +', D.LAYER_NO2 AS LAYER_NO '
			   +', C.KND_NO '
			   +', C.GRP_NO '
			   +', C.PRD_NO '
			   +', C.PRD_NM '
			   +', C.PRD_CMBND '
			   +', C.PRD_CSUNT '
			   +', C.PRD_SLUNT '
			   +', T.UT_LENGTH '
			   +', T.UT_HEIGHT '
			   +', T.UT_WIDTH '
			   +', F.ST_NUM '
			   +', F.INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', F.SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS SALES_AMT_PSD '
			   +', F.REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.REAL_SALES_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', F.INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', F.SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +'FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D '
			   +'  ON D.STYLE=2 '
			   +' AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID2='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID '
			   +' AND F.PRD_ID = D.PRD_ID '
			   +' AND F.TIME_ID >= D.TIME_ID_S '
			   +' AND F.TIME_ID <= D.TIME_ID_E '
			   +'JOIN PMART.PRD_DIM C ON C.PRD_ID = F.PRD_ID '
			   +'JOIN PDATA.PBMCMDT T ON C.PRD_NO = T.FM_CODE '
			   +' UNION ALL '
			   +'SELECT 5 AS DATA_LEVEL '
			   +', F.TIME_ID '
			   +', D.SHELF_TYPE_NAME '
			   +', D.SHELF_GROUP_NAME '
			   +', D.SHELF_NAME '
			   +', D.LAYER_NO '
			   +',C.KND_NO '
			   +', C.GRP_NO '
			   +', C.PRD_NO '
			   +', C.PRD_NM '
			   +', C.PRD_CMBND '
			   +', C.PRD_CSUNT '
			   +', C.PRD_SLUNT '
			   +', T.UT_LENGTH '
			   +', T.UT_HEIGHT '
			   +', T.UT_WIDTH '
			   +', F.ST_NUM '
			   +', F.INPRD_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_AMT_PSD '
			   +', F.SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS SALES_AMT_PSD '
			   +', F.REAL_SALES_AMT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.REAL_SALES_AMT, F.ST_NUM) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD '
			   +', F.INPRD_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.INPRD_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS INPRD_CNT_PSD '
			   +', F.SALES_CNT '
			   +', CAST(PMART.DIVIDE_BY_ZERO(F.SALES_CNT, F.ST_NUM) AS DECIMAL(10,2)) AS SALES_CNT_PSD '
			   +'FROM PMART.SHELF_FACT_DETAIL F '
			   +'JOIN PMART.SHELF_DIM D '
			   +'  ON D.STYLE=1 '
			   +' AND D.L_DAY_ID = '+P_TIME_ID
			   +' AND D.SHELF_ID='+P_SHELF_ID
			   +' AND F.SHELF_ID = D.SHELF_ID ' 
			   +' AND F.PRD_ID = D.PRD_ID ' 
			   +' AND F.TIME_ID >= D.TIME_ID_S ' 
			   +' AND F.TIME_ID <= D.TIME_ID_E '
			   +'JOIN PMART.PRD_DIM C ON C.PRD_ID = F.PRD_ID '
			   +'JOIN PDATA.PBMCMDT T ON C.PRD_NO = T.FM_CODE '
			   +') WITH DATA PRIMARY INDEX(DATA_LEVEL,TIME_ID, SHELF_NAME, LAYER_NO) ON COMMIT PRESERVE ROWS;';
		EXECUTE IMMEDIATE SQLSTR;
		INSERT INTO #VT_FUNC_SHELF_FACT_DETAIL (
		 DATA_LEVEL
                ,TIME_ID
                ,SHELF_TYPE_NAME
                ,SHELF_GROUP_NAME
                ,SHELF_NAME
                ,LAYER_NO
                ,KND_NO
                ,GRP_NO
                ,PRD_NO
                ,PRD_NM
                ,PRD_CMBND
                ,PRD_CSUNT
                ,PRD_SLUNT
                ,UT_LENGTH
                ,UT_HEIGHT
                ,UT_WIDTH
                ,ST_NUM
                ,INPRD_AMT
                ,INPRD_AMT_PSD
                ,SALES_AMT
                ,SALES_AMT_PSD
                ,REAL_SALES_AMT
                ,REAL_SALES_AMT_PSD
                ,INPRD_CNT
                ,INPRD_CNT_PSD
                ,SALES_CNT
                ,SALES_CNT_PSD
                )
                SELECT 
                 DATA_LEVEL
                ,-1 AS TIME_ID
                ,SHELF_TYPE_NAME
                ,SHELF_GROUP_NAME
                ,SHELF_NAME
                ,LAYER_NO
                ,KND_NO
                ,GRP_NO
                ,PRD_NO
                ,PRD_NM
                ,PRD_CMBND
                ,0 AS PRD_CSUNT
                ,0 AS PRD_SLUNT
                ,0 AS UT_LENGTH
                ,0 AS UT_HEIGHT
                ,0 AS UT_WIDTH
                ,SUM(ST_NUM) AS ST_NUM
                ,SUM(INPRD_AMT) AS INPRD_AMT
                ,CAST(PMART.DIVIDE_BY_ZERO(SUM(INPRD_AMT),SUM(ST_NUM)) AS DECIMAL(10,2)) AS INPRD_AMT_PSD
                ,SUM(SALES_AMT) AS SALES_AMT
                ,CAST(PMART.DIVIDE_BY_ZERO(SUM(SALES_AMT),SUM(ST_NUM)) AS DECIMAL(10,2)) AS SALES_AMT_PSD		
                ,SUM(REAL_SALES_AMT) AS REAL_SALES_AMT
                ,CAST(PMART.DIVIDE_BY_ZERO(SUM(REAL_SALES_AMT),SUM(ST_NUM)) AS DECIMAL(10,2)) AS REAL_SALES_AMT_PSD		
                ,SUM(INPRD_CNT) AS INPRD_CNT
                ,CAST(PMART.DIVIDE_BY_ZERO(SUM(INPRD_CNT),SUM(ST_NUM)) AS DECIMAL(10,2)) AS INPRD_CNT_PSD
                ,SUM(SALES_CNT) AS SALES_CNT
                ,CAST(PMART.DIVIDE_BY_ZERO(SUM(SALES_CNT),SUM(ST_NUM)) AS DECIMAL(10,2)) AS SALES_CNT_PSD
		FROM #VT_FUNC_SHELF_FACT_DETAIL 				
                GROUP BY 
                 DATA_LEVEL
                ,SHELF_TYPE_NAME
                ,SHELF_GROUP_NAME
                ,SHELF_NAME
                ,LAYER_NO
                ,KND_NO
                ,GRP_NO
                ,PRD_NO
                ,PRD_NM
		,PRD_CMBND
                ;	
END SP;