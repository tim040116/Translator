REPLACE PROCEDURE PMART.WDWH_3_1_RPT_FUNC
(
   IN I_ORG_TYPE  VARCHAR(2),   
   IN I_ORG_ID    INTEGER,      
   IN I_DAY_ID    INTEGER       
)
SQL SECURITY INVOKER
SP:BEGIN
  DECLARE SQLSTR     VARCHAR(4000) DEFAULT '';
  DECLARE V_DIM_SQL  VARCHAR(4000) DEFAULT '';
  DECLARE V_REMD_SQL VARCHAR(4000) DEFAULT ''; 
  DECLARE V_REMD_WHERE VARCHAR(4000) DEFAULT '';
  DECLARE V_LAST_7    INTEGER;
  DECLARE V_LAST_14   INTEGER;
  SET V_LAST_7  = '' ;
  SET V_LAST_14 = '' ;
  SELECT CAST(CAST(L_DAY_ID AS DATE )-6  AS INTEGER)   
        ,CAST(CAST(L_DAY_ID AS DATE )-13  AS INTEGER)   
	INTO V_LAST_7,V_LAST_14
    FROM PMART.YMWD_TIME
   WHERE L_DAY_ID = I_DAY_ID
  ;
    SET V_DIM_SQL = ' SELECT *
                     FROM (SELECT L_DAY_ID, L_DAY_ID AS L_DATA_DAY_ID, 0 AS LAST_WEEK
                             FROM PMART.YMWD_TIME
                            WHERE L_DAY_ID >= ' + V_LAST_7 + ' AND L_DAY_ID <= ' + I_DAY_ID + '
                           UNION ALL
                           SELECT L_DAY_ID, L_DAY_LAST_WEEK AS L_DATA_DAY_ID, 1 AS LAST_WEEK
                             FROM PMART.YMWD_TIME
                            WHERE L_DAY_ID >= ' + V_LAST_7 + ' AND L_DAY_ID <= ' + I_DAY_ID + '                            
                          ) A,
                    ';  
   IF (I_ORG_TYPE = '-1') THEN
    SET V_DIM_SQL = V_DIM_SQL + '(
                    SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID  AS ORG_ID FROM PMART.LAST_ORG_DIM
                    UNION ALL
                    SELECT DISTINCT  1 AS ORG_LEVEL, DEPT_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID = ' + I_ORG_ID + '
                   ) B';
   END IF;
   IF (I_ORG_TYPE = '1') THEN
    SET V_DIM_SQL = V_DIM_SQL + '(
                    SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID    AS ORG_ID FROM PMART.LAST_ORG_DIM
                    UNION ALL
                    SELECT DISTINCT  1 AS ORG_LEVEL, DEPT_ID   AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID = ' + I_ORG_ID + '
                    UNION ALL
                    SELECT DISTINCT  2 AS ORG_LEVEL, BRANCH_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID = ' + I_ORG_ID + '
                 ) B';
   END IF;
   IF (I_ORG_TYPE = '2') THEN
    SET V_DIM_SQL = V_DIM_SQL + '(
                    SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID    AS ORG_ID FROM PMART.LAST_ORG_DIM
                    UNION ALL
                    SELECT DISTINCT  2 AS ORG_LEVEL, BRANCH_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID = ' + I_ORG_ID + '
                    UNION ALL
                    SELECT DISTINCT  3 AS ORG_LEVEL, RESPON_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID = ' + I_ORG_ID + '
                 ) B';    
   END IF;
   IF (I_ORG_TYPE = '3') THEN
    SET V_DIM_SQL = V_DIM_SQL + '(
                    SELECT DISTINCT -1 AS ORG_LEVEL, TOT_ID    AS ORG_ID FROM PMART.LAST_ORG_DIM
                    UNION ALL
                    SELECT DISTINCT  3 AS ORG_LEVEL, RESPON_ID AS ORG_ID FROM PMART.LAST_ORG_DIM WHERE RESPON_ID = ' + I_ORG_ID + '
                    UNION ALL
                    SELECT DISTINCT  4 AS ORG_LEVEL, OSTORE_ID AS ORG_ID
                      FROM PMART.LATEST_ORG_DIM
                     WHERE RESPON_ID=' + I_ORG_ID + '
                       AND ROUND(OPNDT/100)<=ROUND(' + I_DAY_ID + '/100)
                       AND ROUND(ENDDT/100)>=ROUND(' + I_DAY_ID + '/100)
                       AND ((ASTORE_ID IS NULL AND OPNDT <=' + I_DAY_ID + ') OR
                            (ASTORE_ID IS NOT NULL AND OPNDT <=' + I_DAY_ID + ' AND ' + I_DAY_ID + '< ENDDT))
                 ) B';
   END IF;
    SET V_REMD_WHERE = ' AMT /DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R2,
                         CUST_NUM /DECODE(UPLOAD_STNUM,0,NULL,UPLOAD_STNUM) AS R3,
                         CAST(AMT AS DECIMAL(12,2))/DECODE(CUST_NUM,0,NULL,CUST_NUM) AS R4 ' ;
    SET V_REMD_SQL = 'SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                             ' + V_REMD_WHERE + '
                        FROM PMART.REMD_FACT_SUM
                       WHERE ORG_ID = -1 AND L_DAY_ID >= ' + V_LAST_14 + ' AND L_DAY_ID <= ' + I_DAY_ID + '
                       UNION ALL
                     ';
   IF (I_ORG_TYPE = '-1') THEN
    SET V_REMD_SQL = V_REMD_SQL + ' SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT DEPT_ID FROM PMART.LAST_ORG_DIM WHERE TOT_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '';
   END IF;
   IF (I_ORG_TYPE = '1') THEN
    SET V_REMD_SQL = V_REMD_SQL + ' SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT DEPT_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '
                                      UNION ALL
                                     SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT BRANCH_ID FROM PMART.LAST_ORG_DIM WHERE DEPT_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '';
   END IF;
   IF (I_ORG_TYPE = '2') THEN
    SET V_REMD_SQL = V_REMD_SQL + ' SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT BRANCH_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '
                                      UNION ALL
                                     SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT RESPON_ID FROM PMART.LAST_ORG_DIM WHERE BRANCH_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '';
   END IF;
   IF (I_ORG_TYPE = '3') THEN
    SET V_REMD_SQL = V_REMD_SQL + ' SELECT ORG_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT_SUM A
                                      WHERE A.ORG_ID IN (SELECT DISTINCT RESPON_ID FROM PMART.LAST_ORG_DIM WHERE RESPON_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '
                                      UNION ALL
                                     SELECT A.OSTORE_ID AS MAP_ORG_ID, L_DAY_ID AS MAP_DAY_ID,
                                             ' + V_REMD_WHERE + '
                                       FROM PMART.REMD_FACT A
                                      WHERE A.OSTORE_ID IN (SELECT OSTORE_ID FROM PMART.LAST_ORG_DIM WHERE RESPON_ID=' + I_ORG_ID + ')
                                        AND A.L_DAY_ID >= ' + V_LAST_14 + ' AND A.L_DAY_ID <= ' + I_DAY_ID + '';
   END IF;   
     CALL PMART.P_DROP_TABLE ('#VT_WDWH_3_1_RPT_FUNC');
      SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_WDWH_3_1_RPT_FUNC AS         ' +
                  '(SELECT L_DAY_ID, L_DATA_DAY_ID, LAST_WEEK, ORG_LEVEL, ORG_ID,  ' +
                  '    NVL(ROUND(R2,0),0) AS R2,                                   ' +
                  '    NVL(ROUND(R3,0),0) AS R3,                                   ' +
                  '    NVL(ROUND(R4,2),0) AS R4                                    ' +
                  '  FROM (' + V_DIM_SQL + ') Y                                  ' +
                  '  LEFT JOIN                                                     ' +
                  '  (' + V_REMD_SQL + ') R                                      ' +
                  '    ON Y.L_DATA_DAY_ID = R.MAP_DAY_ID                           ' +
                  '   AND Y.ORG_ID = R.MAP_ORG_ID                                  ' +
                  ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; '; 
  EXECUTE IMMEDIATE SQLSTR;   
END SP;