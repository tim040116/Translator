package etec.common.model.element;

import javax.swing.JButton;

import etec.common.enums.RunStatusEnum;
import etec.common.factory.UIPanelFactory;
import etec.common.view.panel.LogTextArea;
import etec.common.view.panel.ProgressBar;
import etec.common.view.panel.StatusBar;
/*
 * 轉換時的畫面呈現
 * */
public class WriteFileElement {
	public static JButton btnStart;
	public static LogTextArea tsLog;
	public static ProgressBar progressBar;
	public static StatusBar lblStatus;

	public static void init() {
		btnStart = new JButton("開始置換");
		tsLog = UIPanelFactory.addLogTextArea();
		progressBar = UIPanelFactory.addProgressBar();
		lblStatus = UIPanelFactory.addStatusBar();
		lblStatus.setStatus(RunStatusEnum.START);
	}
}
