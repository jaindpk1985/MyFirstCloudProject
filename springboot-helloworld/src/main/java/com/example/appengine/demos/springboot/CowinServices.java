package com.example.appengine.demos.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import java.util.Random;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.TimeZone;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class CowinServices {
	private static Logger logger = Logger.getLogger(CowinServices.class);
	public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private Dao dao = new Dao();
	
	public Session getMailSessionObj() {
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
	
	public void sendEmail(String toEmailId, String subject, String message, String contentType)
			throws MessagingException, UnsupportedEncodingException {
		Session session = getMailSessionObj();
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(Configuration.FROM_EMAIL,"BestAtOne.com"));
			msg.setRecipients(javax.mail.Message.RecipientType.TO, toEmailId);
			msg.setSubject(subject);
			if ("text".equals(contentType)) {
				msg.setText(message);
			} else {
				msg.setContent(message, "text/html");
			}
			Transport.send(msg);
			logger.debug("Mail sent successfully");
		} catch (MessagingException mex ) {
			logger.error("send failed, exception: ", mex);
			throw mex;
		} catch (UnsupportedEncodingException ex ) {
			logger.error("send failed, exception: ", ex);
			throw ex;
		} 
	}
	
	public static String getNextDayInString(int days) {
		Calendar c = Calendar.getInstance();
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		c.add(Calendar.DAY_OF_MONTH, days);
		return dateFormat.format(c.getTime());
	}
	
	public String getCentresDetailByPinCode(String pincode){
		try {
			Date date = Calendar.getInstance().getTime();  
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String strDate = dateFormat.format(date); 
			URL url = new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pincode + "&date=" + strDate);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
			connection.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
			connection.setRequestProperty("sec-fetch-site", "cross-site");
			connection.setRequestProperty("sec-fetch-mode", "cors");
			connection.setRequestProperty("sec-fetch-dest", "empty");
			connection.setRequestProperty("access-control-request-method", "GET");
			connection.setRequestProperty("origin", "https://myfirstcloudproject-316520.appspot.com/");
			connection.setRequestProperty("referer", "https://myfirstcloudproject-316520.appspot.com/");
			connection.setRequestProperty("accept-language", "en-US,en;q=0.9");
			connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
			connection.setRequestProperty("authority", "cdn-api.co-vin.in");
			connection.setRequestProperty("scheme", "https");
			
			// This line makes the request
			InputStream responseStream = connection.getInputStream();
			String jsonResponse = new BufferedReader(
				      new InputStreamReader(responseStream, StandardCharsets.UTF_8))
				        .lines()
				        .collect(Collectors.joining("\n"));
			logger.debug("Response recieved for pin " + pincode + " is ");
			return jsonResponse;
		} catch (Exception e) {
			logger.error("Error while retrieving data from cowin",e);
			try {
				sendEmail(Configuration.TO_EMAIL,"Error while retrieving data from cowin",e.getMessage(),"text");
			} catch (Exception em) {
				//do nothing;
			}
		} 
		return null;
	}
	
	public String getCentresDetailByDistrictId(String districtId){
		try {
			Date date = Calendar.getInstance().getTime();  
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String strDate = dateFormat.format(date); 
			URL url = new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=" + districtId + "&date=" + strDate);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
			connection.setRequestProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
			connection.setRequestProperty("sec-fetch-site", "cross-site");
			connection.setRequestProperty("sec-fetch-mode", "cors");
			connection.setRequestProperty("sec-fetch-dest", "empty");
			connection.setRequestProperty("access-control-request-method", "GET");
			connection.setRequestProperty("origin", "https://myfirstcloudproject-316520.appspot.com/");
			connection.setRequestProperty("referer", "https://myfirstcloudproject-316520.appspot.com/");
			connection.setRequestProperty("accept-language", "en-US,en;q=0.9");
			connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
			connection.setRequestProperty("authority", "cdn-api.co-vin.in");
			connection.setRequestProperty("scheme", "https");
			
			// This line makes the request
			InputStream responseStream = connection.getInputStream();
			String jsonResponse = new BufferedReader(
				      new InputStreamReader(responseStream, StandardCharsets.UTF_8))
				        .lines()
				        .collect(Collectors.joining("\n"));
			logger.debug("Response recieved for districtId " + districtId + " is ");
			return jsonResponse;
		} catch (Exception e) {
			logger.error("Error while retrieving data from cowin",e);
			try {
				sendEmail(Configuration.TO_EMAIL,"Error while retrieving data from cowin",e.getMessage(),"text");
			} catch (Exception em) {
				//do nothing;
			}
		} 
		return null;
	}
	
	
	
	public Map<String,String> getDistinctPinDataOfUsers() throws Exception {
		Map<String, String> pinResponseMap = new HashMap<>();
		List<String> pinCodeList = dao.getDistinctUsersPin();
		int i =1;
		for(String pincode : pinCodeList) {
			if(pincode != null && pincode.trim().length() > 0) {
				Thread.sleep(i *1000);
				String responsData = getCentresDetailByPinCode(pincode);
				if(responsData != null) {
					pinResponseMap.put(pincode,responsData);
				}
				if (i == 9) {
					i = 1;
				} else {
					i = i + 2;
				}
			}
		}
		return pinResponseMap;
	}
		
	public Map<String,String> getDistinctDistrictDataOfUsers() throws Exception {
		Map<String, String> districtResponseMap = new HashMap<>();
		List<String> districtIdList = dao.getDistinctDistrictId();
		int i =1;
		for(String districtId : districtIdList) {
			if(districtId !=null && districtId.trim().length() > 0) {
				Thread.sleep(i *1000);
				String responsData = getCentresDetailByDistrictId(districtId);
				if(responsData != null) {
					districtResponseMap.put(districtId,responsData);
				}
				if (i == 9) {
					i = 1;
				} else {
					i = i + 2;
				}
			}
		}
		return districtResponseMap;
	}
	
	public static int getRandomNumber() {
		Random random = new Random();
		int rand = 0;
		while (true){
		    rand = random.nextInt(11);
		    if(rand !=0) break;
		}
		return rand;
	}
	
	
}
