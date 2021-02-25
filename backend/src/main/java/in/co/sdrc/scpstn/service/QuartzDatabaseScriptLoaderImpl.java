package in.co.sdrc.scpstn.service;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;


//drop table qrtz_fired_triggers;
//DROP TABLE QRTZ_PAUSED_TRIGGER_GRPS;
//DROP TABLE QRTZ_SCHEDULER_STATE;
//DROP TABLE QRTZ_LOCKS;
//drop table qrtz_simple_triggers;
//drop table qrtz_cron_triggers;
//drop table qrtz_simprop_triggers;
//DROP TABLE QRTZ_BLOB_TRIGGERS;
//drop table qrtz_triggers;
//drop table qrtz_job_details;
//drop table qrtz_calendars;
@Service
public class QuartzDatabaseScriptLoaderImpl implements QuartzDatabaseScriptLoader {

	@Autowired
	private DataSource dataSource;

	@Override
	@PostConstruct
	public void loadSQLScript() throws SQLException {
		// TODO Auto-generated method stub
		final Connection connection = dataSource.getConnection();
		try {
			connection.setAutoCommit(false);
			ScriptUtils.executeSqlScript(connection, new EncodedResource(new ClassPathResource("/quartz-postgres.sql"), StandardCharsets.UTF_8));
			connection.commit();
		} finally {
			connection.close();
		}

	}

}
