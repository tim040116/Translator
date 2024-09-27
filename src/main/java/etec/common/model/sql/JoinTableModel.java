package etec.common.model.sql;

import java.util.List;

import etec.framework.context.translater.enums.JoinTypeEnum;

public class JoinTableModel {

	private JoinTypeEnum joinType;

	private String tableName;

	private List<String> lstCondition;

	public JoinTypeEnum getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinTypeEnum joinType) {
		this.joinType = joinType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getLstCondition() {
		return lstCondition;
	}

	public void setLstCondition(List<String> lstCondition) {
		this.lstCondition = lstCondition;
	}


}
