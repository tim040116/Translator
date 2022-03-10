package src.java.view.panel;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import src.java.listener.ReaderListener;

public class IOPathSettingPnl extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 事件監聽器
	ReaderListener lr;
	// 物件
	JFileChooser fcip,fcop;
	JButton btnSub;
	JTextField tfip,tfop;
	public IOPathSettingPnl() {
		init();
		setLayout(new GridLayout(3,1));
	}
	private void init() {
		fcip = new JFileChooser();
		fcop = new JFileChooser();
		tfip = new JTextField(50);
		tfop = new JTextField(50);
		lr = new ReaderListener();
		btnSub = new JButton("確認");

		btnSub.addActionListener(lr);

		add(new JLabel("來源路徑:"));
		add(tfip);
		add(new JLabel("產檔路徑:"));
		add(tfop);
		add(btnSub);
	}
}
