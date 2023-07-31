package etec.common.view.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 選擇讀取檔案並顯示在旁邊的text
 * @author	Tim
 * @since	2023/03/06
 * @version	23.3.1.1
 * 
 */
public class FileButton extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//按鈕
	private JButton btn;
	//文字
	private JTextField text;
	//選擇器
	private JFileChooser chooser;
	
	public FileButton() {
		//初始化
		btn = new JButton("檔案");
		text = new JTextField(100);
		chooser = new JFileChooser();
		btn.setPreferredSize(new Dimension(70,30));
		text.setPreferredSize(new Dimension(100,30));
		//檔案讀取的彈跳視窗
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	                int option = chooser.showOpenDialog(null);
	                if(option == JFileChooser.APPROVE_OPTION){
	                    File file = chooser.getSelectedFile();
	                    text.setText(file.getPath());
	                }
			}
		});
		//設置物件
		add(btn);
		add(text);
		setVisible(true);
	}
	/**
	 * 取得選擇的檔案路徑
	 * */
	public String getFilePath() {
		return text.getText();
	}
	public JButton getBtn() {
		return btn;
	}

	public JTextField getText() {
		return text;
	}

	public JFileChooser getChooser() {
		return chooser;
	}
}
