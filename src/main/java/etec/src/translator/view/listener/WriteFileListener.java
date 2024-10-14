package etec.src.translator.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.common.model.element.WriteFileElement;
import etec.framework.code.interfaces.Controller;
import etec.framework.ui.search_func.enums.RunStatusEnum;

/*
 * 轉換語法並產生檔案
 * */
public class WriteFileListener implements ActionListener {

	private Controller con;

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			@Override
			public void run() {
				try {
					con.run(null);
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
