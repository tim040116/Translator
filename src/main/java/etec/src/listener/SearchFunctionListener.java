package etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.common.enums.RunStatusEnum;
import etec.src.interfaces.Controller;
import etec.view.panel.SearchFunctionPnl;

public class SearchFunctionListener implements ActionListener {

	/*
	 * 整個流程
	 * 
	 */
	
	private Controller con;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				try {
					con.run();
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
