package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.framework.ui.annotation.Application;
import etec.src.translator.controller.AzureController;
import etec.src.translator.file.green_plum.controller.GreenPlumFileController;
import etec.src.translator.view.frame.SearchFunctionFrame;

@Application(value = "AZ")
public class AzureFileApplication implements TranslatorApplication{
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
