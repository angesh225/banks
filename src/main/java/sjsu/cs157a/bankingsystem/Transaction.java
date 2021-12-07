package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Transaction {
	private int transId;
	private int userId;
	private String bankName;
	private String accType;
	private LocalDateTime transDateTime;
	private String location;
	private String summary;
	private String transType;
	private float amount;
	private float netBalance;
	
	public Transaction(int userId, String bankName, String accType, LocalDateTime transDateTime, String location, String summary, String transType, float amount, float netBalance) {
		this.userId = userId;
		this.bankName = bankName;
		this.accType = accType;
		this.transDateTime = transDateTime;
		this.location = location;
		this.summary = summary;
		this.transType = transType;
		this.amount = amount;
		this.netBalance = netBalance;
	}
	
	public int getTransId() { return transId; }
	public LocalDateTime getTransDateTime() { return transDateTime; }
	public String getLocation() { return location; }
	public String getSummary() { return summary; }
	public String getTransType() { return transType; }
	public float getAmount() { return amount; }
	public float getNetBalance() { return netBalance; }
	
	public static List<Transaction> getRecentTransactions(Connection conn, int userId, String bankName, String accType) {
		return Database.getRecentTransactions(conn, userId, bankName, accType);
	}
	
	public static List<Transaction> getMonthlyTransactions(Connection conn, int userId, String bankName, String accType, LocalDate filterDate) {
		return Database.getMonthlyTransactions(conn, userId, bankName, accType, filterDate);
	}
	
	public static void deposit(Connection conn, int userID, String bankName, String accType, String transType, float amount) {
		Database.deposit(conn, userID, bankName, accType, transType, amount);
	}

	public static void withdraw(Connection conn, int userID, String bankName, String accType, String transType, float amount) {
		Database.withdraw(conn, userID, bankName, accType, transType, amount);
	}
}
