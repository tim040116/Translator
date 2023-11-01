REPLACE PROCEDURE PMART.OT_TCFACT_FUNC_TIME(
FP_TIME_ID INTEGER,
FP_ORG_LEVEL INTEGER,FP_ORG_LIST VARCHAR(2000),
FP_PRD_LEVEL INTEGER,FP_PRD_ID VARCHAR(7))
SQL SECURITY INVOKER
SP:BEGIN
DECLARE SQLSTR  VARCHAR(4000);
DECLARE V_TABLE_NAME VARCHAR(30);
   CALL PMART.P_DROP_TABLE ('#VT_OT_TCFACT_FUNC'); 
    SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_OT_TCFACT_FUNC  AS('+
                        'SELECT ORG_ID,  TR_ID,  SUM(S_CNT) AS CNT, SUM(S_AMT) AS AMT '+
                        '  FROM (    '+
                        '       SELECT CAST(I.ORG_ID AS NUMBER) AS ORG_ID,     '+          
	                '              CAST(H.X  AS NUMBER) AS TR_ID,       ' +
                        '              CAST(CASE WHEN I.TIME_RANGE = H.X THEN I.S_CNT ELSE 0 END AS NUMBER) AS S_CNT,  '+
                        '   	       CAST(CASE WHEN I.TIME_RANGE = H.X THEN I.S_AMT ELSE 0 END AS NUMBER) AS S_AMT  '+		
	                '   FROM PMART.FACT_SALES_TIME I ' + 
	                ' CROSS JOIN (SELECT CAST(DAY_OF_CALENDAR AS INTEGER) -1 AS X FROM SYS_CALENDAR.CALENDAR  WHERE X BETWEEN 0 AND 23 ) AS H '+
                        '  WHERE I.TIME_ID='+ FP_TIME_ID +' ' +
                        '    AND I.ORG_ID IN ('+ FP_ORG_LIST +') ' +
			'    AND I.PRD_ID='''+ FP_PRD_ID +''' ' +
	                ' )S GROUP BY ORG_ID,  TR_ID '+
                    ') WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;';
	EXECUTE IMMEDIATE SQLSTR;
END SP;