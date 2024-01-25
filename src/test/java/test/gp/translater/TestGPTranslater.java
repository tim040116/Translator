package test.gp.translater;

import etec.common.exception.UnknowSQLTypeException;
import etec.src.sql.gp.translater.GreemPlumTranslater;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * 測試 gp 的 SQLTranslater
 * 
 * */
public class TestGPTranslater {
	public static String run() throws UnknowSQLTypeException {
		String res = "";
		
		testAddMonths();
		
		return res;
	}
	
	private static void testAddMonths() throws UnknowSQLTypeException {
//CASE1 : ADD_MONTHS
		String q1 = "ADD_MONTHS(a.RPT_DT,-1) = c.RPT_DT";
		String a1 = "CAST(a.RPT_DT-INTERVAL'1 MONTH' AS DATE) = c.RPT_DT";
		String r1 = GreemPlumTranslater.sql.easyReplase(q1);
		if(!a1.equals(r1))
		System.out.println(r1);
		System.out.println("CASE  1 : "+a1.equals(GreemPlumTranslater.sql.easyReplase(q1)));
//CASE2 : ADD_MONTHS
		String q2 = "PAYMENT_DUE_DATE <=ADD_MONTHS(DATE'${TX4Y-M}-01',4) -1";
		String a2 = "PAYMENT_DUE_DATE <=CAST('${TX4Y-M}-01' AS DATE)+INTERVAL'4 MONTH' -1";
		String r2 = GreemPlumTranslater.sql.easyReplase(q2);
		if(!a2.equals(r2))
		System.out.println(r2);
		System.out.println("CASE  2 : "+a2.equals(GreemPlumTranslater.sql.easyReplase(q2)));
//CASE3 : DATE_FORMAT
		String q3 = "CAST((A.AP_PAYM_PLAN_PAID_DT (FORMAT 'YYYY-MM')) as CHAR(7)) AS AP_PAYM_AMT_ORIG_PAID_MN,";
		String a3 = "CAST((TO_DATE(A.AP_PAYM_PLAN_PAID_DT, 'YYYY-MM')) as CHAR(7)) AS AP_PAYM_AMT_ORIG_PAID_MN,";
		String r3 = GreemPlumTranslater.sql.easyReplase(q3);
		if(!a3.equals(r3))
		System.out.println(r3);
		System.out.println("CASE  3 : "+a3.equals(GreemPlumTranslater.sql.easyReplase(q3)));
//CASE4 : DATE_FORMAT
		String q4 = "PLAN_DATE (FORMAT 'YYYY-MM')(CHAR(7)) >= (SELECT MIN(PLAN_DATE)(FORMAT 'YYYY-MM')(CHAR(7)))";
		String a4 = "TO_CHAR(PLAN_DATE, 'YYYY-MM') >= (SELECT TO_CHAR(MIN(PLAN_DATE), 'YYYY-MM'))";
		String r4 = GreemPlumTranslater.sql.easyReplase(q4);
		if(!a4.equals(r4))
		System.out.println(r4);
		System.out.println("CASE  4 : "+a4.equals(GreemPlumTranslater.sql.easyReplase(q4)));
//CASE5 : DATE_FORMAT
		String q5 = "A.PLAN_PAY_DATE BETWEEN CAST(CAST(CAST(CAST(CREATE_NO AS DATE) AS FORMAT 'YYYY-MM') AS VARCHAR(7))||'-01' AS DATE) AND CAST(CREATE_NO AS DATE)-91";
		String a5 = "A.PLAN_PAY_DATE BETWEEN CAST(TO_CHAR(CREATE_NO,'YYYY-MM')||'-01' AS DATE) AND CAST(CREATE_NO AS DATE)-91";
		String r5 = GreemPlumTranslater.sql.easyReplase(q5);
		if(!a5.equals(r5))
		System.out.println(r5);
		System.out.println("CASE  5 : "+a5.equals(r5));
//CASE6 : DATE_FORMAT
		String q6 = "SUBSTR(CAST(CAST(CLNDR_DT AS DATE FORMAT 'YYYYMMDD')+1 AS DATE FORMAT 'YYYY-MM-DD'),9,2)=01";
		String a6 = "TO_CHAR(CAST(CLNDR_DT AS DATE)+1,'DD')=01";
		String r6 = GreemPlumTranslater.sql.easyReplase(q6);
		if(!a6.equals(r6))
		System.out.println(r6);
		System.out.println("CASE  6 : "+a6.equals(r6)); 
////CASE7 : ADD_MONTHS DATE_FORMAT
		String q7 = "SUBSTR(CAST(DATE_ID AS DATE FORMAT 'YYYY-MM-DD'),1,7)\r\n" + 
				"=\r\n" + 
				"SUBSTR(ADD_MONTHS(CAST('${TXDATE}' AS DATE FORMAT 'YYYY-MM-DD'),-1),1,4)\r\n" + 
				"||'-'||\r\n" + 
				"SUBSTR(ADD_MONTHS(CAST('${TXDATE}' AS DATE FORMAT 'YYYY-MM-DD'),-1),6,2)";
		String a7 = "TO_CHAR(DATE_ID,'YYYY-MM')\r\n" + 
				"=\r\n" + 
				"TO_CHAR(CAST('${TXDATE}' AS DATE)-INTERVAL'1 MONTH','YYYY')\r\n" + 
				"||'-'||\r\n" + 
				"TO_CHAR(CAST('${TXDATE}' AS DATE)-INTERVAL'1 MONTH','MM')";
		String r7 = GreemPlumTranslater.sql.easyReplase(q7);
		if(!a7.equals(r7))
		System.out.println(r7);
		System.out.println("CASE  7 : "+a7.equals(r7));
//CASE8 : DATE_FORMAT
		String q8 = "SUBSTR(CAST(A.JURNL_ENTRY_POST_DT AS DATE FORMAT 'YYYY/MM/DD'),6,2)";	
		String a8 = "TO_CHAR(A.JURNL_ENTRY_POST_DT,'MM')";
		String r8 = GreemPlumTranslater.sql.easyReplase(q8);
		if(!a8.equals(r8))
		System.out.println(r8);
		System.out.println("CASE  8 : "+a8.equals(r8));
//CASE9 : ADD_MONTHS
		String q9 = "CAST(A.AP_PAYM_AMT_ORIG_PAID_DT AS DATE FORMAT 'YYYY-MM-DD')(FORMAT 'YYYY-MM')(CHAR(7))";
		String a9 = "TO_CHAR(A.AP_PAYM_AMT_ORIG_PAID_DT,'YYYY-MM')";
		String r9 = GreemPlumTranslater.sql.easyReplase(q9);
		if(!a9.equals(r9))
		System.out.println(r9);
		System.out.println("CASE  9 : "+a9.equals(r9));
//CASE10 : ADD_MONTHS
		String q10 = "ADD_MONTHS(\r\n" + 
				"	CAST(\r\n" + 
				"		CAST(AP_PAYM_AMT_ORIG_DUE_DT AS DATE FORMAT 'YYYY-MM-DD')\r\n" + 
				"		-EXTRACT(DAY FROM CAST(AP_PAYM_AMT_ORIG_DUE_DT AS DATE FORMAT 'YYYY-MM-DD'))\r\n" + 
				"		+1\r\n" + 
				"		AS DATE FORMAT 'YYYY-MM-DD'\r\n" + 
				"	)\r\n" + 
				"	,1\r\n" + 
				")";
		String a10 = "TO_DATE(\r\n" + 
				"	CAST(\r\n" + 
				"		TO_DATE(AP_PAYM_AMT_ORIG_DUE_DT ,'YYYY-MM-DD')\r\n" + 
				"		-EXTRACT(DAY FROM TO_DATE(AP_PAYM_AMT_ORIG_DUE_DT ,'YYYY-MM-DD'))\r\n" + 
				"		+1\r\n" + 
				"		AS DATE)+INTERVAL'1 MONTH' ,'YYYY-MM-DD')";
		String r10 = GreemPlumTranslater.sql.easyReplase(q10);
		if(!a10.equals(r10))
		System.out.println(r10);
		System.out.println("CASE 10 : "+a10.equals(r10));
//CASE11 : 
		String q11 = "SUBSTR(CAST(PLAN_DUE_DT_CB AS DATE FORMAT 'YYYY-MM-DD'),1,4)\r\n" + 
				"|| '-' ||\r\n" + 
				"SUBSTR(ADD_MONTHS(CAST(PLAN_DUE_DT_CB AS DATE FORMAT 'YYYY-MM-DD'),1),6,2)";
		String a11 = "TO_CHAR(PLAN_DUE_DT_CB,'YYYY')\r\n" + 
				"|| '-' ||\r\n" + 
				"TO_CHAR(CAST(PLAN_DUE_DT_CB AS DATE)+INTERVAL'1 MONTH','MM')";
		String r11 = GreemPlumTranslater.sql.easyReplase(q11);
		if(!a11.equals(r11))
		System.out.println(r11);
		System.out.println("CASE 11 : "+a11.equals(r11));
//CASE12 : 
		String q12 = "WHERE CLNDR_DT BETWEEN \r\n" + 
				"SUBSTR(CAST(CAST('${TXDATE}' AS DATE)- 20 AS FORMAT 'YYYY-MM-DD'),1,7)||'-01' \r\n" + 
				"AND\r\n" + 
				"SUBSTR(CAST(LAST_DAY(CAST('${TXDATE}' AS DATE)-1) AS FORMAT 'YYYY-MM-DD'),1,10)";
		String a12 = "WHERE CLNDR_DT BETWEEN \r\n"
				+ "TO_CHAR(CAST('${TXDATE}' AS DATE)- 20,'YYYY-MM')||'-01' \r\n"
				+ "AND\r\n"
				+ "TO_CHAR(DATE_TRUNC('Month',CAST('${TXDATE}' AS DATE)-1)+INTERVAL'1MONTH'-INTERVAL'1DAY','YYYY-MM-DD')";
		String r12 = GreemPlumTranslater.sql.easyReplase(q12);
		if(!a12.equals(r12))
		System.out.println(r12);
		System.out.println("CASE 12 : "+a12.equals(r12));
//CASE13 : (人工)DATE 乘法 
//		String q13 = ",TRIM(A.CLNDR_DT /100+190000) AS CLNDR_MN";
//		String a13 = "TO_CHAR(A.ACLNDR_DT, 'YYYYMM') AS CLNDR_MN";
//		String r13 = GreemPlumTranslater.sql.easyReplase(q13);
//		if(!a13.equals(r13))
//		System.out.println(r13);
//		System.out.println("CASE 13 : "+a13.equals(r13));
//CASE14 : (人工)DATE 除法
//		String q14 = "A.CLNDR_DT >= CAST(ADD_MONTHS( DATE '${LASTTXDATE}',-1)AS INTERGER)/100*100+1";
//		String a14 = "A.CLNDR_DT >= CAST(TO_CHAR( DATE'${LASTTXDATE}'- INTERVAL'1 MONTH','YYYY-MM')||'-01' AS DATE)";
//		String r14 = GreemPlumTranslater.sql.easyReplase(q14);
//		if(!a14.equals(r14))
//		System.out.println(r14);
//		System.out.println("CASE 14 : "+a14.equals(r14));
//CASE15 : DATE FORMAT
		String q15 = ",CAST(CLNDR_MN AS DATE FORMAT 'YYYYMM') AS CLNDR_MN";
		String a15 = ",TO_DATE(CLNDR_MN ,'YYYYMM') AS CLNDR_MN";
		String r15 = GreemPlumTranslater.sql.easyReplase(q15);
		if(!a15.equals(r15))
		System.out.println(r15);
		System.out.println("CASE 15 : "+a15.equals(r15));

//CASE16 : Qualify
/*

SELECT DISTINCT
	TRIM( LEADING '0' FROM VENDORCODE) AS VENDORCODE ,GROUPCODE,GROUPNAME
FROM PSTAGE_CN.DW_VW_VENDORGROUP_EDW
QUALIFY ROW_NUMBER()OVER(
	PARTITION BY NEWVENDORCODE,VENDORCODE,GROUPCODE,GROUPNAME
	ORDER BY NEWVENDORCODE,VENDORCODE,GROUPCODE
)=1

*/
		String q16 = "SELECT DISTINCT\r\n"
				+ "	TRIM( LEADING '0' FROM VENDORCODE) AS VENDORCODE ,GROUPCODE,GROUPNAME\r\n"
				+ "FROM PSTAGE_CN.DW_VW_VENDORGROUP_EDW\r\n"
				+ "QUALIFY ROW_NUMBER()OVER(\r\n"
				+ "	PARTITION BY NEWVENDORCODE,VENDORCODE,GROUPCODE,GROUPNAME\r\n"
				+ "	ORDER BY NEWVENDORCODE,VENDORCODE,GROUPCODE\r\n"
				+ ")=1";
		String a16 = "SELECT DISTINCT\r\n" + 
				"	VENDORCODE ,GROUPCODE,GROUPNAME\r\n" + 
				"FROM ( SELECT\r\n" + 
				"	TRIM( LEADING '0' FROM VENDORCODE) AS VENDORCODE ,GROUPCODE,GROUPNAME\r\n" + 
				"	,ROW_NUMBER()OVER(\r\n" + 
				"	PARTITION BY NEWVENDORCODE,VENDORCODE,GROUPCODE,GROUPNAME\r\n" + 
				"	ORDER BY NEWVENDORCODE,VENDORCODE,GROUPCODE\r\n" + 
				") AS ROW_NUMBER\r\n" + 
				"	FROM PSTAGE_CN.DW_VW_VENDORGROUP_EDW\r\n" + 
				" ) tmp_qrn \r\n" + 
				" where tmp_qrn.ROW_NUMBER =1";
		String r16 = GreemPlumTranslater.dql.changeQualifaRank(q16);
		if(!a16.equals(r16))
		System.out.println(r16);
		System.out.println("CASE 16 : "+a16.equals(r16));
//CASE17 : 
//		String q17 = "";
//		String a17 = "";
//		System.out.println("CASE 17 : "+a17.equals(GreemPlumTranslater.sql.easyReplase(q17)));
//CASE18 : 
//		String q18 = "";
//		String a18 = "";
//		System.out.println("CASE 18 : "+a18.equals(GreemPlumTranslater.sql.easyReplase(q18)));
//CASE19 : 
//		String q19 = "";
//		String a19 = "";
//		System.out.println("CASE 19 : "+a19.equals(GreemPlumTranslater.sql.easyReplase(q19)));
//CASE20 : 
/*
INSERT INTO PTEMP.RPT_EMS_FE_EQP_TACTTIME_TMP1_TT6
SELECT	PLANT_LOC_ID
		,FAB_CODE
		,PLAN_VER_CD
		,MFG_MN
		,CLNDR_MN
		,R12_PROD_ID
		,CAST('' AS VARCHAR(20)) AS ERP_ITEM_ID
		,ITEM_ID
		,DATA_TYPE_CD
		,EQUIP_USG_GRP_ID
		,BN_RANK_NUM
		,MAJOR_FCTR_MEAS
		,MAJOR_ECTR_RW_MEAS
FROM(
	SEL	DISTINCT 
		PLANT_LOC_ID
		,FAB_CODE
		,PLAN_VER_CD
		,MFG_MN
		,CLNDR_MN
		,R12_PROD_ID
		,ITEM_ID
		,DATA_TYPE_CD
		,EQUIP_USG_GRP_ID
		,BN_RANK_NUM
		,MAJOR_FCTR_MEAS
		,MAJOR_ECTR_RW_MEAS
	FROM PDATA.FCT_FCST__TFT_M12_LN A
) A
QUALIFY ROW_NUMBER() OVER
(PARTITION BY PLANT_LOC_ID,MFG_MN,CLNDR_MN,R12_PROD_ID,EQUIP_USG_GRP_ID,ITEM_ID,BN_RANK_NUM ORDER BY PLAN_VER_CD DESC) = 1
 */
		String q20 = "INSERT INTO PTEMP.RPT_EMS_FE_EQP_TACTTIME_TMP1_TT6\r\n" + 
				"SELECT	PLANT_LOC_ID\r\n" + 
				"		,FAB_CODE\r\n" + 
				"		,PLAN_VER_CD\r\n" + 
				"		,MFG_MN\r\n" + 
				"		,CLNDR_MN\r\n" + 
				"		,R12_PROD_ID\r\n" + 
				"		,CAST('' AS VARCHAR(20)) AS ERP_ITEM_ID\r\n" + 
				"		,ITEM_ID\r\n" + 
				"		,DATA_TYPE_CD\r\n" + 
				"		,EQUIP_USG_GRP_ID\r\n" + 
				"		,BN_RANK_NUM\r\n" + 
				"		,MAJOR_FCTR_MEAS\r\n" + 
				"		,MAJOR_ECTR_RW_MEAS\r\n" + 
				"FROM(\r\n" + 
				"	SEL	DISTINCT \r\n" + 
				"		PLANT_LOC_ID\r\n" + 
				"		,FAB_CODE\r\n" + 
				"		,PLAN_VER_CD\r\n" + 
				"		,MFG_MN\r\n" + 
				"		,CLNDR_MN\r\n" + 
				"		,R12_PROD_ID\r\n" + 
				"		,ITEM_ID\r\n" + 
				"		,DATA_TYPE_CD\r\n" + 
				"		,EQUIP_USG_GRP_ID\r\n" + 
				"		,BN_RANK_NUM\r\n" + 
				"		,MAJOR_FCTR_MEAS\r\n" + 
				"		,MAJOR_ECTR_RW_MEAS\r\n" + 
				"	FROM PDATA.FCT_FCST__TFT_M12_LN A\r\n" + 
				") A\r\n" + 
				"QUALIFY ROW_NUMBER() OVER\r\n" + 
				"(PARTITION BY PLANT_LOC_ID,MFG_MN,CLNDR_MN,R12_PROD_ID,EQUIP_USG_GRP_ID,ITEM_ID,BN_RANK_NUM ORDER BY PLAN_VER_CD DESC) = 1";
		String a20 = "";
		String r20 = GreemPlumTranslater.dml.changeInsertSelect(q20);
		if(!a20.equals(r20))
		System.out.println(r20);
		System.out.println("CASE 20 : "+a20.equals(r20));
//CASE21 : 
//		String q21 = "";
//		String a21 = "";
//		System.out.println("CASE 21 : "+a21.equals(GreemPlumTranslater.sql.easyReplase(q21)));
//CASE22 : 
//		String q22 = "";
//		String a22 = "";
//		System.out.println("CASE 22 : "+a22.equals(GreemPlumTranslater.sql.easyReplase(q22)));
//CASE23 : 
		String q23 = "CASE WHEN A.SUBINV_LOC_ID LIKE ANY ('%RG','%RC','%SP')";
		String a23 = "CASE WHEN A.SUBINV_LOC_ID LIKE ANY (ARRAY['%RG','%RC','%SP'])";
		System.out.println("CASE 23 : "+a23.equals(GreemPlumTranslater.sql.easyReplase(q23)));
//CASE24 : 
		String q24 = "SUBSTR(A.MAIN_EXPN_DESC,0,INDEX(A.MAIN_EXPN_DESC,'-'))";
		String a24 = "SUBSTR(A.MAIN_EXPN_DESC,0,POSITION('-' IN A.MAIN_EXPN_DESC))";
		String r24 = GreemPlumTranslater.sql.easyReplase(q24);
//		System.out.println(r24);
		System.out.println("CASE 24 : "+a24.equals(r24));
//CASE25 : (人工) 不同格式間的比較
//		String q25 = "";
//		String a25 = "";
//		System.out.println("CASE 25 : "+a25.equals(GreemPlumTranslater.sql.easyReplase(q25)));
//CASE26 : 
		String q26 = "ZEROIFNULL (A.PLAN_ETD_QTY) AS X.PLAN_QTY";
		String a26 = "COALESCE(A.PLAN_ETD_QTY,0) AS X.PLAN_QTY";
		System.out.println("CASE 26 : "+a26.equals(GreemPlumTranslater.sql.easyReplase(q26)));
//CASE27 : (人工)不同格式間的比較
//		String q27 = "";
//		String a27 = "";
//		System.out.println("CASE 27 : "+a27.equals(GreemPlumTranslater.sql.easyReplase(q27)));
//CASE28 : SEL DATE_FORMAT 
		String q28 = "SEL CAST('${TXDATE}' AS DATE FORMAT 'YYYY-MM-DD') AS SNPSHT_DT";
		String a28 = "SELECT TO_DATE('${TXDATE}' ,'YYYY-MM-DD') AS SNPSHT_DT";
		String r28 = GreemPlumTranslater.sql.easyReplase(q28);
		if(!a28.equals(r28))
		System.out.println(r28);
		System.out.println("CASE 28 : "+a28.equals(r28));
//CASE29 : 
		String q29 = "REPLACE VIEW ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC AS\r\n" + 
				"LOCKING TABLE ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC FOR ACCESS\r\n" + 
				"SELECT A.*, '比例' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC A\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT B.*, '金額' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC B";
		String a29 = "DROP VIEW IF EXISTS ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC;\r\n" + 
				"CREATE VIEW ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC AS\r\n" + 
				"/*LOCKING TABLE ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC FOR ACCESS*/\r\n" + 
				"SELECT A.*, '比例' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC A\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT B.*, '金額' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC B";
		String r29 = GreemPlumTranslater.ddl.changeReplaceView(q29);
		r29 = GreemPlumTranslater.sql.easyReplase(r29);
		r29 = GreemPlumTranslater.other.changeLockingTable(r29);
//		System.out.println(r29);
		System.out.println("CASE 29 : "+a29.equals(r29));
//CASE30 : CURRENT_DATE
		String q30 = "AND CLNDR_DT BETWEEN DATE -125 AND DATE"
				+ "\r\nOR CLNDR_DT =DATE-1";
		String a30 = "AND CLNDR_DT BETWEEN CURRENT_DATE -125 AND CURRENT_DATE\r\n" + 
				"OR CLNDR_DT =CURRENT_DATE-1";
		String r30 = GreemPlumTranslater.sql.easyReplase(q30);
		if(!a30.equals(r30))
		System.out.println(r30);
		System.out.println("CASE 30 : "+a30.equals(r30));
//CASE31 : 
/*
REPLACE VIEW ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC AS
LOCKING TABLE ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC FOR ACCESS
SELECT A.*, '比例' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC A
UNION ALL
SELECT B.*, '金額' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC B
 */
		String q31 = "REPLACE VIEW ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC AS\r\n" + 
				"LOCKING TABLE ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC FOR ACCESS\r\n" + 
				"SELECT A.*, '比例' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC A\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT B.*, '金額' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC B";
		String a31 = "DROP VIEW IF EXISTS ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC;\r\n" + 
				"CREATE VIEW ${VIEW_NCMO_DB}.RPT_EXP_EXPN_JE_LN_ALC AS\r\n" + 
				"/*LOCKING TABLE ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC FOR ACCESS*/\r\n" + 
				"SELECT A.*, '比例' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC A\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT B.*, '金額' AS RM_FLAG FROM ${BIMART_DB}.BI_FM_EXPN_JE_LN_ALC B";
		String r31 = GreemPlumTranslater.sql.easyReplase(q31);
		r31 = GreemPlumTranslater.other.easyReplase(r31);
		r31 = GreemPlumTranslater.ddl.changeReplaceView(r31);
		if(!a31.equals(r31))
		System.out.println(r31);
		System.out.println("CASE 31 : "+a31.equals(r31));
//CASE32 : 
		String q32 = "COLLECT STATISTICS ON ${BIMART_DB}.BI_ACTUAL_IMP_SUM_TW;\r\n" + 
				"INDEX ( PLANT_LOC_ID,MFG_DT,MFG_SHIFT_ID,MFG_ORD_ID);";
		String a32 = "ANALYZE ${BIMART_DB}.BI_ACTUAL_IMP_SUM_TW;\r\n" + 
				"/*INDEX ( PLANT_LOC_ID,MFG_DT,MFG_SHIFT_ID,MFG_ORD_ID);*/";
		String r32 = GreemPlumTranslater.other.easyReplase(q32);
		if(!a32.equals(r32))
		System.out.println(r32);
		System.out.println("CASE 32 : "+a32.equals(r32));
//CASE33 : 
		String q33 = "CASE WHEN CHARACTERS(ORG)>3 THEN SUBSTR(ORG,1,3) ELSE ORG END AS ORG";
		String a33 = "CASE WHEN LENGTH(ORG)>3 THEN SUBSTR(ORG,1,3) ELSE ORG END AS ORG";
		String r33 = GreemPlumTranslater.sql.easyReplase(q33);
		if(!a33.equals(r33))
		System.out.println(r33);
		System.out.println("CASE 33 : "+a33.equals(r33));
//CASE34 : 
		String q34 = "CAST(CAST(CAST(AP1.PAY_DATE AS INTEGER) AS FORMAT '99') AS VARCHAR(2))";
		String a34 = "CAST(TO_CHAR(CAST(AP1.PAY_DATE AS INTEGER),'00') AS VARCHAR(2))";
		String r34 = GreemPlumTranslater.sql.easyReplase(q34);
		if(!a34.equals(r34))
		System.out.println(r34);
		System.out.println("CASE 34 : "+a34.equals(GreemPlumTranslater.sql.easyReplase(q34)));
//CASE35 : LIKE ANY FORMAT 
		String q35 = "TRIM(LEADING '0' FROM CAST(CAST(CAST(s.ORD_NUM AS FLOAT) AS FORMAT '9(12)') AS VARCHAR(12)))\r\n" + 
				"LIKE ANY ('1%' , '7%' , '3%' )";
		String a35 = "TRIM(LEADING '0' FROM CAST(TO_CHAR(CAST(s.ORD_NUM AS FLOAT),'000000000000') AS VARCHAR(12)))\r\n" + 
				"LIKE ANY (ARRAY['1%' , '7%' , '3%'])";
		String r35 = GreemPlumTranslater.sql.easyReplase(q35);
		if(!a35.equals(r35))
		System.out.println(r35);
		System.out.println("CASE 35 : "+a35.equals(r35));
//CASE36 : (人工)
//		String q36 = "";
//		String a36 = "";
//		System.out.println("CASE 36 : "+a36.equals(GreemPlumTranslater.sql.easyReplase(q36)));
//CASE37 : 
		String q37 = "A.PLANT_TYPE_CD = 'TFT' AND COALESCE(PROC_CATG_CD,'') IN 'TOD','DDD','AAA'";
		String a37 = "A.PLANT_TYPE_CD = 'TFT' AND COALESCE(PROC_CATG_CD,'') IN ('TOD','DDD','AAA')";
		String r37 = GreemPlumTranslater.sql.easyReplase(q37);
//		System.out.println(r37);
		System.out.println("CASE 37 : "+a37.equals(r37));
//CASE38 : (人工)
//		String q38 = "";
//		String a38 = "";
//		System.out.println("CASE 38 : "+a38.equals(GreemPlumTranslater.sql.easyReplase(q38)));
//CASE39 : CASECADE
		String q39 = "DROP TABLE tab_name;";
		String a39 = "DROP TABLE IF EXISTS tab_name CASCADE;";
		String r39 = GreemPlumTranslater.ddl.changeDropTableIfExist(q39);
		if(!a39.equals(r39))
		System.out.println(r39);
		System.out.println("CASE 39 : "+a39.equals(r39));
//CASE40 : (人工)
//		String q40 = "";
//		String a40 = "";
//		System.out.println("CASE 40 : "+a40.equals(GreemPlumTranslater.sql.easyReplase(q40)));
//CASE41 : 
//		String q41 = "";
//		String a41 = "";
//		System.out.println("CASE 41 : "+a41.equals(GreemPlumTranslater.sql.easyReplase(q41)));
//CASE42 : NULLIFZERO
		String q42 = "NULLIFZERO(SUM( CASE WHEN LCM_LAM_GRD_WYLD BETWEEN 0 AND 1 THEN LCM_GRD_QTY END) OVER (PARTITION BY PROD_TD,Y.APPLICATION_CD, FAB_LOC_ID /*Q.CMS_SYS*/,Y.AR_FAB ,PROD_CAT))";
		String a42 = "NULLIF(SUM( CASE WHEN LCM_LAM_GRD_WYLD BETWEEN 0 AND 1 THEN LCM_GRD_QTY END) OVER (PARTITION BY PROD_TD,Y.APPLICATION_CD, FAB_LOC_ID /*Q.CMS_SYS*/,Y.AR_FAB ,PROD_CAT),0)";
		String r42 = GreemPlumTranslater.sql.easyReplase(q42);
		if(!a42.equals(r42))
		System.out.println(r42);
		System.out.println("CASE 42 : "+a42.equals(r42));
//CASE43 : 
		String q43 = "SUBSTR(STRTOK(STRTOK(OREPLACE(OREPLACE(A.ITEM_ID,'TOD',''),'CELL',''),'_',1),'-',1),1,4)";
		String a43 = "SUBSTR(SPLIT_PART(SPLIT_PART(REPLACE(REPLACE(A.ITEM_ID,'TOD',''),'CELL',''),'_',1),'-',1),1,4)";
		System.out.println("CASE 43 : "+a43.equals(GreemPlumTranslater.sql.easyReplase(q43)));
//CASE44 : (人工)日期運算
//		String q44 = "";
//		String a44 = "";
//		System.out.println("CASE 44 : "+a44.equals(GreemPlumTranslater.sql.easyReplase(q44)));
//CASE45 : 
		String q45 = "CREATE TABLE ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL_NEW\r\n" + 
				"		  AS <>\r\n" + 
				"SELECT * FROM ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL<> WITH NO DATA";
		String a45 = "CREATE TABLE IF NOT EXISTS ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL_NEW\r\n" + 
				"(LIKE <>\r\n" + 
				"SELECT * FROM ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL<> )";
		String r45 = GreemPlumTranslater.ddl.changeCreateTableIfNotExist(q45);
//		System.out.println(r45);
		System.out.println("CASE 45 : "+a45.equals(r45));
//CASE46_1 : 
		String q46_1 = "DROP TABLE ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL\r\n;";
		String a46_1 = "DROP TABLE IF EXISTS ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL CASCADE;";
		String r46_1 = GreemPlumTranslater.ddl.changeDropTableIfExist(q46_1);
		if(!a46_1.equals(r46_1))
		System.out.println(r46_1);
		System.out.println("CASE 461: "+a46_1.equals(r46_1));
//CASE46_2 : 
		String q46_2 = "RENAME TABLE ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL_NEW\r\n" + 
				"		  TO ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL\r\n" + 
				";";
		String a46_2 = "ALTER TABLE ${MART_NCMO_DB}.BI_FM_EXPN_NON_OP_DTL_NEW\r\n" + 
				"RENAME TO BI_FM_EXPN_NON_OP_DTL;";
		String r46_2 = GreemPlumTranslater.ddl.changeRenameTable(q46_2);
//		System.out.println(r46_2);
		System.out.println("CASE 462: "+a46_2.equals(r46_2));
//CASE47 : 
		String q47 = "SELECT AAA\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT BBB\r\n" + 
				"		MINUS\r\n" + 
				"		SELECT (\r\n" + 
				"			SELECT * FROM CCC\r\n" + 
				"		) XXX\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT DDD";
		String a47 = "SELECT AAA\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT BBB\r\n" + 
				"		EXCEPT\r\n" + 
				"		SELECT (\r\n" + 
				"			SELECT * FROM CCC\r\n" + 
				"		) XXX\r\n" + 
				"	UNION ALL\r\n" + 
				"	SELECT DDD";
		System.out.println("CASE 47 : "+a47.equals(GreemPlumTranslater.sql.easyReplase(q47)));
//CASE48 : 
		String q48 = "CREATE VOLATILE TABLE vt_tmp AS aaa";
		String a48 = "CREATE temp TABLE vt_tmp AS aaa";
		System.out.println("CASE 48 : "+a48.equals(GreemPlumTranslater.ddl.changeCreateVolaTileTable(q48)));
//CASE49 : 
		String q49 = "CURR_ROWID	INTEGER GENERATED ALWAYS AS IDENTITY (CYCLE)";
		String a49 = "CURR_ROWID	SERIAL";
		System.out.println("CASE 49 : "+a49.equals(GreemPlumTranslater.ddl.changeIntegerGeneratedAlwaysAsIdentity(q49)));
//CASE50 : 
		String q50 = "DELETE FROM ${TEMP_DB}.LCD6 A\r\n" + 
				"		   ,${TEMP_DB}.LCD6 B\r\n" + 
				" WHERE A.LCD_LEVEL > B.LCD_LEVEL";
		String a50 = "DELETE FROM ${TEMP_DB}.LCD6 A\r\n" + 
				" USING ${TEMP_DB}.LCD6 B\r\n" + 
				" WHERE A.LCD_LEVEL > B.LCD_LEVEL";
		String r50 = GreemPlumTranslater.dml.changeDeleteTableUsing(q50);
		if(!a50.equals(r50))
		System.out.println(r50);
		System.out.println("CASE 50 : "+a50.equals(r50));
//CASE51 : 
//		String q51 = "";
//		String a51 = "";
//		System.out.println("CASE 51 : "+a51.equals(GreemPlumTranslater.sql.easyReplase(q51)));
//CASE52 : DROP TABLE
		String q52 = "DROP TABLE ${TEMP_DB}.BI_FM_EXPN_JE_LN_ALC_TP1\r\n" + 
				";\r\n" + 
				"\r\n" + 
				"CREATE TABLE ${TEMP_DB}.BI_FM_EXPN_JE_LN_ALC_TP1(\r\n" + 
				"	 REVSN_NUM						VARCHAR(20)\r\n" + 
				"	,BM_CNFM_CD						VARCHAR(6)\r\n" + 
				"	,CNTR_BU_DEPT_PARTY_ID			VARCHAR(50)\r\n" + 
				"	,CNTR_BU_DEPT_PARTY_NAME		VARCHAR(50)\r\n" + 
				"	,DVSN_GRP_DEPT_PARTY_ID			VARCHAR(50)\r\n" + 
				"	,ORIG_CNTR_BU_DEPT_PARTY_ID		VARCHAR(50) --20180320 一事件新增事業群\r\n" + 
				"	,ORIG_CNTR_BU_DEPT_PARTY_NAME	VARCHAR(50)\r\n" + 
				"	,NEW_BU_IND						VARCHAR(50)\r\n" + 
				"	,EXPN_TYPE_CD					VARCHAR(50)\r\n" + 
				"	,MFRS_TYPE_CD					VARCHAR(50)\r\n" + 
				"	,GLBL_CRNCY_AMT					DECIMAL(30,4)\r\n" + 
				")PRIMARY INDEX(REVSN_NUM,BM_CNFM_CD,CNTR_BU_DEPT_PARTY_ID,CNTR_BU_DEPT_PARTY_NAME,DVSN_GRP_DEPT_PARTY_ID)\r\n" + 
				";";
		String a52 = "DROP TABLE IF EXISTS ${TEMP_DB}.BI_FM_EXPN_JE_LN_ALC_TP1 CASCADE;\r\n" + 
				"\r\n" + 
				"CREATE TABLE ${TEMP_DB}.BI_FM_EXPN_JE_LN_ALC_TP1(\r\n" + 
				"	 REVSN_NUM						VARCHAR(20)\r\n" + 
				"	,BM_CNFM_CD						VARCHAR(6)\r\n" + 
				"	,CNTR_BU_DEPT_PARTY_ID			VARCHAR(50)\r\n" + 
				"	,CNTR_BU_DEPT_PARTY_NAME		VARCHAR(50)\r\n" + 
				"	,DVSN_GRP_DEPT_PARTY_ID			VARCHAR(50)\r\n" + 
				"	,ORIG_CNTR_BU_DEPT_PARTY_ID		VARCHAR(50) --20180320 一事件新增事業群\r\n" + 
				"	,ORIG_CNTR_BU_DEPT_PARTY_NAME	VARCHAR(50)\r\n" + 
				"	,NEW_BU_IND						VARCHAR(50)\r\n" + 
				"	,EXPN_TYPE_CD					VARCHAR(50)\r\n" + 
				"	,MFRS_TYPE_CD					VARCHAR(50)\r\n" + 
				"	,GLBL_CRNCY_AMT					DECIMAL(30,4)\r\n" + 
				")DISTRIBUTED BY (REVSN_NUM,BM_CNFM_CD,CNTR_BU_DEPT_PARTY_ID,CNTR_BU_DEPT_PARTY_NAME,DVSN_GRP_DEPT_PARTY_ID)\r\n" + 
				";";
		String r52 = GreemPlumTranslater.ddl.changeDropTableIfExist(q52);
		r52 = GreemPlumTranslater.ddl.changePrimaryIndex(r52);
		if(!a52.equals(r52))
		System.out.println(r52);
		System.out.println("CASE 52 : "+a52.equals(r52));
//CASE53 : 
//		String q53 = "";
//		String a53 = "";
//		System.out.println("CASE 53 : "+a53.equals(GreemPlumTranslater.sql.easyReplase(q53)));

	}
	
}
