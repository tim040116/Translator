package etec.app.main;

import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.framework.translater.exception.TranslateException;
import etec.src.file.ddim.controller.DDIMWriteFileController;
import etec.view.application.AssessmentApplication;
import etec.view.application.AzureFileApplication;
import etec.view.application.FamilyMartApplication;
import etec.view.application.FastTransduceApplication;
import etec.view.application.GreenPlumFileApplication;
import etec.view.application.OldApplication;
import etec.view.application.TranslateStoreFunctionApplication;
import etec.view.application.UIApplication;
import etec.view.application.UncompressApplication;
import test.gp.translater.TestGPTranslater;

public class Main {
	public static void main(String[] args) {
		Log.info("執行項目：" + Params.config.APPLICATION_TYPE);
		switch (Params.config.APPLICATION_TYPE) {
		case "OLD_TRANSLATOR":// 鼎鼎舊版
			OldApplication.run();
			break;
		case "DDIM_TRANSLATOR":// 鼎鼎新板
			UIApplication.run();
			break;
		case "FAMILY_MART_TRANSLATOR":// 全家
			FamilyMartApplication.run();
			break;
		case "ASSESSMENT":// 3 執行 Assessment 作業 包含 3.1 、 3.2
			AssessmentApplication.run();
			break;
//		case "SEARCH_FUNCTION":// 3.1 分析程式後列出清單
//			SearchFunctionApplication.run("方法統計");
//			break;
//		case "SD_MAKER":// 3.2分析CREATE TABLE與法治做成資料表清單
//			SearchDDLApplication.run("SD製作工具");
//			break;

		case "SF_SP":// 3.3轉換sf 跟sp
			TranslateStoreFunctionApplication.run();
			break;
		case "FAST_TRANSDUCE":// 3.4 即時轉換
			FastTransduceApplication.run();
			break;
//		case "GP_TEST":// 4.0 Green Plum 測試
//			try {
//				TestGPTranslater.run();
//			} catch (TranslateException e) {
//				e.printStackTrace();
//			}
//			break;
		case "GP":// 4.1.0.0 Green Plum 轉換
			GreenPlumFileApplication.run();
			break;
		case "AZ":// 4.1.0.0 azure 轉換
			AzureFileApplication.run();
			break;
		case "UNCOMPRESS":
			UncompressApplication.run();
			break;
		default:
			break;
		}
	}
}