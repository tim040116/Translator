package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.assignment.project.sd.controller.SearchDDLToSDIController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class SearchDDLApplication implements UIApplication {

	@Override
	public void run() {
		new SearchFunctionFrame("SD製作工具",new SearchDDLToSDIController());

		
	}
}
