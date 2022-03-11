package src.java.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JList;
import javax.swing.JTextField;

public class ReaderListener implements ActionListener {
	JTextField text;
	JList<File> jl;

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	public void setJTextField(JTextField text) {
		this.text = text;
	}

	public void setJList(JList<File> jl) {
		this.jl = jl;
	}

}
