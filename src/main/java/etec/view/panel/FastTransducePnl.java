package etec.view.panel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import etec.common.interfaces.Controller;
import etec.common.view.panel.StatusBar;
import etec.view.listener.FastTransduceListener;

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
	public JPanel		pnlCtrl;//選項區
	public JCheckBox 	chbIsSetToVarchar;//是否轉換成字串
	public Map<String,JRadioButton>	maprdo = new HashMap<String,JRadioButton>();
//	public JRadioButton rdoAZ;
//	public JRadioButton rdoMS;
//	public JRadioButton rdoGP;
	public ButtonGroup	grpSQLType;
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
		//輸入區
		txtOldScript = new JTextArea();
		txtOldScript.setEditable(true);
		txtOldScript.setLineWrap(true);
		txtOldScript.setWrapStyleWord(true);
		((DefaultCaret)txtOldScript.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//輸出區
		txtNewScript = new JTextArea();
		txtNewScript.setEditable(false);
		txtNewScript.setLineWrap(true);
		txtNewScript.setWrapStyleWord(true);
		((DefaultCaret)txtNewScript.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//執行鍵
		btnRun = new JButton("執行");
		btnRun.addActionListener(lr);
		//狀態欄
		statusBar = new StatusBar();
		//是否轉換成字串
		chbIsSetToVarchar = new JCheckBox("是否轉換成字串");
//		chbIsSetToVarchar.setSelected(true);
//		//要轉換成CTAS還是select into
//		rdoAZ = new JRadioButton("Azure Synapse");
//		rdoAZ.setActionCommand("az");
//		rdoAZ.setSelected(true);
//		rdoMS = new JRadioButton("MS SQL");
//		rdoMS.setActionCommand("ms");
		grpSQLType = new ButtonGroup();
//		grpSQLType.add(rdoAZ);
//		grpSQLType.add(rdoMS);
		//選項區
		pnlCtrl= new JPanel();
		pnlCtrl.setLayout(new GridLayout(1,1));
		pnlCtrl.setPreferredSize(new Dimension(1300, 600));
		pnlCtrl.add(chbIsSetToVarchar);
//		pnlCtrl.add(rdoAZ);
//		pnlCtrl.add(rdoMS);
		//狀態區
		pnlInfo = new JPanel();
		pnlInfo.setLayout(new GridLayout(3,1));
		pnlInfo.setPreferredSize(new Dimension(1300, 600));
		pnlInfo.add(btnRun);
		pnlInfo.add(statusBar);
		pnlInfo.add(pnlCtrl);
		// 設置
		add(new JScrollPane(txtOldScript));
		add(pnlInfo);
		add(new JScrollPane(txtNewScript));
	}
	
	/**
	 * <h1>增加轉換的SQL環境選項</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月20日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	String	參數資料
	 * @param	String	radio顯示文字
	 * @throws	
	 * @exception	
	 * @see
	 * @return	void
			 */
	public void addLanguageRadio(String commend,String show) {
		JRadioButton rdo = new JRadioButton(show);
		maprdo.put(commend, rdo);
		rdo.setActionCommand(commend);
		grpSQLType.add(rdo);
		pnlCtrl.add(rdo);
	}
	
}
