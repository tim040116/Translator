package etec.view.application;

import etec.src.file.assignment.AssessmentController;
import etec.view.frame.SearchFunctionFrame;

public class AssessmentApplication {

	public static void run(String title) {
		new SearchFunctionFrame(title,new AssessmentController());
	}
}
