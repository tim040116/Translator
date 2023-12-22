package test;

import etec.src.main.ParamsFactory;
import test.gp.translater.TestSQLTranslater;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	public static void main(String[] args) {
		try {
			ParamsFactory.init();
			TestSQLTranslater.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
