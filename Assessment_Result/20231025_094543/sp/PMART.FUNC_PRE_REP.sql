REPLACE PROCEDURE PMART.FUNC_PRE_REP
(
   IN P_PRE_PRDTYPE INTEGER,     	 
   IN P_PRE_PRDID VARCHAR(10),      
   IN P_REP_PRDTYPE INTEGER,     	 
   IN P_REP_PRDID VARCHAR(10)       
)
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000) ; 
	DECLARE PRD_COL_1 VARCHAR(20) ; 
	DECLARE PRD_COL_2 VARCHAR(20) ; 
	DECLARE PREPRDWHERE VARCHAR(500); 
	DECLARE REPPRDWHERE VARCHAR(500); 
	CALL PMART.P_DROP_TABLE ('#VT_FUNC_PRE_REP');
    IF P_PRE_PRDTYPE = 1  THEN
		SET PRD_COL_1 = ' KND_ID ' ;
	ELSEIF P_PRE_PRDTYPE = 2 THEN
		SET PRD_COL_1 = ' GRP_ID ' ;
	ELSEIF P_PRE_PRDTYPE = 3 THEN
		SET PRD_COL_1 = ' PRD_ID ' ;
	ELSEIF P_PRE_PRDTYPE = -1 THEN
		SET PRD_COL_1 = ' TOT_ID ' ;
	END IF; 
	IF P_REP_PRDTYPE = 1  THEN
		SET PRD_COL_2 = ' KND_ID ' ;
	ELSEIF P_REP_PRDTYPE = 2 THEN
		SET PRD_COL_2 = ' GRP_ID ' ;
	ELSEIF P_REP_PRDTYPE = 3 THEN
		SET PRD_COL_2 = ' PRD_ID ' ;
	ELSEIF P_REP_PRDTYPE = -1 THEN
		SET PRD_COL_2 = ' TOT_ID ' ;
	END IF; 
	SET PREPRDWHERE = 'AND PRE_FMCODE IN (SELECT DISTINCT PRD_ID FROM PMART.PRD_DIM WHERE '+PRD_COL_1+' = '''+ P_PRE_PRDID + ''') ';
    SET REPPRDWHERE ='AND P_FMCODE IN (SELECT DISTINCT PRD_ID FROM PMART.PRD_DIM WHERE '+PRD_COL_2+' = '''+ P_REP_PRDID + ''') ';
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_FUNC_PRE_REP ( '
						 +'PRD_ID VARCHAR(20), '
						 +'FM_NAME VARCHAR(100)   , '
						 +'ALLOW_DRILL_DOWN VARCHAR(1), '
						 +'PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_LEVEL SMALLINT, '
						 +'DRILL_PRD_ID VARCHAR(7) '						 
	                     +') UNIQUE PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS; ';
	EXECUTE IMMEDIATE SQLSTR;   
    SET SQLSTR = 'INSERT INTO #VT_FUNC_PRE_REP(PRD_ID,FM_NAME,ALLOW_DRILL_DOWN,PRD_LEVEL,DRILL_PRD_LEVEL,DRILL_PRD_ID) '
	                     +'SELECT PRD_ID , FM_NAME , ''N'' , 3,3 ,PRD_ID AS DRILL_PRD_ID FROM( ';
    SET SQLSTR = SQLSTR + ' SELECT DISTINCT D.PRE_FMCODE+'' X ''+ D.P_FMCODE AS PRD_ID '
									 + ' ,P1.PRD_NM+'' X ''+P2.PRD_NM AS FM_NAME'
									 + ' FROM PMART.FACT_ACTCODE_DTL D'
									 + ' LEFT JOIN PMART.PRD_DIM P1'
									 + ' ON D.PRE_FMCODE = P1.PRD_ID '
									 + ' LEFT JOIN PMART.PRD_DIM P2'
									 + ' ON D.P_FMCODE = P2.PRD_ID' 
									 + ' WHERE 1=1 '
									 + PREPRDWHERE
									 + REPPRDWHERE
									 +')A' ;
    EXECUTE IMMEDIATE SQLSTR;   
END SP;