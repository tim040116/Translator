REPLACE PROCEDURE PMART.FUNC_FACT_SUGORDD_DOWNLOAD
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
   DECLARE PRDWHERE VARCHAR(2000);
   DECLARE SELECTWHERE VARCHAR(500);
   DECLARE PRDJOIN VARCHAR(200);
   DECLARE SELECT_RPT VARCHAR(200);
   DECLARE TIMEQ VARCHAR(2000);
   DECLARE TIMENAME VARCHAR(200);
   DECLARE TIMEWHERE VARCHAR(200);
   DECLARE PSD VARCHAR(100);
   DECLARE BASEWHERE VARCHAR(200);
   DECLARE BASETABLE VARCHAR(200);
   DECLARE EXTRASELECT VARCHAR(200);
	 SET ORGWHERE = ' WHERE 1=1 ';
	 SET PRDWHERE = ' WHERE 1=1 ';
	 SET SELECTWHERE = ' ';
      IF P_RPTID = 1  THEN
		  SET PRDJOIN = '  ';
		  SET SELECT_RPT = ' ';
		  SET BASEWHERE = ' AND T1.PRD_ID = -1 ';
		  SET BASETABLE = ' PMART.FACT_SUGGEST_ORDD ';
		  SET EXTRASELECT = ' ';
	  ELSE
		  SET PRDJOIN = ' INNER JOIN PMART.PRD_DIM T4 ON T1.PRD_ID = T4.PRD_ID  ';
		  SET SELECT_RPT = ' , T4.KND_NM ,T4.GRP_NM ,T4.PRD_NM ';
		  SET BASEWHERE = ' ';
		  IF P_TIMETYPE = 'D' THEN
			  SET BASETABLE = ' PMART.FACT_SUGGEST_ORDD_DETAIL ';
		  ELSE
			  SET BASETABLE = ' PMART.FACT_SUGGEST_ORDD ';
		  END IF;
		  SET EXTRASELECT = ' ,T1.FG_AUTO3 ';
	  END IF;
	  IF P_ORGTYPE = 0 THEN
	      SET ORGWHERE = ' WHERE S.PDEPT_ID  = ' + TRIM(P_ORGID);
	  ELSEIF P_ORGTYPE = 1 THEN
	      SET ORGWHERE = ' WHERE S.DEPT_ID  = ' + TRIM(P_ORGID);
	  ELSEIF P_ORGTYPE = 2 THEN
	      SET ORGWHERE = ' WHERE S.BRANCH_ID  = ' + TRIM(P_ORGID);
	  ELSEIF P_ORGTYPE = 3 THEN
	      SET ORGWHERE = ' WHERE S.RESPON_ID  = ' + TRIM(P_ORGID);
	  ELSEIF P_ORGTYPE = 4 THEN
	      SET ORGWHERE = ' WHERE S.OSTORE_ID  = ' + TRIM(P_ORGID);
	  END IF; 
	  IF P_PRDTYPE = 1 THEN
		  SET PRDWHERE = ' AND T4.KND_ID = '''  + P_PRDID + ''' ';
	  ELSEIF P_PRDTYPE = 2 THEN
		  SET PRDWHERE = ' AND T4.GRP_ID = '''  + P_PRDID + ''' ';
	  ELSEIF P_PRDTYPE = 3 THEN
		  SET PRDWHERE = ' AND T4.PRD_ID = '''  + P_PRDID + ''' ';
	  ELSE
	      SET PRDWHERE = '  ';	  
	  END IF;
	  IF P_TIMETYPE = 'M' THEN
		  SET TIMEQ = 'AND D.L_MONTH_ID IN ('+P_TIMEID+')';
		  SET TIMENAME = 'T3.L_MONTH_NAME ';
		  SET TIMEWHERE = ' LEFT JOIN PMART.TIME_M T3 ON T1.TIME_ID = T3.L_MONTH_ID ';
	  ELSEIF P_TIMETYPE = 'W' THEN
		  SET TIMEQ = 'AND D.L_WEEK_ID IN ('+P_TIMEID+')';
		  SET TIMENAME = 'T3.L_MONTH_NAME ';
		  SET TIMEWHERE = ' LEFT JOIN PMART.TIME_W T3 ON T1.TIME_ID = T3.L_WEEK_ID ';
	  ELSE
		  SET TIMEQ = 'AND D.L_DAY_ID IN ('+P_TIMEID+')';
		  SET TIMENAME = 'T3.L_DAY_NAME ';
		  SET TIMEWHERE = ' LEFT JOIN PMART.TIME_D T3 ON T1.TIME_ID = T3.L_DAY_ID ';
	  END IF;
	  IF P_TIMETYPE = 'D' THEN
		  SET PSD = ' ,CAST(T1.SALES_PSD AS DECIMAL(16,2))/CASE WHEN T1.FG_AUTO3 = 0 THEN 1 ELSE T1.FG_AUTO3 END AS SALES_PSD ';
	  ELSE
		  SET PSD = ' ,0 AS SALES_PSD ';
	  END IF;
	  CALL PMART.P_DROP_TABLE ('#VT_FACT_SUGORDD_DOWNLOAD');
	  SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_SUGORDD_DOWNLOAD AS('		
						 +' SELECT T2.PDEPT_NO,T2.PDEPT_SNAME,T2.DEPT_NO,T2.DEPT_SNAME,T2.BRANCH_NO,T2.BRANCH_SNAME,T2.STORE_NO,T2.STORE_NAME '+SELECT_RPT
						 +'               , ' + TIMENAME+' AS TIME_ID '
                         +'               ,BIT_EXTRACT(BIT_AND(B.STNUM_STORE_NUM,M.MASK)) AS STORE_NUM '
                         +'               ,CASE WHEN T1.FG_ORDAUTO >0 THEN ''Y'' ELSE ''N'' END AS SYS_USED '+PSD
                         +'               ,CAST(T1.QT_COLNUM AS DECIMAL(16,2))/CASE WHEN T1.FG_AUTO3 = 0 THEN 1 ELSE T1.FG_AUTO3 END AS QT_COLNUM '  
						 +'               ,CAST(T1.FG_ORDAUTO AS DECIMAL(16,2)) /CASE WHEN T1.FG_ORDALL= 0 THEN 1 ELSE T1.FG_ORDALL END  AS PRD_USED_RATE '
                         +'               ,CAST(T1.RA_STORE AS DECIMAL(16,2))/CASE WHEN T1.FG_AUTO3 = 0 THEN 1 ELSE T1.FG_AUTO3 END AS RA_STORE '
						 +'               ,CAST(T1.QT_MINCOL AS DECIMAL(16,2))/CASE WHEN T1.FG_AUTO3 = 0 THEN 1 ELSE T1.FG_AUTO3 END AS QT_MINCOL '
                         +'               ,T1.QT_STDSTOCK AS ALL_STOCK '  
						 +'               ,T1.QT_STOCK AS SAT_STOCK '
						 +'               ,T1.QT_ORD AS SYS_ORDD '
						 +'               ,T1.QT_ITEM AS STORE_ORDD '+EXTRASELECT
						 +'    FROM '+BASETABLE+' T1 '
						 +'	JOIN PMART.BASIC_MAST_FACT B ON B.TIME_ID = T1.TIME_ID '
						 +'	JOIN PMART.LAST_ORG_DIM_MASK M ON M.ORG_ID= T1.ORG_ID '
						 +'               INNER JOIN ( '
						 +'                                       SELECT PDEPT.PDEPT_NO,PDEPT.PDEPT_SNAME,DEPT.DEPT_NO,DEPT.DEPT_SNAME,BRANCH.BRANCH_NO,BRANCH.BRANCH_SNAME,S.OSTORE_ID,S.STORE_NO,S.STORE_NAME '
						 +'                                          FROM PMART.LAST_ORG_STORE S '
						 +'                                                     LEFT JOIN PMART.ORG_PDEPT PDEPT ON S.PDEPT_ID = PDEPT.PDEPT_ID '
						 +'                                                     LEFT JOIN PMART.ORG_DEPT DEPT ON S.DEPT_ID = DEPT.DEPT_ID '
						 +'                                                     LEFT JOIN PMART.ORG_BRANCH BRANCH ON S.BRANCH_ID = BRANCH.BRANCH_ID '
						 +'                                                     LEFT JOIN PDATA.PBMSTTP T ON S.STORE_TYPE = T.STORE_TYPE '
						 +'                       ' + TRIM(ORGWHERE)
						 +'                                    ) T2 ON T1.ORG_ID = T2.OSTORE_ID '
						 +                                    TIMEWHERE	+PRDJOIN +PRDWHERE
						 +' WHERE T1.TIME_ID IN ('+P_TIMEID+')'+BASEWHERE
			             + ' ) WITH DATA PRIMARY  CHARINDEX(STORE_NO,TIME_ID) ON COMMIT PRESERVE ROWS;';						  					
        EXECUTE IMMEDIATE SQLSTR;		
END SP;