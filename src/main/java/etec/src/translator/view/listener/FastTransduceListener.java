package etec.src.translator.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.common.enums.RunStatusEnum;
import etec.common.interfaces.Controller;
import etec.framework.security.log.service.Log;
import etec.src.translator.view.frame.FastTransduceFrame;

public class FastTransduceListener implements ActionListener {

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
					con.run();
				} catch (Exception e1) {
					FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.FAIL);
					FastTransduceFrame.pnl.txtNewScript.setText(e1.getMessage());
					Log.error(e1);
				}
			}
		}.start();
	}

	public FastTransduceListener(Controller controller) {
		this.con = controller;
	}

}
