package etec.src.controller;

import etec.common.interfaces.Controller;

import etec.src.controller.SearchDDLToSDIController;
import etec.src.controller.SearchFunctionController;

public class AssessmentController implements Controller{

	@Override
	public void run() throws Exception {
		//呼叫執行searchFunction
 	   	 new SearchFunctionController().run();
		//呼叫執行SearchDDLToSDI
		new SearchDDLToSDIController().run();


	}
}
