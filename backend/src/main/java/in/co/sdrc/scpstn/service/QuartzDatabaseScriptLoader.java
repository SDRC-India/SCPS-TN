package in.co.sdrc.scpstn.service;

import java.sql.SQLException;

public interface QuartzDatabaseScriptLoader {

	public void loadSQLScript() throws SQLException;
	
}
