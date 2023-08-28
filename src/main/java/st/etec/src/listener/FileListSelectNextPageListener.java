package st.etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.java.element.BasicElement;

public class FileListSelectNextPageListener implements ActionListener {

	/*
	 * 煥頁
	 * */
	@Override
	public void actionPerformed(ActionEvent e) {
		BasicElement.getJTabbedPane().setSelectedIndex(2);
	}
}
