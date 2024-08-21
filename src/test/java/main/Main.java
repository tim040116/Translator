package main;

import etec.src.login.review.LoginReviewer;
import etec.src.login.view.LoginFrame;
import etec.src.translator.sql.az.translater.SQLTranslater;
import etec.src.translator.view.application.FamilyMartApplication;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		try {
//			LoginFrame frame = new LoginFrame(new FamilyMartApplication(),new LoginReviewer());
//			frame.setVisible(true);
			String res = SQLTranslater.easyReplaceSelect("DECODE(CS_CR_FEE,'05',1,'06',-1) as FG_ADD_TYPE\r\n" + 
					",DECODE(CS_CR_FEE,'05',1,'06',-1,asd) as FG_ADD_TYPE");
			System.out.println(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
