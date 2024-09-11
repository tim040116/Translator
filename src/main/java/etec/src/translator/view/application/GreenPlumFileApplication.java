package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.src.translator.file.green_plum.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class GreenPlumFileApplication  implements TranslatorApplication{
	public void run() {
		new SearchFunctionFrame("Green Plum 測試",new GreenPlumFileController());
	}
}
