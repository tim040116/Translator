package etec.src.tool.project.replace.application;

import etec.framework.code.interfaces.Application;
import etec.src.tool.project.replace.controller.ReplaceToolController;
import etec.src.tool.project.replace.view.ReplaceToolFrame;

public class ReplaceAllApplication  implements Application{
	@Override
	public void run() {
		ReplaceToolFrame frame = new ReplaceToolFrame(new ReplaceToolController());
		frame.setVisible(true);
	}
}
