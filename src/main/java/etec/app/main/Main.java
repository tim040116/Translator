package etec.app.main;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.src.sql.az.controller.DDIMWriteFileController;
import etec.view.application.FamilyMartApplication;
import etec.view.application.FastTransduceApplication;
import etec.view.application.OldApplication;
import etec.view.application.SearchFunctionApplication;
import etec.view.application.TranslateStoreFunctionApplication;
import etec.view.application.UIApplication;

public class Main {
	public static void main(String[] args) {
		Log.info("執行項目："+Params.config.APPLICATION_TYPE);
		switch (Params.config.APPLICATION_TYPE) {
		case "OLD_TRANSLATOR"://鼎鼎舊版
			OldApplication.run(new DDIMWriteFileController());
			break;
		case "DDIM_TRANSLATOR"://鼎鼎新板
			UIApplication.run();
			break;
		case "FAMILY_MART_TRANSLATOR"://全家
			FamilyMartApplication.run();
			break;
		case "SEARCH_FUNCTION"://分析程式後列出清單
			SearchFunctionApplication.run("方法統計");
			break;
		case "SD_MAKER"://3.2分析CREATE TABLE與法治做成資料表清單
			SearchFunctionApplication.run("SD製作工具");
			break; 
		case "SF_SP"://3.3轉換sf 跟sp
			TranslateStoreFunctionApplication.run();
			break;
		case "FAST_TRANSDUCE"://3.4 即時轉換
			FastTransduceApplication.run();
			break;
		default:
			break;
		}
		
		
		
	}
}