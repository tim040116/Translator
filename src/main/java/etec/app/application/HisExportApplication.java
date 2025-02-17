package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.fm.hist_export.controller.HisExportController;
import etec.src.translator.project.azure.fm.hist_export.view.CreateExportHisBTQFrame;

public class HisExportApplication implements UIApplication{

	@Override
	public void run() {
		CreateExportHisBTQFrame frame = new CreateExportHisBTQFrame(new HisExportController());
		frame.setVisible(true);
		
	}

}
