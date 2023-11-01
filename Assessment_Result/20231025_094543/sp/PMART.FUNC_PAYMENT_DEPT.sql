REPLACE PROCEDURE PMART.FUNC_PAYMENT_DEPT
(
   IN P_START_DATE INTEGER,
   IN P_END_DATE INTEGER,
   IN P_PAYTYPE VARCHAR(50)
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(10000) ;    
   DECLARE V_TX_AMT BIGINT;
   DECLARE P_PAYTYPE_FMT VARCHAR(50);
    CALL PMART.P_DROP_TABLE ('#VT_IGA_2_3_DEPT');
	SET P_PAYTYPE_FMT = Replace(TRIM(P_PAYTYPE),',',''',''');
    SET SQLSTR =  ' CREATE MULTISET VOLATILE TABLE #VT_IGA_2_3_DEPT( '
                        +' DAT_SEQ SMALLINT, '
						+' DAT_NAME VARCHAR(20) , '
                        +' DEPT01 BIGINT, '
                        +' DEPT02 BIGINT, '
                        +' DEPT03 BIGINT, '
                        +' DEPT04 BIGINT, '
						+' DEPT05 BIGINT, '
						+' DEPT06 BIGINT '
						+')UNIQUE PRIMARY INDEX(DAT_SEQ) ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;  
	SET SQLSTR = ' INSERT INTO  #VT_IGA_2_3_DEPT  '						  
						  +'SELECT DAT_SEQ,DAT_NAME,NVL(DEPT01,0)DEPT01,NVL(DEPT02,0)DEPT02,NVL(DEPT03,0)DEPT03,NVL(DEPT04,0)DEPT04,NVL(DEPT05,0)DEPT05,NVL(DEPT06,0)DEPT06'
						  +'   FROM  ( '
		                  +'                  SELECT 1 AS DAT_SEQ  '
						  +'                                   ,CAST('''+'總金額'+'''  AS VARCHAR(20)) AS DAT_NAME '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100500 THEN TX_AMT ELSE 0 END) DEPT01 '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100600 THEN TX_AMT ELSE 0 END) DEPT02 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2110200 THEN TX_AMT ELSE 0 END) DEPT03 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120500 THEN TX_AMT ELSE 0 END) DEPT04 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120600 THEN TX_AMT ELSE 0 END) DEPT05 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2130200 THEN TX_AMT ELSE 0 END) DEPT06 '						  
		                  +'                     FROM ( '
					      +'                                    SELECT  COALESCE(ORG.DEPT_ID,-1) AS ORG_ID '
                          +'                                                      ,SUM(TX.AM_TOT  * DECODE(TX.SALE_TYPE,''Z0'',1,-1))TX_AMT    '
                          +'                                        FROM PDATA.TRANS TX    '
                          +'                                                     INNER  JOIN  PMART.LAST_ORG_STORE AS ORG ON TX.OSTORE_ID=ORG.OSTORE_ID '
                          +'                                        WHERE TX.TX_DTTM >= CAST(CAST('''+P_START_DATE+''' AS DATE FORMAT ''YYYYMMDD'')  AS TIMESTAMP(0))+ ((CAST(''00:00:00'' AS TIME(0)) - TIME ''00:00:00'') HOUR TO SECOND(0))  '
                          +'                                               AND TX.TX_DTTM <= CAST(CAST('''+P_END_DATE+'''AS DATE FORMAT ''YYYYMMDD'')  AS TIMESTAMP(0))+ ((CAST(''23:59:59'' AS TIME(0)) - TIME ''00:00:00'') HOUR TO SECOND(0))  '						  						  
                          +'                                           AND TX.SALE_TYPE IN (''Z0'',''R2'',''R3'')  '   
                          +'                                           AND EXISTS (SELECT 1    '
                          +'                                                                          FROM PDATA.TRANS_PAYMENT PAY    '
                          +'                                                                                       INNER JOIN   PDATA.PBMPAYTYPE AS PAYTYPE ON PAY.PAY_TYPE = PAYTYPE.PAY_TYPE AND PAY.PAY_TYPE_ID = PAYTYPE.PAY_TYPE_ID '
                          +'                                                                      WHERE TX.TX_SEQ = PAY.TX_SEQ    '
                          +'                                                                             AND PAY.TX_DTTM >= CAST(CAST('''+P_START_DATE+''' AS DATE FORMAT ''YYYYMMDD'')  AS TIMESTAMP(0))+ ((CAST(''00:00:00'' AS TIME(0)) - TIME ''00:00:00'') HOUR TO SECOND(0))  '
                          +'                                                                             AND PAY.TX_DTTM <= CAST(CAST('''+P_END_DATE+'''AS DATE FORMAT ''YYYYMMDD'')  AS TIMESTAMP(0))+ ((CAST(''23:59:59'' AS TIME(0)) - TIME ''00:00:00'') HOUR TO SECOND(0))  '						  
                          +'                                                                             AND PAYTYPE.PAY_KIND IN  ('''+P_PAYTYPE_FMT+''')  '
						  +'                                                                             AND PAY.SALE_TYPE IN (''Z0'',''R2'',''R3'')  '
                          +'                                                                    )  '
                          +'                                   GROUP BY ROLLUP(ORG.DEPT_ID)  '								 
						  +'                                  ) DAT '
						  +'   UNION ALL '
		                  +'                  SELECT  2 AS DAT_SEQ '
						  +'                                   ,CAST('''+'支付金額合計'+'''  AS VARCHAR(20))  AS DAT_NAME '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100500 THEN PAY_AMT ELSE 0 END) DEPT01 '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100600 THEN PAY_AMT ELSE 0 END) DEPT02 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2110200 THEN PAY_AMT ELSE 0 END) DEPT03 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120500 THEN PAY_AMT ELSE 0 END) DEPT04 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120600 THEN PAY_AMT ELSE 0 END) DEPT05 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2130200 THEN PAY_AMT ELSE 0 END) DEPT06 '							  
		                  +'                     FROM ( '
  						  +'                                         SELECT    PAY.ORG_ID '
  						  +'                                                             ,PAY.PAY_TYPE '
  						  +'                                                             ,PAYTYPE.PAY_GRP_NAME '
  						  +'                                                             ,SUM(PAY.PAY_AMT)PAY_AMT '
  						  +'                                             FROM PMART.FACT_CARD_PAY_PRDGRP PAY '
  						  +'                                                          INNER JOIN  (SELECT DISTINCT PAY_KIND,PAY_GRP,PAY_GRP_NAME  FROM PDATA.PBMPAYTYPE) AS PAYTYPE ON PAY.PAY_TYPE = PAYTYPE.PAY_GRP '
  						  +'                                         WHERE 1=1 '
  						  +'                                        AND PAY.TIME_ID >= '+P_START_DATE
  						  +'                                        AND PAY.TIME_ID <= '+P_END_DATE 
						  +'                                        AND PAYTYPE.PAY_KIND IN  ('''+P_PAYTYPE_FMT+''')  '
                          +'                                        AND PAY.ORG_ID >=2100001 '
                          +'                                        AND PAY.ORG_ID <=2999999 '						  
						  +'                                        AND PAY.GRP_ID <=''-1'' '						  
  						  +'                                        GROUP BY PAY.ORG_ID,PAY.PAY_TYPE,PAYTYPE.PAY_GRP_NAME  '						  
						  +'                                  ) DAT_PAY '		
						  +'   UNION ALL '
		                  +'                  SELECT  (ROW_NUMBER() OVER(ORDER BY PAY_TYPE) + 2) AS DAT_SEQ '
						  +'                                   ,PAY_GRP_NAME  AS DAT_NAME '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100500 THEN PAY_AMT ELSE 0 END) DEPT01 '
						  +'                                   ,SUM(CASE WHEN ORG_ID=2100600 THEN PAY_AMT ELSE 0 END) DEPT02 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2110200 THEN PAY_AMT ELSE 0 END) DEPT03 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120500 THEN PAY_AMT ELSE 0 END) DEPT04 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2120600 THEN PAY_AMT ELSE 0 END) DEPT05 '
		                  +'                                   ,SUM(CASE WHEN ORG_ID=2130200 THEN PAY_AMT ELSE 0 END) DEPT06 '								  
		                  +'                     FROM ( '
  						  +'                                         SELECT     PAY.ORG_ID '
  						  +'                                                             ,PAY.PAY_TYPE '
  						  +'                                                             ,PAYTYPE.PAY_GRP_NAME '
  						  +'                                                             ,SUM(PAY.PAY_AMT)PAY_AMT '
  						  +'                                             FROM PMART.FACT_CARD_PAY_PRDGRP PAY '
  						  +'                                                          INNER JOIN  (SELECT DISTINCT PAY_KIND,PAY_GRP,PAY_GRP_NAME  FROM PDATA.PBMPAYTYPE) AS PAYTYPE ON PAY.PAY_TYPE = PAYTYPE.PAY_GRP '
  						  +'                                         WHERE PAY.GRP_ID <=''-1'' '
  						  +'                                        AND PAY.TIME_ID >= '+P_START_DATE
  						  +'                                        AND PAY.TIME_ID <= '+P_END_DATE 
						  +'                                        AND PAYTYPE.PAY_KIND IN  ('''+P_PAYTYPE_FMT+''')  '
  						  +'                                        GROUP BY PAY.ORG_ID,PAY.PAY_TYPE,PAYTYPE.PAY_GRP_NAME '						  
						  +'                                  ) DAT_PAY '		
						  +'              GROUP BY DAT_PAY.PAY_TYPE,DAT_PAY.PAY_GRP_NAME '
						  +'   ) SRC ; ';
    EXECUTE IMMEDIATE  SQLSTR;   
	SET SQLSTR =  'DELETE FROM #VT_IGA_2_3_DEPT WHERE (SELECT MAX(DAT_SEQ) FROM #VT_IGA_2_3_DEPT)=2;';
	EXECUTE IMMEDIATE  SQLSTR;   
END SP;