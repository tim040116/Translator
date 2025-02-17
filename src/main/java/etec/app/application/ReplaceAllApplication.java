package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.tool.project.replace.controller.ReplaceToolController;
import etec.src.tool.project.replace.view.ReplaceToolFrame;

public class ReplaceAllApplication  implements UIApplication{
	@Override
	public void run() {
		ReplaceToolFrame frame = new ReplaceToolFrame(new ReplaceToolController());
		frame.setVisible(true);
	}
}
