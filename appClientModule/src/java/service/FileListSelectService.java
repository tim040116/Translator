package src.java.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;

import src.java.model.IOPathModel;
import src.java.tools.ReadFileTool;

public class FileListSelectService {
	//取得檔案清單
	public static DefaultListModel<File> getFileList() throws IOException{
		DefaultListModel<File> dlm = new DefaultListModel<File>();
		List<File> lf = ReadFileTool.getFileList(IOPathModel.getInputPath());
		IOPathModel.setListFile(lf);
		for (File f : lf) {
			dlm.addElement(f);
		}
		return dlm;
	}
}
