REPLACE PROCEDURE PMART.FUNC_FACT_PBOASUGORDD
(  
   IN P_RPTID INTEGER,               
   IN P_TIMEID VARCHAR(100),       
   IN P_TIMETYPE VARCHAR(1),  
   IN P_ORGTYPE INTEGER,      
   IN P_ORGID VARCHAR(20),        
   IN P_PRDTYPE INTEGER,        
   IN P_PRDID VARCHAR(7)         
)
SP:BEGIN
 DECLARE SQLSTR  VARCHAR(10000) ; 
 DECLARE V_DATA_TYPE INTEGER; 
 DECLARE V_X_DIM INTEGER;            
 DECLARE V_X_DIM_NM VARCHAR(200) ; 
 DECLARE V_Y_DIM VARCHAR(20) ;        
 DECLARE V_Y_DIM_NM VARCHAR(200) ; 
 DECLARE V_STORE_NUM INTEGER;        
 DECLARE V_SYS_USED VARCHAR(1) ;     
 DECLARE V_SALES_PSD DECIMAL(18,2);  
 DECLARE V_STSET_PCT INTEGER;        
 DECLARE V_MINORD_QTY INTEGER;       
 DECLARE XDIMSELECT VARCHAR(200) ; 
 DECLARE TIMEJOIN VARCHAR(200) ; 
 DECLARE V_P_ORGID VARCHAR(20) ;   
 DECLARE V_P_PRDID VARCHAR(20) ;   
   IF P_ORGID IS NULL  THEN
     SET V_P_ORGID = '-1';
   ELSE
     SET V_P_ORGID = P_ORGID;
   END IF;
   IF P_PRDID IS NULL  THEN
     SET V_P_PRDID = '-1';
   ELSE
     SET V_P_PRDID = P_PRDID;
   END IF;
     IF P_TIMETYPE = 'D' THEN
        SET XDIMSELECT = ' D.L_DAY_NAME AS  X_DIM_NM, ' ;
        SET TIMEJOIN = ' LEFT JOIN PMART.YMWD_TIME D ON A.TIME_ID = D.L_DAY_ID ';
    ELSEIF P_TIMETYPE = 'W' THEN
        SET XDIMSELECT = ' D.L_WEEK_NAME AS  X_DIM_NM, ' ;
        SET TIMEJOIN = ' LEFT JOIN (SELECT DISTINCT L_WEEK_ID,L_WEEK_NAME FROM PMART.YMWD_TIME )D ON  A.TIME_ID = D.L_WEEK_ID ';
    ELSEIF P_TIMETYPE = 'M' THEN
        SET XDIMSELECT = ' D.L_MONTH_NAME AS  X_DIM_NM, ' ;
        SET TIMEJOIN = ' LEFT JOIN (SELECT DISTINCT L_MONTH_ID,L_MONTH_NAME FROM  PMART.YMWD_TIME )D ON  A.TIME_ID = D.L_MONTH_ID ';
     END IF;
    CALL PMART.FUNC_FACT_PORG_DIM(2,V_P_ORGID);
	CALL PMART.FUNC_FACT_PRD_DIM(2,V_P_PRDID,NULL,NULL);
   CALL PMART.P_DROP_TABLE ('#VT_FACT_PBOASUGORDD');
   IF P_RPTID = '1' THEN
     SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE  #VT_FACT_PBOASUGORDD AS( '
	  				+ ' SELECT CASE A.ORG_ID WHEN ' + V_P_ORGID + ' THEN 1 ELSE 2 END DATA_TYPE, '
             		+  ' CAST ( -1 AS INTEGER) AS X_DIM, '
             		+  ' CAST (''-1'' AS VARCHAR(20)) AS  X_DIM_NM , '
             		+  ' A.ORG_ID AS Y_DIM, '
             		+  ' B.ORG_SNAME AS Y_DIM_NM, '
             		+  ' SUM(WORK_DAYS) AS STORE_NUM, '
             		+  ' CAST(NULL AS VARCHAR(1)) AS SYS_USED, '
             		+  ' CASE SUM(P14_WORK_DAYS) WHEN 0 THEN 0 ELSE ROUND(SUM(P14_SALE_QTY*1.00)/SUM(P14_WORK_DAYS*1.00),2) END AS SALES_PSD, '
             		+  ' AVG(STSET_PCT) AS STSET_PCT, '
             		+  ' AVG(MINORD_QTY) AS MINORD_QTY '
      				+  ' FROM '
      				+  ' ( '
      				+  '     SELECT TIME_ID, ORG_ID, PRD_ID '
      				+  '     ,MAX(STORE_QTY) AS STORE_QTY '
      				+  '     ,SUM(USEST_QTY) AS USEST_QTY '
      				+  '     ,SUM(PRD_QTY) AS PRD_QTY '
      				+  '     ,SUM(UCPRD_QTY) AS UCPRD_QTY '
      				+  '     ,SUM(STORD_QTY) AS STORD_QTY ' 
      				+  '     ,SUM(SYSORD_QTY) AS SYSORD_QTY '
      				+  '     ,MAX(WORK_DAYS) AS WORK_DAYS '
      				+  '     ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
      				+  '     ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
      				+  '     ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
      				+  '     ,MAX(STSET_PCT) AS STSET_PCT '
      				+  '     ,MAX(MINORD_QTY) AS MINORD_QTY '
      				+  '     FROM PMART.FACT_PBOA '
      				+  '     WHERE DATA_USE_TYPE IN (0,1) '
      				+  '     GROUP BY TIME_ID, ORG_ID, PRD_ID '
      				+  ' ) A '
      				+  ' INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PORG_DIM) B ON A.ORG_ID = B.ORG_ID '
      				+  TIMEJOIN  
      				+  ' WHERE A.PRD_ID = ''-1'' AND A.TIME_ID IN (' +  P_TIMEID + ') '
      				+  ' GROUP BY A.ORG_ID,B.ORG_SNAME  '
      				+  ' UNION ALL '
      				+  ' SELECT CASE A.ORG_ID WHEN ' + V_P_ORGID + ' THEN 1 ELSE 2 END DATA_TYPE, '
      				+  '        TIME_ID AS X_DIM, '
      				+  XDIMSELECT
      				+  '        A.ORG_ID AS Y_DIM, '
      				+  '        B.ORG_SNAME AS Y_DIM_NM, '
      				+  '        WORK_DAYS AS STORE_NUM, '
      				+  '        USE_SYS AS SYS_USED, '
      				+  '        CASE P14_WORK_DAYS WHEN 0 THEN 0 ELSE ROUND(P14_SALE_QTY*1.00/P14_WORK_DAYS*1.00,2) END AS SALES_PSD, '
      				+  '        STSET_PCT AS STSET_PCT, '
      				+  '        MINORD_QTY AS MINORD_QTY '
      				+  ' FROM '
      				+  ' ( '
      				+  '     SELECT TIME_ID, ORG_ID, PRD_ID '
      				+  '     ,MAX(STORE_QTY) AS STORE_QTY '
      				+  '     ,SUM(USEST_QTY) AS USEST_QTY '
      				+  '     ,SUM(PRD_QTY) AS PRD_QTY '
      				+  '     ,SUM(UCPRD_QTY) AS UCPRD_QTY '
      				+  '     ,SUM(STORD_QTY) AS STORD_QTY '
      				+  '     ,SUM(SYSORD_QTY) AS SYSORD_QTY '
      				+  '     ,MAX(WORK_DAYS) AS WORK_DAYS '
      				+  '     ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
      				+  '     ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
      				+  '     ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
      				+  '     ,MAX(STSET_PCT) AS STSET_PCT '
      				+  '     ,MAX(MINORD_QTY) AS MINORD_QTY '
      				+  '     FROM PMART.FACT_PBOA '
      				+  '     WHERE DATA_USE_TYPE IN (0,1) '
      				+  '     GROUP BY TIME_ID, ORG_ID, PRD_ID '
      				+  ' )A '
      				+  ' INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PORG_DIM) B ON A.ORG_ID = B.ORG_ID '
      				+  TIMEJOIN 
      				+  ' WHERE A.PRD_ID = ''-1'' AND A.TIME_ID IN (' +  P_TIMEID + ') '
					+  '  ) WITH DATA PRIMARY INDEX(X_DIM) ON COMMIT PRESERVE ROWS;';
   ELSEIF P_RPTID = '2' THEN
     SET SQLSTR =' CREATE MULTISET VOLATILE TABLE #VT_FACT_PBOASUGORDD AS( '
	 		 +  ' SELECT CASE A.PRD_ID WHEN ''' + V_P_PRDID + ''' THEN 1 ELSE 2 END DATA_TYPE, '
             +  ' CAST (-1 AS INTEGER) AS X_DIM, '
             +  ' CAST (''-1''  AS VARCHAR(20)) AS  X_DIM_NM , '
             +  ' A.PRD_ID AS Y_DIM, '
             +  ' C.FM_NAME AS Y_DIM_NM, '
             +  ' SUM(WORK_DAYS) AS STORE_NUM, '
             +  ' CAST(NULL AS VARCHAR(1)) AS SYS_USED, '
             +  ' CASE SUM(P14_WORK_DAYS) WHEN 0 THEN 0 ELSE ROUND(SUM(P14_SALE_QTY*1.00)/SUM(P14_WORK_DAYS*1.00),2) END AS SALES_PSD, '
             +  ' AVG(STSET_PCT) AS STSET_PCT, '
             +  ' AVG(MINORD_QTY) AS MINORD_QTY '
      		 +  ' FROM '
      		 +  ' ( '
      		 +  '     SELECT TIME_ID, ORG_ID, PRD_ID '
      		 +  '     ,MAX(STORE_QTY) AS STORE_QTY '
      		 +  '     ,SUM(USEST_QTY) AS USEST_QTY '
      		 +  '     ,SUM(PRD_QTY) AS PRD_QTY '
      		 +  '     ,SUM(UCPRD_QTY) AS UCPRD_QTY '
      		 +  '     ,SUM(STORD_QTY) AS STORD_QTY '
      		 +  '     ,SUM(SYSORD_QTY) AS SYSORD_QTY '
      		 +  '     ,MAX(WORK_DAYS) AS WORK_DAYS '
      		 +  '     ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
      		 +  '     ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
      		 +  '     ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
      		 +  '     ,MAX(STSET_PCT) AS STSET_PCT '
      		 +  '     ,MAX(MINORD_QTY) AS MINORD_QTY '
      		 +  '     FROM PMART.FACT_PBOA '
      		 +  '     WHERE DATA_USE_TYPE IN (0,1) '
      		 +  '     GROUP BY TIME_ID, ORG_ID, PRD_ID '
      		 +  ' ) A '
      		 +  ' INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PORG_DIM) B ON A.ORG_ID = B.ORG_ID '
      		 +  ' INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PRD_DIM) C ON A.PRD_ID = C.PRD_ID '
      		 +  TIMEJOIN
      		 +  ' WHERE A.ORG_ID = ' + V_P_ORGID + ' AND A.TIME_ID IN (' +  P_TIMEID + ') '
      		 +  ' GROUP BY A.PRD_ID,C.FM_NAME '
      		 +  ' UNION ALL '
      		 +  ' SELECT CASE A.PRD_ID WHEN ''' + V_P_PRDID + ''' THEN 1 ELSE 2 END DATA_TYPE, '
      		 +  '        TIME_ID AS X_DIM, '
      		 +  XDIMSELECT 
      		 +  '        A.PRD_ID AS Y_DIM, '
      		 +  '        C.FM_NAME AS Y_DIM_NM, '
      		 +  '        WORK_DAYS AS STORE_NUM, '
      		 +  '        USE_SYS AS SYS_USED, '
      		 +  '        CASE P14_WORK_DAYS WHEN 0 THEN 0 ELSE ROUND(P14_SALE_QTY*1.00/P14_WORK_DAYS*1.00,2) END AS SALES_PSD, '
      		 +  '        STSET_PCT AS STSET_PCT, '
      		 +  '        MINORD_QTY AS MINORD_QTY '
      		 +  ' FROM '
      		 +  ' ( '
      		 +  '     SELECT TIME_ID, ORG_ID, PRD_ID '
      		 +  '     ,MAX(STORE_QTY) AS STORE_QTY '
      		 +  '     ,SUM(USEST_QTY) AS USEST_QTY '
      		 +  '     ,SUM(PRD_QTY) AS PRD_QTY '
      		 +  '     ,SUM(UCPRD_QTY) AS UCPRD_QTY '
      		 +  '     ,SUM(STORD_QTY) AS STORD_QTY '
      		 +  '     ,SUM(SYSORD_QTY) AS SYSORD_QTY '
      		 +  '     ,MAX(WORK_DAYS) AS WORK_DAYS '
      		 +  '     ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
      		 +  '     ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
      		 +  '     ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
      		 +  '     ,MAX(STSET_PCT) AS STSET_PCT '
      		 +  '     ,MAX(MINORD_QTY) AS MINORD_QTY   '
      		 +  '     FROM PMART.FACT_PBOA '
      		 +  '     WHERE DATA_USE_TYPE IN (0,1) '
      		 +  '     GROUP BY TIME_ID, ORG_ID, PRD_ID '
      		 +  ' ) A '
      		 +  '  INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PORG_DIM) B ON A.ORG_ID = B.ORG_ID '
      		 +  '  INNER JOIN (SELECT * FROM #VT_FUNC_FACT_PRD_DIM) C ON A.PRD_ID = C.PRD_ID '
      		 +  TIMEJOIN
      		 +  ' WHERE A.ORG_ID = ' + V_P_ORGID + ' AND A.TIME_ID IN (' +  P_TIMEID + ') '
			 +  '  ) WITH DATA PRIMARY INDEX(X_DIM) ON COMMIT PRESERVE ROWS; ';
   END IF;
  EXECUTE IMMEDIATE SQLSTR;   
END SP;