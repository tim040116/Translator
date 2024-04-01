package etec.view.application;

import etec.src.controller.AssessmentController;
import etec.view.frame.SearchFunctionFrame;

public class AssessmentApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new AssessmentController());
	}
}
