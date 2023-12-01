package test.gp.translater;

import etec.sql.gp.translater.GreemPlumTranslater;

/**
 * @author	Tim
 * @since	2023年11月30日
 * @version	4.0.0.0
 * 
 * 測試 gp 的 SQLTranslater
 * 
 * */
public class TestSQLTranslater {
	public static String run() {
		String res = "";
		
		testAddMonths();
		//CASE3
		String q3 = "";
		String a3 = "";
		
		return res;
	}
	
	private static void testAddMonths() {
		//CASE1 : ADD_MONTHS
		String q1 = "ADD_MONTHS(a.RPT_DT,-1) = c.RPT_DT";
		String a1 = "CAST(a.RPT_DT-INTERVAL'1 MONTH' AS DATE) = c.RPT_DT";
		System.out.println("CASE 1 : "+a1.equals(GreemPlumTranslater.sql.easyReplase(q1)));
//		System.out.println("CASE 1 : "+GreemPlumTranslater.sql.easyReplase(q1));
		//CASE2 : ADD_MONTHS
		String q2 = "PAYMENT_DUE_DATE <=ADD_MONTHS(DATE'${TX4Y-M}-01',4) -1";
		String a2 = "PAYMENT_DUE_DATE <=CAST(DATE'${TX4Y-M}-01'+INTERVAL'4 MONTH' AS DATE) -1";
		System.out.println("CASE 2 : "+a2.equals(GreemPlumTranslater.sql.easyReplase(q2)));
//		System.out.println("CASE 2 : "+GreemPlumTranslater.sql.easyReplase(q2));
		//CASE3 : DATE_FORMAT
		String q3 = "CAST((A.AP_PAYM_PLAN_PAID_DT (FORMAT 'YYYY-MM')) as CHAR(7)) AS AP_PAYM_AMT_ORIG_PAID_MN,";
		String a3 = "CAST((TO_CHAR(A.AP_PAYM_PLAN_PAID_DT, 'YYYY-MM')) as CHAR(7)) AS AP_PAYM_AMT_ORIG_PAID_MN,";
		System.out.println("CASE 3 : "+a3.equals(GreemPlumTranslater.sql.easyReplase(q3)));
//		System.out.println("CASE 3 : "+a3);
//		System.out.println("CASE 3 : "+GreemPlumTranslater.sql.easyReplase(q3));
		//CASE4 : DATE_FORMAT
		String q4 = "PLAN_DATE (FORMAT 'YYYY-MM')(CHAR(7)) >= (SELECT MIN(PLAN_DATE)(FORMAT 'YYYY-MM')(CHAR(7)))";
		String a4 = "CAST(TO_CHAR(PLAN_DATE, 'YYYY-MM') AS CHAR(7)) >= (SELECT CAST(TO_CHAR(MIN(PLAN_DATE), 'YYYY-MM') AS CHAR(7)))";
		System.out.println("CASE 4 : "+a4.equals(GreemPlumTranslater.sql.easyReplase(q4)));
//		System.out.println("CASE 4 : "+a4);
//		System.out.println("CASE 4 : "+GreemPlumTranslater.sql.easyReplase(q4));
//		//CASE5 : ADD_MONTHS
//		String q5 = "";
//		String a5 = "";
//		System.out.println("CASE 5 : "+a5.equals(GreemPlumTranslater.sql.easyReplase(q5)));
//		//CASE6 : ADD_MONTHS
//		String q6 = "";
//		String a6 = "";
//		System.out.println("CASE 6 : "+a6.equals(GreemPlumTranslater.sql.easyReplase(q6)));
//		//CASE7 : ADD_MONTHS
//		String q7 = "";
//		String a7 = "";
//		System.out.println("CASE 7 : "+a7.equals(GreemPlumTranslater.sql.easyReplase(q7)));
//		//CASE8 : ADD_MONTHS
//		String q8 = "";
//		String a8 = "";
//		System.out.println("CASE 8 : "+a8.equals(GreemPlumTranslater.sql.easyReplase(q8)));
//		//CASE9 : ADD_MONTHS
//		String q9 = "";
//		String a9 = "";
//		System.out.println("CASE 9 : "+a9.equals(GreemPlumTranslater.sql.easyReplase(q9)));
//		//CASE10 : ADD_MONTHS
//		String q10 = "";
//		String a10 = "";
//		System.out.println("CASE 10 : "+a10.equals(GreemPlumTranslater.sql.easyReplase(q10)));
	}
	
}
