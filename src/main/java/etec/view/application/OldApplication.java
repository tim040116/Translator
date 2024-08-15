package etec.view.application;

import etec.common.interfaces.Application;
import etec.src.file.ddim.controller.DDIMWriteFileController;
import etec.view.frame.IndexFrame;
import etec.view.listener.WriteFileListener;

public class OldApplication implements Application{

	public void run() {
		
		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
