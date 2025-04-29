package test.az.fm;

import etec.src.translator.project.azure.fm.formal.service.BitFunctionService;

public class TestBitFunctionService {

	public static void run() {
		testReplaceBitAnd();
		testReplaceBitGenAggt();
		testReplaceBitOrAggt();
	}

	public static void testReplaceBitAnd() {
		String str = "SELECT\r\n"
			+ "  SUM(CASE WHEN C.TIME_ID=F.TIME_ID THEN COALESCE(BIT_EXTRACT(BIT_AND(C.STNUM_STORE_NUM,D.MASK)),0) ELSE 0 END) AS WORK_DAYS, -->稼動店數\r\n"
			+ "  SUM(CASE WHEN C.TIME_ID BETWEEN F.P14DT_ORD AND F.P1DT_ORD THEN BIT_EXTRACT(BIT_OR(C.STNUM_STORE_NUM,D.MASK)) ELSE 0 END) AS P14_WORK_DAYS -->稼動店數\r\n"
			+ "FROM  ${MART}.BASIC_MAST_FACT C \r\n"
		;
//		String test = BitFunctionService.replaceBitAnd(str);
//		System.out.println(test);
	}

	public static void testReplaceBitGenAggt() {
		String str = "insert into ${MART}.st_basic_mast_fact(l_day_id,mast_store_num)\r\n"
				+ "select l_day_id,bit_gen_aggt(B.ostore_bit_seq(decimal)) as mast_store_num \r\n"
				+ "from ${MART}.mast_detail A, ${MART}.org_bit_mapping B\r\n"
				+ "where A.ostore_id = B.ostore_id and A.mast_stnum = 1\r\n"
				+ "group by l_day_id;"
		;
		String test = BitFunctionService.replaceBitGenAggt(str);
		System.out.println(test);
	}

	public static void testReplaceBitOrAggt() {
		String str = "insert into ${MART}.st_remd_fact(l_day_id,stnum_store_num,date_type)\r\n"
				+ "select\r\n"
				+ " b.l_month_id as l_day_id\r\n"
				+ ",bit_or_aggt(a.stnum_store_num) as stnum_store_num\r\n"
				+ ",'m'\r\n"
				+ "from ${MART}.st_remd_fact a, ${MART}.ymwd_time b\r\n"
				+ "where a.l_day_id=b.l_day_id\r\n"
				+ "and a.date_type ='d'\r\n"
				+ "group by b.l_month_id;"
		;
		String test = BitFunctionService.replaceBitGenAggt(str);
		System.out.println(test);
	}
}
