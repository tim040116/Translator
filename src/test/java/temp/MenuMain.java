package temp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.reflections.Reflections;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import etec.app.main.Main;
import etec.framework.code.annotation.UIAppId;
import etec.framework.code.interfaces.UIApplication;

public class MenuMain {
	
	
	public static void main(String[] args) {
		try {
			Map<String,UIApplication> map = ApplicationScanner("etec.app.application");
//			for(Entry<String,UIApplication> en : map.entrySet()) {
//				
//			}
			readXml();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish");
	}
	
	public static void readXml() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		String file = "config/xml/Menu_Data.xml";

		try(
			InputStream in = Main.class.getClassLoader().getResourceAsStream("META-INF/"+file);
		){
			//讀取xml檔
			Document doc = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(in);
			XPath xp = XPathFactory.newInstance().newXPath();
			String expression = "";
			@SuppressWarnings("unused")
			NodeList ndlst = (NodeList)xp.compile(expression).evaluate(doc,XPathConstants.NODESET);
			for(int i=0;i<ndlst.getLength();i++) {
				Node nd = ndlst.item(i);
			}
			
		}
	}
	
	
	public static Map<String,UIApplication> ApplicationScanner(String packageName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Reflections rfl = new Reflections(packageName);
		Map<String,UIApplication> map = new HashMap<String,UIApplication>();
		for(Class<?> clazz : rfl.getTypesAnnotatedWith(UIAppId.class)) {
			UIAppId uia = clazz.getAnnotation(UIAppId.class);
			String code = uia.value();
			UIApplication appi =(UIApplication)clazz.getDeclaredConstructor().newInstance();
			map.put(code, appi);
		}
		return map;
	}
}
