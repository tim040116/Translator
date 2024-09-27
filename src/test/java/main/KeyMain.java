package main;

import etec.src.security.login.service.CreateKeyFileService;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class KeyMain {

	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";

	public static void main(String[] args) {
		try {
//			LoginFrame frame = new LoginFrame(new FamilyMartApplication(),new LoginReviewer());
//			frame.setVisible(true);
			System.out.println(CreateKeyFileService.print());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
