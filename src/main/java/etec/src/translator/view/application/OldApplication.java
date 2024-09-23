package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.src.translator.project.azure.ddim.controller.DDIMWriteFileController;
import etec.src.translator.view.frame.IndexFrame;
import etec.src.translator.view.listener.WriteFileListener;

public class OldApplication implements TranslatorApplication{

	public void run() {
		
		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
