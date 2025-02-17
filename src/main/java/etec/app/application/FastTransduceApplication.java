package etec.app.application;

import etec.framework.code.interfaces.UIApplication;
import etec.src.translator.project.azure.fm.poc.controller.FastTransduceController;
import etec.src.translator.view.frame.FastTransduceFrame;
/**
 * <h1>即時轉換功能</h1>
 * <p></p>
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p>run</p>
 *
 * <h2>異動紀錄</h2>
 * <br>2023年11月06日	Tim	建立功能
 * <br>2024年02月20日	Tim	新增GP
 *
 *
 * @author	Tim
 * @version	4.0.0.0
 * @since	3.3.4.0
 * @see		FastTransduceFrame
 * @see		FastTransduceController
 */
public class FastTransduceApplication  implements UIApplication{
	@Override
	public void run() {
		new FastTransduceFrame("即時轉換",new FastTransduceController());
		//設定SQL語言的選項
		FastTransduceFrame.pnl.addLanguageRadio("ms", "MS SQL");
		FastTransduceFrame.pnl.addLanguageRadio("az", "Azure");
		FastTransduceFrame.pnl.addLanguageRadio("gp", "Green Plum");
		FastTransduceFrame.pnl.maprdo.get("gp").setSelected(true);
	}
}
