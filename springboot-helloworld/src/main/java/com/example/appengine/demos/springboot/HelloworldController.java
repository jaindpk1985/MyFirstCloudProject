
package com.example.appengine.demos.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.scheduling.annotation.Scheduled;
import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.mail.*;
import javax.mail.internet.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.json.JSONObject ;
import org.json.JSONArray ;
import org.json.JSONException;
import org.apache.log4j.Logger;


@RestController
public class HelloworldController {
	static Logger logger = Logger.getLogger(HelloworldController.class);
	public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	public static final String BORDER_STYLE = "border: 1px solid black;border-collapse: collapse;text-align: center;";
	public static final String BORDER_COLOR = "background-color:#ADD8E6;";
	public static final String REGISTRATION_MESSAGE_BODY = "Hi,<br>You have been registered for Covid Vaccination Slot Information.<br>We will update you once slots are available with below filter.<table style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><tr><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Pincode:</strong> pincodeVal</td><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Dose:</strong> doseVal</td></tr><tr><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Vaccine:</strong> vaccineVal</td><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Age:</strong> ageVal</td></tr></table><br><a href='http://bestatone.com/covid-vaccination/'>Click to Register for another filter as well</a><br><br>Thanks<br>BestAtOne.com";
	public static final String MESSAGE_BODY = "Hi,<br>Slots are available on below Centres.<br><br>searchParams<br><table>tableBody</table><br><a href='https://selfregistration.cowin.gov.in/'>Click to Book Slot</a><br><br><a href='http://bestatone.com/covid-vaccination/'>Click to Register for another filter as well</a><br><br>Thanks<br>BestAtOne.com";
	public static final String SEARCH_TEXT = "<strong>Pincode:</strong> pincodeVal, <strong>Dose:</strong> doseVal <br> <strong>Age:</strong> ageVal, <strong>Vaccine:</strong> vaccineVal";
	public static final String TABLE_HEADER_TEXT = "<tr><th style ='"+BORDER_STYLE + BORDER_COLOR +"' >Centre</th><th style ='"+BORDER_STYLE + BORDER_COLOR +"' >strDate1</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate2</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate3</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate4</th></tr>";
	public static final String MESSAGE_ROW_TEXT = "<tr><td style ='"+BORDER_STYLE +"'>centreDetailStr</td><td style ='"+BORDER_STYLE +"'>day1Slot</td><td style ='"+BORDER_STYLE +"'>day2Slot</td><td style ='"+BORDER_STYLE +"'>day3Slot</td><td style ='"+BORDER_STYLE +"'>day4Slot</td></tr>";
	public static final String SLOT_MESSAGE_SUBJECT = "Slots available for Covid Vaccination as on strDate";
	public static final String CENTRE_DETAIL_TEXT = "<strong>centreName(<span style='background-color:yellow;'>feeType</span>)</strong><br>centreAddress";
	public static final String SLOT_TEXT = "<div style='background-color:green;color:yellow'>slot</div>";

	public static void main(String[] args) throws Exception {
		/*
		 * //sendSlotAvailabilityNotification(); try {
		 * //sendEmail("jaindpk.1985@gmail.com","Test Schedular",
		 * "Testing schedular","text"); } catch (MessagingException e) {
		 * logger.debug("Testing mail sent"); }
		 * logger.debug("Main method called successfully");
		 */

	}
	
	@GetMapping("/")
	public String hello() {
		return "Hello world - Deepak Jain came to Cloud";
	}

	@CrossOrigin("http://bestatone.com")
	@PostMapping(value = "/saveNumber")
	@ResponseBody
	public String saveNumber(@RequestParam String number, @RequestParam String pinCode, @RequestParam String email,
			@RequestParam String dose, @RequestParam String age, @RequestParam String vaccine) {
		
		try(Statement stmt = Configuration.getStatementFromDB();) {
			String insertQuery = "insert into UserNotificationPref(number,pinCode,email,reg_date,dose,age,vaccine,notification_sent) values('" + number + "','" + pinCode + "','"
					+ email + "',CURRENT_TIMESTAMP(),'" + dose + "','" + age + "','" + vaccine + "',null)";
			stmt.execute(insertQuery);
			logger.debug("Data Successfully saved in DB");
			if(email != null && !email.equals("")) {
				String subject = "Congrats..Registration Successfull";
				String message = REGISTRATION_MESSAGE_BODY.replace("pincodeVal",pinCode).replace("doseVal",dose).replace("ageVal",age).replace("vaccineVal",vaccine);
				sendEmail(email,subject,message,"text/html");
			}
		} catch (Exception e) {
			logger.error("Error while saving number ",e);
			if(e.getCause() != null) {
				return e.getCause().getMessage();
			}else {
				return e.getMessage();
			}
		}
		return "Success";
	}
	//on each 6 minutes
	//@Scheduled(fixedRate = 60000)
	@GetMapping(value = "/scheduleNotification")
	public void notificationSchedular() {
		logger.debug("Schedular called successfully");
		//sendSlotAvailabilityNotification();
		try {
			sendEmail("jaindpk.1985@gmail.com","Test Schedular", "Testing schedular","text");
		} catch (MessagingException e) {
			logger.debug("Testing mail sent");
		}
		logger.debug("Schedular execution complete");
	}
	
	//7:00 am
	//@Scheduled(cron = "0 10 7 * * *")
	public void notificationClearSchedularMor() {
		logger.debug("Clear Schedular called successfully");
		resetAllUserPref();
		logger.debug("Clear Schedular executed successfully");
	}
	
	//@Scheduled(cron = "0 10 18 * * *")
	public void notificationClearSchedularEven() {
		logger.debug("Clear Schedular called successfully");
		resetAllUserPref();
		logger.debug("Clear Schedular executed successfully");
	}
	
	@GetMapping(value = "/fireService")
	public String fireService() {
		resetAllUserPref();
		logger.debug("Reset Schedular Executed Manually");
		return "Preferences are Reset successfully";
	}
	
	public static void sendSlotAvailabilityNotification() {
        try {
			List<UserNotificationPreferences> userPrefList = getAllUserNotiPref();
			Map<String,String> pinResponseMap = getDistinctPinDataOfUsers();
			for(UserNotificationPreferences userPref : userPrefList) {
				sendNotificationByPref(userPref,pinResponseMap);
			}
		} catch (Exception e) {
			logger.error("Error while sending slot availability notification" ,e );
		}
	}

	private static void sendNotificationByPref(UserNotificationPreferences userPref, Map<String,String> pinResponseMap)
			throws Exception, JSONException, MessagingException {
		String jsonResponse =  pinResponseMap.get(userPref.getPincode());
		JSONObject resobj = new JSONObject(jsonResponse);
		JSONArray centers = (JSONArray)resobj.get("centers");
		centers = sortJsonArray(centers);
		Date date = Calendar.getInstance().getTime();  
		String strDate1 = dateFormat.format(date);  
		String strDate2 = getNextDayInString(1);
		String strDate3 = getNextDayInString(2);
		String strDate4 = getNextDayInString(3);
		String rowDetail  = "";
		for (int i = 0; i < centers.length(); i++) {
			JSONObject centre = centers.getJSONObject(i);
			rowDetail  = rowDetail + getRow(userPref, centre,strDate1,strDate2,strDate3,strDate4);
		}
		if(rowDetail.trim().length() == 0) {
			//no need to send notification for this case.
			return;
		}
		
		if(userPref.getEmail() != null) {
			String searchParamTextVal = SEARCH_TEXT.replace("pincodeVal", userPref.getPincode())
					.replace("doseVal",userPref.getDose()).replace("ageVal", userPref.getAge()).replace("vaccineVal",userPref.getVaccine());
			String headerString = getHeader(strDate1,strDate2,strDate3,strDate4);
			String tableBody = headerString + rowDetail;
			String messageBody = MESSAGE_BODY.replace("searchParams",searchParamTextVal).replace("tableBody",tableBody);
			sendEmail(userPref.getEmail(),SLOT_MESSAGE_SUBJECT.replace("strDate",strDate1), messageBody,"text/html");
			logger.debug("Notification mail sent successfully");
			//update userpref in DB notification_sent = 'Y'
			updateUserPrefNotiSent(userPref);
		}
		if(userPref.getNumber() != null) {
			//sent message to this number
		}
	}
	
	private static String getRow(UserNotificationPreferences userPref, JSONObject centre,String strDate1,String strDate2,String strDate3,String strDate4) throws JSONException {
		String rowStr = "";
		String feeType = (String)centre.get("fee_type");
		String feesText = "";
		if("Paid".equals(feeType)) {
			JSONArray feeDetails = (JSONArray)centre.get("vaccine_fees");
			feesText = getFeesText(userPref,feeDetails);
		}
		String feeTypeText = feeType + feesText;
		String centreDetailStr = CENTRE_DETAIL_TEXT.replace("centreName", (String) centre.get("name"))
				.replace("feeType", feeTypeText).replace("centreAddress", (String) centre.get("address"));
		JSONArray allDatesData = getApplicableAgeData(userPref, centre);
		String day1Slot = "NA";
		String day2Slot = "NA";
		String day3Slot = "NA";
		String day4Slot = "NA";
		for (int i = 0; i < allDatesData.length(); i++) {
			JSONObject dateData = allDatesData.getJSONObject(i);
			if(strDate1.equals(dateData.get("date"))) {
				day1Slot = validateAndGetSlot(userPref, dateData);
			}
			if(strDate2.equals(dateData.get("date"))) {
				day2Slot = validateAndGetSlot(userPref, dateData);
			}
			if(strDate3.equals(dateData.get("date"))) {
				day3Slot = validateAndGetSlot(userPref, dateData);
			}
			if(strDate4.equals(dateData.get("date"))) {
				day4Slot = validateAndGetSlot(userPref, dateData);
			}
		}
		if("NA".equals(day1Slot) && "NA".equals(day2Slot) && "NA".equals(day3Slot) && "NA".equals(day4Slot)) {
			return rowStr;
		}
		rowStr = MESSAGE_ROW_TEXT.replace("centreDetailStr", centreDetailStr).replace("day1Slot", day1Slot)
				.replace("day2Slot", day2Slot).replace("day3Slot", day3Slot).replace("day4Slot", day4Slot);
		return rowStr;
	}
	
	public static String validateAndGetSlot(UserNotificationPreferences userPref, JSONObject dateData) throws JSONException {
		String slot = "NA";
		if(checkAgeLimit(userPref,(int)dateData.get("min_age_limit")) && checkVaccine(userPref, (String)dateData.get("vaccine"))){
			if("Dose2".equals(userPref.getDose()) && (int)dateData.get("available_capacity_dose2") > 0){
				slot = String.valueOf((int)dateData.get("available_capacity_dose2"));
				slot = SLOT_TEXT.replace("slot",slot);
			}
			else if("Dose1".equals(userPref.getDose()) && (int)dateData.get("available_capacity_dose1")> 0){
				slot = String.valueOf((int)dateData.get("available_capacity_dose1"));
				slot = SLOT_TEXT.replace("slot",slot);
			}
		}
		return slot;
	}
	
	public static JSONArray sortJsonArray(JSONArray jsonArray) throws JSONException {
		JSONArray sortedJsonArray = new JSONArray();
		try {
			List<JSONObject> myJsonArrayAsList = new ArrayList();
			for (int i = 0; i < jsonArray.length(); i++) {
				myJsonArrayAsList.add(jsonArray.getJSONObject(i));
			}
			Collections.sort(myJsonArrayAsList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
					int compare = 0;
					try {
						String keyA = jsonObjectA.getString("name");
						String keyB = jsonObjectB.getString("name");
						compare = keyA.compareTo(keyB);
					} catch (JSONException e) {
						logger.error("Error while comparing", e);
					}
					return compare;
				}
			});
			for (int i = 0; i < myJsonArrayAsList.size(); i++) {
				sortedJsonArray.put(myJsonArrayAsList.get(i));
			}
		} catch (JSONException e) {
			logger.error("Error while sorting json array", e);
			sortedJsonArray = jsonArray;
		}
		return sortedJsonArray;
	}
	
	private static boolean checkVaccine(UserNotificationPreferences userPref, String vaccine) {
		if("COVAXIN".equals(userPref.getVaccine()) && "COVAXIN".equals(vaccine) || "COVISHIELD".equals(userPref.getVaccine()) && "COVISHIELD".equals(vaccine)){
			return true;
		}
		return false;
	}
	
	private static JSONArray getApplicableAgeData(UserNotificationPreferences userPref, JSONObject centre) throws JSONException {
		JSONArray filteredData = new JSONArray();
		JSONArray sessions = (JSONArray)centre.get("sessions");
		for (int i = 0; i < sessions.length(); i++) {
			JSONObject session = sessions.getJSONObject(i);
			int ageLimit = (int)session.get("min_age_limit");
			if(checkAgeLimit(userPref, ageLimit)) {
				filteredData.put(session);
			}
		}
		return filteredData;
	}
	
	private static boolean checkAgeLimit(UserNotificationPreferences userPref, int min_age_limit){
		if("Age18".equals(userPref.getAge()) && min_age_limit == 18 || "Age45".equals(userPref.getAge()) && min_age_limit == 45){
			return true;
		}
		return false;
   }
	
	private static String getFeesText(UserNotificationPreferences userPref, JSONArray feeDetails) throws JSONException {
		String feesStr = "";
		for (int i = 0; i < feeDetails.length(); i++) {
			JSONObject fees = feeDetails.getJSONObject(i);
			String vaccine = (String)fees.get("vaccine");
			if(vaccine.equals(userPref.getVaccine())) {
				feesStr = (String)fees.get("fee");
				feesStr = " , " + feesStr + "/-";
			}
		}
		return feesStr;
	}
	
	private static String getHeader(String strDate1, String strDate2, String strDate3, String strDate4) {
		return TABLE_HEADER_TEXT.replace("strDate1", strDate1).replace("strDate2", strDate2)
				.replace("strDate3", strDate3).replace("strDate4", strDate4);
	}
	
	public static List<UserNotificationPreferences> getAllUserNotiPref() {
		List<UserNotificationPreferences> userPrefList = new ArrayList<>();
		Statement statement = Configuration.getStatementFromDB();
		String selectQuery = "select * from UserNotificationPref where notification_sent is null";
		try(ResultSet rs = statement.executeQuery(selectQuery);) {
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
	
	public static Map<String,String> getDistinctPinDataOfUsers() throws Exception {
		List<String> pinCodeList = new ArrayList();
		Map<String,String> pinResponseMap = new HashMap<>();
		Statement statement = Configuration.getStatementFromDB();
		String selectQuery = "select distinct(pinCode) from UserNotificationPref where notification_sent is null";
		try(ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				pinCodeList.add(rs.getString("pinCode"));
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		for(String pincode : pinCodeList) {
			String responsData = getCentresDetailByPinCode(pincode);
			pinResponseMap.put(pincode,responsData);
		}
		return pinResponseMap;
	}
	
	public static void updateUserPrefNotiSent(UserNotificationPreferences userPref) {
		String updateQuery = "update UserNotificationPref set notification_sent = 'Y' where id =" + userPref.getId();
		try (Statement statement = Configuration.getStatementFromDB()) {
			statement.executeUpdate(updateQuery);
		} catch (SQLException e) {
			logger.error("Error while udpating status in DB", e);
		}
	}
	
	public static void resetAllUserPref() {
		String updateQuery = "update UserNotificationPref set notification_sent = null where notification_sent = 'Y'";
		try (Statement statement = Configuration.getStatementFromDB()) {
			statement.executeUpdate(updateQuery);
		} catch (SQLException e) {
			logger.error("Error while udpating status in DB", e);
		}
	}
	
	public static String getCentresDetailByPinCode(String pincode)  throws Exception{
		Date date = Calendar.getInstance().getTime();  
		String strDate = dateFormat.format(date);  
		URL url = new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pincode + "&date=" + strDate);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		// This line makes the request
		InputStream responseStream = connection.getInputStream();
		String jsonResponse = new BufferedReader(
			      new InputStreamReader(responseStream, StandardCharsets.UTF_8))
			        .lines()
			        .collect(Collectors.joining("\n"));
		logger.debug("Response recieved for pin " + pincode + " is " +  jsonResponse);
		return jsonResponse;
	}
	
	public static String getNextDayInString(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, days);
		String newDate = dateFormat.format(c.getTime());
		return newDate;
	}

	public static void sendEmail(String toEmailId,String subject, String message, String contentType) throws MessagingException{
		Session session = Configuration.getMailSessionObj();
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom();
			msg.setRecipients(javax.mail.Message.RecipientType.TO, toEmailId);
			msg.setSubject(subject);
			if("text".equals(contentType)) {
				msg.setText(message);
			}
			else {
				msg.setContent(message, "text/html");
			}
			Transport.send(msg);
			logger.debug("Mail sent successfully");
		} catch (MessagingException mex) {
			logger.error("send failed, exception: " , mex);
			throw mex;
		}
	}

}
