package etec.app.application;

import etec.framework.code.annotation.UIAppId;
import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.fm.formal.controller.FamilyMartController2;
import etec.src.translator.project.azure.fm.formal.view.TransduceFrame;

@UIAppId("FM")
public class FamilyMartApplication implements UIApplication{
	@Override
	public void run() {
//		new SearchFunctionFrame("全家轉換",new FamilyMartController());
		TransduceFrame frame = new TransduceFrame(new FamilyMartController2());
		frame.setVisible(true);
	}
}
