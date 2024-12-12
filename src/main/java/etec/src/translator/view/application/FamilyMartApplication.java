package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.translator.project.azure.fm.formal.controller.FamilyMartController2;
import etec.src.translator.project.azure.fm.formal.view.TransduceFrame;

public class FamilyMartApplication  implements Application{
	@Override
	public void run() {
//		new SearchFunctionFrame("全家轉換",new FamilyMartController());
		TransduceFrame frame = new TransduceFrame(new FamilyMartController2());
		frame.setVisible(true);
	}
}
