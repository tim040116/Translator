package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;

public class ReaderListener implements ActionListener {
	JTextField text;
	JList<File> jl;

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultListModel<File> dlm = new DefaultListModel<File>();
		try {
			jl.removeAll();
			List<File> lf = src.java.tools.FileReader.getFileList(text.getText());
			for (File f : lf) {
				dlm.addElement(f);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		jl.setModel(dlm);
	}

	public void setJTextField(JTextField text) {
		this.text = text;
	}

	public void setJList(JList<File> jl) {
		this.jl = jl;
	}

}
