package etec.view.application;

import etec.src.file.gp_test.controller.TestGreenPlumFileController;
import etec.view.frame.SearchFunctionFrame;

public class TestGreenPlumFileApplication {
	public static void run() {
		new SearchFunctionFrame("Green Plum 測試",new TestGreenPlumFileController());
	}
}
