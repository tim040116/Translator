package etec.src.application;

import etec.common.interfaces.Controller;
import etec.src.listener.WriteFileListener;
import etec.view.frame.IndexFrame;

public class OldApplication {

	public static void run(Controller con) {
		
		WriteFileListener lr = new WriteFileListener(con);
		new IndexFrame(lr);
	}
}
