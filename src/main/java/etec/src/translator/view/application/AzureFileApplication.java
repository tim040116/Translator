package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.controller.AzureController;
import etec.src.translator.file.green_plum.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class AzureFileApplication implements Application{
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
