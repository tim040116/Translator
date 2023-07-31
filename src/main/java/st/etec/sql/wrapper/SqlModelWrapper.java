package st.etec.sql.wrapper;

import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.SelectTableModel;

public abstract class SqlModelWrapper {
	
	public abstract CreateTableModel createTable(String sql);
	
	public abstract SelectTableModel selectTable(String sql);
}
