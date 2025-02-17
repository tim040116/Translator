package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.ddim.controller.DDIMWriteFileController;
import etec.src.translator.view.frame.IndexFrame;
import etec.src.translator.view.listener.WriteFileListener;

public class OldApplication implements UIApplication{

	@Override
	public void run() {

		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
