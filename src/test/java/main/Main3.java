package main;

/**
 * 列出source target table清單
 * 
 * 
 * */
public class Main3 {

	public static void main(String[] args) {
		try {
			String test = "aaa\r\nbbb\r\ncccc";
			System.out.println(test.replaceAll("(?i)^(zzzzzz)", "$1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("");

	}



	
}


