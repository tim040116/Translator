package etec.app.application;

import etec.framework.code.annotation.UIAppId;
import etec.framework.code.interfaces.UIApplication;
import etec.src.assignment.project.detail.controller.AssessmentController;
import etec.src.translator.view.frame.SearchFunctionFrame;

@UIAppId("ASSESSMENT")
public class AssessmentApplication implements UIApplication{

	@Override
	public void run() {
		new SearchFunctionFrame("Assessment製作工具",new AssessmentController());
	}
}
