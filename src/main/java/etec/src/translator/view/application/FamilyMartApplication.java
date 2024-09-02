package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.file.fm_poc.service.FamilyMartController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class FamilyMartApplication  implements Application{
	public void run() {
		new SearchFunctionFrame("全家轉換",new FamilyMartController());
	}
}
