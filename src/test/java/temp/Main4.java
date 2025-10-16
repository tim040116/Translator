package temp;

import etec.framework.file.readfile.service.ResourceTool;

public class Main4 {

	public static void main(String[] args) {
		try {
			ResourceTool rt = new ResourceTool();
			String content = rt.readFile("sample/fm/exportBTQ/sampleBTQ.btq");
			content = content.replace("${ETEC_outputFile}", "123123123");
			System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
