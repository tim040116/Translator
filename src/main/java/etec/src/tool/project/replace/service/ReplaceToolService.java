package etec.src.tool.project.replace.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReplaceToolService {
	public static List<String[]> getReplaceList(String strf) throws FileNotFoundException, IOException{
		List<String[]> lstrpl = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(new File(strf));
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {
			while (br.ready()) {
				String line = br.readLine().replace("\uFEFF","");
				if(line.matches("\\s*")) {
					continue;
				}
				String[] arr = line.split(",");
				lstrpl.add(arr);
			}
		}
		return lstrpl;
	}
}
