package st.etec.application;
import etec.common.annotation.Application;
import src.java.view.frame.IndexFrame;

@Application
public class Main {
	public static void main(String[] args) {
		//新增視窗
		new IndexFrame();
	}
}