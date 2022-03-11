package src.java.view.panel;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.java.element.FileListSelectElement;
import src.java.listener.FileListSelectListener;

public class FileListSelectPnl  extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	FileListSelectListener lr;
	// 物件
	
	public FileListSelectPnl() {
		init();
		setLayout(new GridLayout(2,2));
	}
	private void init() {
		//宣告
		FileListSelectElement.init();
		//事件
		lr = new FileListSelectListener();
		FileListSelectElement.btnSelect.addActionListener(lr);
		add(FileListSelectElement.lblTitle);
		add(FileListSelectElement.btnSelect);
		
		add(new JScrollPane(FileListSelectElement.jlS));
		add(FileListSelectElement.btnNext);
//		add(new JScrollPane(jlC));
	}
}
