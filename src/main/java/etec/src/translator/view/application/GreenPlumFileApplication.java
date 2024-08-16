package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.file.green_plum.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication  implements Application{
	public void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
