package etec.src.file.assignment.service.create_list;

import etec.framework.excel_maker.annotation.Column;
import etec.framework.excel_maker.annotation.SheetModel;

@SheetModel(sheetName = "call_sp_list")
public class CallListModel {
	
	@Column(name="PATH_NAME")
	private String pathNm;
	
	@Column(name="FILE_NAME")
	private String fileNm;
	
	@Column(name="SP_NAME")
	private String spNm;
	
	@Column(name="PARAMS")
	private String params;
	
}
