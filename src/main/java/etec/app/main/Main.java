package etec.app.main;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Application;
import etec.framework.security.log.service.Log;
import etec.src.tool.project.replace.application.ReplaceAllApplication;
import etec.src.translator.view.application.AssessmentApplication;
import etec.src.translator.view.application.AzureFileApplication;
import etec.src.translator.view.application.FamilyMartApplication;
import etec.src.translator.view.application.FastTransduceApplication;
import etec.src.translator.view.application.GreenPlumFileApplication;
import etec.src.translator.view.application.OldApplication;
import etec.src.translator.view.application.SearchDDLApplication;
import etec.src.translator.view.application.TranslateStoreFunctionApplication;
import etec.src.translator.view.application.UIApplication;
import etec.src.translator.view.application.UncompressApplication;

public class Main {


	public static void main(String[] args) {
		Log.info("執行項目：" + Params.config.APPLICATION_TYPE);
		Application app = null;
		switch (Params.config.APPLICATION_TYPE) {
			case "OLD_TRANSLATOR":// 鼎鼎舊版
				app = new OldApplication();
				break;
			case "DDIM_TRANSLATOR":// 鼎鼎新板
				app = new UIApplication();
				break;
			case "FM":// 全家
				app = new FamilyMartApplication();
				break;
			case "ASSESSMENT":// 3 執行 Assessment 作業 包含 3.1 、 3.2
				app = new AssessmentApplication();
				break;
	//		case "SEARCH_FUNCTION":// 3.1 分析程式後列出清單
	//			SearchFunctionApplication.run("方法統計");
	//			break;
			case "SD_MAKER":// 3.2分析CREATE TABLE與法治做成資料表清單
				app = new SearchDDLApplication();
				break;
	
			case "SF_SP":// 3.3轉換sf 跟sp
				app = new TranslateStoreFunctionApplication();
				break;
			case "FAST_TRANSDUCE":// 3.4 即時轉換
				app = new FastTransduceApplication();
				break;
	//		case "GP_TEST":// 4.0 Green Plum 測試
	//			try {
	//				TestGPTranslater();
	//			} catch (TranslateException e) {
	//				e.printStackTrace();
	//			}
	//			break;
			case "PG":
			case "GP":// 4.1.0.0 Green Plum 轉換
				app = new GreenPlumFileApplication();
				break;
			case "AZ":// 4.1.0.0 azure 轉換
				app = new AzureFileApplication();
				break;
			case "UNCOMPRESS":
				app = new UncompressApplication();
				break;
			case "REPLACE_ALL"://5.2
				app = new ReplaceAllApplication();
				break;
			default:
				break;
		}
//		LoginFrame frame = new LoginFrame(app,new CheckLoginService(),VersionModel.VERSION);
//		frame.setVisible(true);
		app.run();
	}
}