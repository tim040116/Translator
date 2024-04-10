package etec.view.application;

import etec.src.file.gp_test.controller.GreenPlumFileController;
import etec.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication {
	public static void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
