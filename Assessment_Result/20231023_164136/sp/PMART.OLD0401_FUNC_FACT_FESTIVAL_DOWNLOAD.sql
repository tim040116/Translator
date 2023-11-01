REPLACE PROCEDURE PMART.OLD0401_FUNC_FACT_FESTIVAL_DOWNLOAD
(
   IN P_ACTID VARCHAR(10),	
   IN P_ORGTYPE VARCHAR(10),				
   IN P_ORGID VARCHAR(2000),					   
   IN P_TIMETYPE VARCHAR(10),		
   IN P_TIMEID VARCHAR(2000),     
   IN P_PRDTYPE VARCHAR(100), 
   IN P_PRDID VARCHAR(2000),
   IN P_BRANDID VARCHAR(2000)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(6000);    
   DECLARE ORGWHERE  VARCHAR(2000);  
   DECLARE TIMENAME  VARCHAR(100);  
   DECLARE TIMEWHERE  VARCHAR(2000);  
   DECLARE TABLE_T4  VARCHAR(500);  
   DECLARE PRDNAME  VARCHAR(100);  
   DECLARE PRDJOIN  VARCHAR(2000); 
   DECLARE PRDANDWHERE VARCHAR(200);
   DECLARE BRANDANDWHERE VARCHAR(2000);
   DECLARE P_ACT_SDATE INTEGER;
   DECLARE P_ACT_EDATE INTEGER;
   DECLARE P_L_DAY_OF_WEEK INTEGER;
   SELECT B.ACT_SDATE, B.ACT_EDATE, A.L_DAY_OF_WEEK INTO P_ACT_SDATE, P_ACT_EDATE, P_L_DAY_OF_WEEK
       FROM PMART.TIME_D AS A
		JOIN (SELECT TRIM(CAST(TO_CHAR(ACT_SDATE, 'YYYY') AS INT)  + TO_CHAR(ACT_SDATE, 'MM') + TO_CHAR(ACT_SDATE, 'DD')) ACT_SDATE,
		                TRIM(CAST(TO_CHAR(ACT_EDATE, 'YYYY') AS INT)  + TO_CHAR(ACT_EDATE, 'MM') + TO_CHAR(ACT_EDATE, 'DD')) ACT_EDATE
		    FROM PDATA.FESTL
		 WHERE ACT_ID = P_ACTID
		      AND ACT_TYPE = 1) AS B ON A.L_DAY_ID = B.ACT_SDATE;
		CALL PMART.FUNC_YMWD_TIME_ACTIVEW(P_ACT_SDATE,P_ACT_EDATE,P_L_DAY_OF_WEEK);
		CALL PMART.P_DROP_TABLE('#TIME_ACTIVEW_TEMP');
		CREATE VOLATILE TABLE #TIME_ACTIVEW_TEMP AS (
		SELECT DISTINCT P_ACTID ACT_ID, L_WEEK_ID, ('第'+L_WEEK_ID+'週'+L_CDAY_ID_S+'~') WEK FROM #VT_TIME_ACTIVEW
		) WITH DATA PRIMARY  CHARINDEX( L_WEEK_ID,ACT_ID) ON COMMIT PRESERVE ROWS;
   IF P_ORGTYPE = '1' THEN
		SET ORGWHERE = ' AND C.DEPT_ID  = ' + P_ORGID;   
   ELSEIF P_ORGTYPE = '2' THEN
	    SET ORGWHERE = ' AND C.BRANCH_ID = ' + P_ORGID;
   ELSEIF P_ORGTYPE = '3' THEN
	    SET ORGWHERE = ' AND C.RESPON_ID = ' + P_ORGID;
   ELSEIF P_ORGTYPE = '4' THEN
	    SET ORGWHERE = ' AND C.OSTORE_ID = ' + P_ORGID;
   ELSE
	    SET ORGWHERE = '';
   END IF;   
    IF P_TIMETYPE = 'Y' THEN
	    SET TIMENAME = 'T3.L_YEAR_NAME L_TIME_NM, ';
		SET TIMEWHERE = ' JOIN PMART.TIME_Y T3 ON T1.TIME_ID = T3.L_YEAR_ID AND T3.L_YEAR_ID IN ('+P_TIMEID+') ';
   ELSEIF P_TIMETYPE = 'M' THEN
        SET TIMENAME = 'T3.L_MONTH_NAME L_TIME_NM, ';
	    SET TIMEWHERE = ' JOIN PMART.TIME_M T3 ON T1.TIME_ID = T3.L_MONTH_ID AND T3.L_MONTH_ID IN  ('+P_TIMEID+') ';
   ELSEIF P_TIMETYPE = 'W' THEN
   		SET TIMENAME = 'T3.WEK L_TIME_NM, ';
	    SET TIMEWHERE = ' JOIN #TIME_ACTIVEW_TEMP T3 ON T1.ACT_ID = T3.ACT_ID AND T1.TIME_ID = T3.L_WEEK_ID AND T3.L_WEEK_ID IN  ('+P_TIMEID+') ';
   ELSE
		SET TIMENAME = 'T3.L_DAY_NAME L_TIME_NM, ';
	    SET TIMEWHERE = ' JOIN PMART.TIME_D T3 ON T1.TIME_ID = T3.L_DAY_ID AND T3.L_DAY_ID IN  ('+P_TIMEID+') ';
   END IF;
	SET TABLE_T4 = 'SELECT A.ACT_ID, A.CLASS_ID, A.DIVISION_ID, A.BRAND_ID, A.FM_CODE, A.FM_NAME, A.PRICE, '
								   + '	          B.CSPRG, C.BRAND_NM, D.CODE_NAME '
								   + '	     FROM PDATA.FESTL_PRD_SET A '
								   + '	       JOIN PDATA.FESTL_CSPRG B ON A.ACT_ID = B.ACT_ID AND A.PRICE >= B.SCSP AND A.PRICE <= B.ECSP '
								   + '	       JOIN PDATA.FESTL_BRAND C ON A.ACT_ID = C.ACT_ID AND A.BRAND_ID = C.BRAND_ID '
								   + '	       JOIN PMART.VW_PBMCODE D ON A.GET_ID = D.CODE_ID AND D.CODE_TYPE =  ' +' ''FESTL_GET'+' '''
								   + '	  WHERE A.ACT_ID = ' + P_ACTID ;
	IF P_PRDTYPE = '-1' THEN  			
		SET PRDNAME = ' T4.FM_CODE, T4.FM_NAME, T4.PRICE, T4.CSPRG, T4.BRAND_NM, T4.CODE_NAME AS GET_NM, ';
        SET PRDJOIN = ' JOIN (' + TABLE_T4 + ') T4 ON T4.ACT_ID = ' + P_ACTID + ' AND T1.PRD_ID = CAST(T4.FM_CODE AS INTEGER) ';
		SET PRDANDWHERE = '';
   ELSEIF P_PRDTYPE = '1' THEN
		SET PRDNAME = ' T4.FM_CODE, T4.FM_NAME, T4.PRICE, T4.CSPRG, T4.BRAND_NM, T4.CODE_NAME AS GET_NM, ';        
		SET PRDJOIN = ' JOIN (' + TABLE_T4 + ') T4 ON T4.ACT_ID = ' + P_ACTID + ' AND T1.PRD_ID = CAST(T4.FM_CODE AS INTEGER) ';
		IF P_PRDID <> '' THEN
			SET PRDANDWHERE = ' AND T4.CLASS_ID = ' + P_PRDID;
		ELSE
			SET PRDANDWHERE = '';
		END IF;
   ELSEIF P_PRDTYPE = '2' THEN   
   		SET PRDNAME = ' T4.FM_CODE, T4.FM_NAME, T4.PRICE, T4.CSPRG, T4.BRAND_NM, T4.CODE_NAME AS GET_NM, ';	    
		SET PRDJOIN = ' JOIN (' + TABLE_T4 + ') T4 ON T4.ACT_ID = ' + P_ACTID + ' AND T1.PRD_ID = CAST(T4.FM_CODE AS INTEGER) ';
		SET PRDANDWHERE = ' AND T4.DIVISION_ID = ' + P_PRDID;
   ELSEIF P_PRDTYPE = '3' THEN
        SET PRDNAME = ' T4.FM_CODE, T4.FM_NAME, T4.PRICE, T4.CSPRG, T4.BRAND_NM, T4.CODE_NAME AS GET_NM, ';	 
		SET PRDJOIN = ' JOIN (' + TABLE_T4 + ') T4 ON T4.ACT_ID = ' + P_ACTID + ' AND T1.PRD_ID = CAST(T4.FM_CODE AS INTEGER) ';
		SET PRDANDWHERE = ' AND T1.PRD_ID = ' + P_PRDID; 
   END IF;
   IF P_BRANDID = '' THEN
		SET BRANDANDWHERE = '';
	ELSE
		SET BRANDANDWHERE = ' AND T4.BRAND_ID = ' + P_BRANDID;
	END IF;	    
CALL PMART.P_DROP_TABLE ('#VT_FACT_FESTIVAL_DOWNLOAD');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_FESTIVAL_DOWNLOAD AS('                          										   
						   + ' SELECT T1.ORG_ID, T1.TIME_ID,  '
						   + ' 	            T2.DEPT_NO, T2.BRANCH_NO, T2.STORE_NO, T2.STORE_NAME, ' + TIMENAME + PRDNAME
						   + '                 T1.SALE_CNT, T1.SALE_AMT, T1.FINAL_SALE_AMT '						   					   
						   + ' 	   FROM PMART.FACT_FESTIVAL T1 '
						   + ' 	    JOIN ( '
						   + ' 	 		SELECT A.DEPT_NO, B.BRANCH_NO, C.STORE_NO, C.STORE_NAME, C.OSTORE_ID '
						   + ' 			   FROM ORG_DEPT A '
						   + ' 			    JOIN ORG_BRANCH B ON A.DEPT_ID = B.DEPT_ID '
						   + ' 				JOIN LAST_ORG_STORE C ON C.BRANCH_ID =  B.BRANCH_ID ' + ORGWHERE
						   + ' 	 ) T2 ON T1.ORG_ID = T2.OSTORE_ID '
						   +        TIMEWHERE											 	
						   +        PRDJOIN						
						   + '     WHERE T1.ACT_ID = ' + P_ACTID + ' AND T1.TIME_ID IN ('+P_TIMEID+') '						
						   +       PRDANDWHERE + BRANDANDWHERE
                           + ' ) WITH DATA PRIMARY INDEX(ORG_ID, TIME_ID, FM_CODE) ON COMMIT PRESERVE ROWS;';
		EXECUTE IMMEDIATE SQLSTR; 
END SP;