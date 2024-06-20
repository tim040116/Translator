package etec.view.application;

import etec.src.file.ddim.controller.DDIMWriteFileController;
import etec.view.frame.IndexFrame;
import etec.view.listener.WriteFileListener;

public class OldApplication {

	public static void run() {
		
		WriteFileListener lr = new WriteFileListener(new DDIMWriteFileController());
		new IndexFrame(lr);
	}
}
