package sjsu.cs157a.bankingsystem;

import java.sql.*;
import java.util.List;

public class Bank {
	private String bankName;
	private float balance;
	
	Bank(String bankName, float balance) {
		this.setBankName(bankName);
		this.setBalance(balance);
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}
	
	public static List<Bank> getAllBanks(Connection conn) {
		return Database.getAllBanks(conn);
	}
	
	public static float getBanksBalance(Connection conn, String bankName) {
		return Database.getBanksBalance(conn, bankName);
	}

	public static boolean createBank(Connection conn, String bankName) {
		return Database.createBank(conn, bankName);
	}
}
