package etec.app.application;

import etec.common.factory.UIPanelFactory;
import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.view.frame.BaseFrame;

public class NewDDIMApplication implements UIApplication{

	@Override
	public void run() {
		//新增視窗
		BaseFrame bf = new BaseFrame("SQl 語法轉換");
		bf.add(UIPanelFactory.addFileButton());
		bf.setVisible(true);
	}
}
