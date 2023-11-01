REPLACE PROCEDURE PDATA.STORMKTI_CHECK
(
   IN P_ACT_ID  INTEGER
)
SP:BEGIN
DECLARE SQLSTR  VARCHAR(2000);    
DECLARE ERROR_CNT INTEGER;
DECLARE P_ACT_SDATE_YYY VARCHAR(7);
DECLARE P_ACT_EDATE_YYY VARCHAR(7);
DECLARE P_CHG_SDATE_YYYY VARCHAR(8);
DECLARE P_CHG_EDATE_YYYY VARCHAR(8);
DECLARE P_CHG_SDATE_YYY VARCHAR(7);
DECLARE P_CHG_EDATE_YYY VARCHAR(7);
DECLARE P_R_FM_CODE VARCHAR(7);
DECLARE P_V_FM_CODE VARCHAR(7);
DECLARE P_LAST_NUM INTEGER;
DECLARE P_NOW_NUM INTEGER;
DECLARE I INTEGER;
DECLARE J INTEGER;
DECLARE P_COL6 INTEGER;
DELETE FROM PDATA.STORMKTI  WHERE UPDATE_TIME < DATE - 2;
SELECT CAST((ACT_SDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)),
			   CAST((ACT_EDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)),  			   
			   CAST((CHG_SDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)),
			   CAST((CHG_EDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)),
			   CAST((CHG_SDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)),
			   CAST((CHG_EDATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)), 
			   NVL(R_FM_CODE,'') R_FM_CODE,
			   NVL(V_FM_CODE,'') V_FM_CODE
			   INTO P_ACT_SDATE_YYY, P_ACT_EDATE_YYY, 
			            P_CHG_SDATE_YYYY, P_CHG_EDATE_YYYY, P_CHG_SDATE_YYY, P_CHG_EDATE_YYY,
			            P_R_FM_CODE, P_V_FM_CODE
   FROM PDATA.STORMKT
WHERE ACT_ID = P_ACT_ID;
SET P_ACT_SDATE_YYY = TRIM(TO_NUMBER(SUBSTRING(P_ACT_SDATE_YYY,1,4)) -1911   +  SUBSTRING(P_ACT_SDATE_YYY,5,4));
SET P_ACT_EDATE_YYY = TRIM(TO_NUMBER(SUBSTRING(P_ACT_EDATE_YYY,1,4)) -1911   +  SUBSTRING(P_ACT_EDATE_YYY,5,4));
SET P_CHG_SDATE_YYY = TRIM(TO_NUMBER(SUBSTRING(P_CHG_SDATE_YYY,1,4)) -1911   +  SUBSTRING(P_CHG_SDATE_YYY,5,4));
SET P_CHG_EDATE_YYY = TRIM(TO_NUMBER(SUBSTRING(P_CHG_EDATE_YYY,1,4)) -1911   +  SUBSTRING(P_CHG_EDATE_YYY,5,4));
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '貼紙類型錯誤1.實體 2.虛擬'
WHERE COL2  NOT IN ('1','2') AND INPUT_TYPE = '1' 
     AND ACT_ID = P_ACT_ID;
IF (P_R_FM_CODE='') THEN
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '該檔期沒有實體貼紙商品代號'
WHERE COL2  = '1' AND INPUT_TYPE = '1' 
     AND ACT_ID = P_ACT_ID;
END IF;
IF (P_V_FM_CODE='') THEN
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '該檔期沒有虛擬貼紙商品代號'
WHERE COL2  = '2' AND INPUT_TYPE = '1' 
     AND ACT_ID = P_ACT_ID;
END IF;
CALL PDATA.P_DROP_TABLE('#CHECKERROR_TEMP');
CREATE VOLATILE TABLE #CHECKERROR_TEMP AS (
SELECT  DISTINCT A.COL4, '商品代號不存在' AS COL_ERROR
    FROM PDATA.STORMKTI A
	LEFT JOIN PMART.VW_PBHCMDT B ON  A.COL4 = B.FM_CODE
	WHERE A.INPUT_TYPE = '1' AND B.FM_CODE IS NULL	
	     AND A.ACT_ID = P_ACT_ID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
UPDATE A
FROM  PDATA.STORMKTI  A, #CHECKERROR_TEMP B
SET COL_ERROR = B.COL_ERROR
WHERE A.COL4 = B.COL4 AND A.INPUT_TYPE = '1'
     AND A.ACT_ID = P_ACT_ID;
UPDATE A
FROM  PDATA.STORMKTI  A,
(	SELECT COL2, COL3, COL4
        FROM PDATA.STORMKTI
     WHERE ACT_ID = P_ACT_ID AND INPUT_TYPE = '1'
	 GROUP BY COL2, COL3, COL4 HAVING COUNT(*) > 1) B
SET COL_ERROR = '同一促銷類別下,不能重複商品代號'
WHERE A.COL2 = B.COL2 AND A.COL3 = B.COL3
     AND A.COL4 = B.COL4 AND A.INPUT_TYPE = '1'
	 AND A.ACT_ID = P_ACT_ID;
SELECT COUNT(COL6) INTO P_COL6
  FROM PDATA.STORMKTI
WHERE INPUT_TYPE = '1' AND ACT_ID = P_ACT_ID  AND COL6 <> '';
CALL PDATA.P_DROP_TABLE('#PROMO_CHECK1');
CALL PDATA.P_DROP_TABLE('#PROMO_CHECK2');
CALL PDATA.P_DROP_TABLE('#PROMO_STORMKTI');
IF (P_COL6>0) THEN
CREATE VOLATILE TABLE #PROMO_CHECK1 AS (
SELECT TRIM(TO_CHAR(POMOID,'FM0000')) POMOID,
               CASE WHEN COUNT(POMOID) < 1 THEN 0 WHEN COUNT(POMOID) > 1 THEN 2 ELSE 1 END AS POMOID_CNT
   FROM PDATA.PROMO
WHERE POMOID IN (SELECT DISTINCT TO_NUMBER(COL6)
										  FROM PDATA.STORMKTI
                                       WHERE INPUT_TYPE = '1' AND ACT_ID = P_ACT_ID  AND COL6 <> '')
    AND BDAY_ID BETWEEN P_CHG_SDATE_YYYY AND P_CHG_EDATE_YYYY
    AND EDAY_ID BETWEEN P_CHG_SDATE_YYYY AND P_CHG_EDATE_YYYY
GROUP BY POMOID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
CREATE VOLATILE TABLE #PROMO_CHECK2 AS (
SELECT POMOID,
               CASE WHEN COUNT(POMOID) < 1 THEN 0  ELSE 1 END AS POMOID_CNT
   FROM PDATA.MTHPROMO
WHERE POMOID IN (SELECT DISTINCT COL6
										  FROM PDATA.STORMKTI
                                       WHERE INPUT_TYPE = '1' AND ACT_ID = P_ACT_ID  AND COL6 <> '')
    AND CAST((BTIME_DATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)) BETWEEN P_CHG_SDATE_YYYY AND P_CHG_EDATE_YYYY
    AND CAST((ETIME_DATE (FORMAT 'YYYYMMDD' )) AS CHAR(8)) 	BETWEEN P_CHG_SDATE_YYYY AND P_CHG_EDATE_YYYY
GROUP BY POMOID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
CREATE VOLATILE TABLE #PROMO_STORMKTI AS (
SELECT DISTINCT COL6 AS POMOID, NULL AS POMOID_CNT
	  FROM PDATA.STORMKTI
   WHERE INPUT_TYPE = '1'  AND ACT_ID = P_ACT_ID AND COL6 <> ''
 ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
UPDATE A
FROM #PROMO_STORMKTI  A, #PROMO_CHECK1 B
SET POMOID_CNT = B.POMOID_CNT
WHERE A.POMOID = B.POMOID;
UPDATE A
FROM #PROMO_STORMKTI  A, #PROMO_CHECK2 B
SET POMOID_CNT = B.POMOID_CNT
WHERE A.POMOID = B.POMOID AND A.POMOID_CNT IS NULL;
UPDATE A
FROM  PDATA.STORMKTI  A, #PROMO_STORMKTI B
SET COL_ERROR = CASE WHEN B.POMOID_CNT IS NULL THEN P_CHG_SDATE_YYY+'~'+P_CHG_EDATE_YYY+'查無該組促代號' WHEN B.POMOID_CNT <> 1 THEN '該組促代號，同時間跨不同檔期' ELSE '' END
WHERE A.COL6 = B.POMOID  AND A.INPUT_TYPE = '1'  AND A.ACT_ID = P_ACT_ID AND A.COL6 <> '';
END IF;
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '貼紙類型錯誤1.實體 2.虛擬'
WHERE COL2  NOT IN ('1','2') AND INPUT_TYPE = '2' 
     AND ACT_ID = P_ACT_ID;
IF (P_R_FM_CODE='') THEN
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '該檔期沒有實體貼紙商品代號'
WHERE COL2  = '1' AND INPUT_TYPE = '2' 
     AND ACT_ID = P_ACT_ID;
END IF;
IF (P_V_FM_CODE='') THEN
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '該檔期沒有虛擬貼紙商品代號'
WHERE COL2  = '2' AND INPUT_TYPE = '2' 
     AND ACT_ID = P_ACT_ID;
END IF;
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '機制非數值'
WHERE (TO_NUMBER(COL4) IS  NULL OR TO_NUMBER(COL5) IS  NULL)
AND ACT_ID = P_ACT_ID AND INPUT_TYPE = '2';	
UPDATE PDATA.STORMKTI
SET COL_ERROR = '機制不得大於9999'
WHERE (TO_NUMBER(COL4) IS  NOT NULL AND TO_NUMBER(COL5) IS NOT NULL)
AND (COL4 > 9999 OR COL5 > 9999)
AND ACT_ID = P_ACT_ID
AND INPUT_TYPE = '2';
UPDATE PDATA.STORMKTI
SET COL6 = P_ACT_SDATE_YYY
WHERE NVL(COL6,'') = ''
AND ACT_ID = P_ACT_ID
AND INPUT_TYPE = '2';
UPDATE PDATA.STORMKTI 
SET COL7 = P_ACT_EDATE_YYY
WHERE NVL(COL7,'') = ''
AND ACT_ID = P_ACT_ID
AND INPUT_TYPE = '2';
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '加碼期間(起) 需在檔期之間'
WHERE (COL6 < P_ACT_SDATE_YYY OR COL6 > P_ACT_EDATE_YYY)
AND ACT_ID = P_ACT_ID
AND INPUT_TYPE = '2';
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '加碼期間(迄) 需在檔期之間'
WHERE (COL7 < P_ACT_SDATE_YYY OR COL7 > P_ACT_EDATE_YYY)
AND ACT_ID = P_ACT_ID
AND INPUT_TYPE = '2';
UPDATE PDATA.STORMKTI
SET COL_ERROR = '商品類型錯誤1.商品代號 2.品群番 3.代收項目'
WHERE COL8  NOT IN ('1','2','3') AND INPUT_TYPE = '2'
     AND ACT_ID = P_ACT_ID;
CALL PDATA.P_DROP_TABLE('#CHECKERROR_TEMP1');
CREATE VOLATILE TABLE #CHECKERROR_TEMP1 AS (
SELECT  DISTINCT A.COL9, '商品代號不存在' AS COL_ERROR
    FROM PDATA.STORMKTI A
	LEFT JOIN PMART.VW_PBHCMDT B ON  A.COL9 = B.FM_CODE
	WHERE A.INPUT_TYPE = '2' AND A.COL8 = '1'
	     AND B.FM_CODE IS NULL	
	     AND A.ACT_ID = P_ACT_ID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
UPDATE A
FROM  PDATA.STORMKTI  A, #CHECKERROR_TEMP1 B
SET COL_ERROR = B.COL_ERROR
WHERE A.COL9 = B.COL9
     AND A.INPUT_TYPE = '2' AND A.COL8 = '1'
     AND A.ACT_ID = P_ACT_ID;
CALL PDATA.P_DROP_TABLE('#CHECKERROR_TEMP2');
CREATE VOLATILE TABLE #CHECKERROR_TEMP2 AS (
SELECT  DISTINCT A.COL9, '品群番不存在' AS COL_ERROR
    FROM PDATA.STORMKTI A
	LEFT JOIN PMART.PRD_GRP B ON  A.COL9 = B.GRP_NO
	WHERE A.INPUT_TYPE = '2' AND A.COL8 = '2'
	     AND B.GRP_NO IS NULL
	     AND A.ACT_ID = P_ACT_ID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
UPDATE A
FROM  PDATA.STORMKTI  A, #CHECKERROR_TEMP2 B
SET COL_ERROR = B.COL_ERROR
WHERE A.COL9 = B.COL9
     AND A.INPUT_TYPE = '2' AND A.COL8 = '2'
     AND A.ACT_ID = P_ACT_ID;
UPDATE PDATA.STORMKTI SET COL9 = UPPER(COL9)
WHERE INPUT_TYPE = '2' AND COL8 = '3' AND ACT_ID = P_ACT_ID;
CALL PDATA.P_DROP_TABLE('#CHECKERROR_TEMP3');
CREATE VOLATILE TABLE #CHECKERROR_TEMP3 AS (
SELECT  DISTINCT A.COL9, '代收項目不存在' AS COL_ERROR
    FROM PDATA.STORMKTI A
	LEFT JOIN PDATA.AGMAGNT B ON  A.COL9 = B.SKIND
	WHERE A.INPUT_TYPE = '2' AND A.COL8 = '3'
	     AND B.SKIND IS NULL
	     AND A.ACT_ID = P_ACT_ID
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
UPDATE A
FROM  PDATA.STORMKTI  A, #CHECKERROR_TEMP3 B
SET COL_ERROR = B.COL_ERROR
WHERE A.COL9 = B.COL9
     AND A.INPUT_TYPE = '2' AND A.COL8 = '3'
     AND A.ACT_ID = P_ACT_ID;
UPDATE A
FROM  PDATA.STORMKTI  A,
(	SELECT COL2, COL3, COL4, COL5, COL6, COL7, COL9
        FROM PDATA.STORMKTI
     WHERE ACT_ID = P_ACT_ID AND INPUT_TYPE = '2' AND COL8 = '1'
	 GROUP BY COL2, COL3, COL4, COL5, COL6, COL7, COL9 HAVING COUNT(*) > 1) B
SET COL_ERROR = '同一加碼贈點下,不能重複商品代號'
WHERE A.COL2 = B.COL2 AND A.COL3 = B.COL3
	 AND A.COL4 = B.COL4 AND A.COL5 = B.COL5
	 AND A.COL6 = B.COL6 AND A.COL7 = B.COL7
     AND A.COL9 = B.COL9 AND A.INPUT_TYPE = '2'  AND A.COL8 = '1'
	 AND A.ACT_ID = P_ACT_ID;
UPDATE A
FROM  PDATA.STORMKTI  A,
(	SELECT COL2, COL3, COL4, COL5, COL6, COL7, COL9
        FROM PDATA.STORMKTI
     WHERE ACT_ID = P_ACT_ID AND INPUT_TYPE = '2' AND COL8 = '2'
	 GROUP BY COL2, COL3, COL4, COL5, COL6, COL7, COL9 HAVING COUNT(*) > 1) B
SET COL_ERROR = '同一加碼贈點下,不能重複品群番'
WHERE A.COL2 = B.COL2 AND A.COL3 = B.COL3
     AND A.COL4 = B.COL4 AND A.COL5 = B.COL5
	 AND A.COL6 = B.COL6 AND A.COL7 = B.COL7
     AND A.COL9 = B.COL9 AND A.INPUT_TYPE = '2'  AND A.COL8 = '2'
	 AND A.ACT_ID = P_ACT_ID;
UPDATE A
FROM  PDATA.STORMKTI  A,
(	SELECT COL2, COL3, COL4, COL5, COL6, COL7, COL9
        FROM PDATA.STORMKTI
     WHERE ACT_ID = P_ACT_ID AND INPUT_TYPE = '2' AND COL8 = '3'
	 GROUP BY COL2, COL3, COL4, COL5, COL6, COL7, COL9 HAVING COUNT(*) > 1) B
SET COL_ERROR = '同一加碼贈點下,不能重複代收項目'
WHERE A.COL2 = B.COL2 AND A.COL3 = B.COL3
     AND A.COL4 = B.COL4 AND A.COL5 = B.COL5
	 AND A.COL6 = B.COL6 AND A.COL7 = B.COL7
     AND A.COL9 = B.COL9 AND A.INPUT_TYPE = '2'  AND A.COL8 = '3'
	 AND A.ACT_ID = P_ACT_ID;
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '臨界值(起)非數值'
WHERE TO_NUMBER(COL3) IS  NULL
AND ACT_ID = P_ACT_ID AND INPUT_TYPE = '3';	
UPDATE PDATA.STORMKTI 
SET COL_ERROR = '臨界值(迄)非數值'
WHERE TO_NUMBER(COL4) IS  NULL
AND ACT_ID = P_ACT_ID AND INPUT_TYPE = '3';	
UPDATE PDATA.STORMKTI
SET COL_ERROR = '臨界值(起)(迄)不得小於-999999'
WHERE (TO_NUMBER(COL3) IS  NOT NULL AND TO_NUMBER(COL4) IS NOT NULL)
AND (COL3 < -9999999 OR COL4 < -9999999)
AND ACT_ID = P_ACT_ID AND INPUT_TYPE = '3';
UPDATE PDATA.STORMKTI
SET COL_ERROR = '臨界值(起)(迄)不得大於9999999'
WHERE (TO_NUMBER(COL3) IS  NOT NULL AND TO_NUMBER(COL4) IS NOT NULL)
AND (COL3 > 9999999 OR COL4 > 9999999)
AND ACT_ID = P_ACT_ID AND INPUT_TYPE = '3';
SET I = 0;
SET J = 0;
FOR LOOPVAR AS CUR1 CURSOR FOR
SELECT ACT_ID, COL1,COL2, COL3, COL4
    FROM PDATA.STORMKTI
 WHERE INPUT_TYPE = '3' AND ACT_ID = P_ACT_ID  ORDER BY COL1
DO 
IF (I>0) THEN
	IF (P_LAST_NUM = LOOPVAR.COL3 AND J = 0) THEN
		SET P_LAST_NUM = LOOPVAR.COL4 + 1;
	ELSE
		UPDATE PDATA.STORMKTI
		SET COL_ERROR = '客單價不連續'
		WHERE INPUT_TYPE = '3'
		AND ACT_ID = P_ACT_ID
		AND COL1 = LOOPVAR.COL1;
		 SET J = 1;
    END IF;
ELSE
SET P_LAST_NUM = LOOPVAR.COL4 + 1;
END IF;
SET I = 1;
END FOR;
SELECT COUNT(*) INTO ERROR_CNT
FROM PDATA.STORMKTI
WHERE ACT_ID = P_ACT_ID
     AND COL_ERROR IS NOT NULL;
CALL PDATA.P_DROP_TABLE ('#VT_STORMKTI_CHECK');
 IF (ERROR_CNT = 0) THEN 	
DELETE FROM PDATA.STORMKT_PRD_M WHERE ACT_ID = P_ACT_ID;
DELETE FROM PDATA.STORMKT_PRD_D WHERE ACT_ID = P_ACT_ID;
DELETE FROM PDATA.STORMKT_RAISE_M WHERE ACT_ID = P_ACT_ID;
DELETE FROM PDATA.STORMKT_RAISE_D WHERE ACT_ID = P_ACT_ID;
DELETE FROM PDATA.STORMKT_CSPRG WHERE ACT_ID = P_ACT_ID;
 CALL PDATA.P_DROP_TABLE('#TP_TEMP1');
CREATE VOLATILE TABLE #TP_TEMP1 AS (	
    SELECT ACT_ID, ROW_NUMBER() OVER (ORDER BY COL2, COL3 ASC ) AS ID, COL2, COL3
        FROM PDATA.STORMKTI
     WHERE INPUT_TYPE = '1'	  
	 AND ACT_ID = P_ACT_ID
     GROUP BY ACT_ID,COL2, COL3
) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
 INSERT INTO PDATA.STORMKT_PRD_M (ACT_ID, CLASS_ID, CLASS_NM, STICK_TYPE)
 SELECT ACT_ID, ID, COL3, COL2
        FROM #TP_TEMP1;     
INSERT INTO PDATA.STORMKT_PRD_D (ACT_ID, CLASS_ID, FM_CODE, FM_NAME, UPDATE_TIME)
SELECT T1.ACT_ID, T1.ID,              
              T2.COL4, T2.COL5, DATE
    FROM #TP_TEMP1 T1
	 JOIN (
    SELECT COL2, COL3, COL4, COL5
        FROM PDATA.STORMKTI
     WHERE INPUT_TYPE = '1'
	    AND ACT_ID = P_ACT_ID
	) AS T2 ON T1.COL2 = T2.COL2 AND T1.COL3 = T2.COL3;
 CALL PDATA.P_DROP_TABLE('#TP_TEMP2');
CREATE VOLATILE TABLE #TP_TEMP2 AS (	
SELECT ACT_ID, ROW_NUMBER() OVER (ORDER BY COL2, COL3 ASC ) AS ID,
              COL2, COL3, COL4, COL5, COL6, COL7
   FROM PDATA.STORMKTI
WHERE INPUT_TYPE = '2'
     AND ACT_ID = P_ACT_ID
 GROUP BY ACT_ID,COL2, COL3, COL4, COL5, COL6, COL7
 ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
 INSERT INTO PDATA.STORMKT_RAISE_M(ACT_ID, RAISE_ID,RAISE_NM,STICK_TYPE,PIECE,GIVEPT,RAISE_SDATE,RAISE_EDATE)
 SELECT ACT_ID, ID, COL3, COL2, COL4, COL5,
               CAST(TRIM(TO_NUMBER(SUBSTRING(COL6,1,3)) +1911   + '/' +  SUBSTRING(COL6,4,2) + '/' + SUBSTRING(COL6,6,2)) AS DATE FORMAT 'YYYY-MM-DD'),
			   CAST(TRIM(TO_NUMBER(SUBSTRING(COL7,1,3)) +1911   + '/' +  SUBSTRING(COL7,4,2) + '/' + SUBSTRING(COL7,6,2)) AS DATE FORMAT 'YYYY-MM-DD')
     FROM #TP_TEMP2;
 INSERT INTO PDATA.STORMKT_RAISE_D (ACT_ID, RAISE_ID, FM_CODE, FM_TYPE, UPDATE_TIME)
 SELECT T1.ACT_ID, T1.ID, T2.COL9, T2.COL8, DATE 
     FROM #TP_TEMP2 T1
	   JOIN (
    		SELECT COL2, COL3, COL4, COL5, COL6, COL7, COL8, COL9
			    FROM PDATA.STORMKTI
              WHERE INPUT_TYPE = '2'
			       AND ACT_ID = P_ACT_ID
	 ) AS T2 ON  T1.COL2 = T2.COL2 AND T1.COL3 = T2.COL3
     AND T1.COL4 = T2.COL4 AND T1.COL5 = T2.COL5
	 AND T1.COL6 = T2.COL6 AND T1.COL7 = T2.COL7;
INSERT INTO PDATA.STORMKT_CSPRG (ACT_ID, CSPID, CSPRG, SCSP, ECSP)
SELECT ACT_ID, 
               ROW_NUMBER() OVER (ORDER BY COL1 ASC ) AS ID,
               COL2, COL3, COL4
    FROM PDATA.STORMKTI
 WHERE INPUT_TYPE = '3' AND ACT_ID = P_ACT_ID;
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_STORMKTI_CHECK AS('
                          +' SELECT 0 AS RETURN_VAL, ''SUCCESS'' AS RETURN_DESCR'						  
						  + ' ) WITH DATA UNIQUE PRIMARY INDEX(RETURN_VAL) ON COMMIT PRESERVE ROWS;';
ELSE
SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_STORMKTI_CHECK AS('
                          +' SELECT 1 AS RETURN_VAL, ''FAIL'' AS RETURN_DESCR'						  
						  + ' ) WITH DATA UNIQUE PRIMARY INDEX(RETURN_VAL) ON COMMIT PRESERVE ROWS;';
END IF;
EXECUTE IMMEDIATE SQLSTR;   
END SP;