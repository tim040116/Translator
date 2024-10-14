package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.translator.project.azure.ddim.controller.DDIMWriteFileController;
import etec.src.translator.view.frame.IndexFrame;
import etec.src.translator.view.listener.WriteFileListener;

public class OldApplication implements Application{

	@Override
	public void run() {

		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
