package sjsu.cs157a.bankingsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 *
 */
public class App {
	public static void main( String[] args ) throws Exception
    {
    	// Prepare MySQL connection.
        final Connection conn = SQLConnector.getInstance().getConnection();
        if (conn == null) {
            System.out.println( "No connection to local MySQL server. Please try again later." );
            throw new SQLException();
        }
        else {
        	System.out.println( "Connection to MySQL ready." );	
        }
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
        	  User.archiveUsers(conn);
          }
        }, 0, 1000*60);

        
        // User state.   
        int userID = -1;
        String firstName = null;
        String lastName = null;
        String email = null;
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
    	boolean loggedIn = false;
        
        // Let user log in.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to login(1), register(2), or quit(3)? (Please input 1~3).");
        boolean inputValid = false;
        while (!inputValid) {
        	switch (scanner.nextLine()) {
        	case "1":
        		inputValid = true;
            	while (!loggedIn) {
                    String pw = null;
                	System.out.println("Please input your email.");
                	email = scanner.nextLine();
                	System.out.println("Please input your password.");
                	pw = scanner.nextLine();
                	userID = User.login(conn, email, pw);
                	if (userID == -1) {
                		System.out.println("The given email and password do not match for any user within the banking system. Please try again.");
                	}
                	else {
                		loggedIn = true;
                	}
            	}
        		break;
        	case "2":
        		inputValid = true;
            	while (!loggedIn) {
                    String pw = null;
                	System.out.println("Please input your first name.");
                	firstName = scanner.nextLine();
                	System.out.println("Please input your lastName.");
                	lastName = scanner.nextLine();
                	System.out.println("Please input your email.");
                	email = scanner.nextLine();
                	System.out.println("Please input your password.");
                	pw = scanner.nextLine();
                	userID = User.register(conn, firstName, lastName, email, pw);
                	if (userID == -2) {
                		System.out.println("Given email is already in use.");
                	}
                	if (userID < 0) {
                		System.out.println("Registration failed. Please try again.");
                	}
                	else {
                		loggedIn = true;
                	}
            	}  
        		break;
        	case "3":
        		inputValid = true;
        		System.out.println("Quitting.");
        		break;
        	default:
        		System.out.println("Invalid input. Please try again.");
        		break;
        	}
        }    
        
        // Logged in message.
        if (loggedIn) {
        	System.out.println("Welcome to your personal banking tracker, " + email + ". Today is " + date + ". It is currently " + time + ".");
        	System.out.println("Please input a number from 1~4 show all actions related to the corresponding category.");
        }
        
        // Banking system usage loop.
        while (loggedIn) {
            System.out.println("Accounts (1) | Transactions (2) | Loans (3) | Banks (4) | Delete User (8) | Sign Out (0)");
            switch (scanner.nextLine()) {
            case "1":
            	List<Account> userBankAccounts;
            	List<Transaction> transactions;
            	List<Bank> banks;
            	List<Loan> loans;
            	String bankName;
            	Account account;
            	float amount;
            	System.out.println("Please input a number from 1~4 to select an action.");
            	System.out.println("Create Bank Account (1) | Delete Bank Account (2) | Show Accounts at a Given Bank (3) | Check Account Balance (4) | Calculate Your Net Worth (5)");
            	switch (scanner.nextLine()) {
                case "1":
                	banks = Bank.getAllBanks(conn);
                	
                	if(banks.size() == 0) {
                		System.out.println("There are no banks in the system. To create an account first create a bank.");
                	}
                	else {
	                	String[] accountTypes = {"Checking", "Savings", "Loans"};
	                	
	                	System.out.println("Available banks.");
	                	for(int i = 0; i < banks.size(); i++) {
	                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
	                	}
	                	System.out.println("\nPlease input the number of the bank where you would like to open an account.");
	                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
	                	System.out.println("Please input the number of the account type you would like to open.");
	                	System.out.println("Checking (1) | Saving (2) | Loan (3)");
	                	String accType = accountTypes[scanner.nextInt() - 1];
	                	Account.createBankAccount(conn, bankName, accType, userID); 
                	}
                	break;
                case "2":
                	banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank where you would like to delete an account.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType() + " $" + userBankAccounts.get(i).getBalance());
                	}
                	
                	System.out.println("\nPlease input the number of the account you would like to delete.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);
                	
                	Account.deleteBankAccount(conn, bankName, account.getAccType(), userID);
                	System.out.println("Your " + bankName + " " + account.getAccType() + " account has been deleted.");
                	break;
                case "3":
                	banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank where you would like to view your view your accounts.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType() + " account under user " + userBankAccounts.get(i).getFirstName());
                	}
                	break;
                case "4":
                	banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank where you would like to check an account balance.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType());
                	}
                	
                	System.out.println("\nPlease input the number of the account whos balance you would like to check.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);
                	
                	System.out.println("Balance of " + bankName + " " + account.getAccType() + " account: $" + Account.getBankAccountBalance(conn, bankName, account.getAccType(), userID));
                	
                	break;
                case "5":
                	System.out.println("Your net worth across your accounts: $" + Account.calculateNetWorth(conn, userID));
                	break;
            	}
            	break; 
	        case "2":
		    	System.out.println("Please input a number from 1~4 to select an action.");
		    	System.out.println("Deposit (1) | Withdraw (2) | Check Latest Transactions (3) | Check Transactions for a Month (4)");
            	switch (scanner.nextLine()) {
            	case "1":
                	banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank where you would like to deposit.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType());
                	}
                	
                	System.out.println("\nPlease input the number of the account you would like to deposit into.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);
                	System.out.println("\nPlease input the amount you would like to deposit.");
                	amount = scanner.nextFloat();
                	Transaction.deposit(conn, userID, bankName, account.getAccType(), "Deposit", amount);
                	System.out.println("Deposit complete");
            		break;
            	case "2":
            		banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank where you would like to withdraw.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType() + " $" + userBankAccounts.get(i).getBalance());
                	}
                	
                	System.out.println("\nPlease input the number of the account you would like to withdraw from.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);
                	System.out.println("\nPlease input the amount you would like to withdraw.");
                	amount = scanner.nextFloat();
                	Transaction.withdraw(conn, userID, bankName, account.getAccType(), "Withdraw", amount);
                	System.out.println("Withdraw complete");
            		break;
            	case "3":
            		banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank your account is from.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	if (userBankAccounts.size() == 0) {
                		System.out.println("You have no accounts at this bank.");
                		break;
                	}
                	
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType() + " $" + userBankAccounts.get(i).getBalance());
                	}
                	
                	System.out.println("\nPlease input the number of the account you would like to use.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);  
            		
            		transactions = Transaction.getRecentTransactions(conn, userID, bankName, account.getAccType());
                	for(Transaction t : transactions) {
                		System.out.println("ID: " + t.getTransId() + " | Date: " + t.getTransDateTime() + " | Location: " + t.getLocation() + " | Summary: " + t.getSummary() + " | Type: " + t.getAmount() + " | Amount: $" + t.getAmount() + " | Net Balance: $" + t.getNetBalance());
                	}
                	break;
            	case "4":
            		banks = Bank.getAllBanks(conn);
                	
                	for(int i = 0; i < banks.size(); i++) {
                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
                	}
                	System.out.println("\nPlease input the number of the bank your account is from.");
                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
                	userBankAccounts = Account.getAllUserBankAccountsAtBank(conn, bankName, userID);
                	if (userBankAccounts.size() == 0) {
                		System.out.println("You have no accounts at this bank.");
                		break;
                	}
                	for(int i = 0; i < userBankAccounts.size(); i++) {
                		System.out.println("(" + (i + 1) + ")" + " " + userBankAccounts.get(i).getAccType() + " $" + userBankAccounts.get(i).getBalance());
                	}           	
                	System.out.println("\nPlease input the number of the account you would like to use.");
                	account = userBankAccounts.get(scanner.nextInt() - 1);
                	
            		System.out.println("\nPlease input the month (1~12) of the transaction you wish to see.");
            		int month = scanner.nextInt();
            		System.out.println("\nPlease input the year (i.e: 2021) of the transaction you wish to see.");
            		int year = scanner.nextInt();
            		LocalDate filterDate = LocalDate.of(year, month, 1);
            		
            		transactions = Transaction.getMonthlyTransactions(conn, userID, bankName, account.getAccType(), filterDate);
                	for(Transaction t : transactions) {
                		System.out.println("ID: " + t.getTransId() + " | Date: " + t.getTransDateTime() + " | Location: " + t.getLocation() + " | Summary: " + t.getSummary() + " | Type: " + t.getAmount() + " | Amount: $" + t.getAmount() + " | Net Balance: $" + t.getNetBalance());
                	}
            		break;
            	}
	        	break;
		    case "3":
		    	System.out.println("Please input a number from 1~3 to select an action.");
		    	System.out.println("Show Loans (1) | Open New Loan (2)");
            	switch (scanner.nextLine()) {
            	case "1":
                	loans = Loan.getLoans(conn, userID);
                	
                	if(loans.size() == 0) {
                		System.out.println("You do not have any loans.");
                	}
                	else {
                		for(int i = 0; i < loans.size(); i++) {
                			System.out.println("(" + (i + 1) + ")" + " " + loans.get(i).getBankName() + " $" +loans.get(i).getAmount());
                		}
                	}
            		break;
            	case "2":
            		banks = Bank.getAllBanks(conn);
                	
                	if(banks.size() == 0) {
                		System.out.println("There are no banks in the system. To create an account first create a bank.");
                	}
                	else {
	                	System.out.println("Available banks.");
	                	for(int i = 0; i < banks.size(); i++) {
	                		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
	                	}
	                	System.out.println("\nPlease input the number of the bank where you would like to open a loan.");
	                	bankName = banks.get(scanner.nextInt() - 1).getBankName();
	                	System.out.println("\nPlease input the amount of the loan.");
	                	amount = scanner.nextFloat();
	                	Loan.createLoan(conn, userID, bankName, amount);
	                	System.out.println("Loan successfully taken out from " + bankName + " for $" + amount);
                	}
            		break;
            	}
		    	break;
		    case "4":
		    	System.out.println("Please input a number from 1~2 to select an action.");
		    	System.out.println("Check Bank Balance (1) | Create Bank (2)");
            	switch (scanner.nextLine()) {
                case "1":
			    	banks = Bank.getAllBanks(conn);
			    	
	            	for(int i = 0; i < banks.size(); i++) {
	            		System.out.println("(" + (i + 1) + ") " + banks.get(i).getBankName());
	            	}
	            	System.out.println("\nPlease input the number of the bank whos balance you would like to check.");
	            	bankName = banks.get(scanner.nextInt() - 1).getBankName();
	            	
	            	System.out.println("The balance of " + bankName + " is $" + Bank.getBanksBalance(conn, bankName));
	            	break;
                case "2":
			    	
                	System.out.println("\nPlease input the name of the bank where you would like to open an account.");
                	bankName = scanner.nextLine().toUpperCase();
                	Bank.createBank(conn, bankName);
	            	break;
            	}
		    	break;
		    case "8":
		    	System.out.println("Are you sure? This will delete the user permanently, making all assets inaccessible. (Y/N)");
		    	String confirmation = scanner.nextLine();
		    	if (confirmation.toUpperCase().equals("Y")) {
		    		System.out.println("User '" + email + "' has been deleted.");
		    		User.deleteUser(conn, userID);
		    		loggedIn = false;
		    	}
		    	else {
		    		System.out.println("Deletion canceled.");
		    	}
		    	break;
		    case "0":
		    	System.out.println("Signing out.");
		    	loggedIn = false;
            }
            System.out.println();
        }
        
        // Close scanner on end.
        scanner.close();
	}
}
