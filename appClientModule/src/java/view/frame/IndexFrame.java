package src.java.view.frame;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import src.java.element.BasicElement;
import st.etec.view.panel.FileListSelectPnl;
import st.etec.view.panel.IOPathSettingPnl;
import st.etec.view.panel.WriteFilePnl;

public class IndexFrame extends JFrame{
	/**
	 * 畫面外框
	 */
	private static final long serialVersionUID = 1L;
	//
	JTabbedPane tp;
	JPanel pl;

	public IndexFrame() {
		init();
		basicSetting();	
	}

	void basicSetting() {
		setTitle("查詢檔案");
		setIconImage(getToolkit().getImage("test.jpg"));
		setSize(1800, 1000);// 設定視窗大小(長,寬)
		setLocation(0,0); // --> 設定視窗開啟時左上角的座標，也可帶入Point物件
        setLocationRelativeTo(null); // --> 設定開啟的位置和某個物件相同，帶入null則會在畫面中間開啟
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	void init() {
		tp = new JTabbedPane();
		BasicElement.setJTabbedPane(tp);
		pl = new JPanel();
		//pl.setPreferredSize(new Dimension(1200, 800));
		tp.addTab("檔案路徑設定", new IOPathSettingPnl());
		tp.addTab("查詢目錄下檔案", new FileListSelectPnl());
		tp.addTab("產製檔案", new WriteFilePnl());
		pl.add(tp);
		add(pl);
	}
}
