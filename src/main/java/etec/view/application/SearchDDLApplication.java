package etec.view.application;

import etec.src.controller.SearchDDLToSDIController;
import etec.view.frame.SearchFunctionFrame;

public class SearchDDLApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchDDLToSDIController());
	}
}
