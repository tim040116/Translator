package test;

import test.az.fm.TestBitFunctionService;
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
			
//			TestGPTranslater.run();//GP
//			TestConvertSafely.run();//Convert Safely
			TestBitFunctionService.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
