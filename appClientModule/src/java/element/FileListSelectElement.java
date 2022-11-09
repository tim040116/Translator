package src.java.element;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;


/*
 * 檔案清單的畫面物件
 * 
 * 已廢棄，暫無功能
 * 
 * */
public class FileListSelectElement {
	// 物件
	public static JButton btnSelect,btnNext;
	public static JList<File> jlS,jlC;
	public static JLabel lblTitle;
	
	public static void init() {
		btnSelect = new JButton("查詢檔案");
		btnNext = new JButton("下一步");
		jlS = new JList<File>();
		jlS.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lblTitle = new JLabel("查詢到的檔案:");
	}
}
