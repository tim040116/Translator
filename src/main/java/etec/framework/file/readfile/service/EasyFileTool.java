package etec.framework.file.readfile.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/*檔案處理的工具
 * */
public class EasyFileTool {
	//read file
	private FileInputStream fis;
	private InputStreamReader isr;
	private BufferedReader br;
	//write file
	private FileOutputStream fos;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	//write gzip
	private FileOutputStream gfos;
	private GZIPOutputStream gos;
	private BufferedOutputStream gbos;

	public BufferedReader getBr() {
		return br;
	}
	public void startRead(File f) throws IOException {
		startRead(f,"UTF-8");
	}
	public void startRead(File f,String charset) throws IOException {
		if (!f.exists()) {
			throw new IOException("File doesn't exist");
		}
		fis = new FileInputStream(f);
		isr = new InputStreamReader(fis,charset);
		br = new BufferedReader(isr);
	}
	public void startWrite(File newFile) throws IOException{
		newFile.getParentFile().mkdirs();
		if (!newFile.exists()) {
			newFile.createNewFile();
		}
		fos = new FileOutputStream(newFile, true);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bw = new BufferedWriter(osw);
	}
	public void startGZIPWrite(File newFile) throws IOException{
		newFile.getParentFile().mkdirs();
		if (!newFile.exists()) {
			newFile.createNewFile();
		}
		gfos = new FileOutputStream(newFile);
		gos = new GZIPOutputStream(gfos);
		gbos = new BufferedOutputStream(gos);
	}
	public void write(String content) throws IOException {
		bw.write(content);
	}
	public void writeline(String content) throws IOException {
		bw.write(content+"\r\n");
	}
	public void writecsv(String[] arr,String split,String quote) throws IOException {
		boolean f = false;
		for(String col : arr) {
			bw.write((f?split:"")+quote+col+quote);
			if(!f){f=true;}
		}
		bw.write("\r\n");
	}
	public void writeGZIP() throws IOException {
		int c;
		while ((c = br.read()) != -1) {
			gbos.write(String.valueOf((char) c).getBytes("UTF-8"));
		}
	}
	public void writeGZIP(String content) throws IOException {
		gbos.write(content.getBytes("UTF-8"));
	}
	public void close() throws IOException {
		closeRead();
		closeWrite();
		closeWriteGZIP();
	}
	public void closeRead() throws IOException {
		if (br != null) {
			br.close();
		}
		if (isr != null) {
			isr.close();
		}
		if (fis != null) {
			fis.close();
		}
	}
	public void closeWrite() throws IOException {
		if (bw != null) {
			bw.close();
		}
		if (osw != null) {
			osw.close();
		}
		if (fos != null) {
			fos.close();
		}
	}
	public void closeWriteGZIP() throws IOException {
		if (gbos != null) {
			gbos.close();
		}
		if (gos != null) {
			gos.close();
		}
		if (gfos != null) {
			gfos.close();
		}
	}


}
