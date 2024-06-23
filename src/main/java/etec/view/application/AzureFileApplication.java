package etec.view.application;

import etec.src.file.azure.service.AzureController;
import etec.src.file.green_plum.controller.GreenPlumFileController;
import etec.view.frame.SearchFunctionFrame;

public class AzureFileApplication {
	public static void run() {
		new SearchFunctionFrame("Azure 轉換",new AzureController());
	}
}
