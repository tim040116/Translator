package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

import src.java.element.FileListSelectElement;
import src.java.service.FileListSelectService;

public class FileListSelectListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultListModel<File> dlm = null;
		try {
			dlm = FileListSelectService.getFileList();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileListSelectElement.jlS.removeAll();
		FileListSelectElement.jlS.setModel(dlm);
	}
}