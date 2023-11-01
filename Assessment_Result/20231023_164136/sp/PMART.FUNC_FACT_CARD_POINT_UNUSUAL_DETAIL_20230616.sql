REPLACE PROCEDURE PMART.FUNC_FACT_CARD_POINT_UNUSUAL_DETAIL_20230616
(
   IN P_TIME INTEGER,
   IN P_ORGID VARCHAR(10),
   IN P_CARD_TYPE SMALLINT,                        
   IN P_CARD_MEMBER_ID VARCHAR(40),
   IN P_QUERY_TYPE SMALLINT                      
)
SP:BEGIN
     DECLARE SQLSTR  VARCHAR(10000);
	 DECLARE ORGSELECT VARCHAR(2000);
	 DECLARE ORGWHERE VARCHAR(2000);  
	 DECLARE TIMEWHERE VARCHAR(10);
	 DECLARE QUERYWHERE VARCHAR(100);
	 DECLARE QUERYWHEREBYQTYPE VARCHAR(100);
	 DECLARE QUERYTABLE VARCHAR(50);
	 DECLARE QUERYCOLUMN VARCHAR(20);
	 SET TIMEWHERE = SUBSTRING(CAST( P_TIME AS VARCHAR(8)),1,4)+'-'+SUBSTRING(CAST( P_TIME AS VARCHAR(8)),5,2)+'-'+SUBSTRING(CAST( P_TIME AS VARCHAR(8)),7,2);
     SET QUERYWHEREBYQTYPE = '';
	 IF P_QUERY_TYPE = 1 OR P_QUERY_TYPE = 3 THEN
         SET QUERYWHEREBYQTYPE = '        AND S.STORE_NO = '''+P_ORGID+'''  ';
	 END IF;
     CALL PMART.P_DROP_TABLE ('#VT_FACT_CARD_POINT_UNUSUAL_DETAIL');
	 IF P_CARD_TYPE = 10  THEN 
	     IF P_QUERY_TYPE = 1 OR P_QUERY_TYPE = 2 THEN
 	         SET QUERYWHERE = ' AND PROC_CODE IN (''2S02'',''2S03'') ';
	     ELSEIF P_QUERY_TYPE = 3 OR P_QUERY_TYPE = 4 THEN
    	     SET QUERYWHERE = ' AND PROC_CODE IN (''2S06'',''2S08'')';
	     END IF;	 
         SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_CARD_POINT_UNUSUAL_DETAIL AS( '	
	  					  + ' SELECT T2.OSTORE_ID '
						  + '                ,T2.PDEPT_ID AS PDEPT_ID '
	 					  + '                ,T2.PDEPT_SNAME AS PDEPT_NAME '		
	 					  + '                ,T2.DEPT_ID AS DEPT_ID '
	 					  + '                ,T2.DEPT_SNAME AS DEPT_NAME '						  
	 					  + '                ,T2.BRANCH_ID AS BRANCH_ID '
	 					  + '                ,T2.BRANCH_SNAME AS BRANCH_NAME '
	 					  + '                ,T2.RESPON_ID '
	 					  + '                ,T2.RESPON_NAME AS RESPON_NAME '
	 					  + '                ,T2.STORE_NO AS STORE_ID '
	 					  + '                ,T2.STORE_NAME '						  
						  +'                 ,T.TM_NO  '
                          +'                 ,T.NO_DEAL  '
                          +'                 ,SUBSTRING(TO_CHAR(T.TX_DTTM),12,5) AS TX_TIME  '
                          +'                 ,T.AM_TOT AS TX_AMT  '
                          +'                 ,P.BASE_POINT  '
                          +'                 ,P.EX_POINT  '
                          +'   FROM (       SELECT TX_SEQ,OSTORE_ID,UNIT_PRICE AS BASE_POINT,SUM(DWH_FG*TXN_POINT) AS EX_POINT '
                          +'                        FROM PDATA.TRANS_STORE_POINT '
						  +'                     WHERE  TX_DTTM >= CAST(''' + TIMEWHERE + ''' AS DATE )  AND TX_DTTM <= CAST(''' + TIMEWHERE + ''' AS DATE ) '
						  +'                            AND MEMBER_ID = '''+P_CARD_MEMBER_ID+'''  '
						  +                             QUERYWHERE
				          +'                GROUP BY TX_SEQ,OSTORE_ID,UNIT_PRICE '
						  +'               )P  '
	 					  + '                INNER JOIN (  '
	 					  + '                                              SELECT S.OSTORE_ID,S.RESPON_ID,S.BRANCH_ID,S.DEPT_ID,S.PDEPT_ID,S.STORE_NO,S.STORE_NAME '
	 					  + '                                                               ,P.PDEPT_SNAME,D.DEPT_SNAME,B.BRANCH_SNAME,R.RESPON_NAME '
	 					  + '                                                  FROM PMART.LAST_ORG_STORE S  '
	 					  + '                                                               LEFT JOIN PMART.ORG_DEPT D ON S.DEPT_ID = D.DEPT_ID '
						  + '                                                               LEFT JOIN PMART.ORG_PDEPT P ON S.PDEPT_ID = P.PDEPT_ID '
	 					  + '                                                               LEFT JOIN PMART.ORG_BRANCH B ON S.BRANCH_ID = B.BRANCH_ID '
	 					  + '                                                               LEFT JOIN PMART.ORG_RESPON R ON S.RESPON_ID = R.RESPON_ID  '
	 					  + '                                         ) T2 ON P.OSTORE_ID = T2.OSTORE_ID '						  
                          +'               INNER JOIN PDATA.TRANS T ON P.TX_SEQ = T.TX_SEQ  '
                          +'               INNER JOIN PMART.LAST_ORG_STORE S ON P.OSTORE_ID = S.OSTORE_ID  '
                          +' WHERE  T.TX_DTTM >= CAST(''' + TIMEWHERE + ''' AS DATE ) '
					      +'        AND T.TX_DTTM <= CAST(''' + TIMEWHERE + ''' AS DATE ) '
                          +        QUERYWHEREBYQTYPE
                          + ' ) WITH DATA PRIMARY INDEX(TM_NO,NO_DEAL,TX_TIME) ON COMMIT PRESERVE ROWS;';					  	
	 ELSEIF P_CARD_TYPE = 6  OR P_CARD_TYPE = 7 THEN
	     IF P_QUERY_TYPE = 1 OR P_QUERY_TYPE = 2 THEN
 	         SET QUERYTABLE = 'PDATA.TRANS_CARD_POINT_ADD';
			 SET QUERYCOLUMN = 'QT_PAYPOINT';
	     ELSEIF P_QUERY_TYPE = 3 OR P_QUERY_TYPE = 4 THEN
    	     SET QUERYTABLE = 'PDATA.TRANS_CARD_POINT_EXG';
			 SET QUERYCOLUMN = 'QT_TRANSPOINT';
	     END IF;
         SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_FACT_CARD_POINT_UNUSUAL_DETAIL AS( '	
	  					  + ' SELECT T2.OSTORE_ID '
						  + '                ,T2.PDEPT_ID AS PDEPT_ID '
	 					  + '                ,T2.PDEPT_SNAME AS PDEPT_NAME '		
	 					  + '                ,T2.DEPT_ID AS DEPT_ID '
	 					  + '                ,T2.DEPT_SNAME AS DEPT_NAME '									  
	 					  + '                ,T2.BRANCH_ID AS BRANCH_ID '
	 					  + '                ,T2.BRANCH_SNAME AS BRANCH_NAME '
	 					  + '                ,T2.RESPON_ID '
	 					  + '                ,T2.RESPON_NAME AS RESPON_NAME '
	 					  + '                ,T2.STORE_NO AS STORE_ID '
	 					  + '                ,T2.STORE_NAME '	
						  +'                 ,T.TM_NO  '
                          +'                 ,T.NO_DEAL  '
                          +'                 ,SUBSTRING(TO_CHAR(T.TX_DTTM),12,5) AS TX_TIME  '
                          +'                 ,T.AM_TOT AS TX_AMT  '
                          +'                 ,P.BASE_POINT  '
                          +'                 ,P.EX_POINT  '
                          +'   FROM (       SELECT TX_SEQ,OSTORE_ID,100 AS BASE_POINT,SUM('+TRIM(QUERYCOLUMN) +')  AS EX_POINT '
                          +'                         FROM '+TRIM(QUERYTABLE)
						  +'                     WHERE  TX_DTTM >= CAST(''' + TIMEWHERE + ''' AS DATE )  AND TX_DTTM <= CAST(''' + TIMEWHERE + ''' AS DATE ) '
						  +'                            AND NVL(CARD_NO,CARD_IC_NO) = '''+P_CARD_MEMBER_ID+'''  '
				          +'                GROUP BY TX_SEQ,OSTORE_ID '
						  +'               )P  '
	 					  + '                INNER JOIN (  '
	 					  + '                                              SELECT S.OSTORE_ID,S.RESPON_ID,S.BRANCH_ID,S.DEPT_ID,S.PDEPT_ID,S.STORE_NO,S.STORE_NAME '
	 					  + '                                                               ,P.PDEPT_SNAME,D.DEPT_SNAME,B.BRANCH_SNAME,R.RESPON_NAME '
	 					  + '                                                  FROM PMART.LAST_ORG_STORE S  '
	 					  + '                                                               LEFT JOIN PMART.ORG_DEPT D ON S.DEPT_ID = D.DEPT_ID '
						  + '                                                               LEFT JOIN PMART.ORG_PDEPT P ON S.PDEPT_ID = P.PDEPT_ID '
	 					  + '                                                               LEFT JOIN PMART.ORG_BRANCH B ON S.BRANCH_ID = B.BRANCH_ID '
	 					  + '                                                               LEFT JOIN PMART.ORG_RESPON R ON S.RESPON_ID = R.RESPON_ID  '
	 					  + '                                         ) T2 ON P.OSTORE_ID = T2.OSTORE_ID '									  
                          +'               INNER JOIN PDATA.TRANS T ON P.TX_SEQ = T.TX_SEQ  '
                          +'               INNER JOIN PMART.LAST_ORG_STORE S ON P.OSTORE_ID = S.OSTORE_ID  '
                          +' WHERE  T.TX_DTTM >= CAST(''' + TIMEWHERE + ''' AS DATE ) '
					      +'        AND T.TX_DTTM <= CAST(''' + TIMEWHERE + ''' AS DATE ) '
                          +         QUERYWHEREBYQTYPE
                          + ' ) WITH DATA PRIMARY INDEX(TM_NO,NO_DEAL,TX_TIME) ON COMMIT PRESERVE ROWS;';			 						  
	 END IF;
     EXECUTE IMMEDIATE SQLSTR;   
END SP;