REPLACE PROCEDURE PMART.FUNC_FACT_STMARKETING_CSPRG
(
   IN P_ACTID INTEGER,
   IN P_ORGTYPE VARCHAR(10),   
   IN P_ORGID VARCHAR(2000),
   IN P_TIMEID VARCHAR(2000)
)
SP:BEGIN
    DECLARE SQLSTR  VARCHAR(2000);    
	DECLARE ORGSELECT VARCHAR(2000);
	DECLARE ORGWHERE VARCHAR(2000);  
	SET ORGWHERE = '';
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
	CALL PMART.P_DROP_TABLE ('#VT_FUNC_FACT_STMARKETING_CSPRG');
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FUNC_FACT_STMARKETING_CSPRG AS('
	                     + ' SELECT  DAT.TIME_ID,DAT.ACT_ID,DAT.CSPID, SUM(DAT.TOT_TX_CNT) TRAN_CNT  FROM ( '
	    			     + ' SELECT  T1.ORG_ID,T1.TIME_ID,T1.ACT_ID,CSPRG.CSPID, T1.CUST_PRICE, T1.TOT_TX_CNT '
						 + '    FROM  (      SELECT ORG_ID,TIME_ID,ACT_ID,CAST(ROUND(SUM(TX_AMT)/SUM(TX_CNT)*1.0) AS SMALLINT)CUST_PRICE,SUM(TX_CNT) AS TOT_TX_CNT '
	                     + '                           FROM PMART.FACT_STMARKETING '
	                     + '                   GROUP BY ORG_ID,TIME_ID,ACT_ID '
	                     + '                ) T1 '
						 + '  	 INNER JOIN ( '
						 + '	                     	SELECT ' + ORGSELECT 
  					     + '			                    FROM PMART.LAST_ORG_STORE '
						 + '                            WHERE 1=1 ';
    IF P_ORGID<> '' THEN     
        SET SQLSTR = SQLSTR + '			' + ORGWHERE;
    END IF;
	SET SQLSTR = SQLSTR + '		      ) T2 ON T1.ORG_ID = T2.OSTORE_ID '	
	                                 + 'INNER JOIN PDATA.STORMKT_CSPRG CSPRG ON CSPRG.ACT_ID = T1.ACT_ID AND T1.CUST_PRICE BETWEEN SCSP AND ECSP '
	    	  				         + ' WHERE T1.TIME_ID IN ('+P_TIMEID+')'				 
			    			         + '       AND T1.ACT_ID = '  + P_ACTID
									 + ' ) DAT GROUP BY DAT.TIME_ID,DAT.ACT_ID,DAT.CSPID '
					    	         + ' ) WITH DATA PRIMARY INDEX(TIME_ID,ACT_ID,CSPID) ON COMMIT PRESERVE ROWS;';
    EXECUTE IMMEDIATE SQLSTR;   
END SP;