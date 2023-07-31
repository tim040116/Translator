package test.transformer;

import java.io.IOException;
import java.net.URISyntaxException;

import etec.common.utils.ResourceTool;
import st.etec.sql.transformer.impl.TeradataToAzureSynapseSqlTransformer;

public class TestTeradataToAzureSynapseSqlTransformer {
	
	public static TeradataToAzureSynapseSqlTransformer tf = new TeradataToAzureSynapseSqlTransformer();
	
	public static void testCreateTable() throws IOException, URISyntaxException {
		ResourceTool rt = new ResourceTool();
		String src = rt.readFile("latest_org_dim.txt");
		String res = tf.transformCreateTable(null);
	}
}
