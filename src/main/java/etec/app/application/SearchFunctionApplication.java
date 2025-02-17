package etec.app.application;

import etec.src.assignment.project.sd.controller.SearchFunctionController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class SearchFunctionApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new SearchFunctionController());
	}
}
