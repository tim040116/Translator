package src.java.view.frame;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class BaseFrame extends JFrame{
	/**
	 * 畫面外框
	 */
	private static final long serialVersionUID = 1L;

	public BaseFrame(String title) {
		basicSetting(title);
	}

	void basicSetting(String title) {
		setTitle(title);
//		setIconImage(getToolkit().getImage("test.jpg"));
		setSize(1300,900);// 設定視窗大小(長,寬)
		setLocation(0,0); // --> 設定視窗開啟時左上角的座標，也可帶入Point物件
        setLocationRelativeTo(null); // --> 設定開啟的位置和某個物件相同，帶入null則會在畫面中間開啟
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}


}
