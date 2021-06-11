package com.example.appengine.demos.springboot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class CowinServices {
	private static Logger logger = Logger.getLogger(CowinServices.class);
	public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private Dao dao = new Dao();
	
	public void sendEmail(String toEmailId,String subject, String message, String contentType) throws MessagingException{
		Session session = getMailSessionObj();
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
	
	public static String getNextDayInString(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, days);
		String newDate = dateFormat.format(c.getTime());
		return newDate;
	}
	
	public String getCentresDetailByPinCode(String pincode)  throws Exception{
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
	
	public Map<String,String> getDistinctPinDataOfUsers() throws Exception {
		List<String> pinCodeList = new ArrayList();
		Map<String, String> pinResponseMap = dao.getDistinctUsersPin(pinCodeList);
		for(String pincode : pinCodeList) {
			String responsData = getCentresDetailByPinCode(pincode);
			pinResponseMap.put(pincode,responsData);
		}
		return pinResponseMap;
	}
	
	
	
	public static Session getMailSessionObj() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.googlemail.com");
		props.put("mail.from", Configuration.FROM_EMAIL);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Configuration.FROM_EMAIL, Configuration.EMAIL_PASSCODE);
			}
		});
		return session;
	}
}
