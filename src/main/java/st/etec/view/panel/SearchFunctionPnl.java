package st.etec.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import etec.common.enums.RunStatusEnum;
import st.etec.src.listener.SearchFunctionListener;
import st.etec.src.params.Params;

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
	public static JTextArea tsLog;
	public static JProgressBar pbWriteFile;
	public static JLabel lblStatus;
	// 事件監聽器
	SearchFunctionListener lr;

	public SearchFunctionPnl() {
		init();
		setLayout(new GridLayout(4, 2));
		setPreferredSize(new Dimension(1300, 600));
	}

	private void init() {
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
		tsLog = new JTextArea();
		pbWriteFile = new JProgressBar();
		pbWriteFile.setStringPainted(true);
		lblStatus = new JLabel();
		lblStatus.setOpaque(true);
		setStatus(RunStatusEnum.START);
		tsLog.setEditable(false);
		tsLog.setLineWrap(true);
		tsLog.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)tsLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// 排版
		Dimension dLbl = new Dimension(20, 10);
		lblIp.setPreferredSize(dLbl);
		lblIp.setPreferredSize(dLbl);

		// 事件
		lr = new SearchFunctionListener();
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
		add(pbWriteFile);
		add(lblStatus);
		add(new JScrollPane(tsLog));

	}
	
	public static void setStatus(RunStatusEnum status) {
		switch (status) {
		case START:
			lblStatus.setBackground(new Color(255,255,255));
			lblStatus.setText("就緒");
			break;
		case WORKING:
			lblStatus.setBackground(new Color(255,125,0));
			lblStatus.setText("執行中...");
			break;
		case SUCCESS:
			lblStatus.setBackground(new Color(0,255,0));
			lblStatus.setText("成功");
			break;
		case FAIL:
			lblStatus.setBackground(new Color(255,0,0));
			lblStatus.setText("失敗");
			break;
		}
	}
	public static void setLog(String level,String content) {
		tsLog.append(sfabs.format(new Date())+ " ["+level.toUpperCase()+"] " + content + "\r\n");
	}
	public static void setL(String level,String content) {
		tsLog.append(sfabs.format(new Date())+ " ["+level.toUpperCase()+"] " + content);
	}
	public static void setLo(String content) {
		tsLog.append(content);
	}
	public static void clearLog() {
		tsLog.setText("");
	}

	public static void setProgressBar(int i) {
		pbWriteFile.setValue(i);
	}
}
