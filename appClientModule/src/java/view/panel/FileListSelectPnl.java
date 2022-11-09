package src.java.view.panel;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.java.element.FileListSelectElement;
import src.java.listener.FileListSelectListener;
import src.java.listener.FileListSelectNextPageListener;

public class FileListSelectPnl  extends JPanel {
	/**
	 * 檔案清單畫面
	 * 
	 * 已廢棄
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	FileListSelectListener lr;
	FileListSelectNextPageListener lr2;
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
		lr2 = new FileListSelectNextPageListener();
		FileListSelectElement.btnSelect.addActionListener(lr);
		FileListSelectElement.btnNext.addActionListener(lr2);
		add(FileListSelectElement.lblTitle);
		add(FileListSelectElement.btnSelect);
		
		add(new JScrollPane(FileListSelectElement.jlS));
		add(FileListSelectElement.btnNext);
//		add(new JScrollPane(jlC));
	}
}
