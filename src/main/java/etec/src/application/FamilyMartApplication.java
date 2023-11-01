package etec.src.application;

import etec.src.controller.FamilyMartController;
import etec.view.frame.SearchFunctionFrame;

public class FamilyMartApplication {
	public static void run() {
		new SearchFunctionFrame(new FamilyMartController());
	}
}
