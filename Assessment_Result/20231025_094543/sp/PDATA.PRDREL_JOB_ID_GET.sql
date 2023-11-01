CREATE PROCEDURE PDATA.PRDREL_JOB_ID_GET
()
SQL SECURITY INVOKER
SP:BEGIN
DECLARE P_CNT INTEGER DEFAULT 0 ;
DECLARE P_TODAY VARCHAR(10) ; 
DECLARE P_PARA_VAL2 VARCHAR(10) DEFAULT '0001';  
DECLARE SQLSTR  VARCHAR(4000) DEFAULT ''; 
LOCKING  PDATA.SYS_PARA       FOR ACCESS;
SELECT CAST( CAST(CURRENT_DATE AS DATE FORMAT 'YYMMDD') AS VARCHAR(8)) INTO P_TODAY ;
CALL PMART.P_DROP_TABLE ('#VT_PRDREL_JOB_ID_GET');
	SELECT COUNT(*)
	INTO P_CNT
	FROM PDATA.SYS_PARA
	WHERE FUN_CLASS ='PRDREL'
	AND PARA_TYPE = 'JOB_ID'
	AND PARA_ID =1
	AND PARA_VAL1 =  P_TODAY;
	IF (P_CNT = 0) THEN
		UPDATE  PDATA.SYS_PARA
		SET PARA_VAL1=  P_TODAY
		        ,PARA_VAL2= P_PARA_VAL2
		WHERE  FUN_CLASS ='PRDREL'
		AND PARA_TYPE = 'JOB_ID'
		AND PARA_ID =1;
	ELSE
		SELECT PARA_VAL2
		INTO P_PARA_VAL2
		FROM PDATA.SYS_PARA
		WHERE FUN_CLASS ='PRDREL'
		AND PARA_TYPE = 'JOB_ID'
		AND PARA_ID =1;
		SET P_PARA_VAL2 = CAST(CAST(P_PARA_VAL2 AS INTEGER) +1 AS VARCHAR(10));
		SET P_PARA_VAL2 = SUBSTRING('0000',1, 4-CASE WHEN LENGTH(P_PARA_VAL2)<=4 THEN   LENGTH(P_PARA_VAL2) ELSE 0 END ) + P_PARA_VAL2;	
		UPDATE  PDATA.SYS_PARA
		SET PARA_VAL2=  P_PARA_VAL2 
		WHERE  FUN_CLASS ='PRDREL'
		AND PARA_TYPE = 'JOB_ID'
		AND PARA_ID =1;
	END IF ;
  SET SQLSTR = ' CREATE MULTISET VOLATILE TABLE #VT_PRDREL_JOB_ID_GET AS('
						  'SELECT ' + P_TODAY + P_PARA_VAL2 + 'AS JOB_ID' +
                          ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS; ';
  EXECUTE IMMEDIATE SQLSTR; 	
END SP;