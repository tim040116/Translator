package etec.src.translator.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import etec.framework.code.interfaces.Controller;
import etec.framework.security.log.service.Log;
import etec.framework.ui.search_func.enums.RunStatusEnum;
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
					con.run(null);
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
