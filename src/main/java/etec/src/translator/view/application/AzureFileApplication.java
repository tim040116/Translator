package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.framework.ui.annotation.Application;
import etec.src.translator.project.azure.az.controller.AzureController;
import etec.src.translator.view.frame.SearchFunctionFrame;

@Application(value = "AZ")
public class AzureFileApplication implements TranslatorApplication{
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
