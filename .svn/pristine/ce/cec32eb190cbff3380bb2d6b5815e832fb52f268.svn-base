package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import src.java.service.WriteFileService;

public class WriteFileListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				try {
					WriteFileService.writeFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
