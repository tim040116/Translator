REPLACE  PROCEDURE PMART.FUNC_FACT_BF
(
   IN P_ORGTYPE VARCHAR(10),   
   IN P_GROUP VARCHAR(500),
   IN P_TIMEID VARCHAR(2000),
   IN P_ORGID VARCHAR(2000),
   IN P_CUSTTYPE VARCHAR(500),
   IN P_TIMERANGE VARCHAR(500),   
   IN P_DISTRICT VARCHAR(100)
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(6000);    
	 DECLARE ORGSELECT VARCHAR(2000);
	 DECLARE ORGWHERE VARCHAR(2000);  
   	 DECLARE P_DISTRICT_IN VARCHAR(100);
     SET P_DISTRICT_IN = Replace(TRIM(P_DISTRICT),',',''',''');
	 IF P_DISTRICT = '' THEN
	 	SET ORGWHERE = ' WHERE 1=1 ';		
	 ELSE
	 	SET ORGWHERE = ' WHERE SHOP_DISTTRICT_MAIN IN ('''  + P_DISTRICT_IN + ''') ';
	 END IF;	 
	IF P_ORGTYPE = '0' THEN
		SET ORGSELECT = 'DEPT_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND PDEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '1' THEN
		SET ORGSELECT = 'BRANCH_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND DEPT_ID  = ' + P_ORGID;   
	ELSEIF P_ORGTYPE = '2' THEN
		SET ORGSELECT = 'RESPON_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND BRANCH_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '3' THEN
		SET ORGSELECT = 'OSTORE_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND RESPON_ID = ' + P_ORGID;
	ELSEIF P_ORGTYPE = '4' THEN
		SET ORGSELECT = 'OSTORE_ID AS GROUP_ORG_ID, OSTORE_ID';
		SET ORGWHERE = ORGWHERE + ' AND OSTORE_ID = ' + P_ORGID;
	ELSE
		SET ORGSELECT = 'PDEPT_ID AS GROUP_ORG_ID, OSTORE_ID';
	END IF;   
CALL PMART.P_DROP_TABLE ('#VT_FACT_BREAKFASTTICKET_TEMP');
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_BREAKFASTTICKET_TEMP AS('
						 + ' SELECT ' + P_GROUP + ', '
					     + '               SUM(T1.TRAN_CNT) AS TRAN_CNT, SUM(T1.TRAN_AMT) AS TRAN_AMT, '					
						 + '				   SUM(T1.TICKET_PAY_AMT) AS TICKET_PAY_AMT, '
						 + '				   SUM(T1.TICKET_CNT) AS TICKET_CNT, '
						 + '				   SUM(T1.DIS_SUB_AMT) AS DIS_SUB_AMT '
						 + '	    FROM PMART.FACT_BREAKFASTTICKET T1 '
						 + '		JOIN ( '
						 + '			SELECT ' + ORGSELECT 
						 + '			    FROM PMART.LAST_ORG_STORE '
						 + '			' + ORGWHERE
						 + '		) T2 ON T1.ORG_ID = T2.OSTORE_ID '
						 + '		WHERE T1.TIME_ID IN ('+P_TIMEID+')' 
						 + CASE WHEN TRIM(P_CUSTTYPE)='' AND POSITION('CARD_TYPE' IN P_GROUP)=0 THEN ' AND T1.CUST_RANGE=-1 ' WHEN TRIM(P_CUSTTYPE)='' THEN ''  ELSE ' AND T1.CUST_RANGE IN('+P_CUSTTYPE+') ' END
                         + CASE WHEN TRIM(P_TIMERANGE)='' AND POSITION('TIME_RANGE' IN P_GROUP)=0 THEN '  AND T1.TIME_RANGE=-1 ' WHEN TRIM(P_TIMERANGE)='' THEN ''  ELSE ' AND T1.TIME_RANGE IN('+P_TIMERANGE+') ' END	
						 + '		 GROUP BY ' + P_GROUP
			             + ' ) WITH DATA PRIMARY INDEX('+ P_GROUP +') ON COMMIT PRESERVE ROWS;';						  					
        EXECUTE IMMEDIATE SQLSTR;   
		CALL PMART.P_DROP_TABLE ('#VT_FACT_BREAKFASTTICKET');
		SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_BREAKFASTTICKET AS('						
							+ '	SELECT 1 DATA_TYPE, NULL TIME_ID,NULL Y_DIM,  '
							+ '	SUM(TRAN_CNT) AS TRAN_CNT, SUM(TRAN_AMT) AS TRAN_AMT,  '
							+ '	SUM(TICKET_PAY_AMT) AS TICKET_PAY_AMT, SUM(TICKET_CNT) AS TICKET_CNT,  SUM(DIS_SUB_AMT) AS DIS_SUB_AMT '
							+ '	FROM #VT_FACT_BREAKFASTTICKET_TEMP '
							+ '	UNION ALL '
							+ '	SELECT 2 DATA_TYPE, TIME_ID,NULL Y_DIM, '
							+ '	SUM(TRAN_CNT) AS TRAN_CNT, SUM(TRAN_AMT) AS TRAN_AMT,  ' 
							+ '	SUM(TICKET_PAY_AMT) AS TICKET_PAY_AMT, SUM(TICKET_CNT) AS TICKET_CNT,  SUM(DIS_SUB_AMT) AS DIS_SUB_AMT '
							+ '	FROM #VT_FACT_BREAKFASTTICKET_TEMP '
							+ '	GROUP BY TIME_ID '
							+ '	UNION ALL '
							+ '	SELECT 3 DATA_TYPE, NULL TIME_ID, ' + Replace(TRIM(P_GROUP),'TIME_ID,','')  + '  Y_DIM, '
							+ '	SUM(TRAN_CNT) AS TRAN_CNT, SUM(TRAN_AMT) AS TRAN_AMT,  ' 
							+ '	SUM(TICKET_PAY_AMT) AS TICKET_PAY_AMT, SUM(TICKET_CNT) AS TICKET_CNT,  SUM(DIS_SUB_AMT) AS DIS_SUB_AMT '
							+ '	FROM #VT_FACT_BREAKFASTTICKET_TEMP '
							+ '	GROUP BY ' + Replace(TRIM(P_GROUP),'TIME_ID,','') 
							+ '	UNION ALL '
							+ '	SELECT 4 DATA_TYPE,TIME_ID, ' + Replace(TRIM(P_GROUP),'TIME_ID,','')  + '  Y_DIM, '
							+ '	TRAN_CNT, TRAN_AMT, TICKET_PAY_AMT, TICKET_CNT, DIS_SUB_AMT '
							+ '	FROM #VT_FACT_BREAKFASTTICKET_TEMP '
						    + ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
        EXECUTE IMMEDIATE SQLSTR;   
END SP;