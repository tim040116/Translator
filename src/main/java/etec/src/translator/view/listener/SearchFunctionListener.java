package etec.src.translator.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.framework.code.interfaces.Controller;
import etec.framework.ui.search_func.enums.RunStatusEnum;
import etec.src.translator.view.panel.SearchFunctionPnl;

public class SearchFunctionListener implements ActionListener {

	/*
	 * 整個流程
	 *
	 */

	private Controller con;

	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			@Override
			public void run() {
				try {
					con.run(null);
				} catch (Exception e1) {
					SearchFunctionPnl.lblStatus.setStatus(RunStatusEnum.FAIL);
					e1.printStackTrace();
					SearchFunctionPnl.tsLog.setLog("錯誤",e1.getMessage());
				}
			}
		}.start();
	}

	public SearchFunctionListener(Controller controller) {
		this.con = controller;
	}

}
