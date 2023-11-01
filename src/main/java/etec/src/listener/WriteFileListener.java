package etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.common.enums.RunStatusEnum;
import etec.src.controller.DDIMWriteFileController;
import etec.src.interfaces.Controller;
import src.java.element.WriteFileElement;

/*
 * 轉換語法並產生檔案
 * */
public class WriteFileListener implements ActionListener {

	private Controller con;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				try {
					con.run();
				} catch (Exception e) {
					WriteFileElement.setLog("產生失敗");
					WriteFileElement.setStatus(RunStatusEnum.FAIL);
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public WriteFileListener(Controller controller) {
		con = controller;
	}
}
