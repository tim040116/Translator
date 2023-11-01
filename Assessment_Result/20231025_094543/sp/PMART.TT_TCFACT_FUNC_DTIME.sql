REPLACE PROCEDURE PMART.TT_TCFACT_FUNC_DTIME(
FP_TIME_TYPE CHAR CASESPECIFIC,FP_TIME_LIST VARCHAR(2000),
FP_ORG_LEVEL NUMBER,FP_ORG_ID NUMBER,
FP_PRD_LEVEL NUMBER,FP_PRD_ID VARCHAR(7))
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000);
DECLARE V_TABLE_NAME VARCHAR(60);
   CALL PMART.P_DROP_TABLE ('#VT_TT_TCFACT_FUNC'); 
   IF FP_TIME_TYPE='D' AND FP_ORG_LEVEL=4 AND FP_PRD_LEVEL=3 THEN
	  SET V_TABLE_NAME = 'PMART.FACT_SALES_TIME_DETAIL';
    ELSE
	   SET V_TABLE_NAME = 'PMART.FACT_SALES_TIME';
   END IF;
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_TT_TCFACT_FUNC  AS('+
      ' SELECT  TIME_ID,TR_ID,SUM(S_CNT) AS CNT,SUM(S_AMT) AS AMT FROM ( '+
	  ' SELECT CAST(A.TIME_ID AS NUMBER) AS TIME_ID,A.PRD_ID,CAST(H.X AS NUMBER )AS TR_ID, '+
      ' CASE WHEN A.TIME_RANGE = H.X THEN CAST(A.S_CNT AS NUMBER) ELSE CAST(0 AS NUMBER) END AS S_CNT, '+
	  ' CASE WHEN A.TIME_RANGE = H.X THEN CAST(A.S_AMT AS NUMBER) ELSE CAST(0 AS NUMBER) END AS S_AMT '+
      ' FROM '+ V_TABLE_NAME + ' A ' +
	  ' CROSS JOIN (SELECT CAST(DAY_OF_CALENDAR AS INTEGER) -1 AS X FROM SYS_CALENDAR.CALENDAR  WHERE X BETWEEN 0 AND 23 ) AS H'+
      ' WHERE A.TIME_ID IN ('+ FP_TIME_LIST + ') '+
      ' AND A.ORG_ID='+ FP_ORG_ID +' AND A.PRD_ID='''+ FP_PRD_ID +''' '+
      ' ) A GROUP BY TIME_ID,TR_ID '+
      ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END SP;