package src.java.element;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import etec.common.enums.RunStatusEnum;
/*
 * 轉換時的畫面呈現
 * */
public class WriteFileElement {
	public static JButton btnStart;
	public static JTextArea tsLog;
	public static JProgressBar pbWriteFile;
	public static JLabel lblStatus;
	
	public static void init() {
		btnStart = new JButton("開始置換");
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
	}

	public static void setLog(String content) {
		tsLog.append("\r\n" + content);
	}

	public static void clearLog() {
		tsLog.setText("");
	}

	public static void setProgressBar(int i) {
		pbWriteFile.setValue(i);
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
}
