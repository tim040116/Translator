REPLACE   PROCEDURE PMART.PS_TRANS_PRODUCT_DETAIL
(
)
SP:BEGIN
	DECLARE SQLSTR  VARCHAR(32000);
	DECLARE SQLCREATE  VARCHAR(30000);
	DECLARE P_JOB_ID  VARCHAR(20000);
	DECLARE P_TX_SDT  VARCHAR(20000);
	DECLARE P_TX_EDT  VARCHAR(20000);
	SET P_JOB_ID =  (SELECT *  FROM #VT_PS_JOB_ID);
	SET P_TX_SDT = '''' + (SELECT TX_SDT + ' 00:00:00' AS TX_SDT FROM PDATA.PRDREL_SET WHERE  JOB_ID = P_JOB_ID) + '''';
	SET P_TX_EDT = '''' + (SELECT TX_EDT + ' 23:59:59' AS TX_EDX FROM PDATA.PRDREL_SET WHERE  JOB_ID = P_JOB_ID) + '''';
 	CALL PMART.P_DROP_TABLE ('#VT_PS_TRANS_PRODUCT_DETAIL');
SET SQLSTR = ' 
SELECT * FROM PDATA.TRANS_PRODUCT_DETAIL
WHERE  TX_DTTM BETWEEN  '+ P_TX_SDT + ' AND '+P_TX_EDT+'   
'
; 
   SET SQLCREATE = 
   'CREATE MULTISET VOLATILE TABLE #VT_PS_TRANS_PRODUCT_DETAIL AS( '
   +SQLSTR 
   +'	) WITH DATA PRIMARY INDEX(TX_SEQ) ON COMMIT PRESERVE ROWS;'
   ;
EXECUTE IMMEDIATE SQLCREATE;   
END SP;