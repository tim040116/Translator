package etec.src.file.assignment.service.create_list;

import etec.common.csv_maker.annotation.CSVModel;
import etec.common.csv_maker.annotation.Column;

@CSVModel(fileName = "call_sp_list")
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