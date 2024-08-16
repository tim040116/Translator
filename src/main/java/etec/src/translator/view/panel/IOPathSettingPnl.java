package etec.src.translator.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import etec.common.model.element.IOPathSettingElement;
import etec.src.translator.view.listener.IOPathSettingListener;

public class IOPathSettingPnl extends JPanel {

	/**
	 * 設定產入籍產出的設定
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	IOPathSettingListener lr;

	public IOPathSettingPnl() {
		init();
		setLayout(new GridLayout(3, 2));
		setPreferredSize(new Dimension(600, 300));
	}

	private void init() {
		// 物件宣告
		IOPathSettingElement.init();

		// 排版
		Dimension dLbl = new Dimension(20, 10);
		IOPathSettingElement.lblIp.setPreferredSize(dLbl);
		IOPathSettingElement.lblIp.setPreferredSize(dLbl);

		// 事件
		lr = new IOPathSettingListener();
		IOPathSettingElement.btnSub.addActionListener(lr);

		// 設置
		add(IOPathSettingElement.lblIp);
		add(IOPathSettingElement.tfIp);
		//add(IOPathSettingElement.btnIp);
		add(IOPathSettingElement.lblOp);
		add(IOPathSettingElement.tfOp);
		//add(IOPathSettingElement.btnOp);
		add(IOPathSettingElement.btnSub);
	}
}
