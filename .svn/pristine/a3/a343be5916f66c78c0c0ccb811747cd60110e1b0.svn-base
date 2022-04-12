package src.java.service.transducer;

import java.io.IOException;

import src.java.params.BasicParams;
import src.java.tools.ReadFileTool;

public class FastloadTransducer {
	public static String run(String fn,String fc) throws IOException {
		String result = "Success";
		createFastloadLst(fn,fc);
		return result;
	}
	//FAST　LOAD
	private static String createFastloadLst(String fn,String fc) throws IOException {
		System.out.println("fastload");
		String result = "";
		String file = BasicParams.getOutputPath()+"lst\\lst_fastload.txt";
		String txt="";
		boolean flag = false;
		for(String line : fc.split("\r\n")) {
			line = line.replaceAll("\\s+"," ");
			if(line.contains("SET RECORD")) {
				flag = true;
				String rec = line;
				if(!line.contains("UNFORMATTED")) {
					rec = rec.replace("VARTEXT", "").replace("DISPLAY_ERRORS NOSTOP", "");
				}
				rec = rec.replace("SET RECORD", "").replace(" ", "").replace(";", "");
				 txt= fn + " " + rec;
			}
			if(flag) {
				if(line.contains("INSERT INTO")) {
					String ins = line.replace("INSERT INTO", "").replace("(", "");
					txt = txt + ins;
					ReadFileTool.addFile(file, txt);
				}
			}
		}
		return result;
	}
}
