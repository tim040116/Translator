package etec.src.translator.project.azure.fm.hist_export.application;

import etec.framework.code.interfaces.Application;
import etec.src.translator.project.azure.fm.hist_export.controller.HisExportController;
import etec.src.translator.project.azure.fm.hist_export.view.CreateExportHisBTQFrame;

public class HisExportApplication implements Application{

	@Override
	public void run() {
		CreateExportHisBTQFrame frame = new CreateExportHisBTQFrame(new HisExportController());
		frame.setVisible(true);
		
	}

}
