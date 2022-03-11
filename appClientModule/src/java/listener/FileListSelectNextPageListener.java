package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

import src.java.element.BasicElement;
import src.java.element.FileListSelectElement;
import src.java.service.FileListSelectService;

public class FileListSelectNextPageListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		BasicElement.getJTabbedPane().setSelectedIndex(2);
	}
}
