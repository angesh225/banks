package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.util.List;

public class Loan {
	private String bankName;
	private float amount;
	
	public Loan(String bankName, float amount) {
		this.setBankName(bankName);
		this.setAmount(amount);
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public static List<Loan> getLoans(Connection conn, int userID) {
		return Database.getLoans(conn, userID);
	}
	
	public static void createLoan(Connection conn, int userID, String bankName, float amount) {
		Database.createLoan(conn, userID, bankName, amount);
	}

}
