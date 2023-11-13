package etec.src.controller;

import etec.common.enums.RunStatusEnum;
import etec.src.interfaces.Controller;
import etec.src.service.FamilyMartFileTransduceService;
import etec.view.frame.FastTransduceFrame;

/**
 * @author	Tim
 * @since	2023年11月8日
 * @version	3.4.0.1
 * 快入轉換
 * 
 * */
public class FastTransduceController implements Controller {

	public void run() throws Exception {
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.WORKING);
		String script = FastTransduceFrame.pnl.txtOldScript.getText();
		String result = FamilyMartFileTransduceService.transduceSQLScript(script);
		FastTransduceFrame.pnl.txtNewScript.setText(result);
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.SUCCESS);
	}
}
