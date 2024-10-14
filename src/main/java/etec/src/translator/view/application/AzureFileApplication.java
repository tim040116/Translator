package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.translator.project.azure.az.controller.AzureController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class AzureFileApplication implements Application{
	@Override
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
