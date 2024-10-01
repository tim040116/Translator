package etec.src.translator.view.application;

import etec.common.interfaces.TranslatorApplication;
import etec.src.security.project.compress.controller.UncompressController;

public class UncompressApplication implements TranslatorApplication {

	@Override
	public void run() {
		try {
			(new UncompressController()).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
