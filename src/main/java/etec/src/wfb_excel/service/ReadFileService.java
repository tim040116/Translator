package etec.src.wfb_excel.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.excel_maker.util.POIUtil;

public class ReadFileService {
	/**
	 * <h1>將直式的資料轉為衡的</h1>
	 * <p>將excel轉成excel</p>
	 * <p>
	 * <br>原檔為直式資料,一個sheet為一筆資料
	 * <br>會有空行或合併儲存格
	 * <br>固定col 1是標題 col 2是資料
	 * </p>
	 * <p>
	 * <br>
	 * </p>
	 * <h2>異動紀錄</h2>
	 * <br>2024年7月11日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static void compile(String readPath,String writePath) throws IOException{
		Map<String,Integer> mapTitle = new HashMap<String,Integer>();//所有header及對應的col id
		int lastColId = 0;//新增header的col id
		int lastRowId = 1;//最後一筆的ID
		//建檔
		File f = new File(writePath); 
        if(!f.exists()) {f.createNewFile();}
		try (
			Workbook rwb = POIUtil.read(readPath);//原檔	
			SXSSFWorkbook wwb = new SXSSFWorkbook(100);//新檔
			FileOutputStream fos = new FileOutputStream(writePath);//寫檔
		){
			//新檔案初始化
			Sheet ws = wwb.createSheet("資料清單");
			Row header = ws.createRow(0);
			for(Sheet sheet : rwb) {//取得分頁
				Row nr = ws.createRow(lastRowId);//新增行
				for (Row row : sheet) {//取得每一行
					//取得值
					String key = "";//標題
					String val = "";//內容
					int flag = 0;
					for(Cell cell : row) {//取得每一格
						if(cell.getCellType() == CellType.BLANK) {continue;}//跳過空格
						//塞入值
						else if(flag==0) {
							key = POIUtil.getStringValue(cell).trim();
							flag++;
						}
						else if(flag==1) {
							val = POIUtil.getStringValue(cell).trim();
							flag++;
						}
						else if(flag==2) {break;}
					}
					nr.createCell(mapTitle.get(key)).setCellValue(val);//寫入資料
				}
				lastRowId++;
			}
			wwb.write(fos);//寫入檔案
		}
	}
}
