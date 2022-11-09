package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

import src.java.element.BasicElement;
import src.java.element.FileListSelectElement;
import src.java.element.IOPathSettingElement;
import src.java.params.BasicParams;
import src.java.service.FileListSelectService;
import src.java.service.IOpathSettingService;

public class IOPathSettingListener implements ActionListener {


	/*
	 * 儲存來源及目標的路徑
	 * 
	 * */
	@Override
	public void actionPerformed(ActionEvent e) {
		IOpathSettingService.setPath(IOPathSettingElement.tfIp.getText(), IOPathSettingElement.tfOp.getText());
		FileListSelectElement.lblTitle.setText("根目錄:"+BasicParams.getInputPath());
		DefaultListModel<File> dlm = null;
		try {
			dlm = FileListSelectService.getFileList();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileListSelectElement.jlS.removeAll();
		FileListSelectElement.jlS.setModel(dlm);
		BasicElement.getJTabbedPane().setSelectedIndex(2);
	}

}
