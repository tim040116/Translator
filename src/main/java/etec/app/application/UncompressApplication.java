package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.security.project.compress.controller.UncompressController;

public class UncompressApplication implements UIApplication {

	@Override
	public void run() {
		try {
			(new UncompressController()).run(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
