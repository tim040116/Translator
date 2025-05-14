package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.tool.project.replace.view.CompareToolFrame;

public class CompareToolApplication implements UIApplication{

	@Override
	public void run() {
		CompareToolFrame frame = new CompareToolFrame();
		frame.setVisible(true);
	}

}
