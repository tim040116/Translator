package etec.src.translator.view.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import etec.common.view.panel.FileButton;

/**
 * 輸入讀檔產檔路徑的頁面
 *
 * @author	Tim
 * @since	2023/03/28
 * @version	dev
 *
 */
public class ChooseIOPathPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// 按鈕
	private JButton btn;
	// 文字
	private JTextField text;
	// 選擇器
	private JFileChooser chooser;
	//讀檔
	private FileButton readPath;
	public ChooseIOPathPanel() {

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
}
