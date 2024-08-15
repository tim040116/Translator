package etec.view.application;

import etec.common.factory.UIPanelFactory;
import etec.common.interfaces.Application;
import etec.view.frame.BaseFrame;

public class UIApplication implements Application{

	public void run() {
		//新增視窗
		BaseFrame bf = new BaseFrame("SQl 語法轉換");
		bf.add(UIPanelFactory.addFileButton());
		bf.setVisible(true);
	}
}
