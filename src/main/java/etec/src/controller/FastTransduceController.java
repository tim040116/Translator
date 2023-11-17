package etec.src.controller;

import etec.common.enums.RunStatusEnum;
import etec.src.interfaces.Controller;
import etec.src.service.FastTransduceService;
import etec.view.frame.FastTransduceFrame;

/**
 * @author	Tim
 * @since	2023年11月8日
 * @version	3.4.1.1
 * 快入轉換
 * 
 * */
public class FastTransduceController implements Controller {

	public void run() throws Exception {
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.WORKING);
		String script = FastTransduceFrame.pnl.txtOldScript.getText();
		boolean isToVarchar = FastTransduceFrame.pnl.chbIsSetToVarchar.isSelected();
		String sqlType = FastTransduceFrame.pnl.grpSQLType.getSelection().getActionCommand();
		String result = FastTransduceService.transduceSetExcute(script, isToVarchar,sqlType);
//		String result = FamilyMartFileTransduceService.transduceSQLScript(script);
		FastTransduceFrame.pnl.txtNewScript.setText(result);
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.SUCCESS);
	}
}
