package main;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class Main {

	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";

	public static void main(String[] args) {
		String test = "create multiset table pmart.sample_table A.AA as select a.aa";
		System.out.println(test.replaceAll("(?i)\\QA.AA\\E", "B.BB"));
	}


}
