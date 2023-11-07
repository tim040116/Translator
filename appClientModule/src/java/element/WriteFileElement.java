package src.java.element;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

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
