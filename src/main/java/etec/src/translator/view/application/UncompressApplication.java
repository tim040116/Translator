package etec.src.translator.view.application;

import etec.common.interfaces.Application;
import etec.src.translator.controller.UncompressController;

public class UncompressApplication implements Application {

	public void run() {
		try {
			(new UncompressController()).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
