package etec.src.application;

import etec.src.controller.SearchFunctionController;
import etec.view.frame.SearchFunctionFrame;

public class SearchFunctionApplication {

	public static void run() {
		new SearchFunctionFrame(new SearchFunctionController());
	}
}
