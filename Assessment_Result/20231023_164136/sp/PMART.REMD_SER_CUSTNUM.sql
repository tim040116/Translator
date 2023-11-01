REPLACE PROCEDURE PMART.REMD_SER_CUSTNUM (IN P_AC_TYPE INTEGER)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR  VARCHAR(1000) DEFAULT '';  
  DECLARE P_L_DAY_ID   INTEGER;
  DECLARE P_L_DAY_ID_N DATE;
  DECLARE STORE_CS CURSOR FOR STORE_SQL;    
  CALL PMART.P_DROP_TABLE ('#VT_REMD_SER_CUSTNUM'); 
  SET SQLSTR =' CREATE MULTISET VOLATILE TABLE #VT_REMD_SER_CUSTNUM ( ' +
              '  L_DAY_ID  INTEGER ' +
              ' ,OSTORE_ID INTEGER ' +
              ' ,SCUST_NUM INTEGER ' +
              ' ) PRIMARY  CHARINDEX(OSTORE_ID,L_DAY_ID) ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR;
  IF P_AC_TYPE = 1 THEN
     SET SQLSTR =' SELECT ' +
                 '  (CAST(REMD_DATE AS INTEGER) + 19110000 ) AS L_DAY_ID' +
                 ' ,(CAST(CAST(REMD_DATE AS INTEGER) +110000  AS DATE) ) AS L_DAY_ID_N' +
                 '   FROM PSTAGE.AC_RMMMDDI' +
                 '  GROUP BY REMD_DATE' +
                 '  ; ';
  END IF;
  IF P_AC_TYPE = 0 THEN  
     SET SQLSTR =' SELECT ' +
                 '  REMDDT AS L_DAY_ID' +
                 ' ,CAST( REMDDT - 19000000 AS DATE ) AS L_DAY_ID_N' +
                 '   FROM PSTAGE.DS_REMD0300_R6_I1' +
                 '  GROUP BY REMDDT' +
                 '  ; ';  	
  END IF;
  PREPARE STORE_SQL FROM SQLSTR;
  OPEN STORE_CS;  
  L1:
  WHILE (SQLCODE =0) 
  DO    
     L1_1:
        BEGIN 
         FETCH STORE_CS INTO P_L_DAY_ID,P_L_DAY_ID_N  ;
         IF SQLSTATE <> '00000' THEN LEAVE L1; END IF; 	   
            LOCKING PDATA.TRANS   FOR ACCESS;
            LOCKING PMART.ORG_DIM FOR ACCESS;
	    INSERT INTO #VT_REMD_SER_CUSTNUM (L_DAY_ID,OSTORE_ID,SCUST_NUM) 
            SELECT P_L_DAY_ID AS L_DAY_ID 
                  ,F.OSTORE_ID AS OSTORE_ID 
                  ,COUNT (*) AS SCUST_NUM 
              FROM ( SELECT * 
                       FROM PDATA.TRANS A 
                      WHERE  A.TX_DTTM = P_L_DAY_ID_N
                        AND A.TRAN_TYPE =2 
                        AND A.SALE_TYPE='Z0'
                        AND TO_CHAR(A.STORE_ID)+A.TM_NO+SUBSTRING(A.NO_DEAL,3,10) 
                         NOT IN (SELECT TO_CHAR(B.STORE_ID)+B.TM_NO+TO_CHAR(B.RTN_TX_SEQ) AS CHECK_NO 
                                   FROM PDATA.TRANS B
                                  WHERE B.TX_DTTM =P_L_DAY_ID_N
                                    AND B.SALE_TYPE IN ('R2','R3') ) 
                   ) E
              INNER JOIN 
                   ( 					
                    SELECT * 
                      FROM PMART.ORG_DIM C
                     WHERE C.STORE_ID IN (SELECT MAX(D.STORE_ID)
                   		       FROM PMART.ORG_DIM D
                   		      WHERE ((D.OPNDT <= P_L_DAY_ID  AND D.ENDDT = P_L_DAY_ID )
                   		          OR (D.OPNDT <= P_L_DAY_ID  AND D.ENDDT > P_L_DAY_ID ))
                   		      GROUP BY D.OSTORE_ID)
                   ) F 
                 ON E.OSTORE_ID = F.OSTORE_ID
         GROUP BY F.OSTORE_ID ; 
     END L1_1;
  END WHILE L1;      
  CLOSE STORE_CS;
END SP;