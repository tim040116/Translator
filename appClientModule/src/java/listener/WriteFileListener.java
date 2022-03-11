package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

import src.java.element.FileListSelectElement;
import src.java.service.FileListSelectService;
import src.java.service.WriteFileService;

public class WriteFileListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			WriteFileService.writeFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
