package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		String fileName = "doc/版本紀錄";
		InputStream in = Main.class.getResourceAsStream("/META-INF/"+fileName);
		
		try (
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		) {
			while(br.ready()) {
				String line = br.readLine();
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
