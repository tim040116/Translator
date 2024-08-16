package etec.src.translator.view.application;

import etec.src.translator.controller.SearchDDLToSDIController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class SearchDDLApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchDDLToSDIController());
	}
}
