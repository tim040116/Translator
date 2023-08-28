package st.etec.main;
import st.etec.src.application.OldApplication;
import st.etec.src.application.SearchFunctionApplication;
import st.etec.src.application.UIApplication;
import st.etec.src.params.ParamsFactory;

public class Main {
	public static void main(String[] args) {
		ParamsFactory.init();
//		OldApplication.run();
		SearchFunctionApplication.run();
//		UIApplication.run();
//		OldApplication.run();
	}
}