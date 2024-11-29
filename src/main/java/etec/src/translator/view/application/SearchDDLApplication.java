package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.assignment.project.sd.controller.SearchDDLToSDIController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class SearchDDLApplication  implements Application {

	@Override
	public void run() {
		new SearchFunctionFrame("SD製作工具",new SearchDDLToSDIController());

		
	}
}
