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
	
	public void saveUserPref(String number, String pinCode, String email, String dose, String age, String vaccine, String districtId, String districtName) {
		try(Statement statement = getStatementFromDB()) {
			String insertQuery = "insert into UserNotificationPref(number,pinCode,email,reg_date,dose,age,vaccine,district_name,district_id) values('" + number + "','" + pinCode + "','"
					+ email + "',CURRENT_TIMESTAMP(),'" + dose + "','" + age + "','" + vaccine + "','" + districtName + "','" + districtId + "')";
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
	
	public List<String> getDistinctUsersPin() {
		List<String> pinCodeList = new ArrayList<>();
		String selectQuery = "select distinct(pinCode) from UserNotificationPref where notification_sent is null and pinCode is not null";
		try(Statement statement = getStatementFromDB();
				ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				pinCodeList.add(rs.getString("pinCode"));
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return pinCodeList;
	}
	
	public List<String> getDistinctDistrictId() {
		List<String> districtIdList = new ArrayList<>();
		String selectQuery = "select distinct(district_id) from UserNotificationPref where notification_sent is null and district_id is not null";
		try(Statement statement = getStatementFromDB();
				ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				districtIdList.add(rs.getString("district_id"));
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return districtIdList;
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
				userPref.setDistrictId(rs.getString("district_id"));
				userPref.setDistrictName(rs.getString("district_name"));
				userPrefList.add(userPref);
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return userPrefList;
	}
	
}
