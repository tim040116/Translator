REPLACE PROCEDURE PMART.OLD0401_FUNC_PAYMENT_PRD_GRP
(
   IN P_START_DATE INTEGER,
   IN P_END_DATE INTEGER,
   IN P_ORGID INTEGER,
   IN P_PAYTYPE VARCHAR(3),
   IN P_GRP_ID INTEGER
)
SP:BEGIN
   DECLARE SQLSTR  VARCHAR(5000)  ; 
    CALL PMART.P_DROP_TABLE ('#VT_IGA_2_4_PRDGRP_TX');	
    CALL PMART.P_DROP_TABLE ('#VT_IGA_2_4_PRDGRP');
    SET SQLSTR =  ' CREATE MULTISET VOLATILE TABLE #VT_IGA_2_4_PRDGRP_TX('
                        +' TX_CNT BIGINT, '
						+' TX_AMT BIGINT '
						+') ON COMMIT PRESERVE ROWS;';
		EXECUTE IMMEDIATE SQLSTR;   		
    SET SQLSTR = ' INSERT INTO #VT_IGA_2_4_PRDGRP_TX'
                          +'   SELECT CAST(SUM(DECODE(TX.SALE_TYPE,''Z0'',1,-1)) AS BIGINT) TX_CNT,CAST(SUM(AM_TOT*DECODE(TX.SALE_TYPE,''Z0'',1,-1)) AS BIGINT) TX_AMT    '        
                          +'      FROM PDATA.TRANS TX    '  
						  +'                   INNER  JOIN  PMART.LAST_ORG_STORE AS ORG ON TX.OSTORE_ID=ORG.OSTORE_ID '
                          +'    WHERE TX.TX_DTTM >=  CAST('''+P_START_DATE+'''  AS DATE FORMAT ''YYYYMMDD'' )    '   
                          +'           AND TX.TX_DTTM <=  CAST('''+P_END_DATE+'''  AS DATE FORMAT ''YYYYMMDD'' )    '   
                          +'           AND TX.SALE_TYPE IN (''Z0'',''R2'',''R3'')     '   
                          +'           AND EXISTS (SELECT 1    '   
					      +'                                            FROM PDATA.TRANS_PAYMENT PAY    '  
						  +'                                                         INNER JOIN  PDATA.PBMPAYTYPE AS PAYM ON PAY.PAY_TYPE = PAYM.PAY_TYPE AND PAY.PAY_TYPE_ID = PAYM.PAY_TYPE_ID '
					      +'                                       WHERE TX.TX_SEQ = PAY.TX_SEQ    '  
					      +'                                               AND PAY.TX_DTTM >=  CAST('''+P_START_DATE+'''  AS DATE FORMAT ''YYYYMMDD'' )  '
					      +'                                               AND PAY.TX_DTTM <=  CAST('''+P_END_DATE+'''  AS DATE FORMAT ''YYYYMMDD'' )  '
			              +'                                     )		';
    IF (P_ORGID <> -1 AND SUBSTRING(P_ORGID,1,1)=3) THEN 
	    SET SQLSTR = SQLSTR + ' AND ORG.PDEPT_ID = '+P_ORGID;
	ELSEIF (P_ORGID <> -1 AND SUBSTRING(P_ORGID,1,1)=2) THEN 
	    SET SQLSTR = SQLSTR + ' AND ORG.DEPT_ID = '+P_ORGID;
	ELSEIF (P_ORGID <> -1 AND SUBSTRING(P_ORGID,1,1)=1) THEN 
		SET SQLSTR = SQLSTR + ' AND ORG.BRANCH_ID = '+P_ORGID;
	ELSEIF (P_ORGID <> -1 AND P_ORGID>9999999) THEN 
		SET SQLSTR = SQLSTR + ' AND ORG.RESPON_ID = '+P_ORGID;
	ELSEIF (P_ORGID <> -1 AND P_ORGID<999999) THEN 
		SET SQLSTR = SQLSTR + ' AND ORG.OSTORE_ID = '+ CAST(P_ORGID AS VARCHAR(6));
	END IF;
    EXECUTE IMMEDIATE SQLSTR; 	
    SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_IGA_2_4_PRDGRP AS(  ';
    IF (P_GRP_ID = -1) THEN 					  
        SET SQLSTR = SQLSTR +'       SELECT  GRP.KND_NO AS GRP_NO'
						                     +'                        ,GRP.KND_NAME AS GRP_NAME ';
    ELSE 
        SET SQLSTR = SQLSTR +'       SELECT  GRP.GRP_NO'
						                     +'                        ,GRP.GRP_NAME';
    END IF; 
	SET SQLSTR = SQLSTR  +'                        ,DAT.GRP_ID'
										  +'    			         ,CAST(DAT.PAY_TYPE_AMT AS BIGINT) PAY_TYPE_AMT '
										  +' 		                 ,CAST(DAT.ALL_PAY_TYPE_AMT AS BIGINT) ALL_PAY_TYPE_AMT '
				                          +'                        ,(CAST ( (1.0000 * DAT.PAY_TYPE_AMT/DAT_TX.TX_AMT) AS DECIMAL(10,4) ) * 100) PAY_TYPE_RATIO '
				                          +'                        ,(CAST ( (1.0000 * DAT.ALL_PAY_TYPE_AMT/DAT_TX.TX_AMT) AS DECIMAL(10,4) ) * 100) ALL_PAY_TYPE_RATIO '
					                      +'                        ,(CAST ( (1.0000 * DAT.PAY_TYPE_AMT/DAT.ALL_PAY_TYPE_AMT) AS DECIMAL(10,4) ) * 100) AMT_IN_ALL_RATIO '
				                          +'       FROM '
				                          +'       (	'
				                          +'       SELECT PAY_TYPE.GRP_ID '
				                          +'                        ,CAST(SUM(PAY_TYPE.PAY_AMT) AS BIGINT) PAY_TYPE_AMT '
				                          +'                        ,CAST(SUM(ALL_PAY_TYPE.PAY_AMT) AS BIGINT) ALL_PAY_TYPE_AMT '
				                          +'           FROM ( '
				                          +'                                 SELECT GRP_ID  '
				                          +'                                                   ,SUM(PAY_AMT)PAY_AMT  '
				                          +'                                     FROM PMART.FACT_CARD_PAY_PRDGRP   '
				                          +'                                 WHERE TIME_ID >=  '+P_START_DATE
				                          +'                                         AND TIME_ID <= '+P_END_DATE
				                          +'                                        AND ORG_ID = '+P_ORGID
				                          +'                                        AND PAY_TYPE = '''+P_PAYTYPE+'''  ';
	 IF (P_GRP_ID = -1) THEN 					  
                          SET SQLSTR = SQLSTR + '    AND GRP_ID >= 100000001 AND GRP_ID <= 100000099 ';
     ELSEIF (P_GRP_ID >= 100000001 AND P_GRP_ID <= 100000099) THEN 					  
	                      SET  SQLSTR = SQLSTR +'    AND GRP_ID >= (1000000+'+P_GRP_ID+'-100000000)*10 '
						                                        +'    AND GRP_ID <= (1000000+'+P_GRP_ID+'-100000000)*10+9 ';     
	 ELSE 
	                      SET SQLSTR = SQLSTR +'    AND GRP_ID = '+P_GRP_ID;
     END IF;     
     SET SQLSTR = SQLSTR +'                           GROUP BY  GRP_ID  ' 
                                          +'                         )PAY_TYPE '
                                          +'                        INNER JOIN '    
						                  +'                        ( '
                                          +'                                  SELECT GRP_ID ' 
                                          +'                                                   ,SUM(PAY_AMT)PAY_AMT  '
                                          +'                                      FROM PMART.FACT_CARD_PAY_PRDGRP   '
                                          +'                                 WHERE TIME_ID >=  '+P_START_DATE
                                          +'                                         AND TIME_ID <= '+P_END_DATE
                                          +'                                        AND ORG_ID = '+P_ORGID;
	 IF (P_GRP_ID = -1) THEN 					  
                          SET SQLSTR = SQLSTR + '    AND GRP_ID >= 100000001 AND GRP_ID <= 100000099 ';
     ELSEIF (P_GRP_ID >= 100000001 AND P_GRP_ID <= 100000099) THEN 					  
	                      SET  SQLSTR = SQLSTR +'    AND GRP_ID >= (1000000+'+P_GRP_ID+'-100000000)*10 '
						                                        +'    AND GRP_ID <= (1000000+'+P_GRP_ID+'-100000000)*10+9 ';
	 ELSE
	                      SET SQLSTR = SQLSTR +'    AND GRP_ID = '+P_GRP_ID;
     END IF;                     
	 SET SQLSTR = SQLSTR+'                             GROUP BY  GRP_ID   '
                                         +'                        ) ALL_PAY_TYPE   '
                                         +'                        ON PAY_TYPE.GRP_ID =ALL_PAY_TYPE.GRP_ID   '
                                         +'     GROUP BY PAY_TYPE.GRP_ID	'
                                         +'     )DAT     '	
                                         +'     LEFT JOIN  ( '
                                         +'                             SELECT CAST(NVL(SUM(TX_CNT),1) AS BIGINT) TX_CNT,CAST(NVL(SUM(TX_AMT),1) AS BIGINT) TX_AMT '
                                         +'     			               	FROM (SELECT CAST(TX_CNT AS BIGINT)  TX_CNT, CAST(TX_AMT AS BIGINT)  TX_AMT '
                                         +'     				                              FROM #VT_IGA_2_4_PRDGRP_TX '
                                         +'     			                             ) B  '
										 +'                           )DAT_TX ON 1=1 ';
	IF (P_GRP_ID = -1) THEN 					  
        SET SQLSTR = SQLSTR + '   INNER JOIN PMART.PRD_KND GRP ON DAT.GRP_ID =GRP.KND_ID  ';
    ELSE 
	    SET SQLSTR = SQLSTR +'    INNER JOIN PMART.PRD_GRP GRP ON DAT.GRP_ID =GRP.GRP_ID  ';
    END IF;                     
    SET SQLSTR = SQLSTR +'UNION ALL ' 
	                                 +'   SELECT * FROM ( '
                                     +'                                     SELECT ''0''  AS GRP_NO '
									 +'                                                      ,'''' AS GRP_NAME '
									 +'                                                      ,0 GRP_ID '
									 +'                                                      ,CAST(TX_CNT AS BIGINT)  AS PAY_TYPE_AMT '
									 +'                                                      ,CAST(TX_AMT AS BIGINT) AS   ALL_PAY_TYPE_AMT'        
									 +'                                                      ,0 PAY_TYPE_RATIO '
									 +'                                                      ,0 ALL_PAY_TYPE_RATIO '
									 +'                                                      ,0 AMT_IN_ALL_RATIO '
									 +'                                          FROM #VT_IGA_2_4_PRDGRP_TX  '
									 +'   ) DAT_TOT ';
     SET SQLSTR = SQLSTR  + ' ) WITH DATA PRIMARY INDEX(GRP_ID) ON COMMIT PRESERVE ROWS;';	  
    EXECUTE IMMEDIATE SQLSTR;   
	SET SQLSTR = 'DELETE FROM #VT_IGA_2_4_PRDGRP WHERE (SELECT MAX(GRP_ID) FROM #VT_IGA_2_4_PRDGRP)=0;	';
    EXECUTE IMMEDIATE SQLSTR; 
END SP;