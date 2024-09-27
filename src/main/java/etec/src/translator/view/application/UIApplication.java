package etec.src.translator.view.application;

import etec.common.factory.UIPanelFactory;
import etec.common.interfaces.TranslatorApplication;
import etec.src.translator.view.frame.BaseFrame;

public class UIApplication implements TranslatorApplication{

	@Override
	public void run() {
		//新增視窗
		BaseFrame bf = new BaseFrame("SQl 語法轉換");
		bf.add(UIPanelFactory.addFileButton());
		bf.setVisible(true);
	}
}
