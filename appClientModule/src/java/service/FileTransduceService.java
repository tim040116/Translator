package src.java.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import src.java.enums.FileTypeEnum;
import src.java.model.BasicModel;
import src.java.tools.ReadFileTool;

public class FileTransduceService {
	public static String run(File f) throws IOException {
		String result  = "";
		String fn  = f.getPath()+f.getName();
		String fc = ReadFileTool.readFile(f);
		//區分種類
		FileTypeEnum type =  getType(fc);
		switch (type) {
		case EXPORT:
			result = buildExportFile(fn,fc);
		break;
		case FASTLOAD:
			result = buildFastloadFile(fn,fc);
		break;
		case TRANSACTION:
			result = buildTransactionFile(fn,fc);
			break;
		}
		result = searchSelect(result);
		return result;
	}
	private static String buildExportFile(String fn,String fc) {
		System.out.println("buildExportFile");
		String result = "";
		return result;
	}
	//FAST　LOAD
	private static String buildFastloadFile(String fn,String fc) throws IOException {
		System.out.println("buildFastloadFile");
		String result = "";
		String file = BasicModel.getOutputPath()+"lst\\lst_fastload.txt";
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
	private static String buildTransactionFile(String fn,String fc) {
		String result = "";
		System.out.println("buildTransactionFile");

		return result;
	}
	//取得每一段select語句
	private static String searchSelect(String c) {
		String result = "";
		String stat = "";
		String select = "";
		for(String line : c.split("\r\n")) {
			if(line.toUpperCase().contains("SELECT")) {
				stat = "select";
			} else {
				//result += line;
			}
			if("select".equals(stat)){
				//處理
				select = select + line+" ";
				if(line.toUpperCase().contains(";")) {
					result = result + transduceSelect(select) + "\r\n";
					select = "";
					stat = "";
				}
			}
		}
		return result;
	}
	//處理所有置換
	private static String transduceSelect(String c) {
		String result = c;
		result = upperCase(result);
		result = pureReplace(result);
		
		
		return result;
	}
	//SQL 語句換成全大寫
	private static String upperCase(String c) {
		String result = "";
		for(String str : c.split(" ")) {
			//欄位命名不要改大寫
			if(Pattern.matches("[\"|'].*[\"|']", str)) {
				result += str+" ";
			}else {
				result += str.toUpperCase()+" ";
			}
		}
		result = result.replaceAll("\\s+", " ");
		return result;
	}
	//區分類型
	private static FileTypeEnum getType(String c) {
		String t = c.toUpperCase().replaceAll("\\s+", "");
		if("OUTPUT_FILE".contains(t)) {
			return FileTypeEnum.EXPORT;
		}else if(t.contains("BEGINLOADING")) {
			return FileTypeEnum.FASTLOAD;
		}else {
			return FileTypeEnum.TRANSACTION; 
		}
	}
	//單純置換字串
	private static String pureReplace(String c) {
		String result = c;
		result = result
					.replaceAll("\\|\\|", "+")
					.replaceAll("SUBSTR", "SUBSTRING")
				;
		return result;
	}
}
