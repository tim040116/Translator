package temp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.file.readfile.service.FileTool;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *處理DDL的position的問題
 * */
public class Main2 {

	public static void main(String[] args) {
		try {
			List <File> lstf = FileTool.getFileList("C:\\Users\\user\\Desktop\\Trans\\Target");
			for(File f : lstf) {
				String content = FileTool.readFile(f);
				String newContent = "";
				content = content
					.replaceAll("[\\S\\s]+<Columns>", "")
					.replaceAll("<\\/Columns>[\\S\\s]+", "")
				;
				List<String> lstSubstr = new ArrayList<String>();
				String reg = "<Column>(?:\\s*(?:"
						+  "<ColumnName>(?<ColumnName>\\w+)<\\/ColumnName>"
						+ "|<StartPosition>(?<StartPosition>\\d+)<\\/StartPosition>"
						+ "|<EndPosition>(?<EndPosition>\\d+)<\\/EndPosition>"
						+ "|<[^>]+>[^<]+<\\/[^>]+>)"
						+ ")+\\s*<\\/Column>";
				Matcher m = Pattern.compile(reg).matcher(content);
				while(m.find()) {
					String colNm = m.group("ColumnName").toLowerCase();
					int begPst = Integer.parseInt(m.group("StartPosition"));
					int endPst = Integer.parseInt(m.group("EndPosition"));
					String str = "substring(data_row,"+begPst+","+(endPst-begPst+1)+") AS " + colNm;
					lstSubstr.add(str);
				}
				newContent = "select\r\n\t "
						+ String.join("\r\n\t,", lstSubstr)
						+ "\r\nfrom ";
				FileTool.addFile("C:\\Users\\user\\Desktop\\Trans\\Assessment_Result\\"+f.getName(), newContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
