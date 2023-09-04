package src.java.view.frame;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import src.java.element.BasicElement;
import st.etec.view.panel.FileListSelectPnl;
import st.etec.view.panel.IOPathSettingPnl;
import st.etec.view.panel.SearchFunctionPnl;
import st.etec.view.panel.WriteFilePnl;

public class SearchFunctionFrame extends JFrame{
	/**
	 * 畫面外框
	 */
	private static final long serialVersionUID = 1L;
	//
	JTabbedPane tp;
	JPanel pl;

	public SearchFunctionFrame() {
		init();
		basicSetting();	
	}

	void basicSetting() {
		setTitle("程式搜尋");
		setIconImage(getToolkit().getImage("test.jpg"));
		setSize(1330, 540);// 設定視窗大小(長,寬)
		setLocation(0,0); // --> 設定視窗開啟時左上角的座標，也可帶入Point物件
        setLocationRelativeTo(null); // --> 設定開啟的位置和某個物件相同，帶入null則會在畫面中間開啟
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	void init() {
		add(new SearchFunctionPnl());
	}
}
