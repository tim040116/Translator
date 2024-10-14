package etec.app.main;

import java.nio.charset.Charset;

import etec.framework.file.readfile.service.FileTool;
import etec.src.security.project.login.service.CreateKeyFileService;

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
			String id = "etec";
			String pass = "abc123";
			String limitDate = "2026/12/31";
			String key = CreateKeyFileService.print(id, pass, limitDate);
			System.out.println(key);
			FileTool.overrideFile("sec.txt",Charset.forName("UTF-8"),key);
			System.out.println("加密完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
