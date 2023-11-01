CREATE PROCEDURE PMART.OLD0401_FD_EXPORT_RPT
(
   IN P_EMP_NO  VARCHAR(10),
   IN P_S_DAY INTEGER,
   IN P_E_DAY INTEGER,
   IN P_VAL_PERT_CF INTEGER,
   IN P_VAL_PERT DECIMAL(7,4),   
   IN P_ORG_TYPE INTEGER,
   IN P_SAL_NUM_CF INTEGER,
   IN P_SAL_NUM DECIMAL(5,2),
   IN P_REMD_DAY_CF INTEGER,
   IN P_REMD_DAY INTEGER   
)
SP:BEGIN
DECLARE SQLSTR  VARCHAR(3000);    
DECLARE SQLCF VARCHAR(6);
DECLARE P_ORG_SET_TYPE INTEGER;
DECLARE P_PRD_SET_TYPE INTEGER;
DECLARE P_TODAY INTEGER;
DECLARE P_VAL DECIMAL(7,4);
SET P_TODAY = CAST((DATE (FORMAT 'YYYYMMDD' )) AS CHAR(8));
SET P_VAL = CAST(P_VAL_PERT AS DECIMAL(7,4)) / CAST(100 AS DECIMAL(7,4));
CALL PMART.P_DROP_TABLE('#VT_ORG_TEMP');
CREATE MULTISET VOLATILE TABLE #VT_ORG_TEMP AS PMART.LATEST_ORG_DIM WITH NO DATA ON COMMIT PRESERVE ROWS;
SELECT DISTINCT SET_TYPE
    INTO P_ORG_SET_TYPE
FROM PMART.FD_EXPORT_SET
WHERE EMP_NO = P_EMP_NO
     AND SET_FLAG = 'O';
IF P_ORG_SET_TYPE = 0 THEN
	INSERT INTO #VT_ORG_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'O' ) T1
	   JOIN (SELECT *
				FROM PMART.LATEST_ORG_DIM 
				WHERE OPNDT<=P_TODAY AND ENDDT>P_TODAY) T2 ON T1.SET_ID = T2.PDEPT_ID;
ELSEIF P_ORG_SET_TYPE = 1 THEN
	INSERT INTO #VT_ORG_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'O' ) T1
	   JOIN (SELECT *
				FROM PMART.LATEST_ORG_DIM 
				WHERE OPNDT<=P_TODAY AND ENDDT>P_TODAY) T2 ON T1.SET_ID = T2.DEPT_ID;
ELSEIF P_ORG_SET_TYPE = 2 THEN
	INSERT INTO #VT_ORG_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'O' ) T1
	   JOIN (SELECT *
				FROM PMART.LATEST_ORG_DIM 
				WHERE OPNDT<=P_TODAY AND ENDDT>P_TODAY) T2 ON T1.SET_ID = T2.BRANCH_ID;
ELSEIF P_ORG_SET_TYPE = 3 THEN
	INSERT INTO #VT_ORG_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'O' ) T1
	   JOIN (SELECT *
				FROM PMART.LATEST_ORG_DIM 
				WHERE OPNDT<=P_TODAY AND ENDDT>P_TODAY) T2 ON T1.SET_ID = T2.OSTORE_ID;
END IF;
CALL PMART.P_DROP_TABLE('#VT_PRD_TEMP');
CREATE MULTISET VOLATILE TABLE #VT_PRD_TEMP 
(
PRD_ID				INTEGER,
KND_ID				INTEGER,
GRP_ID				INTEGER,
FM_CODE			VARCHAR(7),
FM_NAME		    VARCHAR(64) 
)
UNIQUE PRIMARY INDEX ( PRD_ID ) ON COMMIT PRESERVE ROWS;
SELECT DISTINCT SET_TYPE
    INTO P_PRD_SET_TYPE
FROM PMART.FD_EXPORT_SET
WHERE EMP_NO = P_EMP_NO
     AND SET_FLAG = 'P';
IF P_PRD_SET_TYPE = 1 THEN
	INSERT INTO #VT_PRD_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'P' ) T1
	   JOIN (SELECT PRD_ID, KND_ID, GRP_ID, FM_CODE, FM_NAME
	               FROM PMART.PRD_PRD) T2 ON T1.SET_ID = T2.KND_ID;	
ELSEIF P_PRD_SET_TYPE = 2 THEN
	INSERT INTO #VT_PRD_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'P' ) T1
	   JOIN (SELECT PRD_ID, KND_ID, GRP_ID, FM_CODE, FM_NAME
	               FROM PMART.PRD_PRD) T2 ON T1.SET_ID = T2.GRP_ID;
ELSEIF P_PRD_SET_TYPE = 3 THEN
	INSERT INTO #VT_PRD_TEMP
	SELECT T2.* 
	   FROM (	   
	   SELECT SET_ID FROM PMART.FD_EXPORT_SET 
	   WHERE EMP_NO = P_EMP_NO AND SET_FLAG = 'P' ) T1
	   JOIN (SELECT PRD_ID, KND_ID, GRP_ID, FM_CODE, FM_NAME
	               FROM PMART.PRD_PRD) T2 ON T1.SET_ID = T2.PRD_ID;
END IF;
CALL PMART.P_DROP_TABLE('#VT_PLAN_NUM');
CREATE MULTISET VOLATILE TABLE #VT_PLAN_NUM 
(
TIME_ID				INTEGER,
OSTORE_ID		INTEGER
)
PRIMARY INDEX ( OSTORE_ID ) ON COMMIT PRESERVE ROWS;
FOR LOOPVAR AS CUR1 CURSOR FOR
SELECT L_DAY_ID
FROM PMART.YMWD_TIME
WHERE L_DAY_ID BETWEEN P_S_DAY AND P_E_DAY
ORDER BY L_DAY_ID
DO 
INSERT INTO #VT_PLAN_NUM
SELECT T1.TIME_ID, T1.OSTORE_ID
FROM (		
		SELECT X1.TIME_ID, X2.OSTORE_ID 
		FROM (
					SELECT A.TIME_ID, CAST(B.BIT_NUM AS INTEGER) AS BIT_NUM
					FROM(
						SELECT TIME_ID, PLAN_STORE_NUM
						FROM PMART.BASIC_OST_FACT
						WHERE TIME_ID = LOOPVAR.L_DAY_ID				     
					) A, TABLE(BIT_LIST(A.PLAN_STORE_NUM)) AS B
		) X1, PMART.ORG_BIT_MAPPING X2
		WHERE X1.BIT_NUM = X2.OSTORE_BIT_SEQ		
) T1
JOIN PMART.LAST_ORG_DIM T2 ON T1.OSTORE_ID = T2.OSTORE_ID;
END FOR;
CALL PMART.P_DROP_TABLE('#VT_ORG_PLAN');
CREATE MULTISET VOLATILE TABLE #VT_ORG_PLAN 
(
PDEPT_ID		INTEGER,
PDEPT_NM   VARCHAR(50) ,
DEPT_ID		INTEGER,
DEPT_NM   VARCHAR(50) ,
BRANCH_ID		INTEGER,
BRANCH_NM   VARCHAR(50) ,
RESPON_ID INTEGER,
RESPON_NM VARCHAR(50) ,
STORE_ID INTEGER,
STORE_NO VARCHAR(6) ,
STORE_NM VARCHAR(18) ,
OSTORE_ID INTEGER,
PLAN_STORE_NUM INTEGER,
FILTER_ORG INTEGER
)
PRIMARY INDEX ( OSTORE_ID ) ON COMMIT PRESERVE ROWS;
INSERT INTO #VT_ORG_PLAN
SELECT T1.PDEPT_ID, T1.PDEPT_NM, T1.DEPT_ID, T1.DEPT_NM,
            T1.BRANCH_ID, T1.BRANCH_NM,T1.RESPON_ID, T1.RESPON_NM,
			T1.STORE_ID, T1.STORE_NO, T1.STORE_NM, T1.OSTORE_ID,
			NVL(T2.PLAN_STORE_NUM,0) PLAN_STORE_NUM,
			(CASE WHEN P_REMD_DAY_CF=1 THEN CASE WHEN NVL(T2.PLAN_STORE_NUM,0) < P_REMD_DAY THEN 1 ELSE 0 END
					   WHEN P_REMD_DAY_CF=4 THEN CASE WHEN NVL(T2.PLAN_STORE_NUM,0) <= P_REMD_DAY THEN 1 ELSE 0 END 
					   ELSE 0 END) AS FILTER_ORG
   FROM  (  SELECT * FROM #VT_ORG_TEMP)  T1
   LEFT JOIN (SELECT OSTORE_ID,  COUNT(OSTORE_ID) AS PLAN_STORE_NUM			
				      FROM #VT_PLAN_NUM GROUP BY OSTORE_ID) T2 ON T1.OSTORE_ID = T2.OSTORE_ID;
CALL PMART.P_DROP_TABLE('#VT_TRANS');
CREATE MULTISET VOLATILE TABLE #VT_TRANS 
(
OSTORE_ID		INTEGER,
PRD_ID		INTEGER,
FM_CODE	 VARCHAR(7) ,
FM_NAME VARCHAR(32) ,
SALES_CNT	INTEGER,
INPRD_CNT	INTEGER
)
PRIMARY INDEX ( OSTORE_ID ) ON COMMIT PRESERVE ROWS;
INSERT INTO #VT_TRANS
SELECT T2.ORG_ID, T2.PRD_ID, T1.FM_CODE, T1.FM_NAME, T2.SALES_CNT, T2.INPRD_CNT
FROM (
SELECT * FROM #VT_PRD_TEMP
) T1,
(	SELECT D.ORG_ID, D.PRD_ID,
					SUM(D.SALES_CNT) SALES_CNT, 
					SUM(D.INPRD_CNT) INPRD_CNT
		FROM PMART.BASIC_MFACT_DETAIL D, #VT_ORG_TEMP O
		WHERE D.TIME_ID >= P_S_DAY 
		   AND D.TIME_ID <= P_E_DAY
		   AND D.ORG_ID = O.OSTORE_ID
		GROUP BY D.ORG_ID, D.PRD_ID
) T2 
WHERE T1.PRD_ID = T2.PRD_ID;
CALL PMART.P_DROP_TABLE('#VT_PRD_TEMP');
CALL PMART.P_DROP_TABLE('#VT_PLAN_NUM');
CALL PMART.P_DROP_TABLE('#VT_FD_DETAIL');
CREATE MULTISET VOLATILE TABLE #VT_FD_DETAIL 
(
		PDEPT_ID		INTEGER,
	  	PDEPT_NM				VARCHAR(50) ,
		DEPT_ID		INTEGER,
	  	DEPT_NM				VARCHAR(50) ,
		BRANCH_ID		INTEGER,
		BRANCH_NM			VARCHAR(50) ,
		RESPON_NM			VARCHAR(50) ,
		STORE_NO				VARCHAR(6) ,
		STORE_NM				VARCHAR(50) ,
		OSTORE_ID		INTEGER,
		PRD_NO						VARCHAR(7) ,
		PRD_NM						VARCHAR(32) ,
		SALES_CNT			INTEGER,
		SALES_PRET		  DECIMAL(6,2),
		INPRD_CNT			INTEGER,
		INPRD_PRET		  DECIMAL(6,2),
		VAL_PERT			DECIMAL(5,2),
		FILTER_SAL		   INTEGER
)
NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
INSERT INTO #VT_FD_DETAIL
SELECT PDEPT_ID, PDEPT_NM,DEPT_ID, DEPT_NM, BRANCH_ID, BRANCH_NM,
			RESPON_NM, STORE_NO, STORE_NM, 
			OSTORE_ID,
			FM_CODE, FM_NAME, 
			SALES_CNT, 
			SALES_PRET,
			INPRD_CNT, 
			INPRD_PRET,
			(CASE WHEN INPRD_PRET > 0 THEN (SALES_PRET / INPRD_PRET) ELSE 0 END ) AS VAL_PERT, 
			(CASE WHEN P_SAL_NUM_CF=0 THEN CASE WHEN SALES_PRET > P_SAL_NUM THEN 1 ELSE 0 END
		   				WHEN P_SAL_NUM_CF=3 THEN CASE WHEN SALES_PRET >= P_SAL_NUM THEN 1 ELSE 0 END 
		   				ELSE 0 END) AS FILTER_SAL						
FROM (
		SELECT T1.PDEPT_ID, T1.PDEPT_NM, T1.DEPT_ID, T1.DEPT_NM, T1.BRANCH_ID, T1.BRANCH_NM,
					 T1.RESPON_NM, T1.STORE_NO, T1.STORE_NM, T1.OSTORE_ID,
					T2.FM_CODE, T2.FM_NAME, 
					T2.SALES_CNT, 
					(CASE WHEN T1.PLAN_STORE_NUM > 0 THEN ( CAST(T2.SALES_CNT AS DECIMAL(6,2)) / T1.PLAN_STORE_NUM) ELSE 0 END) AS SALES_PRET,
					T2.INPRD_CNT, 
					(CASE WHEN T1.PLAN_STORE_NUM > 0 THEN ( CAST(T2.INPRD_CNT AS DECIMAL(6,2)) / T1.PLAN_STORE_NUM) ELSE 0 END) AS INPRD_PRET
		FROM  
		( SELECT * FROM #VT_ORG_PLAN WHERE FILTER_ORG = 0 ) T1,
		( SELECT * FROM #VT_TRANS) T2 
		WHERE T1.OSTORE_ID = T2.OSTORE_ID
) AS TEMP1;		
CALL PMART.P_DROP_TABLE('#VT_FD_EXPORT_RPT');	  
CREATE MULTISET VOLATILE TABLE #VT_FD_EXPORT_RPT 
	  (
	  	PDEPT_NM				VARCHAR(50) ,
		DEPT_NM				VARCHAR(50) ,
		BRANCH_NM				VARCHAR(50) ,
		RESPON_NM			VARCHAR(50) ,
		STORE_NO				VARCHAR(6) ,
		STORE_NM				VARCHAR(50) ,
		PRD_NO						VARCHAR(7) ,
		PRD_NM						VARCHAR(32) ,
		STORE_ERR_NUM			INTEGER,
		STORE_ERR_RATE			DECIMAL(5,2),
		INPRD_NUM					 DECIMAL(5,2),
		SAL_NUM							DECIMAL(5,2),
		VAL_PERT						DECIMAL(5,2)
	  )	   NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;
IF P_VAL_PERT_CF = 0 THEN
SET SQLCF = ' > ';
ELSEIF P_VAL_PERT_CF = 1 THEN
SET SQLCF = ' < ';
ELSEIF P_VAL_PERT_CF = 2 THEN
SET SQLCF = ' = ';
ELSEIF P_VAL_PERT_CF = 3 THEN
SET SQLCF = ' >= ';
ELSEIF P_VAL_PERT_CF = 4 THEN
SET SQLCF = ' <= ';
END IF;
IF P_ORG_TYPE = 0 THEN
SET SQLSTR = 'INSERT INTO #VT_FD_EXPORT_RPT '
					+ 'SELECT  T1.PDEPT_NM, NULL, NULL, NULL, NULL, NULL, T1.PRD_NO, T1.PRD_NM, T1.STORE_ERR_NUM, '
					+ '		(CAST(T1.STORE_ERR_NUM AS DECIMAL(6,2)) / CAST(T2.STORE_ALL_NUM AS DECIMAL(6,2))) AS STORE_ERR_RATE, '
					+ '			NULL, NULL, NULL '
					+ '	FROM ( '
					+ '	SELECT PDEPT_ID, DEPT_ID, DEPT_NM, PRD_NO, PRD_NM, COUNT(OSTORE_ID) AS STORE_ERR_NUM '
					+ '	FROM #VT_FD_DETAIL '
					+ '	WHERE FILTER_SAL = 0 '
					+ '	AND VAL_PERT ' + SQLCF + P_VAL
					+ '	GROUP BY PDEPT_ID, PDEPT_NM, DEPT_ID, DEPT_NM, PRD_NO, PRD_NM '
					+ '	) T1, '
					+ '	( '
					+ '	SELECT DEPT_ID, COUNT(OSTORE_ID) AS STORE_ALL_NUM '
					+ '	FROM #VT_ORG_TEMP '
					+ '	GROUP BY PDEPT_ID '
					+ '	) T2 '
					+ '	WHERE T1.PDEPT_ID = T2.PDEPT_ID; ';
ELSEIF P_ORG_TYPE = 1 THEN
SET SQLSTR = 'INSERT INTO #VT_FD_EXPORT_RPT '
					+ 'SELECT  T1.PDEPT_NM, T1.DEPT_NM, NULL, NULL, NULL, NULL, T1.PRD_NO, T1.PRD_NM, T1.STORE_ERR_NUM, '
					+ '		(CAST(T1.STORE_ERR_NUM AS DECIMAL(6,2)) / CAST(T2.STORE_ALL_NUM AS DECIMAL(6,2))) AS STORE_ERR_RATE, '
					+ '			NULL, NULL, NULL '
					+ '	FROM ( '
					+ '	SELECT PDEPT_ID, DEPT_ID, DEPT_NM, PRD_NO, PRD_NM, COUNT(OSTORE_ID) AS STORE_ERR_NUM '
					+ '	FROM #VT_FD_DETAIL '
					+ '	WHERE FILTER_SAL = 0 '
					+ '	AND VAL_PERT ' + SQLCF + P_VAL
					+ '	GROUP BY PDEPT_ID, PDEPT_NM, DEPT_ID, DEPT_NM, PRD_NO, PRD_NM '
					+ '	) T1, '
					+ '	( '
					+ '	SELECT DEPT_ID, COUNT(OSTORE_ID) AS STORE_ALL_NUM '
					+ '	FROM #VT_ORG_TEMP '
					+ '	GROUP BY DEPT_ID '
					+ '	) T2 '
					+ '	WHERE T1.DEPT_ID = T2.DEPT_ID; ';
ELSEIF P_ORG_TYPE =2 THEN
	SET SQLSTR = 'INSERT INTO #VT_FD_EXPORT_RPT '
						+ '	SELECT  T1.PDEPT_NM, T1.DEPT_NM, T1.BRANCH_NM, NULL, NULL, NULL, T1.PRD_NO, T1.PRD_NM, T1.STORE_ERR_NUM, '
						+ '			(CAST(T1.STORE_ERR_NUM AS DECIMAL(6,2)) / CAST(T2.STORE_ALL_NUM AS DECIMAL(6,2))) AS STORE_ERR_RATE, '
						+ '			NULL, NULL, NULL '
						+ '	FROM ( '
						+ '		SELECT PDEPT_ID, DEPT_ID, DEPT_NM, BRANCH_ID, BRANCH_NM, PRD_NO, PRD_NM, COUNT(OSTORE_ID) AS STORE_ERR_NUM '
						+ '		FROM #VT_FD_DETAIL '
						+ '		WHERE FILTER_SAL = 0 '
						+ '	AND VAL_PERT ' + SQLCF + P_VAL
						+ '		GROUP BY PDEPT_ID, PDEPT_NM, DEPT_ID, DEPT_NM, BRANCH_ID, BRANCH_NM, PRD_NO, PRD_NM '
						+ '	) T1, '
						+ '	( '
						+ '			SELECT BRANCH_ID, COUNT(OSTORE_ID) AS STORE_ALL_NUM '
						+ '			FROM #VT_ORG_TEMP '
						+ '			GROUP BY BRANCH_ID '
						+ '	) T2 '
						+ '	WHERE T1.BRANCH_ID = T2.BRANCH_ID;';
ELSEIF P_ORG_TYPE= 3 THEN
SET SQLSTR = 'INSERT INTO #VT_FD_EXPORT_RPT '
					+ '	SELECT PDEPT_NM, DEPT_NM, BRANCH_NM, RESPON_NM, STORE_NO, STORE_NM, '
					+ '				PRD_NO, PRD_NM, '
					+ '				NULL, NULL, '
					+ '				INPRD_PRET, SALES_PRET, VAL_PERT '
					+ '	FROM #VT_FD_DETAIL '
					+ '	WHERE FILTER_SAL = 0 '
					+ '	AND VAL_PERT ' + SQLCF + P_VAL;
END IF;
EXECUTE IMMEDIATE SQLSTR;   
CALL PMART.P_DROP_TABLE('#VT_ORG_TEMP');
CALL PMART.P_DROP_TABLE('#VT_ORG_PLAN');
CALL PMART.P_DROP_TABLE('#VT_TRANS');
CALL PMART.P_DROP_TABLE('#VT_FD_DETAIL');
END SP;