package sjsu.cs157a.bankingsystem;

import java.io.FileReader;
import java.sql.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A connector containing a maintained, singular connection to the database.
 * To retrieve an instance of the class, use getInstance(). To retrieve a connection, use getConnection() on said instance.
 * Singleton, so NOT thread safe. Do note that connection pooling would be best practice. However, such won't be necessary considering that we (probably) won't be using concurrency and/or cloud-shared databases.
 * @author Hoai-Nam Phung
 *
 */
public class SQLConnector {
	public static final String DB_NAME = "bank_system";
	private static Connection conn;
	private static SQLConnector sqlConnector = new SQLConnector();
	
	// Initialized at start of JVM.
	private SQLConnector() {
		getConnection();
	}

	/**
	 * Retrieves singleton instance of the SQLConnector, which contains a maintained connection to the MySQL database.
	 * Note that Singleton based connections ARE NOT thread safe.
	 * Connection pooling is the best method, but I don't think we will need concurrency + MySQL is locally stored, so whatever.
	 * @return Returns the singleton instance of the class.
	 */
	public static synchronized SQLConnector getInstance() {
		if (sqlConnector == null) {
			sqlConnector = new SQLConnector();
		}
		return sqlConnector;
	}
	
	/**
	 * Retrieves a connection to the MySQL database.
	 * @return Returns a connection object used for querying the database.
	 */
	public Connection getConnection() {
		String[] login = null;
		if (conn == null) {
			try {
				login = getCredentials();
				conn = DriverManager.getConnection(login[0] + DB_NAME, login[1], login[2]);
	        }
			// Database might not yet exist.
			catch (SQLSyntaxErrorException dbDNE) {
				try
				{
					Connection tempConn = DriverManager.getConnection(login[0], login[1], login[2]);
					Database.createDatabase(tempConn, DB_NAME);
					tempConn.close();
					conn = DriverManager.getConnection(login[0] + DB_NAME, login[1], login[2]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		return conn;
	}

	/**
	 * Retrieve database login parameters as a string array containing url, user, password.
	 * @return Returns a string array containing database login parameters. [0] = url, [1] = user, [2] = password.
	 */
	private static String[] getCredentials() {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("config.json"));
			JSONObject jsonObj = (JSONObject) obj;
			
			// Get DB parameters from config file.
			String url = (String) jsonObj.get("url");
			String username = (String) jsonObj.get("username");
			String password = (String) jsonObj.get("password");
			return new String[]{url, username, password};
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
