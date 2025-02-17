package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.greenplum.gp.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication  implements UIApplication{
	@Override
	public void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
