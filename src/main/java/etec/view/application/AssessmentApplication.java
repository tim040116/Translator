package etec.view.application;

import etec.common.interfaces.Application;
import etec.src.file.assignment.AssessmentController;
import etec.view.frame.SearchFunctionFrame;

public class AssessmentApplication implements Application{

	public void run() {
		new SearchFunctionFrame("Assessment製作工具",new AssessmentController());
	}
}
