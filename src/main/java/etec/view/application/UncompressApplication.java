package etec.view.application;

import etec.common.interfaces.Application;
import etec.src.controller.UncompressController;

public class UncompressApplication implements Application {

	public void run() {
		try {
			(new UncompressController()).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
