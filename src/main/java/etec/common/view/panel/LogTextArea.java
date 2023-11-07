package etec.common.view.panel;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class LogTextArea extends JTextArea{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LogTextArea() {
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		((DefaultCaret)getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	
	public void setLog(String level,String content) {
		append(sfabs.format(new Date())+ " ["+level.toUpperCase()+"] " + content + "\r\n");
	}
	public void setL(String level,String content) {
		append(sfabs.format(new Date())+ " ["+level.toUpperCase()+"] " + content);
	}
	public void setOg(String content) {
		append(content);
	}
	public void clearLog() {
		setText("");
	}
}
