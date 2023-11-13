package etec.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import etec.common.enums.RunStatusEnum;
import etec.common.factory.UIPanelFactory;
import etec.common.view.panel.LogTextArea;
import etec.common.view.panel.ProgressBar;
import etec.common.view.panel.StatusBar;
import etec.main.Params;
import etec.src.interfaces.Controller;
import etec.src.listener.SearchFunctionListener;

public class SearchFunctionPnl  extends JPanel {

	/**
	 * 設定產入籍產出的設定
	 */
	private static final long serialVersionUID = 1L;
	static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	//物件
	public static JFileChooser fcIp;
	public static JFileChooser fcOp;
	public static JButton btnIp;
	public static JButton btnOp;
	public static JButton btnSub;
	public static JTextField tfIp;
	public static JTextField tfOp;
	public static JLabel lblIp;
	public static JLabel lblOp;
	public static LogTextArea tsLog;
	public static ProgressBar progressBar;
	public static StatusBar lblStatus;
	// 事件監聽器
	SearchFunctionListener lr;

	public SearchFunctionPnl(Controller con) {
		init(con);
	}

	private void init(Controller con) {
		setLayout(new GridLayout(4, 2));
		setPreferredSize(new Dimension(1300, 600));
		// 初始化
		fcIp = new JFileChooser();
		fcOp = new JFileChooser();
		tfIp = new JTextField(Params.config.INIT_INPUT_PATH);
		tfOp = new JTextField(Params.config.INIT_OUTPUT_PATH);
		btnIp = new JButton("路徑");
		btnOp = new JButton("路徑");
		btnSub = new JButton("確認");
		lblIp = new JLabel("來源路徑:");
		lblOp = new JLabel("產檔路徑:");
		tsLog = UIPanelFactory.addLogTextArea();
		progressBar = UIPanelFactory.addProgressBar();
		
		lblStatus = UIPanelFactory.addStatusBar();
		lblStatus.setStatus(RunStatusEnum.START);
		// 排版
		Dimension dLbl = new Dimension(20, 10);
		lblIp.setPreferredSize(dLbl);
		lblOp.setPreferredSize(dLbl);
		// 事件
		lr = new SearchFunctionListener(con);
		btnSub.addActionListener(lr);

		// 設置
		add(lblIp);
		add(tfIp);
		/**
		 * 2023/08/25 Tim
		 * 應jason要求，取消輸入產出路徑的功能
		 * */
//		add(lblOp);
//		add(tfOp);
		add(btnSub);
		add(progressBar);
		add(lblStatus);
		add(new JScrollPane(tsLog));
	}
}
