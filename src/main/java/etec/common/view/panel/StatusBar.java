package etec.common.view.panel;

import java.awt.Color;

import javax.swing.JLabel;

import etec.common.enums.RunStatusEnum;

public class StatusBar extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public StatusBar() {
		setOpaque(true);
		//初始化
		setVisible(true);
	}
	public void setStatus(RunStatusEnum status) {
		switch (status) {
		case START:
			setBackground(new Color(255,255,255));
			setText("就緒");
			break;
		case WORKING:
			setBackground(new Color(255,125,0));
			setText("執行中...");
			break;
		case SUCCESS:
			setBackground(new Color(0,255,0));
			setText("成功");
			break;
		case FAIL:
			setBackground(new Color(255,0,0));
			setText("失敗");
			break;
		}
	}
}
