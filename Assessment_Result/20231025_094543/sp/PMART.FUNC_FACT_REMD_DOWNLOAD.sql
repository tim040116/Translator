REPLACE PROCEDURE PMART.FUNC_FACT_REMD_DOWNLOAD
(
    IN P_RPTID SMALLINT,
    IN P_TIMEID VARCHAR(500),
	IN P_TIMETYPE VARCHAR(1),
    IN P_ORGTYPE SMALLINT,
    IN P_ORGID VARCHAR(10),
    IN P_DISTRICTTYPE SMALLINT,
    IN P_DISTRICT VARCHAR(20),
    IN P_OPENYEAR SMALLINT,
    IN P_STORETYPE VARCHAR(1)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(10000);
	DECLARE ORGWHERE VARCHAR(2000);  
   DECLARE TIMENAME  VARCHAR(100);  
   DECLARE TIMEWHERE  VARCHAR(2000);  	
    SET ORGWHERE = ' WHERE 1=1 ';
    IF P_ORGTYPE = 1 THEN
	    SET ORGWHERE = ORGWHERE + ' AND S.DEPT_ID  = ' + TRIM(P_ORGID); 
    ELSEIF P_ORGTYPE = 2 THEN
	    SET ORGWHERE = ORGWHERE + ' AND S.BRANCH_ID = ' + TRIM(P_ORGID);
    ELSEIF P_ORGTYPE = 3 THEN
	    SET ORGWHERE = ORGWHERE + ' AND S.RESPON_ID = ' + TRIM(P_ORGID);
    ELSEIF P_ORGTYPE = 4 THEN
	    SET ORGWHERE = ORGWHERE + ' AND S.OSTORE_ID = ' + TRIM(P_ORGID);
    END IF;   
    IF P_DISTRICTTYPE = 1 THEN
	    SET ORGWHERE = ORGWHERE + ' AND SHOP_DISTTRICT_MAIN = '''  + P_DISTRICT + ''' ';		  
    ELSEIF P_DISTRICTTYPE = 2 THEN
	  	SET ORGWHERE = ORGWHERE + ' AND SEC_DISTRICT = '''  + P_DISTRICT + ''' ';
    END IF;
    IF P_OPENYEAR <> -1 THEN
        SET ORGWHERE = ORGWHERE + ' AND OPEN_YEAR =' + P_OPENYEAR;	  
    END IF;
    IF TRIM(P_STORETYPE) <> '' THEN
        SET ORGWHERE = ORGWHERE + ' AND S.STORE_TYPE =''' + TRIM(P_STORETYPE) + ''' ';	  
    END IF;
   IF P_TIMETYPE = 'Y' THEN
	    SET TIMENAME = 'T3.L_YEAR_NAME ';
		SET TIMEWHERE = ' LEFT JOIN PMART.TIME_Y T3 ON T1.TIME_ID = T3.L_YEAR_ID ';
   ELSEIF P_TIMETYPE = 'M' THEN
        SET TIMENAME = 'T3.L_MONTH_NAME ';
	    SET TIMEWHERE = ' LEFT JOIN PMART.TIME_M T3 ON T1.TIME_ID = T3.L_MONTH_ID ';
   ELSE
		SET TIMENAME = 'T3.L_DAY_NAME ';
	    SET TIMEWHERE = ' LEFT JOIN PMART.TIME_D T3 ON T1.TIME_ID = T3.L_DAY_ID ';
   END IF;	
    CALL PMART.P_DROP_TABLE ('#VT_FACT_REMD_DOWNLOAD');
    CALL PMART.P_DROP_TABLE ('#VT_REMD_FACT');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_REMD_FACT AS('
              + '  SELECT '
              + '   T1.OSTORE_ID AS ORG_ID '
              + '  ,COALESCE(T1.L_DAY_ID,T2.L_MONTH_ID,COALESCE(T2.L_YEAR_ID,-1)) AS TIME_ID '	      
		  + '  ,SUM(CASE WHEN T1.CUST_NUM IS NULL  THEN 0 ELSE CAST(T1.CUST_NUM AS DECIMAL(12,0)) END ) AS TX_CNT  '
		  + '  ,SUM(CASE WHEN T1.AMT IS NULL  THEN 0 ELSE CAST(T1.AMT AS DECIMAL(16,2)) END ) AS TX_AMT  '
              + '  ,SUM(CASE WHEN T1.PLAN_STNUM =1  THEN 1 ELSE 0 END ) AS REMD_DAY  '
              + '    FROM PMART.REMD_FACT T1 INNER JOIN PMART.TIME_D AS T2 '
              + '      ON T1.L_DAY_ID = T2.L_DAY_ID '
              + '   WHERE T1.L_DAY_ID < CAST(TO_CHAR(CURRENT_DATE ,''YYYYMMDD'')  AS INTEGER) ' 
              + 'GROUP BY T1.OSTORE_ID,ROLLUP(T2.L_YEAR_ID,T2.L_MONTH_ID,T1.L_DAY_ID) '
    	      + ' )  WITH DATA UNIQUE PRIMARY  CHARINDEX(TIME_ID , ORG_ID ) ON COMMIT PRESERVE ROWS;';	
    EXECUTE IMMEDIATE SQLSTR;    
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_REMD_DOWNLOAD AS('		  
	   					 +' SELECT T2.DEPT_NO,T2.DEPT_SNAME,T2.BRANCH_NO,T2.BRANCH_SNAME,T2.STORE_NO,T2.STORE_NAME,T2.STTP_TYPE_NM,T2.OPEN_YEAR,T2.OPEN_DATE '
						 +'               ,T2.SHOP_DISTTRICT_NM,T2.SEC_DISTRICT_NM, ' + TIMENAME+' AS TIME_ID '
						 +'               ,T1.TX_AMT,T1.TX_CNT,ROUND((CAST(T1.TX_AMT AS DECIMAL(12,2)) / CASE WHEN T1.TX_CNT=0 THEN 1 ELSE T1.TX_CNT END),2) AS CUST_PRICE, T1.REMD_DAY '
						 +'    FROM #VT_REMD_FACT T1 '
						 +'               INNER JOIN ( '
						 +'                                       SELECT DEPT.DEPT_NO,DEPT.DEPT_SNAME,BRANCH.BRANCH_NO,BRANCH.BRANCH_SNAME,S.OSTORE_ID,S.STORE_NO,S.STORE_NAME,T.STTP_SNAME AS STTP_TYPE_NM,OST.OPEN_YEAR,OST.OPEN_DATE '
						 +'                                                      ,SHOP.SHOPDIST_NAME AS SHOP_DISTTRICT_NM '
						 +'                                                      ,SEC_SHOP.SHOPDIST_NAME AS SEC_DISTRICT_NM,S.STORE_TYPE '
						 +'                                                      ,S.SHOP_DISTTRICT_MAIN AS SHOP_DISTTRICT, NVL(S.SHOP_ZONE_MAIN,'''')+NVL(S.SHOP_ZONE_SEC1,'''')+NVL(S.SHOP_ZONE_SEC2,'''') AS SEC_DISTRICT '
						 +'                                          FROM PMART.LAST_ORG_STORE S '
						 +'                                                     LEFT JOIN PMART.VW_SHOPDIST_DIM SHOP ON S.SHOP_DISTTRICT_MAIN = SHOP.SHOPDIST_ID '
						 +'                                                     LEFT JOIN PMART.VW_SHOPDIST_DIM SEC_SHOP ON  NVL(S.SHOP_ZONE_MAIN,'''')+NVL(S.SHOP_ZONE_SEC1,'''')+NVL(S.SHOP_ZONE_SEC2,'''') = SEC_SHOP.SHOPDIST_ID '
						 +'                                                     LEFT JOIN PMART.ORG_DEPT DEPT ON S.DEPT_ID = DEPT.DEPT_ID '
						 +'                                                     LEFT JOIN PMART.ORG_BRANCH BRANCH ON S.BRANCH_ID = BRANCH.BRANCH_ID '
						 +'                                                     LEFT JOIN PDATA.PBMSTTP T ON S.STORE_TYPE = T.STORE_TYPE '
						 +'                                                     LEFT JOIN  (  SELECT CAST(OSTORE_NO AS INTEGER) AS OSTORE_ID, (CAST(TO_CHAR(OPEN_DATE,''YYYY'') AS INTEGER) - 1911) AS OPEN_YEAR '
						 +'                                                                                          ,(CAST(SUBSTRING(TO_CHAR(OPEN_DATE,''YYYY/MM/DD''),1,4) AS INTEGER)-1911)+ SUBSTRING(TO_CHAR(OPEN_DATE,''YYYY/MM/DD''),5,6)   AS OPEN_DATE'
						 +'                                                                               FROM PDATA.PBMSTOR '
						 +'                                                                            WHERE STORE_NO = OSTORE_NO '
						 +'                                                                         ) AS OST ON S.OSTORE_ID = OST.OSTORE_ID '
						 +'                       ' + TRIM(ORGWHERE)
						 +'                                    ) T2 ON T1.ORG_ID = T2.OSTORE_ID '
						 +                                    TIMEWHERE
						 +' WHERE T1.TIME_ID IN ('+P_TIMEID+')' 		
			             + ' ) WITH DATA PRIMARY  CHARINDEX(STORE_NO,TIME_ID) ON COMMIT PRESERVE ROWS;';						  					
        EXECUTE IMMEDIATE SQLSTR;   
END SP;