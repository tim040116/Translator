package st.etec.main;
import st.etec.application.OldApplication;
import st.etec.application.UIApplication;
import st.etec.params.ParamsFactory;

public class Main {
	public static void main(String[] args) {
		ParamsFactory.init();
		OldApplication.run();
//		UIApplication.run();
//		OldApplication.run();
	}
}