package etec.view.application;

import etec.src.sql.az.controller.SearchFunctionController;
import etec.view.frame.SearchFunctionFrame;

public class SearchFunctionApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchFunctionController());
	}
}
