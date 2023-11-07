package etec.common.factory;

import etec.common.view.panel.FileButton;
import etec.common.view.panel.LogTextArea;
import etec.common.view.panel.ProgressBar;
import etec.common.view.panel.StatusBar;

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
