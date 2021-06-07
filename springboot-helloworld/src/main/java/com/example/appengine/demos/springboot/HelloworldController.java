/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.io.OutputStream;
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
import org.apache.log4j.Logger;


@RestController
public class HelloworldController {
	static Logger logger = Logger.getLogger(HelloworldController.class);
	private static final String INSTANCE_ID = "YOUR_INSTANCE_ID_HERE";
	private static final String CLIENT_ID = "YOUR_CLIENT_ID_HERE";
	private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET_HERE";
	private static final String GATEWAY_URL = "http://api.whatsmate.net/v1/telegram/single/message/" + INSTANCE_ID;

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
			String insertQuery = "insert into UserNotificationPref values(null,'" + number + "','" + pinCode + "','"
					+ email + "',CURRENT_TIMESTAMP(),'" + dose + "','" + age + "','" + vaccine + "')";
			stmt.execute(insertQuery);
			logger.debug("Data Successfully updated in DB");
			if(email != null && !email.equals("")) {
				String subject = "Congrats..Registration Successfull";
				String message = "Hi,\nYou have been registered for Covid Vaccination Slot Information.\nWe will update you once slots are available.\n";
				message = message + "Pincode:-" + pinCode + "\n";
				message = message + "Dose:-" + dose + "\n";
				message = message + "Age:-" + age+ "\n";
				message = message + "Vaccine:-" + vaccine + "\n";
				message = message + "\n\nThanks\nBestAtOne.com";
				sendEmail(email,subject,message);
			}
		} catch (Exception e) {
			logger.error("Error while saving number ",e);
			return "Error";
		}
		return "Success";
	}

	@Scheduled(fixedRate = 60000)
	public void notificationSchedular() {
		logger.debug("Schedular called successfully");
		sendSlotAvailabilityNotification();
		logger.debug("Schedular execution complete");
	}
	
	@PostMapping(value = "/fireService")
	public void fireService() {
		sendSlotAvailabilityNotification();
		logger.debug("Schedular Executed Manually");
	}

	public static void sendMessage(String number, String message) throws Exception {
		String jsonPayload = new StringBuilder().append("{").append("\"number\":\"").append(number).append("\",")
				.append("\"message\":\"").append(message).append("\"").append("}").toString();

		URL url = new URL(GATEWAY_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
		conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
		conn.setRequestProperty("Content-Type", "application/json");

		OutputStream os = conn.getOutputStream();
		os.write(jsonPayload.getBytes());
		os.flush();
		os.close();

		int statusCode = conn.getResponseCode();
		System.out.println("Response from Telegram Gateway: \n");
		System.out.println("Status Code: " + statusCode);
		BufferedReader br = new BufferedReader(
				new InputStreamReader((statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()));
		String output;
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();
	}

	
	public static void main(String[] args) throws Exception {
		sendSlotAvailabilityNotification();
		logger.debug("Main method called successfully");
	}
	
	public static void sendSlotAvailabilityNotification() {
        try {
			List<UserNotificationPreferences> userPrefList = getAllUserNotiPref();
			
			for(UserNotificationPreferences userPref : userPrefList) {
				String jsonResponse =  getCentresDetailByPinCode(userPref.getPincode());
				logger.debug(jsonResponse);
				JSONObject resobj = new JSONObject(jsonResponse);
				JSONArray centers = (JSONArray)resobj.get("centers");
				
				for (int i = 0; i < centers.length(); i++) {
					JSONObject objects = centers.getJSONObject(i);
				}
				if(userPref.getEmail() != null) {
					//sent mail to this email id
					String subject = "Slots available for Covid Vaccination";
					String bodyMessage = "";
					sendEmail(userPref.getEmail(),subject, getSlotsAvailabilityTemplate(bodyMessage));
					logger.debug("Notification mail sent successfully");
				}
				if(userPref.getNumber() != null) {
					//sent message to this number
				}
			}
		} catch (Exception e) {
			logger.error("Error while sending slot availability notification" ,e );
		}
	}
	
	public static List<UserNotificationPreferences> getAllUserNotiPref() {
		List<UserNotificationPreferences> userPrefList = new ArrayList<>();
		Statement statement = Configuration.getStatementFromDB();
		String selectQuery = "select * from UserNotificationPref";
		try(ResultSet rs = statement.executeQuery(selectQuery);) {
			while (rs.next()) {
				UserNotificationPreferences userPref = new UserNotificationPreferences();
				userPref.setEmail(rs.getString("email"));
				userPref.setPincode(rs.getString("pinCode"));
				userPref.setDose(rs.getString("dose"));
				userPref.setAge(rs.getString("age"));
				userPref.setVaccine(rs.getString("vaccine"));
				userPrefList.add(userPref);
			}
		} catch (SQLException e) {
			logger.error("Error while execution of select query",e);
		}
		return userPrefList;
	}
	
	public static String getCentresDetailByPinCode(String pincode)  throws Exception{
		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
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
		return jsonResponse;
	}

	public static void sendEmail(String toEmailId,String subject, String message) throws MessagingException{
		Session session = Configuration.getMailSessionObj();
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom();
			msg.setRecipients(Message.RecipientType.TO, toEmailId);
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
			logger.debug("Mail sent successfully");
		} catch (MessagingException mex) {
			logger.error("send failed, exception: " , mex);
			throw mex;
		}
	}
	
	public static String getSlotsAvailabilityTemplate(String slotMessage) {
		String message = "Hi,\nSlots are available on below Centres\n";
		message = message + slotMessage + "\n\n";
		message = message + "\n\nThanks\nBestAtOne.com";
		return message;
	}

}
