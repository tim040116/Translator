REPLACE PROCEDURE PMART.FUNC_FACT_SUGORDD
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
   DECLARE ORGWHERE VARCHAR(2000);
   DECLARE SELECTORG VARCHAR(200);
   DECLARE FSELECTORG VARCHAR(200); 
   DECLARE PRDWHERE VARCHAR(2000);
   DECLARE SELECTPRD VARCHAR(200);
   DECLARE FSELECTPRD VARCHAR(200); 
   DECLARE SELECTWHERE VARCHAR(500);
   DECLARE BASETABLE VARCHAR(200);
   DECLARE SELECT_CALCNT VARCHAR(200);
   DECLARE SELECT_X VARCHAR(200);
   DECLARE OSELECT_ORG VARCHAR(50);
   DECLARE OFSELECT_ORG VARCHAR(50);
   DECLARE TEMP_JOIN VARCHAR(200);
   DECLARE EXTRA_SELECT VARCHAR(50);
   DECLARE PSD VARCHAR(50);
   DECLARE FPSD VARCHAR(100);
   DECLARE JOINCNT VARCHAR(50);
   DECLARE JOINORG VARCHAR(50);
	 SET ORGWHERE = ' WHERE 1=1 ';
	 SET PRDWHERE = ' WHERE 1=1 ';
	 SET SELECTWHERE = ' ';
	 SET SELECT_X = ' T.TIME_ID ';
	 SET OSELECT_ORG = ' ';
	 SET OFSELECT_ORG = ' ';
      IF P_RPTID = 1  THEN
		  SET SELECT_CALCNT = ' T.ORG_ID ';
		  SET EXTRA_SELECT = ' ,O.* ';
		  SET SELECTWHERE = ' AND B.PRD_ID = -1 ';
		  SET JOINCNT = ' AND B.ORG_ID = C.ORG_ID ';
		  IF P_ORGTYPE = 1 THEN
			  SET TEMP_JOIN = ' JOIN #VT_BASE_ORG O ON O.ORG_ID = B.ORG_ID ';
		  ELSEIF P_ORGTYPE = 2 THEN
			  SET TEMP_JOIN = ' JOIN #VT_BASE_ORG O ON O.S_ID = B.ORG_ID ';
		  ELSEIF P_ORGTYPE = 3 THEN
			  SET TEMP_JOIN = ' JOIN #VT_BASE_ORG O ON O.S_ID = B.ORG_ID ';
		  ELSEIF P_ORGTYPE = 4 THEN
			  SET TEMP_JOIN = ' JOIN #VT_BASE_ORG O ON O.ORG_ID = B.ORG_ID ';
		  ELSE
			  SET TEMP_JOIN = ' JOIN #VT_BASE_ORG O ON O.ORG_ID = B.ORG_ID ';
		  END IF;
	  ELSEIF P_RPTID = 2  THEN
		  SET SELECT_CALCNT = ' T.PRD_ID ';
		  SET EXTRA_SELECT = ' ,B.ORG_ID ';
		  SET TEMP_JOIN = ' JOIN #VT_BASE_PRD P ON P.PRD_ID = B.PRD_ID ';
		  SET SELECTWHERE = ' AND B.ORG_ID = -1 ';
		  SET JOINCNT = '  ';
	  END IF;
	  IF P_ORGTYPE = 0 THEN
		  SET ORGWHERE = ' WHERE PDEPT_ID  = ' + TRIM(P_ORGID);
		  SET SELECTORG = ' DEPT_ID ';	  
		  SET FSELECTORG = ' PDEPT_ID ';	 
	  ELSEIF P_ORGTYPE = 1 THEN
	      SET ORGWHERE = ' WHERE DEPT_ID  = ' + TRIM(P_ORGID);
		  SET SELECTORG = ' BRANCH_ID ';	  
		  SET FSELECTORG = ' DEPT_ID ';	  
	  ELSEIF P_ORGTYPE = 2 THEN
	      SET ORGWHERE = ' WHERE BRANCH_ID  = ' + TRIM(P_ORGID);
		  SET SELECTORG = ' RESPON_ID ';
		  SET FSELECTORG = ' BRANCH_ID ';
		  SET OSELECT_ORG = ' ,OSTORE_ID AS S_ID';
		  SET OFSELECT_ORG = ' ,BRANCH_ID AS S_ID';
	  ELSEIF P_ORGTYPE = 3 THEN
	      SET ORGWHERE = ' WHERE RESPON_ID  = ' + TRIM(P_ORGID);
		  SET SELECTORG = ' OSTORE_ID ';
		  SET FSELECTORG = ' RESPON_ID ';
		  SET OSELECT_ORG = ' ,OSTORE_ID AS S_ID';
		  SET OFSELECT_ORG = ' ,OSTORE_ID AS S_ID';
	  ELSEIF P_ORGTYPE = 4 THEN
	      SET ORGWHERE = ' WHERE OSTORE_ID  = ' + TRIM(P_ORGID);
		  SET SELECTORG = ' OSTORE_ID ';
		  SET FSELECTORG = ' OSTORE_ID ';
	  ELSE
		  SET SELECTORG = ' PDEPT_ID ';
		  SET FSELECTORG = ' CAST(-1 AS INTEGER) ';	
	  END IF; 
	  IF P_PRDTYPE = 1 THEN
		  SET PRDWHERE = ' WHERE KND_ID = '''  + P_PRDID + ''' ';
		  SET SELECTPRD = ' GRP_ID ';	  
		  SET FSELECTPRD = ' KND_ID ';
	  ELSEIF P_PRDTYPE = 2 THEN
		  SET PRDWHERE = ' WHERE GRP_ID = '''  + P_PRDID + ''' ';
		  SET SELECTPRD = ' PRD_ID ';	  
		  SET FSELECTPRD = ' GRP_ID ';
	  ELSEIF P_PRDTYPE = 3 THEN
		  SET PRDWHERE = ' WHERE PRD_ID = '''  + P_PRDID + ''' ';
		  SET SELECTPRD = ' PRD_ID ';	  
		  SET FSELECTPRD = ' PRD_ID ';
	  ELSE
	      SET SELECTPRD = ' KND_ID ';	  
		  SET FSELECTPRD = ' CAST(-1 AS INTEGER) ';
	  END IF;
	  SET BASETABLE = ' PMART.FACT_SUGGEST_ORDD ';
	  IF P_TIMETYPE = 'D' THEN
		  SET PSD = ' ,SUM(SALES_PSD) AS SALES_PSD ';
		  SET FPSD = ' ,CAST(AVG(SALES_PSD) AS DECIMAL(16,2)) AS SALES_PSD ';
	  ELSE
		  SET PSD = ' ,0 AS SALES_PSD ';
		  SET FPSD = ' ,0 AS SALES_PSD ';
	  END IF;
	  CALL PMART.P_DROP_TABLE ('#VT_BASE_ORG');
	  CALL PMART.P_DROP_TABLE ('#VT_BASE_PRD');
	  CALL PMART.P_DROP_TABLE ('#VT_BASE_CNT');
	  CALL PMART.P_DROP_TABLE ('#VT_BASE_TEMP');
	  CALL PMART.P_DROP_TABLE ('#VT_SUG_ORDD_TEMP');
	  CALL PMART.P_DROP_TABLE ('#VT_FACT_SUGORDD');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_BASE_ORG AS( '
						 + ' SELECT DISTINCT '+SELECTORG+' AS ORG_ID '+OSELECT_ORG
						 + ' FROM PMART.LAST_ORG_DIM ' +ORGWHERE 
						 + ' UNION '
						 + ' SELECT DISTINCT '+FSELECTORG+' AS ORG_ID '+OFSELECT_ORG
						 + ' FROM PMART.LAST_ORG_DIM ' +ORGWHERE 
						 + ' ) WITH DATA PRIMARY INDEX(ORG_ID) ON COMMIT PRESERVE ROWS;';			
EXECUTE IMMEDIATE SQLSTR;  
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_BASE_PRD AS( '
						 + ' SELECT DISTINCT '+SELECTPRD+' AS PRD_ID '
						 + ' FROM PMART.PRD_DIM ' +PRDWHERE 
						 + ' UNION '
						 + ' SELECT DISTINCT '+FSELECTPRD+' AS PRD_ID '
						 + ' FROM PMART.PRD_DIM ' +PRDWHERE 
						 + ' ) WITH DATA PRIMARY INDEX(PRD_ID) ON COMMIT PRESERVE ROWS;';			
EXECUTE IMMEDIATE SQLSTR;  
IF P_RPTID = 1 THEN
	SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_BASE_CNT AS( '
							 + ' SELECT A.TIME_ID,D.ORG_ID '
							 + ' ,BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,D.MASK)) AS STORE_NUM '
							 + ' FROM PMART.BASIC_MAST_FACT A '
							 + ' ,(SELECT MASK,ORG_ID FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID IN ( SELECT ORG_ID FROM #VT_BASE_ORG O ))D '
							 + ' WHERE  A.TIME_ID IN ('+P_TIMEID+') '
							 + ' ) WITH DATA PRIMARY  CHARINDEX(ORG_ID,TIME_ID) ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
ELSE
	SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_BASE_CNT AS( '
							 + ' SELECT A.TIME_ID '
							 + ' ,BIT_EXTRACT(BIT_AND(A.STNUM_STORE_NUM,D.MASK)) AS STORE_NUM '
							 + ' FROM PMART.BASIC_MAST_FACT A '
							 + ' ,(SELECT MASK,ORG_ID FROM PMART.LAST_ORG_DIM_MASK WHERE ORG_ID IN ( -1 ))D '
							 + ' WHERE  A.TIME_ID IN ('+P_TIMEID+') '
							 + ' ) WITH DATA PRIMARY INDEX(TIME_ID) ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END IF;
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_BASE_TEMP AS( '
						 + ' SELECT TIME_ID,ORG_ID,PRD_ID '
						 + ' ,SUM(FG_ORDALL) AS FG_ORDALL ,SUM(FG_ORDAUTO) AS FG_ORDAUTO ,SUM(QT_MINCOL) AS QT_MINCOL '
						 + ' ,SUM(RA_STORE) AS RA_STORE ,SUM(QT_COLNUM) AS QT_COLNUM ,SUM(QT_STDSTOCK) AS QT_STDSTOCK '
						 + ' ,SUM(QT_STOCK) AS QT_STOCK ,SUM(QT_ORD) AS QT_ORD ,SUM(QT_ITEM) AS QT_ITEM ,SUM(SALES_PSD) AS SALES_PSD '
						 + ' FROM ('
						 + ' SELECT  B.TIME_ID,B.PRD_ID,B.FG_ORDALL,B.FG_ORDAUTO '
						 + ' ,CAST(B.QT_MINCOL AS DECIMAL(16,2))/CASE WHEN B.FG_AUTO3 = 0 THEN 1 ELSE B.FG_AUTO3 END AS QT_COLNUM '
						 + ' ,CAST(B.RA_STORE AS DECIMAL(16,2))/CASE WHEN B.FG_AUTO3 = 0 THEN 1 ELSE B.FG_AUTO3 END AS RA_STORE '
						 + ' ,CAST(B.QT_COLNUM AS DECIMAL(16,2))/CASE WHEN B.FG_AUTO3 = 0 THEN 1 ELSE B.FG_AUTO3 END AS QT_MINCOL '
						 + ' ,B.QT_STDSTOCK ,B.QT_STOCK ,B.QT_ORD ,B.QT_ITEM '
						 + ' ,CAST(B.SALES_PSD AS DECIMAL(16,2))/CASE WHEN B.FG_AUTO3 = 0 THEN 1 ELSE B.FG_AUTO3 END AS SALES_PSD'+EXTRA_SELECT
						 + '	    FROM ' + BASETABLE + ' B '
						 + TEMP_JOIN
						 + ' 	    AND B.TIME_ID IN ('+P_TIMEID+')' +SELECTWHERE
						 + ' 	    ) D GROUP BY TIME_ID,ORG_ID,PRD_ID '
						 + ' ) WITH DATA PRIMARY  CHARINDEX(ORG_ID,TIME_ID) ON COMMIT PRESERVE ROWS;';			
EXECUTE IMMEDIATE SQLSTR; 
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_SUG_ORDD_TEMP AS( '
						 + ' SELECT B.TIME_ID ,B.ORG_ID ,B.PRD_ID '
						 + ' ,B.FG_ORDALL ,B.FG_ORDAUTO ,B.QT_MINCOL '
						 + ' ,B.RA_STORE ,B.QT_COLNUM ,B.QT_STDSTOCK '
						 + ' ,B.QT_STOCK ,B.QT_ORD ,B.QT_ITEM ,B.SALES_PSD , C.STORE_NUM '
						 + ' FROM #VT_BASE_TEMP B '
						 + ' JOIN #VT_BASE_CNT C ON B.TIME_ID = C.TIME_ID '+JOINCNT
						 + ' ) WITH DATA PRIMARY  CHARINDEX(ORG_ID,TIME_ID) ON COMMIT PRESERVE ROWS;';			
EXECUTE IMMEDIATE SQLSTR; 
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_SUGORDD AS( '
						 + ' SELECT  1 AS DATA_TYPE'
						 + '                ,CAST(-1 AS INTEGER)  AS TIME_ID '
						 + '                ,' + SELECT_CALCNT + ' AS Y_DIM '
						 + '                ,SUM(T.STORE_NUM) AS STORE_NUM '
						 + '                ,CASE WHEN SUM(T.FG_ORDAUTO) >0 THEN ''Y'' ELSE ''N'' END AS SYS_USED '+FPSD
						 + '                ,CAST(AVG(T.QT_COLNUM) AS DECIMAL(16,2)) AS QT_COLNUM '
						 + '                ,CAST(SUM(FG_ORDAUTO) AS DECIMAL(16,2)) /CASE WHEN SUM(FG_ORDALL) = 0 THEN 1 ELSE SUM(FG_ORDALL) END AS PRD_USED_RATE '
						 + '                ,CAST(AVG(T.RA_STORE) AS DECIMAL(16,2)) AS RA_STORE '
						 + '                ,CAST(AVG(T.QT_MINCOL) AS DECIMAL(16,2)) AS QT_MINCOL '
						 + '                ,SUM(T.QT_STDSTOCK) AS ALL_STOCK '
						 + '                ,SUM(T.QT_STOCK) AS SAT_STOCK '
						 + '                ,SUM(T.QT_ORD) AS SYS_ORDD '
						 + '                ,SUM(T.QT_ITEM) AS STORE_ORDD '
						 + '	    FROM #VT_SUG_ORDD_TEMP T '
						 + ' 	    GROUP BY ' + SELECT_CALCNT
						 + ' UNION ALL '
						 + ' SELECT  2 AS DATA_TYPE'
						 + '                ,T.TIME_ID  AS TIME_ID '
						 + '                ,' + SELECT_CALCNT + ' AS Y_DIM '
						 + '                ,SUM(T.STORE_NUM) AS STORE_NUM '
						 + '                ,CASE WHEN SUM(T.FG_ORDAUTO) >0 THEN ''Y'' ELSE ''N'' END AS SYS_USED '+PSD
						 + '                ,SUM(T.QT_COLNUM) AS QT_COLNUM '
						 + '                ,CAST(SUM(FG_ORDAUTO) AS DECIMAL(16,2)) /CASE WHEN SUM(FG_ORDALL) = 0 THEN 1 ELSE SUM(FG_ORDALL) END AS PRD_USED_RATE '
						 + '                ,SUM(T.RA_STORE) AS RA_STORE '
						 + '                ,SUM(T.QT_MINCOL) AS QT_MINCOL '
						 + '                ,SUM(T.QT_STDSTOCK) AS ALL_STOCK '
						 + '                ,SUM(T.QT_STOCK) AS SAT_STOCK '
						 + '                ,SUM(T.QT_ORD) AS SYS_ORDD '
						 + '                ,SUM(T.QT_ITEM) AS STORE_ORDD '
						 + '	    FROM #VT_SUG_ORDD_TEMP T '
						 + ' 	    GROUP BY '+SELECT_X+' , '+ SELECT_CALCNT
						 + ' ) WITH DATA PRIMARY  CHARINDEX(Y_DIM,TIME_ID) ON COMMIT PRESERVE ROWS;';			
EXECUTE IMMEDIATE SQLSTR;  
END SP;