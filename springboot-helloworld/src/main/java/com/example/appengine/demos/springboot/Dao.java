package com.example.appengine.demos.springboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Dao {
	private static Logger logger = Logger.getLogger(Dao.class);
	
	public Statement getStatementFromDB() {
		Statement statement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(Configuration.CONNECTION_URL, Configuration.DB_USERNAME,
					Configuration.DB_PASSCODE);
			statement = con.createStatement();
		} catch (Exception e) {
			logger.error("Exception while taking DB Connection", e);
		}
		return statement;
	}
	
	public void saveUserPref(String number, String pinCode, String email, String dose, String age, String vaccine) {
		try(Statement statement = getStatementFromDB()) {
			String insertQuery = "insert into UserNotificationPref(number,pinCode,email,reg_date,dose,age,vaccine,notification_sent) values('" + number + "','" + pinCode + "','"
					+ email + "',CURRENT_TIMESTAMP(),'" + dose + "','" + age + "','" + vaccine + "',null)";
			statement.execute(insertQuery);
		} catch (SQLException e) {
			logger.error("Exception while saving user preferences", e);
		}
	}
	
	public void updateUserPrefNotiSent(UserNotificationPreferences userPref) {
		String updateQuery = "update UserNotificationPref set notification_sent = 'Y' where id =" + userPref.getId();
		try (Statement statement = getStatementFromDB()) {
			statement.executeUpdate(updateQuery);
		} catch (SQLException e) {
			logger.error("Error while udpating status in DB", e);
		}
	}
	
	public void resetAllUserPref() {
		String updateQuery = "update UserNotificationPref set notification_sent = null where notification_sent = 'Y'";
		try (Statement statement = getStatementFromDB()) {
			statement.executeUpdate(updateQuery);
		} catch (SQLException e) {
			logger.error("Error while reseting status in DB", e);
		}
	}
	
	public Map<String, String> getDistinctUsersPin(List<String> pinCodeList) {
		Map<String,String> pinResponseMap = new HashMap<>();
		String selectQuery = "select distinct(pinCode) from UserNotificationPref where notification_sent is null";
		try(Statement statement = getStatementFromDB();
				ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				pinCodeList.add(rs.getString("pinCode"));
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return pinResponseMap;
	}
	
	public List<UserNotificationPreferences> getAllUserNotiPref() {
		List<UserNotificationPreferences> userPrefList = new ArrayList<>();
		String selectQuery = "select * from UserNotificationPref where notification_sent is null";
		try(Statement statement = getStatementFromDB();
				ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				UserNotificationPreferences userPref = new UserNotificationPreferences();
				userPref.setId(rs.getLong("id"));
				userPref.setEmail(rs.getString("email"));
				userPref.setPincode(rs.getString("pinCode"));
				userPref.setDose(rs.getString("dose"));
				userPref.setAge(rs.getString("age"));
				userPref.setVaccine(rs.getString("vaccine"));
				userPref.setNotificationSent(rs.getString("notification_sent"));
				userPrefList.add(userPref);
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return userPrefList;
	}
	
}
