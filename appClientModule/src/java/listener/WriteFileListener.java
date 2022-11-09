package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.java.service.WriteFileService;

/*
 * 轉換語法並產生檔案
 * */
public class WriteFileListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				WriteFileService.writeFile();
			}
		}.start();
	}
}
