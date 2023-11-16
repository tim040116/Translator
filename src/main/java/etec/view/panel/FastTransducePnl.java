package etec.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import etec.common.view.panel.StatusBar;
import etec.src.interfaces.Controller;
import etec.src.listener.FastTransduceListener;

/**
 * @author	Tim
 * @since	2023年11月8日
 * @version	3.4.0.1
 * 
 * 快速轉換的UI介面
 * 
 * */
public class FastTransducePnl  extends JPanel {

	/**
	 * 設定產入籍產出的設定
	 */
	private static final long serialVersionUID = 1L;
	
	//物件
	public JTextArea	txtOldScript;//輸入欄位
	public JTextArea	txtNewScript;//輸出欄位
	public JPanel		pnlInfo;//狀態區
	public JButton		btnRun;//執行按鍵
	public StatusBar	statusBar;//狀態列
	// 事件監聽器
	FastTransduceListener lr;

	public FastTransducePnl(Controller con) {
		init(con);
		
	}

	private void init(Controller con) {
		setLayout(new GridLayout(1,3));
		setPreferredSize(new Dimension(1300, 675));
		// 事件
		lr = new FastTransduceListener(con);
		// 建置物件
		txtOldScript = new JTextArea() {//輸入區
			{
				setEditable(true);
				setLineWrap(true);
				setWrapStyleWord(true);
				((DefaultCaret)getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			}
		};
		txtNewScript = new JTextArea() {//輸出區
			{
				setEditable(false);
				setLineWrap(true);
				setWrapStyleWord(true);
				((DefaultCaret)getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			}
		};
		btnRun = new JButton("執行") {
			{
				addActionListener(lr);
			}
		};
		statusBar = new StatusBar();
		pnlInfo = new JPanel() {
			{
				setLayout(new GridLayout(3,1));
				setPreferredSize(new Dimension(1300, 600));
				add(btnRun);
				add(statusBar);
			}
		};
		
		// 設置
		add(new JScrollPane(txtOldScript));
		add(pnlInfo);
		add(new JScrollPane(txtNewScript));
	}
}
