package main;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class Main2 {

	static String txt = "\r\n"
			+ "	SELECT\r\n"
			+ "		CAST(a.l_day_id AS integer) AS time_id\r\n"
			+ "		,CAST(b.ostore_id AS integer) as org_id <ConvertRemarkSafely_dash_32>\r\n"
			+ "\r\n"
			+ "		,a.prd_id  as prd_id 			<ConvertRemarkSafely_dash_33>\r\n"
			+ "\r\n"
			+ "		,CAST(sum(a.order_mqty * a.order_base_qty) AS integer) AS order_cnt\r\n"
			+ "		,CAST(sum(a.order_mqty * a.order_base_qty * a.slunt) AS integer) AS order_amt\r\n"
			+ "		,C";

	
	public static void main(String[] args) {
		
		System.out.println(txt				.replaceAll("\r\n[\\t ]*\r\n","\r\n")
);
	}

}
