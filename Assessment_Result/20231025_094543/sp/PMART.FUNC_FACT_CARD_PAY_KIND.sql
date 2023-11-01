REPLACE PROCEDURE PMART.FUNC_FACT_CARD_PAY_KIND
(
   IN P_ORGID VARCHAR(50), 
   IN P_SDATE VARCHAR(20),
   IN P_EDATE VARCHAR(20)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(10000);   
   DECLARE ORGSTR VARCHAR(100); 
   DECLARE V_SDATE INTEGER;
   DECLARE V_EDATE INTEGER;
CALL PMART.P_DROP_TABLE ('#VT_FACT_CARD_PAY_KIND');
IF SUBSTRING(P_ORGID,1,4) = 1000 THEN
	SET ORGSTR = ' AND S1.BRANCH_ID =' + P_ORGID;
ELSEIF SUBSTRING(P_ORGID,1,4) = 2000 THEN
    SET ORGSTR = ' AND S1.DEPT_ID =' + P_ORGID;
ELSEIF SUBSTRING(P_ORGID,1,4) = 3000 THEN
	SET ORGSTR = ' AND S1.PDEPT_ID =' + P_ORGID;
ELSE
    SET ORGSTR = '';
END IF;
SET V_SDATE = CAST(SUBSTRING(P_SDATE,1,4) + SUBSTRING(P_SDATE,6,2) + SUBSTRING(P_SDATE,9,2) AS INTEGER);
SET V_EDATE = CAST(SUBSTRING(P_EDATE,1,4) + SUBSTRING(P_EDATE,6,2) + SUBSTRING(P_EDATE,9,2) AS INTEGER);
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_CARD_PAY_KIND AS('
                          +' SELECT '
						  + P_ORGID + ' AS ORG_ID, T1.TOTAL_AMT, T1.TOTAL_CNT, '
						  + 'T2.DATA_TYPE, T2.PAY_KIND, T2.PAY_GRP, T2.PAY_GRP_NAME, T2.PAY_GRP_NAME_1,'
						  + 'T2.PAY_AMT, T2.PAY_CNT,'
						  + 'CAST((CAST (T2.PAY_AMT AS DECIMAL(20,6)))/(CAST (T1.TOTAL_AMT AS DECIMAL(20,6)))  * 100  AS DECIMAL(20,4))  AS PAY_PERCENT'
						  + ' FROM ('
						  + '	    	 SELECT '  + P_ORGID + ' AS ORG_ID, '
						  + '              SUM(AM_TOT * DECODE(TX.SALE_TYPE,''Z0'',1,-1) ) TOTAL_AMT, SUM(DECODE(TX.SALE_TYPE,''Z0'',1,-1)) TOTAL_CNT '
						  + '		        FROM PDATA.TRANS TX '	
						  + '			   INNER JOIN  PMART.LAST_ORG_STORE AS S1 ON TX.OSTORE_ID=S1.OSTORE_ID' + ORGSTR
						  + '  WHERE TX.TX_DTTM >= CAST('''+ P_SDATE +''' AS DATE)  '
                          + '         AND TX.TX_DTTM <= CAST('''+ P_EDATE +''' AS DATE)  '
                          + '         AND TX.SALE_TYPE IN (''Z0'',''R2'',''R3'') '
                          +'          AND EXISTS (SELECT 1    '   
					      +'                                        FROM PDATA.TRANS_PAYMENT PAY    '  
						  +'                                                     INNER JOIN  PDATA.PBMPAYTYPE AS PAYM ON PAY.PAY_TYPE = PAYM.PAY_TYPE AND PAY.PAY_TYPE_ID = PAYM.PAY_TYPE_ID '
					      +'                                    WHERE TX.TX_SEQ = PAY.TX_SEQ    '  
					      +'                                           AND PAY.TX_DTTM >=  CAST('''+ P_SDATE +''' AS DATE)  '
					      +'                                           AND PAY.TX_DTTM <=  CAST('''+ P_EDATE +''' AS DATE)  '
			              +'                                   )		'						  
						  + '	) T1'
						  + '  JOIN  ( '
						  + ' SELECT 2 AS DATA_TYPE, '  + P_ORGID + ' AS ORG_ID '
					      + '                ,PTYPE.PAY_KIND '
						  + '			      ,PTYPE.PAY_GRP, PTYPE.PAY_GRP_NAME, PTYPE.PAY_GRP_NAME_1 '
					      + '                ,SUM(ISNULL(PM.PAY_AMT)) PAY_AMT, SUM(ISNULL(PM.PAY_CNT)) PAY_CNT '
					      + '     FROM PMART.FACT_CARD_PAY_PRDGRP PM '
						  + '	   JOIN ( '
						  + '	   		SELECT DISTINCT PAY_KIND, PAY_GRP, PAY_GRP_NAME, (PAY_KIND+PAY_GRP_NAME) PAY_GRP_NAME_1 FROM PDATA.PBMPAYTYPE '
						  + '	   ) PTYPE ON PM.PAY_TYPE = PTYPE.PAY_GRP '
						  + '	    WHERE PM.TIME_ID >= ' + V_SDATE
					      + '		    AND PM.TIME_ID <= ' + V_EDATE
						  + '		    AND PM.ORG_ID = ' + P_ORGID
						  + '		    AND PM.GRP_ID = ''-1'' '
						  + '	    GROUP BY PTYPE.PAY_KIND,PTYPE.PAY_GRP, PTYPE.PAY_GRP_NAME, PTYPE.PAY_GRP_NAME_1 '
						  + ' UNION ALL '
						  + ' SELECT 1 AS DATA_TYPE, '  + P_ORGID + ' AS ORG_ID '
					      + '                ,PTYPE.PAY_KIND '
						  + '			      ,'''' PAY_GRP, '''' PAY_GRP_NAME, '''' PAY_GRP_NAME_1 '
					      + '                ,SUM(ISNULL(PM.PAY_AMT)) PAY_AMT, SUM(ISNULL(PM.PAY_CNT)) PAY_CNT '
					      + '     FROM PMART.FACT_CARD_PAY_PRDGRP PM '
						  + '	   JOIN ( '
						  + '	   		SELECT DISTINCT PAY_KIND FROM PDATA.PBMPAYTYPE '
						  + '	   ) PTYPE ON PM.PAY_TYPE = PTYPE.PAY_KIND '
						  + '	    WHERE PM.TIME_ID >= ' + V_SDATE
					      + '		    AND PM.TIME_ID <= ' + V_EDATE
						  + '		    AND PM.ORG_ID = ' + P_ORGID
						  + '		    AND PM.GRP_ID =  ''-1'' '
						  + '	    GROUP BY PTYPE.PAY_KIND'						  
						  + ' 	) T2 ON T1.ORG_ID = T2.ORG_ID '
						  + ' ) WITH DATA UNIQUE PRIMARY INDEX(DATA_TYPE,PAY_KIND, PAY_GRP) ON COMMIT PRESERVE ROWS;';
     EXECUTE IMMEDIATE SQLSTR;   
END SP;