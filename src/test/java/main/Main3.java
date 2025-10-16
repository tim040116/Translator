package main;

import etec.framework.file.xml_reader.XmlReader;

/**
 * 列出source target table清單
 * 
 * 
 * */
public class Main3 {

	public static void main(String[] args) {
		try {
//			XmlReader xml = XmlReader.readResourceFile("config/xml/Menu_Data.xml");			
//			for(XmlReader i : xml) {
//				System.out.println(i.getKey()+" "+i.size());
//				for(XmlReader ii : i) {
//					System.out.println("\t"+ii.getKey()+" "+ii.size());
//					for(XmlReader iii : ii) {
//						System.out.println("\t\t"+iii.getKey()+" "+iii.size());
//						
//					}
//				}
//			}
			
			XmlReader xml = new XmlReader();
			System.out.println(xml.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("");

	}



	
}


