package com.example.appengine.demos.springboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class Configuration {

	private static final String CONNECTION_URL = "";
	private static final String DB_USERNAME = "";
	private static final String DB_PASSCODE = "";
	private static final String FROM_EMAIL = "";
	private static final String EMAIL_PASSCODE = "";
	
	public static Statement getStatementFromDB() {
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSCODE);
			statement = con.createStatement();
		} catch (Exception e) {
			System.out.println("Exception while taking DB Connection" + e.getStackTrace());
		}
		return statement;
	}
	
	public static Session getMailSessionObj() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.googlemail.com");
		props.put("mail.from", FROM_EMAIL);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.setProperty("smtp_port", "25");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSCODE);
			}
		});
		return session;
	}
}
