package etec.view.application;

import etec.src.sql.az.controller.FastTransduceController;
import etec.view.frame.FastTransduceFrame;
/**
 * @version	3.3.4.0
 * @author	Tim
 * @since	2023年11月06日
 * 
 * 即時轉換功能
 * 		
 * 
 * */
public class FastTransduceApplication {
	public static void run() {
		new FastTransduceFrame("即時轉換",new FastTransduceController());
	}
}
