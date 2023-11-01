REPLACE PROCEDURE PMART.REMD_CIG_CUSTNUM 
(
   IN P_AC_TYPE  INTEGER
)
SQL SECURITY INVOKER
SP:BEGIN
DECLARE P_DAY DATE;
DECLARE P_DAY_VAL INTEGER;
CALL PMART.P_DROP_TABLE('#VT_REMD_CIG_CUSTNUM');
CREATE VOLATILE TABLE #VT_REMD_CIG_CUSTNUM  ,
,
,
,
     DEFAULT MERGEBLOCKRATIO
     (
       L_DAY_ID INTEGER,
	   OSTORE_ID INTEGER,      
       CIG_CUST_NUM INTEGER
	)  NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
IF P_AC_TYPE = 1 THEN
		FOR LOOPVAR AS CUR1 CURSOR FOR
		SELECT (CAST(REMD_DATE AS INTEGER) + 19110000 ) AS L_DAY_ID
			FROM PSTAGE.AC_RMMMDDI
		 GROUP BY L_DAY_ID
		DO     
				SET P_DAY = CAST(SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),1,4)+'/'+SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),5,2)+'/'+SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),7,2) AS DATE FORMAT 'YYYY/MM/DD');
				SET P_DAY_VAL = LOOPVAR.L_DAY_ID;
				LOCKING PDATA.TRANS FOR ACCESS
				LOCKING PDATA.TRANS_PRODUCT_DETAIL FOR ACCESS
				LOCKING PMART.ORG_DIM FOR ACCESS
				LOCKING PMART.PRD_DIM FOR ACCESS
				 INSERT INTO #VT_REMD_CIG_CUSTNUM (L_DAY_ID, OSTORE_ID, CIG_CUST_NUM)
				SELECT  LOOPVAR.L_DAY_ID AS L_DAY_ID , E.OSTORE_ID,COUNT (*) AS CIG_CUST_NUM
	                    FROM PDATA.TRANS C
	                    INNER JOIN
	                    (  	 SELECT *
	                            FROM PMART.ORG_DIM
	                            WHERE STORE_ID IN (
	                                  SELECT MAX(STORE_ID)
	                                  FROM PMART.ORG_DIM
	                                  WHERE ((OPNDT<=P_DAY_VAL AND ENDDT=P_DAY_VAL) OR (OPNDT<=P_DAY_VAL AND ENDDT>P_DAY_VAL))
	                                  GROUP BY OSTORE_ID)                                 
	                        ) E ON C.OSTORE_ID=E.OSTORE_ID
	                    , (SELECT TX_SEQ,SUM(AM_ITEM*DWH_FG) AS AMT
	                    FROM PDATA.TRANS_PRODUCT_DETAIL A
	                    INNER JOIN PMART.PRD_DIM  B ON A.CD_FMCODE=B.PRD_ID AND B.KND_NO='58'
	                    WHERE A.TX_DTTM=P_DAY
	                    GROUP BY TX_SEQ) D
	                    WHERE C.TX_SEQ=D.TX_SEQ
		                    AND C.AM_TOT=D.AMT
		                    AND C.TX_DTTM=P_DAY
		                    AND C.SALE_TYPE ='Z0'
		                    AND  TO_CHAR(C.STORE_ID)+C.TM_NO+SUBSTRING(C.NO_DEAL,3,10) 
						NOT IN (SELECT  TO_CHAR(H.STORE_ID)+H.TM_NO+TO_CHAR(H.RTN_TX_SEQ) AS CHECK_NO
		                                FROM PDATA.TRANS H
		                              	WHERE H.TX_DTTM=P_DAY
		                                 	AND H.SALE_TYPE IN ('R2','R3') )
	                    GROUP BY E.OSTORE_ID;
		END FOR;    
ELSE
		FOR LOOPVAR AS CUR1 CURSOR FOR
		SELECT DISTINCT REMDDT AS L_DAY_ID FROM PSTAGE.DS_REMD0300_R6_I1 ORDER BY REMDDT
		DO     
				SET P_DAY = CAST(SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),1,4)+'/'+SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),5,2)+'/'+SUBSTRING(TRIM( LOOPVAR.L_DAY_ID),7,2) AS DATE FORMAT 'YYYY/MM/DD');
				SET P_DAY_VAL = LOOPVAR.L_DAY_ID;
				LOCKING PDATA.TRANS FOR ACCESS
				LOCKING PDATA.TRANS_PRODUCT_DETAIL FOR ACCESS
				LOCKING PMART.ORG_DIM FOR ACCESS
				LOCKING PMART.PRD_DIM FOR ACCESS
				 INSERT INTO #VT_REMD_CIG_CUSTNUM (L_DAY_ID, OSTORE_ID, CIG_CUST_NUM)
				SELECT  LOOPVAR.L_DAY_ID AS L_DAY_ID , E.OSTORE_ID,COUNT (*) AS CIG_CUST_NUM
	                    FROM PDATA.TRANS C
	                    INNER JOIN
	                    (  	 SELECT *
	                            FROM PMART.ORG_DIM
	                            WHERE STORE_ID IN (
	                                  SELECT MAX(STORE_ID)
	                                  FROM PMART.ORG_DIM
	                                  WHERE ((OPNDT<=P_DAY_VAL AND ENDDT=P_DAY_VAL) OR (OPNDT<=P_DAY_VAL AND ENDDT>P_DAY_VAL))
	                                  GROUP BY OSTORE_ID)                                 
	                        ) E ON C.OSTORE_ID=E.OSTORE_ID
	                    , (SELECT TX_SEQ,SUM(AM_ITEM*DWH_FG) AS AMT
	                    FROM PDATA.TRANS_PRODUCT_DETAIL A
	                    INNER JOIN PMART.PRD_DIM  B ON A.CD_FMCODE=B.PRD_ID AND B.KND_NO='58'
	                    WHERE A.TX_DTTM=P_DAY
	                    GROUP BY TX_SEQ) D
	                    WHERE C.TX_SEQ=D.TX_SEQ
		                    AND C.AM_TOT=D.AMT
		                    AND C.TX_DTTM=P_DAY
		                    AND C.SALE_TYPE ='Z0'
		                    AND  TO_CHAR(C.STORE_ID)+C.TM_NO+SUBSTRING(C.NO_DEAL,3,10) 
						NOT IN (SELECT  TO_CHAR(H.STORE_ID)+H.TM_NO+TO_CHAR(H.RTN_TX_SEQ) AS CHECK_NO
		                                FROM PDATA.TRANS H
		                              	WHERE H.TX_DTTM=P_DAY
		                                 	AND H.SALE_TYPE IN ('R2','R3') )
	                    GROUP BY E.OSTORE_ID;
		END FOR;    
END IF;
END SP;