package src.java.view.panel;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import src.java.listener.ReaderListener;

public class FileListSelectPnl  extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	ReaderListener lr;
	// 物件
	JTextField text;
	JButton button;
	JList<File> jl;
	public FileListSelectPnl() {
		init();
	}
	private void init() {
		lr = new ReaderListener();
		text = new JTextField(10);
		button = new JButton("確認");
		jl = new JList<File>();

		jl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		lr.setJTextField(text);
		lr.setJList(jl);
		button.addActionListener(lr);

		add(text);
		add(new JLabel("查詢路徑:"));
		add(button);
		add(new JScrollPane(jl));
	}
}
