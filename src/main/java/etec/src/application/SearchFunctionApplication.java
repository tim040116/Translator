package etec.src.application;

import etec.src.controller.SearchFunctionController;
import etec.view.frame.SearchFunctionFrame;

public class SearchFunctionApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchFunctionController());
	}
}
