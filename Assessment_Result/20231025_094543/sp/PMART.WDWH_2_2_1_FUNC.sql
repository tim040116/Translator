REPLACE PROCEDURE PMART.WDWH_2_2_1_FUNC
(
	I_ORG_LEVEL VARCHAR(100),
	I_ORG_ID VARCHAR(100),
	I_PRD_LEVEL VARCHAR(100),
	I_PRD_ID VARCHAR(100),
	I_EMP_NO VARCHAR(100)
)
SQL SECURITY INVOKER
SP:BEGIN
	DECLARE SQLSTR VARCHAR(10000);
	DECLARE V_SQLSTR VARCHAR(10000);
	DECLARE V_EMP_NO VARCHAR(100);
	CALL PMART.P_DROP_TABLE ('#VT_WDWH_2_2_1_FUNC');
	IF EXISTS(SELECT 1 FROM PDATA.WDWH_IMP_PRD_SET WHERE EMP_NO = I_EMP_NO) THEN
		SET V_EMP_NO = I_EMP_NO;
	ELSE
		SET V_EMP_NO = 'SYSTEM';
	END IF;
	IF I_ORG_LEVEL = '4' AND I_PRD_LEVEL = '0' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.OST_NO = '+ I_ORG_ID +' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '4' AND I_PRD_LEVEL = '1' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.KND_NO = '''+ I_PRD_ID +''' '
									+ 'AND S1.OST_NO = '+ I_ORG_ID +' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '4' AND I_PRD_LEVEL = '2' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.GRP_NO = '''+ I_PRD_ID +''' '
									+ 'AND S1.OST_NO = '+ I_ORG_ID +' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '4' AND I_PRD_LEVEL = '3' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
									+ 'AND S1.OST_NO = '+ I_ORG_ID +' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '3' AND I_PRD_LEVEL = '3' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
									+ 'AND S1.SREMP_NO = '''+ I_ORG_ID +''' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '2' AND I_PRD_LEVEL = '3' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
									+ 'AND S1.BRANCH_ID = '''+ I_ORG_ID +''' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '1' AND I_PRD_LEVEL = '3' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
									+ 'AND S1.DEPT_ID = '''+ I_ORG_ID +''' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	ELSEIF I_ORG_LEVEL = '0' AND I_PRD_LEVEL = '3' THEN
		IF I_ORG_ID = '-1' THEN
			SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
										+ 'S1.PRD_ID AS V_PRD_ID, '
										+ 'S1.SAL_PSD AS V_SAL_PSD, '
										+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
										+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
										+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
										+ 'WHERE S1.GRP_NO = PS.GRP_NO '
										+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
										+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
		ELSE
			SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
										+ 'S1.PRD_ID AS V_PRD_ID, '
										+ 'S1.SAL_PSD AS V_SAL_PSD, '
										+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
										+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
										+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
										+ 'WHERE S1.GRP_NO = PS.GRP_NO '
										+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
										+ 'AND S1.PDEPT_ID = '''+ I_ORG_ID +''' '
										+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
		END IF;
	ELSEIF I_ORG_LEVEL = '-1' AND I_PRD_LEVEL = '3' THEN
		SET V_SQLSTR =  'SELECT S1.OST_NO AS V_OST_NO, '
									+ 'S1.PRD_ID AS V_PRD_ID, '
									+ 'S1.SAL_PSD AS V_SAL_PSD, '
									+ 'ROUND(S1.STK_STD, 0) V_STK_STD, '
									+ 'S1.STK_CNT_INST AS V_STK_CNT_INST '
									+ 'FROM PMART.WDWH_STK_INST_STD S1, PDATA.WDWH_IMP_PRD_SET PS '
									+ 'WHERE S1.GRP_NO = PS.GRP_NO '
									+ 'AND S1.PRD_ID = '''+ I_PRD_ID +''' '
									+ 'AND PS.EMP_NO = '''+ V_EMP_NO +''' ';
	END IF;
	SET SQLSTR = 'CREATE MULTISET VOLATILE TABLE #VT_WDWH_2_2_1_FUNC  AS( ' 
						+ V_SQLSTR
						+ ' ) WITH DATA PRIMARY  CHARINDEX( V_PRD_ID,V_OST_NO) ON COMMIT PRESERVE ROWS;'; 
	EXECUTE IMMEDIATE SQLSTR;  
END SP;