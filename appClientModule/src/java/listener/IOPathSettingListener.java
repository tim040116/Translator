package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.java.element.BasicElement;
import src.java.element.FileListSelectElement;
import src.java.element.IOPathSettingElement;
import src.java.model.IOPathModel;
import src.java.service.IOpathSettingService;

public class IOPathSettingListener implements ActionListener {


	@Override
	public void actionPerformed(ActionEvent e) {
		IOpathSettingService.setPath(IOPathSettingElement.tfIp.getText(), IOPathSettingElement.tfOp.getText());
		FileListSelectElement.lblTitle.setText("根目錄:"+IOPathModel.getInputPath());
		BasicElement.getJTabbedPane().setSelectedIndex(1);
	}

}
