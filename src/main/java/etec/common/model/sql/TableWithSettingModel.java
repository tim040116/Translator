package etec.common.model.sql;

public class TableWithSettingModel {

	private String clustered;

	private String distribution;

	private String partition;

	public String getClustered() {
		return clustered;
	}

	public void setClustered(String clustered) {
		this.clustered = clustered;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

}
