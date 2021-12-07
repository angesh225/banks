package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
	
	public static void archiveUsers(Connection conn) {
		Database.archiveUsers(conn);
	}
	/**
	 * Registers a user to be part of the banking system.
	 * @param conn The MySQL connection.
	 * @param firstName The user's first name.
	 * @param lastName The user's first name.
	 * @param email The user's email.
	 * @param pw The user's password.
	 * @return Returns the new userID of the user added to the banking system. 
	 * If given fields have invalid length or character, returns -3.
	 * If email is already in use, returns -2.
	 * If query to database fails to insert a new user for an unspecified reason, returns -1.
	 */
	public static int register(Connection conn, String firstName, String lastName, String email, String pw) {
		if (!validateFields(firstName, lastName, email, pw)) {
			return -3;
		}
		byte[] encodedPassword = encode(pw);
		return Database.createUser(conn, firstName, lastName, email, encodedPassword);
	}
	
	/**
	 * Retrieves a userID of the user with the given credentials from the banking system.
	 * @param conn The MySQL connection.
	 * @param email The user's email.
	 * @param pw The user's password.
	 * @return Returns a userID. If email and password are do not match an existing user, returns -1.
	 */
	public static int login(Connection conn, String email, String pw) {
		byte[] encodedPassword = encode(pw);
		return Database.getUserID(conn, email, encodedPassword);
	}
	
	/**
	 * Deletes a user from the banking system.
	 * @param conn The MySQL connection.
	 * @param userID The user's userID.
	 * @return Returns true if user is successfully deleted from the banking system.
	 */
	public static boolean deleteUser(Connection conn, int userID) {
		return Database.deleteUser(conn, userID);
	}
	
	/**
	 * Validates values retrieved from input fields based on length and contents.
	 * @param firstName The first name input.
	 * @param lastName The last name input.
	 * @param email The email input.
	 * @param pw The password input.
	 * @return Returns true if all fields are valid and pass all restrictions.
	 */
	private static boolean validateFields(String firstName, String lastName, String email, String pw) {
		final int NAME_MAX_LENGTH = 36;
		final int EMAIL_MAX_LENGTH = 256;
		final int PW_MAX_LENGTH = 256;
		final int PW_MIN_LENGTH = 8;
		
		boolean invalid = false;
		if (firstName.length() > NAME_MAX_LENGTH) {
			System.out.println("Your first name can only be up to " + NAME_MAX_LENGTH + " characters.");
			invalid = true;
		}
		if (lastName.length() > NAME_MAX_LENGTH) {
			System.out.println("Your last name can only be up to " + NAME_MAX_LENGTH + " characters.");
			invalid = true;
		}
		if (email.length() > EMAIL_MAX_LENGTH) {
			System.out.println("Your email can only be up to " + EMAIL_MAX_LENGTH + " characters.");
			invalid = true;
		}
		if (pw.length() > PW_MAX_LENGTH || pw.length() < PW_MIN_LENGTH) {
			System.out.println("Your password must be between " + PW_MIN_LENGTH + " and " + PW_MAX_LENGTH + " characters.");
			invalid = true;
		}
		if (invalid) {
			return false;
		}
		return true;
	}
	
	/**
	 * Encodes a password into a byte array using SHA-256.
	 * @param pw The password to encode.
	 * @return Returns the encoded password as a byte array.
	 */
	private static byte[] encode(String pw) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			return md.digest(pw.getBytes(StandardCharsets.UTF_8));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
