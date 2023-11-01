REPLACE PROCEDURE PMART.FUNC_FACT_PBOASUGORDD_D
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
 DECLARE  V_PDEPT_NO  VARCHAR(6) ;        
 DECLARE  V_PDEPT_SNAME  VARCHAR(50) ;    
 DECLARE  V_DEPT_NO  VARCHAR(6) ;         
 DECLARE  V_DEPT_SNAME VARCHAR(50) ;      
 DECLARE  V_BRANCH_NO VARCHAR(6) ;        
 DECLARE  V_BRANCH_SNAME VARCHAR(50) ;    
 DECLARE  V_RESPON_NO VARCHAR(10) ;       
 DECLARE  V_RESPON_NM VARCHAR(50) ;      
 DECLARE  V_STORE_NO VARCHAR(6) ;         
 DECLARE  V_STORE_NAME VARCHAR(18) ;      
 DECLARE  V_KND_NM VARCHAR(50) ;          
 DECLARE  V_GRP_NM VARCHAR(50) ;          
 DECLARE  V_PRD_NM VARCHAR(50) ;         
 DECLARE  V_TIME_ID VARCHAR(20) ;        
 DECLARE  V_STORE_NUM INTEGER;           
 DECLARE  V_SYS_USED VARCHAR(1) ;         
 DECLARE  V_SALES_PSD DECIMAL(18,2);     
 DECLARE  V_STSET_PCT INTEGER;           
 DECLARE  V_MINORD_QTY INTEGER;          
 DECLARE  SQLSTR  VARCHAR(2000) ; 
 DECLARE  ORGWHERE VARCHAR(1000) ; 
 DECLARE  PRDWHERE VARCHAR(1000) ; 
 DECLARE  TIMESELETE VARCHAR(100) ; 
 DECLARE  TIMEGROUP VARCHAR(100) ; 
 DECLARE  TIMEJOIN VARCHAR(1000) ; 
  IF P_ORGTYPE = -1 THEN
       SET ORGWHERE = ' B.TOT_ID = -1 ';
  ELSEIF P_ORGTYPE = 0 THEN
       SET ORGWHERE = ' B.PDEPT_ID = ' + P_ORGID ;
  ELSEIF P_ORGTYPE = 1 THEN
       SET ORGWHERE = ' B.DEPT_ID = ' + P_ORGID ;
  ELSEIF P_ORGTYPE = 2 THEN
       SET ORGWHERE = ' B.BRANCH_ID = ' + P_ORGID ;
  ELSEIF P_ORGTYPE = 3 THEN
       SET ORGWHERE = ' B.RESPON_ID = ' + P_ORGID ;
  ELSEIF P_ORGTYPE = 4 THEN
       SET ORGWHERE = ' B.STORE_ID = ' + P_ORGID ;
  END IF;
  IF P_PRDTYPE = -1 THEN
       SET PRDWHERE = ' C.TOT_ID = ''-1'' ' ;
  ELSEIF P_PRDTYPE = 1 THEN
       SET PRDWHERE = ' C.KND_ID = ''' + P_PRDID +'''';
  ELSEIF P_PRDTYPE = 2 THEN
       SET PRDWHERE = ' C.GRP_ID = ''' + P_PRDID +'''';
  ELSEIF P_PRDTYPE = 3 THEN
       SET PRDWHERE = ' C.PRD_ID = ''' + P_PRDID +'''';
  END IF;
  IF P_TIMETYPE = 'D' THEN
    SET TIMESELETE = ' ,D.L_DAY_NAME AS  TIME_ID ' ;
    SET TIMEJOIN = ' LEFT JOIN PMART.YMWD_TIME D ON A.TIME_ID = D.L_DAY_ID ';
    SET TIMEGROUP = ',D.L_DAY_NAME ';
  ELSEIF P_TIMETYPE = 'W' THEN
    SET TIMESELETE = ' ,D.L_WEEK_NAME AS  TIME_ID ' ;
    SET TIMEJOIN = ' LEFT JOIN (SELECT DISTINCT L_WEEK_ID,L_WEEK_NAME FROM  PMART.YMWD_TIME )D ON  A.TIME_ID = D.L_WEEK_ID ';
    SET TIMEGROUP = ',D.L_WEEK_NAME ';
  ELSEIF P_TIMETYPE = 'M' THEN
    SET TIMESELETE = ' ,D.L_MONTH_NAME AS  TIME_ID ' ;
    SET TIMEJOIN = ' LEFT JOIN (SELECT DISTINCT L_MONTH_ID,L_MONTH_NAME FROM  PMART.YMWD_TIME )D ON  A.TIME_ID = D.L_MONTH_ID ';
    SET TIMEGROUP = ',D.L_MONTH_NAME ';
  END IF;
  	CALL PMART.P_DROP_TABLE ('#VT_FACT_PBOASUGORDD_D'); 
  IF P_RPTID = 1 THEN
      SET SQLSTR =  ' CREATE MULTISET VOLATILE TABLE #VT_FACT_PBOASUGORDD_D AS( '
	    +  '  SELECT B.PDEPT_NO AS PDEPT_NO,B.PDEPT_NM AS PDEPT_SNAME,B.DEPT_NO AS DEPT_NO,B.DEPT_NM AS DEPT_SNAME, B.BRANCH_NO,B.BRANCH_NM AS BRANCH_SNAME '
        +  '  ,B.RESPON_NO AS RESPON_NO,B.RESPON_NM AS RESPON_NM,B.STORE_NO AS STORE_NO,B.STORE_NM AS STORE_NAME,'' '' AS KND_NM, '' '' AS GRP_NM, '' '' AS PRD_NM ' 
        +  TIMESELETE
        +  '  ,ROUND(AVG(WORK_DAYS),0) AS STORE_NUM '
        +  '  ,MAX(USE_SYS) AS SYS_USED '
        +  '  ,CASE SUM(P14_WORK_DAYS) WHEN 0 THEN 0 ELSE ROUND(SUM(P14_SALE_QTY)/SUM(P14_WORK_DAYS),2) END AS SALES_PSD '
        +  '  ,ROUND(AVG(STSET_PCT),0) AS STSET_PCT  '
        +  '  ,ROUND(AVG(MINORD_QTY),0) AS MINORD_QTY '
        +  '  FROM  '
        +  '  ( '
        +  '    SELECT TIME_ID, ORG_ID, PRD_ID '
        +  '    ,MAX(STORE_QTY) AS STORE_QTY '
        +  '    ,SUM(USEST_QTY) AS USEST_QTY '
        +  '    ,SUM(PRD_QTY) AS PRD_QTY '
        +  '    ,SUM(UCPRD_QTY) AS UCPRD_QTY '
        +  '    ,SUM(STORD_QTY) AS STORD_QTY '
        +  '    ,SUM(SYSORD_QTY) AS SYSORD_QTY '
        +  '    ,MAX(WORK_DAYS) AS WORK_DAYS '
        +  '    ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
        +  '    ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
        +  '    ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
        +  '    ,MAX(STSET_PCT) AS STSET_PCT '
        +  '    ,MAX(MINORD_QTY) AS MINORD_QTY '
        +  '    FROM PMART.FACT_PBOA_DETAIL  '
        +  '    WHERE DATA_USE_TYPE IN (0,1) '
        +  '    GROUP BY TIME_ID, ORG_ID, PRD_ID '
        +  '   )A '
        +  '     LEFT JOIN PMART.LAST_ORG_DIM B ON A.ORG_ID = B.OSTORE_ID '
        +  '     LEFT JOIN PMART.PRD_DIM C ON A.PRD_ID = C.PRD_ID '
        +  TIMEJOIN 
        +  '  WHERE A.TIME_ID IN (' + P_TIMEID + ') AND ' + ORGWHERE + ' AND ' + PRDWHERE
        +  '  GROUP BY B.PDEPT_NO,B.PDEPT_NM,B.DEPT_NO,B.DEPT_NM, B.BRANCH_NO,B.BRANCH_NM,B.RESPON_NO,B.RESPON_NM,B.STORE_NO,B.STORE_NM ' + TIMEGROUP 
		+  '  ) WITH DATA PRIMARY INDEX(STORE_NO) ON COMMIT PRESERVE ROWS;';
  ELSEIF P_RPTID = 2 THEN
       SET SQLSTR =' CREATE MULTISET VOLATILE TABLE #VT_FACT_PBOASUGORDD_D AS( '
	    +  '  SELECT B.PDEPT_NO AS PDEPT_NO,B.PDEPT_NM AS PDEPT_SNAME,B.DEPT_NO AS DEPT_NO,B.DEPT_NM AS DEPT_SNAME,B.BRANCH_NO,B.BRANCH_NM AS BRANCH_SNAME '
        +  '  ,B.RESPON_NO AS RESPON_NO,B.RESPON_NM AS RESPON_NM,B.STORE_NO AS STORE_NO,B.STORE_NM AS STORE_NAME,C.KND_NM, C.GRP_NM, C.PRD_NM '
        +  TIMESELETE 
        +  '  ,ROUND(AVG(WORK_DAYS),0) AS STORE_NUM '
        +  '  ,MAX(USE_SYS) AS SYS_USED '
        +  '  ,CASE SUM(P14_WORK_DAYS) WHEN 0 THEN 0 ELSE ROUND(SUM(P14_SALE_QTY)/SUM(P14_WORK_DAYS),2) END AS SALES_PSD '
        +  '  ,ROUND(AVG(STSET_PCT),0) AS STSET_PCT '
        +  '  ,ROUND(AVG(MINORD_QTY),0) AS MINORD_QTY '
        +  '  FROM '
        +  '  ( '
        +  '    SELECT TIME_ID, ORG_ID, PRD_ID '
        +  '    ,MAX(STORE_QTY) AS STORE_QTY '
        +  '    ,SUM(USEST_QTY) AS USEST_QTY '
        +  '    ,SUM(PRD_QTY) AS PRD_QTY '
        +  '    ,SUM(UCPRD_QTY) AS UCPRD_QTY '
        +  '    ,SUM(STORD_QTY) AS STORD_QTY '
        +  '    ,SUM(SYSORD_QTY) AS SYSORD_QTY '
        +  '    ,MAX(WORK_DAYS) AS WORK_DAYS '
        +  '    ,CASE WHEN SUM(CASE USE_SYS WHEN ''Y'' THEN 1 ELSE 0 END)>1 THEN ''Y'' ELSE ''N'' END USE_SYS '
        +  '    ,MAX(P14_SALE_QTY) AS P14_SALE_QTY '
        +  '    ,MAX(P14_WORK_DAYS) AS P14_WORK_DAYS '
        +  '    ,MAX(STSET_PCT) AS STSET_PCT '
        +  '    ,MAX(MINORD_QTY) AS MINORD_QTY '
        +  '    FROM PMART.FACT_PBOA_DETAIL '
        +  '    WHERE DATA_USE_TYPE IN (0,1) '
        +  '    GROUP BY TIME_ID, ORG_ID, PRD_ID '
        +  '   )A '
        +  '     LEFT JOIN PMART.LAST_ORG_DIM B ON A.ORG_ID = B.OSTORE_ID '
        +  '     LEFT JOIN PMART.PRD_DIM C ON A.PRD_ID = C.PRD_ID '
        +  TIMEJOIN
        +  '  WHERE A.TIME_ID IN (' + P_TIMEID + ') AND ' + ORGWHERE + ' AND ' + PRDWHERE
        +  '  GROUP BY B.PDEPT_NO,B.PDEPT_NM, B.DEPT_NO,B.DEPT_NM,B.BRANCH_NO,B.BRANCH_NM,B.RESPON_NO,B.RESPON_NM,B.STORE_NO,B.STORE_NM,C.KND_NM, C.GRP_NM, C.PRD_NM' + TIMEGROUP
		+  '  ) WITH DATA PRIMARY INDEX(STORE_NO) ON COMMIT PRESERVE ROWS;';
  END IF;
  EXECUTE IMMEDIATE SQLSTR;   
END SP;