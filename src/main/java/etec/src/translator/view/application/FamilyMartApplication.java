package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.src.translator.project.azure.fm.formal.controller.FamilyMartController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class FamilyMartApplication  implements TranslatorApplication{
	public void run() {
		new SearchFunctionFrame("全家轉換",new FamilyMartController());
	}
}
