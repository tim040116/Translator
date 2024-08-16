package etec.src.translator.view.application;

import etec.src.translator.controller.SearchFunctionController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class SearchFunctionApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchFunctionController());
	}
}
