package etec.src.translator.project.azure.fm.poc.controller;

import java.util.Map;

import etec.framework.code.interfaces.Controller;
import etec.framework.ui.search_func.enums.RunStatusEnum;
import etec.src.translator.project.azure.fm.poc.service.FastTransduceService;
import etec.src.translator.sql.gp.translater.GreenPlumTranslater;
import etec.src.translator.view.frame.FastTransduceFrame;

/**
 * @author	Tim
 * @since	2023年11月8日
 * @version	3.4.1.1
 * 快入轉換
 *
 * */
public class FastTransduceController implements Controller {

	@Override
	public void run(Map<String,Object> args) throws Exception {
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.WORKING);
		String script = FastTransduceFrame.pnl.txtOldScript.getText();
		boolean isToVarchar = FastTransduceFrame.pnl.chbIsSetToVarchar.isSelected();
		String sqlType = FastTransduceFrame.pnl.grpSQLType.getSelection().getActionCommand();
		String result = "";
		switch(sqlType) {
			case "gp" :
				result = GreenPlumTranslater.dql.easyReplace(script);
				break;
			case "az" :
			case "ms" :
				result = FastTransduceService.transduceSetExcute(script, isToVarchar,sqlType);
				break;
		}
//		String result = FamilyMartFileTransduceService.transduceSQLScript(script);
		FastTransduceFrame.pnl.txtNewScript.setText(result);
		FastTransduceFrame.pnl.statusBar.setStatus(RunStatusEnum.SUCCESS);
	}
}
