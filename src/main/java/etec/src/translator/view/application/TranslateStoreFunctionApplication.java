package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.controller.TranslateStoreFunctionController;
import etec.src.translator.view.frame.SearchFunctionFrame;
/**
 * @version	3.3.1.0
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 應Jason要求轉換store function
 * 		
 * 
 * */
public class TranslateStoreFunctionApplication  implements Application{
	public void run() {
		new SearchFunctionFrame("SP SF 轉換",new TranslateStoreFunctionController());
	}
}