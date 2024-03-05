package etec.view.application;

import etec.src.file.family_mart.service.FamilyMartController;
import etec.view.frame.SearchFunctionFrame;

public class FamilyMartApplication {
	public static void run() {
		new SearchFunctionFrame("全家轉換",new FamilyMartController());
	}
}
