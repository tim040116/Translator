package test.gp.translater;

import etec.framework.convert_safely.ConvertSubQuerySafely;

public class TestConvertSafely {
	
	public static void run() {
		testConvertSubQuerySafely();
	}
	
	public static void testConvertSubQuerySafely() {
		String sample = "select \r\n" + 
				"	a0\r\n" + 
				"	,b0\r\n" + 
				"from (\r\n" + 
				"	select\r\n" + 
				"		a1\r\n" + 
				"		,b1\r\n" + 
				"	from (\r\n" + 
				"		select\r\n" + 
				"			a2\r\n" + 
				"			,b2\r\n" + 
				"		from c2\r\n" + 
				"		left join d2_1\r\n" + 
				"		  on a2 = a2\r\n" + 
				"		 and b2 = b2\r\n" + 
				"		inner join d2_2\r\n" + 
				"		  on a2 = a2\r\n" + 
				"		 and b2 = b2\r\n" + 
				"		union all\r\n" + 
				"		select\r\n" + 
				"			a3\r\n" + 
				"			,b3\r\n" + 
				"		from c3\r\n" + 
				"		left join d3_1\r\n" + 
				"		  on 1=1\r\n" + 
				"		left join d3_2\r\n" + 
				"		  on 1=1\r\n" + 
				"		union \r\n" + 
				"		select \r\n" + 
				"			a4\r\n" + 
				"			,b4\r\n" + 
				"		from c4\r\n" + 
				"	)\r\n" + 
				"	left join (\r\n" + 
				"		select *\r\n" + 
				"		from c5\r\n" + 
				"		left join (\r\n" + 
				"			select * from c6\r\n" + 
				"			union all\r\n" + 
				"			select * from c7\r\n" + 
				"		)\r\n" + 
				"	)\r\n" + 
				")";
		ConvertSubQuerySafely csqs = new ConvertSubQuerySafely();
		csqs.savelyConvert(sample, (t)->{
			System.out.println("###sub");
			System.out.println(t);
			return "";
		});
	}
}
