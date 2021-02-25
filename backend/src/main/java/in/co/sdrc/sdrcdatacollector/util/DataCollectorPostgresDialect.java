package in.co.sdrc.sdrcdatacollector.util;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL94Dialect;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

public class DataCollectorPostgresDialect extends PostgreSQL94Dialect {

	public DataCollectorPostgresDialect() {
		super();
//		this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
		this.registerHibernateType(
	            Types.OTHER, JsonNodeBinaryType.class.getName());
	}
}
