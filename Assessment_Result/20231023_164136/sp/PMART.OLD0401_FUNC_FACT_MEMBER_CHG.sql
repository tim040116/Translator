REPLACE PROCEDURE PMART.OLD0401_FUNC_FACT_MEMBER_CHG
(
   IN P_RPTID SMALLINT,
   IN P_TIMEID VARCHAR(1000),
   IN P_TIMETYPE VARCHAR(1),     
   IN P_ORGTYPE SMALLINT,
   IN P_ORGID VARCHAR(20),   
   IN P_PRDTYPE SMALLINT,
   IN P_PRDID VARCHAR(20)   
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(20000);
   DECLARE TIME_SELECT VARCHAR(500);
   DECLARE SELECT_CALCNT VARCHAR(200);   
   DECLARE ORGWHERE VARCHAR(2000);
   DECLARE ORG_FSELECT VARCHAR(50);
   DECLARE ORG_SELECT VARCHAR(50);  
   DECLARE PRDWHERE VARCHAR(50);
   DECLARE PRD_FSELECT VARCHAR(50);
   DECLARE PRD_SELECT VARCHAR(50);
   DECLARE JOINTABLE VARCHAR(50);
   DECLARE JOINPRDCOLUMN VARCHAR(20);
   DECLARE SRCTYPE VARCHAR(20);
   DECLARE BASETABLE VARCHAR(50);
   DECLARE Y_DIM VARCHAR(20);
   DECLARE TIMEWHERE_CALCNT VARCHAR(500);
   SET TIME_SELECT = ' AND B.TIME_ID IN ('+P_TIMEID+') ';
   IF P_TIMETYPE = 'Y' THEN
	   SET TIMEWHERE_CALCNT = '  WHERE L_YEAR_ID IN ('+P_TIMEID+')';
   ELSEIF P_TIMETYPE = 'M' THEN
	   SET TIMEWHERE_CALCNT = '  WHERE L_MONTH_ID IN ('+P_TIMEID+')';
   ELSEIF P_TIMETYPE = 'D' THEN
	   SET TIMEWHERE_CALCNT = '  WHERE L_DAY_ID IN ('+P_TIMEID+')';
   ELSEIF  P_TIMETYPE = 'W' THEN	
       SET TIMEWHERE_CALCNT = '  WHERE L_WEEK_ID IN ('+P_TIMEID+')';
   END IF;
   SET ORGWHERE = '  ';
   SET PRDWHERE = ' WHERE 1=1 ';
	IF P_ORGTYPE = 0 THEN
		SET ORGWHERE = ORGWHERE + ' AND S.PDEPT_ID  = ' + TRIM(P_ORGID); 
		SET ORG_FSELECT = ' S.PDEPT_ID ';
		SET ORG_SELECT = ' S.DEPT_ID ';
	ELSEIF P_ORGTYPE = 1 THEN
		SET ORGWHERE = ORGWHERE + ' AND S.DEPT_ID  = ' + TRIM(P_ORGID); 
		SET ORG_FSELECT = ' S.DEPT_ID ';
		SET ORG_SELECT = ' S.BRANCH_ID ';
	ELSEIF P_ORGTYPE = 2 THEN
		SET ORGWHERE = ORGWHERE + ' AND S.BRANCH_ID = ' + TRIM(P_ORGID);
		SET ORG_FSELECT = ' S.BRANCH_ID ';
		SET ORG_SELECT = ' S.RESPON_ID ';
	ELSEIF P_ORGTYPE = 3 THEN
		SET ORGWHERE = ORGWHERE + ' AND S.RESPON_ID = ' + TRIM(P_ORGID);
		SET ORG_FSELECT = ' S.RESPON_ID ';
		SET ORG_SELECT  = ' S.OSTORE_ID ';
	ELSEIF P_ORGTYPE = 4 THEN
		SET ORGWHERE = ORGWHERE + ' AND S.OSTORE_ID = ' + TRIM(P_ORGID);
		SET ORG_FSELECT = ' S.OSTORE_ID ';
		SET ORG_SELECT = ' S.OSTORE_ID ';
	ELSE
		SET ORGWHERE = ORGWHERE;	
		SET ORG_FSELECT = ' CAST(''-1'' AS INTEGER) ';
		SET ORG_SELECT = ' S.PDEPT_ID ';
	END IF; 
	IF P_PRDTYPE = 1 THEN
		SET PRDWHERE = ' AND P.KND_ID  = ' + TRIM(P_PRDID);  
		SET PRD_FSELECT = ' P.KND_ID ';
		SET PRD_SELECT = ' P.GRP_ID ';
	ELSEIF P_PRDTYPE = 2 THEN
		SET PRDWHERE = ' AND P.GRP_ID = ' + TRIM(P_PRDID);
		SET PRD_FSELECT = ' P.GRP_ID ';
		SET PRD_SELECT = ' P.PRD_ID ';
	ELSEIF P_PRDTYPE = 3 THEN
		SET PRDWHERE = ' AND P.PRD_ID = ' + TRIM(P_PRDID);
		SET PRD_FSELECT = ' P.PRD_ID ';
		SET PRD_SELECT = ' P.PRD_ID ';
	ELSE
		SET PRDWHERE = PRDWHERE; 	
		SET PRD_FSELECT = ' CAST(''-1'' AS INTEGER) ';
		SET PRD_SELECT = ' P.KND_ID ';
	END IF;  
   IF P_RPTID = 1 THEN
	   SET BASETABLE = ' PMART.FACT_MEMBER_CHG ';
	   SET Y_DIM = ' B.PRD_ID ';
	   SET JOINTABLE = ' JOIN #VT_PRD P ON B.PRD_ID = P.PRD_ID ';
	   IF P_ORGTYPE = -1 THEN
			SET SELECT_CALCNT = ' AND B.ORG_ID = -1 ';
		ELSE
			SET SELECT_CALCNT = ' AND B.ORG_ID = '+P_ORGID;
		END IF;		
   END IF;
	SET JOINPRDCOLUMN = ' D.PRE_FMCODE ';
	CALL PMART.P_DROP_TABLE ('#VT_FACT_MEMBER_CHG_TMP');
	CALL PMART.P_DROP_TABLE ('#VT_FACT_MEMBER_CHG');
	CALL PMART.P_DROP_TABLE ('#VT_FACT_MEMBER_CHG_TXDAY');	
	CALL PMART.P_DROP_TABLE ('#VT_ORG');
	CALL PMART.P_DROP_TABLE ('#VT_PRD');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE SYSDBA.#VT_ORG AS( '
                      +'     SELECT '+ORG_FSELECT+' AS ORG_ID FROM PMART.LAST_ORG_STORE S WHERE 1=1 ' + ORGWHERE 
					  +'     UNION '
					  +'     SELECT '+ORG_SELECT+' AS ORG_ID FROM PMART.LAST_ORG_STORE S WHERE 1=1 ' + ORGWHERE 					
					  + '  ) WITH DATA PRIMARY INDEX(ORG_ID) ON COMMIT PRESERVE ROWS;';			  
EXECUTE IMMEDIATE SQLSTR;   
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_PRD AS( '
                      +'     SELECT '+PRD_FSELECT+' AS PRD_ID FROM PMART.PRD_DIM P ' 
					  +'     INNER JOIN PDATA.PAS_PACK_ITEMD D ON P.PRD_ID = '+ JOINPRDCOLUMN + PRDWHERE 
					  +'     UNION '
					  +'     SELECT '+PRD_SELECT+' AS PRD_ID FROM PMART.PRD_DIM P ' 
					  +'     INNER JOIN PDATA.PAS_PACK_ITEMD D ON P.PRD_ID = '+ JOINPRDCOLUMN + PRDWHERE 
					  + '  ) WITH DATA PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS;';			  
EXECUTE IMMEDIATE SQLSTR;   	
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_MEMBER_CHG_TMP AS( '
						 + ' SELECT B.TIME_ID ,'+Y_DIM+' AS Y_DIM '					
						 + ' ,B.CHG_CNT ,B.CHG_TXCNT ,B.CHG_QTY ,B.CHG_AMT '						
						 + ' FROM '+BASETABLE+' B '
						 + ' JOIN  #VT_ORG O ON B.ORG_ID = O.ORG_ID '+JOINTABLE+TIME_SELECT+SELECT_CALCNT
						 + ' ) WITH DATA PRIMARY  CHARINDEX(Y_DIM,TIME_ID) ON COMMIT PRESERVE ROWS;';	
EXECUTE IMMEDIATE SQLSTR;   
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_MEMBER_CHG AS( '
						 + ' SELECT  '
						 + '          CASE WHEN (TIME_ID<>-1 AND Y_DIM<>CAST(-1 AS VARCHAR(2)))  THEN 4  '
						 + '                      WHEN (TIME_ID=-1 AND Y_DIM<>CAST(-1 AS VARCHAR(2)))  THEN 2 '
						 + '   				   WHEN (TIME_ID<>-1 AND Y_DIM=CAST(-1 AS VARCHAR(2)))  THEN 3 '
						 + '			     	   ELSE  1 END  AS DATA_TYPE ,	'
						 + '           TIME_ID ,Y_DIM	'							
					     +'  , SUM(CHG_CNT) AS  CHG_CNT '
						 + ' ,SUM(CHG_TXCNT) AS CHG_TXCNT , SUM(CHG_QTY)   AS CHG_QTY ,SUM(CAST(CHG_AMT AS BIGINT)) AS CHG_AMT '					
						 + ' FROM ('
						 + ' SELECT TIME_ID ,Y_DIM ,CHG_CNT  CHG_CNT ,CHG_TXCNT ,CHG_QTY ,CHG_AMT '	
						 + ' FROM #VT_FACT_MEMBER_CHG_TMP '
						 + ' UNION ALL '
						 + ' SELECT -1 AS TIME_ID,Y_DIM ,CHG_CNT ,CHG_TXCNT ,CHG_QTY ,CHG_AMT '						
						 + ' FROM #VT_FACT_MEMBER_CHG_TMP '
						 + ' ) DAT GROUP BY TIME_ID ,Y_DIM '
						 + ' ) WITH DATA PRIMARY  CHARINDEX(Y_DIM,TIME_ID) ON COMMIT PRESERVE ROWS;';	
EXECUTE IMMEDIATE SQLSTR;   
END SP;