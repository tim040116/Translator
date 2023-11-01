REPLACE PROCEDURE PMART.B2BIGA_1_1_CPC_PRD_FUNC
(
	IN P_A_ID VARCHAR(20),
	IN P_CPC_ID VARCHAR(30) CHARACTER SET UNICODE CASESPECIFIC
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(20000) Collate Chinese_Taiwan_Stroke_CI_AS;
	CALL PMART.P_DROP_TABLE ('#VT_B2BIGA_1_1_CPC_PRD_FUNC');
	SET SQLSTR ='CREATE MULTISET VOLATILE TABLE #VT_B2BIGA_1_1_CPC_PRD_FUNC  AS( '
	+ ' SELECT DISTINCT X.FM_CODE AS CPC_PRD_ID, X.FM_CODE + '' '' + X.FM_NAME AS CPC_PRD '
	+ ' FROM PDATA.PBMCMDT X, PDATA.CPC_PROD Y '
	+ ' WHERE X.FM_CODE = Y.CMNO '
	+ ' AND Y.A_ID = '''+ P_A_ID +''' '
	+ ' AND Y.CPC_ID = '''+ P_CPC_ID +''' '
	+ ' ) WITH DATA NO PRIMARY INDEX ON COMMIT PRESERVE ROWS;' ; 
	EXECUTE IMMEDIATE SQLSTR;
END SP;