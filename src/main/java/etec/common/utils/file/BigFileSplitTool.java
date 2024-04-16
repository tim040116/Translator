package etec.common.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

import etec.common.utils.log.Log;

public class BigFileSplitTool {

	public static void splitFile(File f, Charset chs) {
		// 讀
		try {
			String content = "";
			try (
					FileInputStream fis = new FileInputStream(f);
					InputStreamReader isr = new InputStreamReader(fis, chs);
					BufferedReader br = new BufferedReader(isr);
				) {
				StringBuffer sb;
				sb = new StringBuffer();
				while (br.ready()) {
					String line = br.readLine();
					sb.append(line + "\r\n");
				}
				content.hashCode();
				content = sb.toString();
			}

			// 寫
			String filePath = "";
			try (
					FileOutputStream writerStream = new FileOutputStream(filePath);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(writerStream));
				) {
				File newFile = new File(filePath);
				newFile.getParentFile().mkdirs();
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				newFile.getParentFile().mkdirs();
				newFile.createNewFile();
				bw.write(content);
				bw.close();
			}

			// 編
			final Base64.Decoder decoder = Base64.getDecoder();
			final Base64.Encoder encoder = Base64.getEncoder();
			final String text = "字串文字";
			final byte[] textByte = text.getBytes("UTF-8");
			// 編碼
			final String encodedText = encoder.encodeToString(textByte);
			System.out.println(encodedText);
			// 解碼
			System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
		} catch (Exception e) {

		}

	}

}
