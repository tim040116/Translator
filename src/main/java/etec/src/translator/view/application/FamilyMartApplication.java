package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.translator.project.azure.fm.formal.controller.FamilyMartController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class FamilyMartApplication  implements Application{
	@Override
	public void run() {
		new SearchFunctionFrame("全家轉換",new FamilyMartController());
	}
}
