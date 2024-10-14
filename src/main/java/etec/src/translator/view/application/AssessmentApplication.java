package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.assignment.project.detail.controller.AssessmentController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class AssessmentApplication implements Application{

	@Override
	public void run() {
		new SearchFunctionFrame("Assessment製作工具",new AssessmentController());
	}
}
