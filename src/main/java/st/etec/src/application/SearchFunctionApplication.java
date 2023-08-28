package st.etec.src.application;

import etec.common.annotation.Application;
import src.java.view.frame.IndexFrame;
import src.java.view.frame.SearchFunctionFrame;

@Application
public class SearchFunctionApplication {

	public static void run() {
		new SearchFunctionFrame();
	}
}
