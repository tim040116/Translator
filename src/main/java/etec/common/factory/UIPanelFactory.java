package etec.common.factory;

import etec.framework.ui.search_func.pnl.FileButton;
import etec.framework.ui.search_func.pnl.LogTextArea;
import etec.framework.ui.search_func.pnl.ProgressBar;
import etec.framework.ui.search_func.pnl.StatusBar;

/**
 * UI介面的小物件
 *
 * @author Tim
 * @version dev
 * @since 2023/03/28
 *
 */
public class UIPanelFactory {

	/**
	 * @author Tim
	 * @version dev
	 * @since 2023/03/28
	 *
	 * 檔案選擇器
	 */
	public static FileButton addFileButton() {
		return new FileButton();
	}
	/**
	 * @author Tim
	 * @version 3.4.0.1
	 * @since 2023/11/07
	 *
	 * 狀態條
	 */
	public static StatusBar addStatusBar() {
		return new StatusBar();
	}

	/**
	 * @author Tim
	 * @version 3.4.0.1
	 * @since 2023/11/07
	 *
	 * 進度條
	 */
	public static ProgressBar addProgressBar() {
		return new ProgressBar();
	}
	/**
	 * @author Tim
	 * @version 3.4.0.1
	 * @since 2023/11/07
	 *
	 * Log記錄欄
	 */
	public static LogTextArea addLogTextArea() {
		return new LogTextArea();
	}


}
