package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.src.translator.project.greenplum.gp.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication  implements TranslatorApplication{
	public void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
