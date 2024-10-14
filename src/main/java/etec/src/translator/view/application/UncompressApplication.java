package etec.src.translator.view.application;

import etec.framework.code.interfaces.Application;
import etec.src.security.project.compress.controller.UncompressController;

public class UncompressApplication implements Application {

	@Override
	public void run() {
		try {
			(new UncompressController()).run(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
