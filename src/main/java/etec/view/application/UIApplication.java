package etec.view.application;

import etec.common.factory.UIPanelFactory;
import etec.view.frame.BaseFrame;

public class UIApplication{

	public static void run() {
		//新增視窗
		BaseFrame bf = new BaseFrame("SQl 語法轉換");
		bf.add(UIPanelFactory.addFileButton());
		bf.setVisible(true);
	}
}
