package etec.view.application;

import etec.common.interfaces.Controller;
import etec.view.frame.IndexFrame;
import etec.view.listener.WriteFileListener;

public class OldApplication {

	public static void run(Controller con) {
		
		WriteFileListener lr = new WriteFileListener(con);
		new IndexFrame(lr);
	}
}
