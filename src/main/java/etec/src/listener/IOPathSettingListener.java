package etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

import etec.common.model.BasicParams;
import etec.src.service.FileListSelectService;
import etec.src.service.IOpathSettingService;
import etec.view.frame.IndexFrame;
import src.java.element.FileListSelectElement;
import src.java.element.IOPathSettingElement;

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
//		IndexFrame.jtp.setSelectedIndex(2);
	}

}
