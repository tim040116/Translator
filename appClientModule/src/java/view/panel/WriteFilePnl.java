package src.java.view.panel;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import src.java.element.WriteFileElement;
import src.java.listener.WriteFileListener;

public class WriteFilePnl  extends JPanel {
	/**
	 * 產製檔案
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	// 物件
	
	public WriteFilePnl() {
		init();
		setLayout(new GridLayout(2,2));
	}
	private void init() {
		WriteFileElement.init();
		WriteFileListener lr = new WriteFileListener();
		WriteFileElement.btnStart.addActionListener(lr);
		add(WriteFileElement.btnStart);
	}
}
