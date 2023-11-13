package etec.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import etec.common.model.element.WriteFileElement;


public class WriteFilePnl extends JPanel {
	/**
	 * 產製檔案
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	// 物件

	/**
	 * @param	listener	
	 * 
	 * */
	public WriteFilePnl(ActionListener listener) {
		init(listener);
		setLayout(new GridLayout(4, 1));
		setPreferredSize(new Dimension(600, 300));
	}

	private void init(ActionListener listener) {
		WriteFileElement.init();
//		WriteFileListener lr = new WriteFileListener();
		WriteFileElement.btnStart.addActionListener(listener);
		add(WriteFileElement.btnStart);
		add(new JScrollPane(WriteFileElement.tsLog));
		add(WriteFileElement.progressBar);
		add(WriteFileElement.lblStatus);
	}
}
