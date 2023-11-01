REPLACE PROCEDURE PMART.NEWFILLBLANK_NCIG (P_MONTH_ID NUMBER)
SQL SECURITY INVOKER
SP:BEGIN
MERGE INTO PMART.REMD_FACT_NCIG T USING
(
SELECT
 A.L_DAY_ID  AS L_DAY_ID
,A.OSTORE_ID AS OSTORE_ID
,A.STORE_ID  AS STORE_ID
,B.AMT       AS BUDGET_ST_AVG_AMT
,B.TAMT      AS BUDGET_ST_TOT_AMT
,C.AMT       AS BUDGET_DEPT_AVG_AMT
,C.TAMT      AS BUDGET_DEPT_TOT_AMT
,0           AS STATUS
,0           AS AC
,E.AMT       AS BUDGET_PDEPT_AVG_AMT
,E.TAMT      AS BUDGET_PDEPT_TOT_AMT
FROM
(
  SELECT
   B.L_DAY_ID AS L_DAY_ID
  ,OSTORE_ID
  ,STORE_ID
    FROM PMART.LATEST_ORG_DIM
     ,(SELECT L_DAY_ID
         FROM PMART.YMWD_TIME WHERE L_MONTH_ID= P_MONTH_ID
      ) B
   WHERE ((OPNDT <  B.L_DAY_ID  AND B.L_DAY_ID <= ENDDT AND B.L_DAY_ID  <  20131015 )
       OR (OPNDT <= B.L_DAY_ID  AND B.L_DAY_ID <  ENDDT AND B.L_DAY_ID  >= 20131015 )
         )
) A
LEFT OUTER JOIN
(
  SELECT OSTORE_ID,YYYYMM,AVG(AMT) AS AMT,SUM(TAMT) AS TAMT
    FROM PDATA.ST_REMD1400M_NCIG
   GROUP BY OSTORE_ID,YYYYMM
) B
ON (A.OSTORE_ID=B.OSTORE_ID AND ROUND(A.L_DAY_ID/100)=B.YYYYMM)
LEFT OUTER JOIN
(
   SELECT
    T2.OSTORE_ID AS OSTORE_ID
   ,T1.YYYYMM AS YYYYMM
   ,T1.AMT AS AMT
   ,T1.TAMT AS TAMT
     FROM PDATA.ST_REMD1400Y_NCIG T1,PMART.LAST_ORG_DIM T2
    WHERE T1.DEPT_NO=T2.DEPT_NO
) C
ON (A.OSTORE_ID=C.OSTORE_ID 
AND ROUND(A.L_DAY_ID/100)=C.YYYYMM )
LEFT JOIN
   (
   SELECT
    T2.OSTORE_ID AS OSTORE_ID
   ,T1.YYYYMM AS YYYYMM
   ,T1.AMT AS AMT
   ,T1.TAMT AS TAMT
   ,T1.SLDAY AS SLDAY   
    FROM PDATA.ST_REMD1400Y_NCIG T1,PMART.LAST_ORG_DIM T2
   WHERE T1.DEPT_NO=T2.PDEPT_NO
   ) E
 ON (A.OSTORE_ID=E.OSTORE_ID 
AND TRUNC(A.L_DAY_ID/100,0)=E.YYYYMM)
) S
 ON (T.L_DAY_ID=S.L_DAY_ID 
AND  T.OSTORE_ID=S.OSTORE_ID)
WHEN NOT MATCHED THEN INSERT
(
 L_DAY_ID
,OSTORE_ID
,STORE_ID
,BUDGET_ST_AVG_AMT
,BUDGET_ST_TOT_AMT
,BUDGET_DEPT_AVG_AMT
,BUDGET_DEPT_TOT_AMT
,STATUS
,AC
,BUDGET_PDEPT_AVG_AMT
,BUDGET_PDEPT_TOT_AMT
)
VALUES
(
 S.L_DAY_ID
,S.OSTORE_ID
,S.STORE_ID
,S.BUDGET_ST_AVG_AMT
,S.BUDGET_ST_TOT_AMT
,S.BUDGET_DEPT_AVG_AMT
,S.BUDGET_DEPT_TOT_AMT
,S.STATUS
,S.AC
,S.BUDGET_PDEPT_AVG_AMT
,S.BUDGET_PDEPT_TOT_AMT
)
;
END SP;