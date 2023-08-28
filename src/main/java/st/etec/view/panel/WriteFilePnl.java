package st.etec.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.java.element.WriteFileElement;
import st.etec.src.listener.WriteFileListener;

public class WriteFilePnl extends JPanel {
	/**
	 * 產製檔案
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	// 物件

	public WriteFilePnl() {
		init();
		setLayout(new GridLayout(4, 1));
		setPreferredSize(new Dimension(600, 300));
	}

	private void init() {
		WriteFileElement.init();
		WriteFileListener lr = new WriteFileListener();
		WriteFileElement.btnStart.addActionListener(lr);
		add(WriteFileElement.btnStart);
		add(new JScrollPane(WriteFileElement.tsLog));
		add(WriteFileElement.pbWriteFile);
		add(WriteFileElement.lblStatus);
	}
}
