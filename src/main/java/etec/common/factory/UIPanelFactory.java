package etec.common.factory;

import etec.common.view.panel.FileButton;

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
	 * 
	 * 檔案選擇器
	 * @author Tim
	 * @version dev
	 * @since 2023/03/28
	 */
	public static FileButton addFileButton() {
		return new FileButton();
	}
}
