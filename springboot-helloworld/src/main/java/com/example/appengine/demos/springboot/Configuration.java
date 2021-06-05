package com.example.appengine.demos.springboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Configuration {
	public static Statement getStatementFromDB() {
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			statement = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statement;
	}
}
