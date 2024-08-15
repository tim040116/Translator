package etec.view.application;

import etec.common.interfaces.Application;
import etec.src.controller.AzureController;
import etec.src.file.green_plum.controller.GreenPlumFileController;
import etec.view.frame.SearchFunctionFrame;

public class AzureFileApplication implements Application{
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
