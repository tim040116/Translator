package etec.framework.excel_maker.reader;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import etec.framework.excel_maker.util.POIUtil;

public class XlsReader {
	
	public static void readLine(String path) throws IOException{
		//讀檔
		Workbook wb = POIUtil.read(path);
		//分析
		for(Sheet sheet : wb) {//取得分頁
			for (Row row : sheet) {//取得每一行
				if(POIUtil.hasData(row)) {continue;}//跳過空行
				for(Cell cell : row) {//取得欄位
					if (cell.getCellType() == CellType.BLANK) {continue;}//跳過空格
				}
			}
		}
	}
}
