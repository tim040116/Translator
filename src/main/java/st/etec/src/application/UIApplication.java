package st.etec.src.application;

import etec.common.annotation.Application;
import etec.common.factory.UIPanelFactory;
import st.etec.view.frame.BaseFrame;

@Application
public class UIApplication{

	public static void run() {
		//新增視窗
		BaseFrame bf = new BaseFrame("SQl 語法轉換");
		bf.add(UIPanelFactory.addFileButton());
		bf.setVisible(true);
	}
}
