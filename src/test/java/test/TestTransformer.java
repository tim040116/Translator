package test;

import java.io.IOException;
import java.net.URISyntaxException;

import test.transformer.TestTeradataToAzureSynapseSqlTransformer;

public class TestTransformer {
	public static void test() throws IOException, URISyntaxException {
		TestTeradataToAzureSynapseSqlTransformer.testCreateTable();
	}
}
