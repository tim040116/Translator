package etec.view.application;

import etec.common.interfaces.Application;
import etec.src.file.green_plum.controller.GreenPlumFileController;
import etec.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication  implements Application{
	public void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
