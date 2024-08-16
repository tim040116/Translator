package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.file.ddim.controller.DDIMWriteFileController;
import etec.src.translator.view.frame.IndexFrame;
import etec.src.translator.view.listener.WriteFileListener;

public class OldApplication implements Application{

	public void run() {
		
		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
