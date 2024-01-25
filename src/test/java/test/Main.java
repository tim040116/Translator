package test;

import test.gp.translater.TestConvertSafely;
import test.gp.translater.TestGPTranslater;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	public static void main(String[] args) {
		try {
			//GP
//			TestGPTranslater.run();
			//Convert Safely
			TestConvertSafely.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
