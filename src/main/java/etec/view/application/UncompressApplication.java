package etec.view.application;

import etec.common.annotation.Application;
import etec.src.controller.UncompressController;

@Application("UNCOMPRESS")
public class UncompressApplication {

	public static void run() {
		try {
			(new UncompressController()).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
