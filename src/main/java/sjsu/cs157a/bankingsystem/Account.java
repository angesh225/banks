package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.util.List;

public class Account {
	private String firstName;
	private String accType;
	private float balance;
	
	public Account(String firstName, String accType, float balance) {
		this.setFirstName(firstName);
		this.setAccType(accType);
		this.setBalance(balance);
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	/**
	 * @param conn The MySql connection
	 * @param bankName The banks name
	 * @param accType The account type
	 * @param balance The account balance
	 * @param userID The users ID
	 * @return Return boolean true if the bank account is created
	 */
	public static boolean createBankAccount(Connection conn, String bankName, String accType , int userID) {
		return Database.createBankAccount(conn, bankName, accType, userID);
	}
	
	/**
	 * @param conn The MySql connection
	 * @param The banks name
	 * @param userID The users ID
	 * @return Return list of all user accounts for a particular bank
	 */
	public static List<Account> getAllUserBankAccountsAtBank(Connection conn, String bankName, int userID) {
		return Database.getAllUserBankAccountsAtBank(conn, bankName, userID);
	}
	
	/**
	 * @param conn The MySql connection
	 * @param bankName The banks name
	 * @param accType The account type
	 * @param userID The users ID
	 * @return Return the balance of a particular bank account
	 */
	public static float getBankAccountBalance(Connection conn, String bankName, String accType, int userID) {
		return Database.getBankAccountBalance(conn, bankName, accType, userID);
	}

	/**
	 * @param conn The MySql connection
	 * @param bankName The banks name
	 * @param accType The account type
	 * @param userID The users ID
	 * @return Return boolean true if the bank account is deleted
	 */
	public static boolean deleteBankAccount(Connection conn, String bankName, String accType, int userID) {
		return Database.deleteBankAccount(conn, bankName, accType, userID);
	}

	/**
	 * @param conn The MySql connection
	 * @param userID The users Id
	 * @return Return the net worth of a particular user across all of their bank accounts
	 */
	public static float calculateNetWorth(Connection conn, int userID) {
		return Database.calculateNetWorth(conn, userID);
	}
}
