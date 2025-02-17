package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.az.controller.AzureController;
import etec.src.translator.view.frame.SearchFunctionFrame;

public class AzureFileApplication implements UIApplication{
	@Override
	public void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
