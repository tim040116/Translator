package etec.view.application;

import etec.src.file.assignment.AssessmentController;
import etec.view.frame.SearchFunctionFrame;

public class AssessmentApplication {

	public static void run() {
		new SearchFunctionFrame("Assessment製作工具",new AssessmentController());
	}
}
