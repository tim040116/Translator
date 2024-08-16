package etec.src.translator.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.src.translator.view.frame.IndexFrame;

public class FileListSelectNextPageListener implements ActionListener {

	/*
	 * 煥頁
	 * */
	@Override
	public void actionPerformed(ActionEvent e) {
		IndexFrame.jtp.setSelectedIndex(2);
	}
}
