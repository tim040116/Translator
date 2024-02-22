package etec.view.application;

import etec.src.controller.TranslateStoreFunctionController;
import etec.view.frame.SearchFunctionFrame;
/**
 * @version	3.3.1.0
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 應Jason要求轉換store function
 * 		
 * 
 * */
public class TranslateStoreFunctionApplication {
	public static void run() {
		new SearchFunctionFrame("SP SF 轉換",new TranslateStoreFunctionController());
	}
}
