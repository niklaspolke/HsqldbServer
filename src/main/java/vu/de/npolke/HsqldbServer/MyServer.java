package vu.de.npolke.HsqldbServer;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;

public class MyServer {
	public static final String CMD_START = "start";
	public static final String CMD_STOP = "stop";

	public static final String FILE_PROPERTIES = "server";
	public static final String FILE_LOGGING = "hsqlServer.log";

	public static final int CONNECTION_PORT = 9001;
	public static final String CONNECTION = "jdbc:hsqldb:hsql://localhost:" + CONNECTION_PORT + "/";
	public static final String CONNECTION_USER = "SA";
	public static final String CONNECTION_PASSWORD = "";
	public static final String CONNECTION_CMD_STOP = "SHUTDOWN";

	public static final String PROPERTY_PORT = "server.port";
	public static final String PROPERTY_DBNAME_1 = "server.dbname.0";

	public static void start() {
		try {
			HsqlProperties p = new HsqlProperties(FILE_PROPERTIES);
			p.load();
			Server server = new Server();
			server.setProperties(p);
			final PrintWriter LOGGER = new PrintWriter(new File(FILE_LOGGING));
			server.setLogWriter(LOGGER);
			server.setErrWriter(LOGGER);
			server.start();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void stop() {
		try {
			HsqlProperties p = new HsqlProperties(FILE_PROPERTIES);
			p.load();
			String port = p.getProperty(PROPERTY_PORT);
			String database = p.getProperty(PROPERTY_DBNAME_1);
			String connection = CONNECTION + database;
			if (port != null) {
				connection = connection.replace("" + CONNECTION_PORT, port);
			}
			try (Connection conn = DriverManager.getConnection(connection, CONNECTION_USER, CONNECTION_PASSWORD);
					Statement stat = conn.createStatement()) {
				stat.executeUpdate(CONNECTION_CMD_STOP);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length > 0 && CMD_STOP.equalsIgnoreCase(args[0])) {
			stop();
		} else {
			start();
		}
	}
}
