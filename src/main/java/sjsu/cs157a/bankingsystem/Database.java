package sjsu.cs157a.bankingsystem;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {

	/**
	 * Creates a new database if it doesn't exist.
	 * 
	 * @param conn   The MySQL connection.
	 * @param dbName The new database name.
	 * @return Returns true if given database exists, whether or not it needed to be
	 *         added.
	 */
	public static boolean createDatabase(Connection conn, String dbName) {
		try {
			String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creates a new table if it doesn't exist. This is a template; the SQL string
	 * should be modified to create a specific kind of table.
	 * 
	 * @param conn      The MySQL connection.
	 * @param tableName The new table name.
	 * @param dropTable Flag indicating whether or existing tables of the same name
	 *                  should be dropped beforehand.
	 * @return Returns true if given table exists, whether or not it needed to be
	 *         added.
	 */
	public static boolean createTable(Connection conn, String tableName, boolean dropTable) {
		try {
			String dropSql = "DROP TABLE IF EXISTS " + tableName;
			String createSql = "CREATE TABLE IF NOT EXISTS " + tableName + "(uid INT PRIMARY KEY AUTO_INCREMENT, "
					+ "sampleText VARCHAR(255)";
			Statement stmt = conn.createStatement();
			if (dropTable) {
				stmt.execute(dropSql);
			}
			stmt.execute(createSql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creates a new user.
	 * 
	 * @param conn      The MySQL connection.
	 * @param firstName The user's first name.
	 * @param lastName  The user's last name.
	 * @param email     The user's email. Must be unique.
	 * @param pw        The user's password.
	 * @return Returns the new users unique userID. If unique key 'email' is already
	 *         in use, returns -2. If creation fails for any other reason, returns
	 *         -1.
	 */
	public static int createUser(Connection conn, String firstName, String lastName, String email, byte[] pw) {
		try {
			String sql = "CALL CreateUser(?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, email);
			pstmt.setBytes(4, pw);
			// Success check.
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated == 0) {
				throw new SQLException("No rows affected: user was not created.");
			}
			// Return new userID.
			ResultSet rs = pstmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new SQLException("No rows affected: user was not created.");
			}
		} catch (SQLIntegrityConstraintViolationException emailException) {
			return -2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Deletes a user with a given userID.
	 * 
	 * @param conn   The MySQL connection.
	 * @param userID The user's unique user id.
	 * @return Returns true on successful deletion.
	 */
	public static boolean deleteUser(Connection conn, int userID) {
		try {
			String sql = "CALL DeleteUser(?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			int rowsUpdated = pstmt.executeUpdate();
			// Success check.
			if (rowsUpdated == 0) {
				throw new SQLException("No rows affected: user was not deleted.");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves a given user's userID.
	 * 
	 * @param conn  The MySQL connection.
	 * @param email The user's email.
	 * @param pw    The user's password.
	 * @return Returns the given user's userID. If query fails or user DNE, returns
	 *         -1.
	 */
	public static int getUserID(Connection conn, String email, byte[] pw) {
		try {
			String sql = "CALL GetUserID(?, ?, ?);";
			CallableStatement cstmt = conn.prepareCall(sql);
			cstmt.setString(1, email);
			cstmt.setBytes(2, pw);
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.executeUpdate();
			int userID = cstmt.getInt(3);
			if (userID == 0) {
				throw new SQLException("UserID does not exist for given email and password.");
			}
			return userID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @param conn     The MySql connection
	 * @param bankName The name of the bank
	 * @param accType The type of account
	 * @param userID The users ID
	 * @return Returns true if the account is created
	 */
	public static boolean createBankAccount(Connection conn, String bankName, String accType, int userID) {
		try {
			String sql = "CALL CreateBankAccount(?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setFloat(3, 0);
			pstmt.setInt(4, userID);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param conn The MySql connection
	 * @return Returns all banks in the database
	 */
	public static List<Bank> getAllBanks(Connection conn) {
		ResultSet rset = null;
		List<Bank> banks = new ArrayList<Bank>();
		try {
			String sql = "CALL GetAllBanks()";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				banks.add(new Bank(rset.getString(1), rset.getFloat(2)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return banks;
	}

	/**
	 * @param conn   The MySql connection
	 * @param userID The users ID
	 * @return Returns the account type and balance of all accounts at a given bank
	 */
	public static List<Account> getAllUserBankAccountsAtBank(Connection conn, String bankName, int userID) {
		ResultSet rset = null;
		List<Account> userBankAccounts = new ArrayList<Account>();
		try {
			String sql = "CALL GetAllUserBankAccountsAtBank(?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setInt(2, userID);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				userBankAccounts.add(new Account(rset.getString("firstName"), rset.getString("accType"), rset.getFloat("balance")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userBankAccounts;
	}

	/**
	 * @param conn     The MySql connection
	 * @param bankName The name of the bank
	 * @param accType  The type of account
	 * @param userID   The users ID
	 * @return
	 */
	public static boolean deleteBankAccount(Connection conn, String bankName, String accType, int userID) {
		try {
			String sql = "CALL DeleteBankAccount(?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setInt(3, userID);
			int rowsUpdated = pstmt.executeUpdate();
			// Success check.
			if (rowsUpdated == 0) {
				throw new SQLException("No rows affected: user was not deleted.");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param conn     The MySql connection
	 * @param bankName The name of the bank
	 * @param accType  The type of account
	 * @param userID   The users ID
	 * @return Returns the balance of a users account
	 */
	public static float getBankAccountBalance(Connection conn, String bankName, String accType, int userID) {
		ResultSet rset = null;
		float balance = -1;
		try {
			String sql = "CALL GetBankAccountBalance(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setInt(3, userID);
			rset = pstmt.executeQuery();
			rset.next();

			balance = rset.getFloat(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return balance;
	}

	/**
	 * @param conn   The MySql connection
	 * @param userID The users ID
	 * @return Return a users net worth
	 */
	public static float calculateNetWorth(Connection conn, int userID) {
		ResultSet rset = null;
		float netWorth = -1;
		try {
			String sql = "CALL CalculateNetWorth(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			rset = pstmt.executeQuery();
			rset.next();

			netWorth = rset.getFloat(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return netWorth;
	}

	public static List<Transaction> getRecentTransactions(Connection conn, int userId, String bankName, String accType) {
		ResultSet rs = null;
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			String sql = "CALL GetRecentTransactions(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			pstmt.setString(2, bankName);
			pstmt.setString(3, accType);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int uid = rs.getInt("userID");
				String bn = rs.getString("bankName");
				String at = rs.getString("accType");
				String l = rs.getString("location");
				String s = rs.getString("summary");
				String tt = rs.getString("transType");
				float a = rs.getFloat("amount");
				float nb = rs.getFloat("netBalance");
				transactions.add(new Transaction(rs.getInt("userID"), rs.getString("bankName"), rs.getString("accType"), rs.getTimestamp("transDateTime").toLocalDateTime(), rs.getString("location"), rs.getString("summary"), rs.getString("transType"), rs.getFloat("amount"), rs.getFloat("netBalance")));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return transactions;
	}
	
	public static List<Transaction> getMonthlyTransactions(Connection conn, int userId, String bankName, String accType, LocalDate filterDate) {
		ResultSet rs = null;
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			String sql = "CALL GetMonthlyTransactions(?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			pstmt.setString(2, bankName);
			pstmt.setString(3, accType);
			pstmt.setDate(4, Date.valueOf(filterDate));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transactions.add(new Transaction(rs.getInt("userID"), rs.getString("bankName"), rs.getString("accType"), rs.getTimestamp("transDateTime").toLocalDateTime(), rs.getString("location"), rs.getString("summary"), rs.getString("transType"), rs.getFloat("amount"), rs.getFloat("netBalance")));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return transactions;
	}

	public static void archiveUsers(Connection conn) {
        String sql = "CALL ArchiveUsers(?);";
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            LocalDate today = LocalDate.now().minusYears(1);
            pstmt.setTimestamp(1, Timestamp.valueOf(today.atStartOfDay()));
        }
        catch (SQLException e){
        	e.printStackTrace();
        }
    }

	public static float getBanksBalance(Connection conn, String bankName) {
		ResultSet rset = null;
		float balance = -1;
		try {
			String sql = "CALL GetBanksBalance(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			rset = pstmt.executeQuery();
			rset.next();
			
			balance = rset.getFloat(1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return balance;
	}

	public static boolean createBank(Connection conn, String bankName) {
		try {
			String sql = "CALL CreateBank(?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setFloat(2, 0);
			pstmt.executeUpdate();
			return true;
		}
		catch(SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
			System.out.println("This bank already exists");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void deposit(Connection conn, int userID, String bankName, String accType, String transType, float amount) {
		try {
			String sql = "CALL Deposit(?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			pstmt.setString(2, bankName);
			pstmt.setString(3, accType);
			pstmt.setString(4, transType);
			pstmt.setFloat(5, amount);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void withdraw(Connection conn, int userID, String bankName, String accType, String transType, float amount) {
		try {
			String sql = "CALL Withdraw(?, ?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			pstmt.setString(2, bankName);
			pstmt.setString(3, accType);
			pstmt.setString(4, transType);
			pstmt.setFloat(5, amount);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Loan> getLoans(Connection conn, int userID) {
		ResultSet rs = null;
		List<Loan> loans = new ArrayList<Loan>();
		try {
			String sql = "CALL GetLoans(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				loans.add(new Loan(rs.getString("bankName"), rs.getFloat("amount")));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return loans;
	}

	public static void createLoan(Connection conn, int userID, String bankName, float amount) {
		try {
			String sql = "CALL CreateLoan(?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			pstmt.setString(2, bankName);
			pstmt.setString(3, "Loans");
			pstmt.setFloat(4, amount);
			pstmt.executeUpdate();
		}
		catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("You do not have a loan account open with this bank. Please first create one.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
