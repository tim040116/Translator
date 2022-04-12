package src.java.element;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class WriteFileElement {
	public static JButton btnStart;
	public static JTextArea tsLog;
	public static JProgressBar pbWriteFile;
	public static void init() {
		btnStart = new JButton("開始置換");
		tsLog = new JTextArea();
		pbWriteFile = new JProgressBar();
		pbWriteFile.setStringPainted(true);
	}
	public static void setLog(String content) {
		tsLog.append("\r\n"+content);
		
	}
	public static void clearLog() {
		tsLog.setText("");
	}
	public static void setProgressBar(int i) {
		pbWriteFile.setValue(i);
	}
}
