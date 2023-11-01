package etec.src.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import etec.common.enums.RunStatusEnum;
import etec.common.model.BasicParams;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import etec.main.Params;
import etec.src.controller.SearchFunctionController;
import etec.src.interfaces.Controller;
import etec.src.service.CreateListService;
import etec.src.service.IOpathSettingService;
import etec.src.service.SearchFunctionService;
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
					SearchFunctionPnl.setStatus(RunStatusEnum.FAIL);
					e1.printStackTrace();
					SearchFunctionPnl.setLog("錯誤",e1.getMessage());
				}
			}
		}.start();
	}
	
	public SearchFunctionListener(Controller controller) {
		this.con = controller;
	}
	
}
