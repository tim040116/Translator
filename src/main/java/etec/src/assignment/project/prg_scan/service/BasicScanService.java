package etec.src.assignment.project.prg_scan.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class BasicScanService {
	
	protected static final String[] ARR_SCHEMA_NAME = { "PDATA", "PMART", "PTEMP", "PSTAGE" };
	
	protected Sheet sht;
	
	private int rowId = 1;
	
	public BasicScanService(SXSSFWorkbook wb,String sheetName,String[] columns) {
		this.sht = wb.createSheet(sheetName);
		//header
		Row headerRow = sht.createRow(0);
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
		}
	}
	
	protected static String getSchemaName(){return "(?:"+String.join("|",ARR_SCHEMA_NAME)+")";};
	
	protected void addRow(String[] arr) {
		int cellId = 0;
		Row row = sht.createRow(rowId);
		for(String data : arr) {
			row.createCell(cellId).setCellValue(data);
			cellId++;
		}
		rowId++;
	}
	
}
