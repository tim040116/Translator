package etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.common.enums.RunStatusEnum;
import etec.common.model.element.WriteFileElement;
import etec.src.interfaces.Controller;

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
					WriteFileElement.tsLog.setLog("資訊","產生失敗");
					WriteFileElement.lblStatus.setStatus(RunStatusEnum.FAIL);
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public WriteFileListener(Controller controller) {
		con = controller;
	}
}
