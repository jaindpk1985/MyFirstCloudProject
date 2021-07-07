
package com.example.appengine.demos.springboot;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;
import org.json.JSONArray ;
import org.json.JSONException;
import org.json.JSONObject ;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Scheduled;


@RestController
public class HelloworldController {
	static Logger logger = Logger.getLogger(HelloworldController.class);
	public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	public static final String BORDER_STYLE = "border: 1px solid black;border-collapse: collapse;text-align: center;";
	public static final String BORDER_COLOR = "background-color:#ADD8E6;";
	public static final String REGISTRATION_MESSAGE_BODY = "Dear User,<br><br>You have been registered for Covid Vaccination Slot Information.<br><br>We will update you once slots are available with below filter.<br><table style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'>"
			+ "<tr><td colspan='2' style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>District:</strong> districtVal</td></tr>"
			+ "<tr><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Pincode:</strong> pincodeVal</td><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Dose:</strong> doseVal</td></tr><tr><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Vaccine:</strong> vaccineVal</td><td style='border: 1px solid black;border-collapse: collapse;background-color:#00ff00;'><strong>Age:</strong> ageVal</td></tr></table><br><a href='http://bestatone.com/covid-vaccination/'>Click to Register for another filter</a><br><br>Thanks<br><a href='http://bestatone.com/'>BestAtOne.com</a>";
	public static final String MESSAGE_BODY = "Dear User,<br>Slots are available on below Centres.<br><br>searchParams<br><table>tableBody</table><br>"
			+ "<a href='https://selfregistration.cowin.gov.in/'>Click to Book Slot</a><br><br>"
			+ "<a href='https://myfirstcloudproject-316520.appspot.com/getCurrentSlotsStatus/?filterStr=132'>Click to get current Slots availability for this filter</a><br><br>"
			+ "<a href='http://bestatone.com/covid-vaccination/'>Click to Register for another filter</a><br><br>"
			+ "<a href='http://bestatone.com/search-covid-vaccination-centre-and-get-notification-immediately-when-slots-comes/'>Click to give feedback and Suggestions in comment section</a><br><br>"
			+ "Thanks<br><a href='http://bestatone.com/'>BestAtOne.com</a>";
	public static final String SEARCH_TEXT = "<strong>District:</strong> districtVal<br><strong>Pincode:</strong> pincodeVal, <strong>Dose:</strong> doseVal <br> <strong>Age:</strong> ageVal, <strong>Vaccine:</strong> vaccineVal";
	public static final String TABLE_HEADER_TEXT = "<tr><th style ='"+BORDER_STYLE + BORDER_COLOR +"' >Centre</th><th style ='"+BORDER_STYLE + BORDER_COLOR +"' >strDate1</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate2</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate3</th><th style ='"+BORDER_STYLE+ BORDER_COLOR +"' >strDate4</th></tr>";
	public static final String MESSAGE_ROW_TEXT = "<tr><td style ='"+BORDER_STYLE +"'>centreDetailStr</td><td style ='"+BORDER_STYLE +"'>day1Slot</td><td style ='"+BORDER_STYLE +"'>day2Slot</td><td style ='"+BORDER_STYLE +"'>day3Slot</td><td style ='"+BORDER_STYLE +"'>day4Slot</td></tr>";
	public static final String SLOT_MESSAGE_SUBJECT = "Slots available for Covid Vaccination as on strDate";
	public static final String CENTRE_DETAIL_TEXT = "<strong>centreName(<span style='background-color:yellow;'>feeType</span>)</strong><br>centreAddress";
	public static final String SLOT_TEXT = "<div style='background-color:green;color:yellow'>slot</div>";
	private Dao dao = new Dao();
	private CowinServices cowinServices = new CowinServices();
	
	public static void main(String[] args) throws Exception {
		HelloworldController helloworldController = new HelloworldController();
		//helloworldController.sendSlotAvailabilityNotification();
		//CowinServices cowinServices = new CowinServices();
		//cowinServices.getCentresDetailByPinCode("110092");
		
		//helloworldController.getCurrentSlotsStatus("132");
		
	}
	
	
	
	
	@GetMapping("/")
	public String hello() {
		return "Hello world - Deepak Jain came to Cloud";
	}

	@CrossOrigin("http://bestatone.com")
	@PostMapping(value = "/saveNumber")
	@ResponseBody
	public String saveNumber(@RequestParam String number, @RequestParam String pinCode, @RequestParam String email,
			@RequestParam String dose, @RequestParam String age, @RequestParam String vaccine, @RequestParam String districtId, @RequestParam String districtName) {
		try {
			dao.saveUserPref(number, pinCode, email, dose, age, vaccine,districtId,districtName);
			logger.debug("Data Successfully saved in DB");
			
			if(email != null && !email.equals("")) {
				String subject = "Congrats..Registration Successfull";
				String message = REGISTRATION_MESSAGE_BODY.replace("districtVal",districtName).replace("pincodeVal",pinCode).replace("doseVal",dose).replace("ageVal",age).replace("vaccineVal",vaccine);
				
				ExecutorService executorService = Executors.newFixedThreadPool(10);
				Runnable runTask  = () -> {
					try {
						cowinServices.sendEmail(email,subject,message,"text/html");
						checkSlotAndSendNoti(number, pinCode, email, dose, age, vaccine, districtId, districtName);
					} catch (Exception e) {
						logger.error("Error while send registration and slot mail",e);
					}
				};
				executorService.execute(runTask);
				executorService.shutdown();
			}
		} catch (Exception e) {
			logger.error("Error while send registration mail",e);
			if(e.getCause() != null) {
				return e.getCause().getMessage();
			}else {
				return e.getMessage();
			}
		}
		return "Success";
	}


	//on each 5 minutes
	@GetMapping(value = "/scheduleNotification")
	@ResponseBody
	//@Scheduled(fixedDelay = 3000)
	public ResponseEntity  notificationSchedular() {
		logger.debug("Schedular called successfully");
		try {
			sendSlotAvailabilityNotification();
		} catch (Exception e1) {
			logger.error("Error while sending notification",e1);
			try {
				cowinServices.sendEmail(Configuration.TO_EMAIL,"Error while executing schedular",e1.getMessage(),"text");
			} catch (Exception e) {
				//do nothing;
			}
			return new ResponseEntity(HttpStatus.METHOD_FAILURE);
		}
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
		
	@GetMapping(value = "/fireService")
	public String fireService() {
		dao.resetAllUserPref();
		logger.debug("Reset Schedular Executed Manually");
		return "Preferences are Reset successfully";
	}
	
	@GetMapping(value = "/resetNotifications")
	@ResponseBody
	public ResponseEntity resetNotifications() {
		dao.resetAllUserPref();
		logger.debug("Reset Schedular Executed Automatically");
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	
	private void sendSlotAvailabilityNotification() throws Exception {
		List<UserNotificationPreferences> userPrefList = dao.getAllUserNotiPref();
		Map<String, String> pinResponseMap = cowinServices.getDistinctPinDataOfUsers();
		Map<String, String> districtResponseMap = cowinServices.getDistinctDistrictDataOfUsers();
		for (UserNotificationPreferences userPref : userPrefList) {
			sendNotificationByPref(userPref, pinResponseMap, districtResponseMap);
		}
	}

	public boolean sendNotificationByPref(UserNotificationPreferences userPref, Map<String,String> pinResponseMap, Map<String,String> districtResponseMap)
			throws Exception, JSONException, MessagingException {
		String jsonResponse;
		if(userPref.getDistrictId() != null && userPref.getDistrictId().trim().length() > 0) {
			jsonResponse = districtResponseMap.get(userPref.getDistrictId());
		}else {
			jsonResponse =  pinResponseMap.get(userPref.getPincode());
		}
		if(jsonResponse == null) {
			return false;
		}
		JSONObject resobj = new JSONObject(jsonResponse);
		JSONArray centers = (JSONArray)resobj.get("centers");
		centers = sortJsonArray(centers);
		String strDate1 = CowinServices.getNextDayInString(0); 
		String strDate2 = CowinServices.getNextDayInString(1);
		String strDate3 = CowinServices.getNextDayInString(2);
		String strDate4 = CowinServices.getNextDayInString(3);
		String rowDetail  = "";
		for (int i = 0; i < centers.length(); i++) {
			JSONObject centre = centers.getJSONObject(i);
			rowDetail  = rowDetail + getRow(userPref, centre,strDate1,strDate2,strDate3,strDate4);
		}
		if(rowDetail.trim().length() == 0) {
			//no need to send notification for this case.
			return false;
		}
		
		if(userPref.getEmail() != null) {
			String districtVal = "";
			String pincode = "";
			if(userPref.getDistrictId() != null) {
				districtVal = userPref.getDistrictName();
			}
			if(userPref.getPincode() != null) {
				pincode = userPref.getPincode();
			}
			String searchParamTextVal = SEARCH_TEXT.replace("districtVal", districtVal).replace("pincodeVal", pincode)
					.replace("doseVal",userPref.getDose()).replace("ageVal", userPref.getAge()).replace("vaccineVal",userPref.getVaccine());
			String headerString = getHeader(strDate1,strDate2,strDate3,strDate4);
			String tableBody = headerString + rowDetail;
			String messageBody = MESSAGE_BODY.replace("searchParams",searchParamTextVal).replace("tableBody",tableBody);
			cowinServices.sendEmail(userPref.getEmail(),SLOT_MESSAGE_SUBJECT.replace("strDate",strDate1), messageBody,"text/html");
			logger.debug("Notification mail sent successfully");
			//update userpref in DB notification_sent = 'Y'
			dao.updateUserPrefNotiSent(userPref);
		}
		if(userPref.getNumber() != null) {
			//sent message to this number
		}
		return true;
	}
	
	private String getHeader(String strDate1, String strDate2, String strDate3, String strDate4) {
		return TABLE_HEADER_TEXT.replace("strDate1", strDate1).replace("strDate2", strDate2)
				.replace("strDate3", strDate3).replace("strDate4", strDate4);
	}
	
	private String getRow(UserNotificationPreferences userPref, JSONObject centre,String strDate1,String strDate2,String strDate3,String strDate4) throws JSONException {
		String rowStr = "";
		String feeType = (String)centre.get("fee_type");
		String feesText = "";
		if("Paid".equals(feeType)) {
			try {
				JSONArray feeDetails = (JSONArray)centre.get("vaccine_fees");
				feesText = getFeesText(userPref,feeDetails);
			} catch (Exception e) {
				//do nothing
			}
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
	
	private String validateAndGetSlot(UserNotificationPreferences userPref, JSONObject dateData) throws JSONException {
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
	
	private JSONArray sortJsonArray(JSONArray jsonArray) throws JSONException {
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
	
	private boolean checkVaccine(UserNotificationPreferences userPref, String vaccine) {
		if("COVAXIN".equals(userPref.getVaccine()) && "COVAXIN".equals(vaccine) || "COVISHIELD".equals(userPref.getVaccine()) && "COVISHIELD".equals(vaccine)){
			return true;
		}
		return false;
	}
	
	private JSONArray getApplicableAgeData(UserNotificationPreferences userPref, JSONObject centre) throws JSONException {
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
	
	private boolean checkAgeLimit(UserNotificationPreferences userPref, int min_age_limit){
		if("Age18".equals(userPref.getAge()) && min_age_limit == 18 || "Age45".equals(userPref.getAge()) && min_age_limit == 45){
			return true;
		}
		return false;
   }
	
	private String getFeesText(UserNotificationPreferences userPref, JSONArray feeDetails) throws JSONException {
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
	
	private void checkSlotAndSendNoti(String number, String pinCode, String email, String dose, String age, String vaccine, String districtId, String districtName) {
		Map<String,String> pinResponseMap = new HashMap<>();
		Map<String,String> districtResponseMap = new HashMap<>();
		UserNotificationPreferences userNotPref = new UserNotificationPreferences();
		userNotPref.setNumber(number);
		userNotPref.setPincode(pinCode);
		userNotPref.setEmail(email);
		userNotPref.setDose(dose);
		userNotPref.setAge(age);
		userNotPref.setVaccine(vaccine);
		userNotPref.setDistrictId(districtId);
		userNotPref.setDistrictName(districtName);
		if(pinCode != null && pinCode.trim().length() > 0) {
			String responseData = cowinServices.getCentresDetailByPinCode(pinCode);
			pinResponseMap.put(pinCode, responseData);
			try {
				sendNotificationByPref(userNotPref,pinResponseMap,districtResponseMap);
			} catch (Exception e) {
				logger.error("Error while sending slot mail while registration" , e);
			}
		}
		else {
			String responseData = cowinServices.getCentresDetailByDistrictId(districtId);
			districtResponseMap.put(districtId, responseData);
			try {
				sendNotificationByPref(userNotPref,pinResponseMap ,districtResponseMap);
			} catch (Exception e) {
				logger.error("Error while sending slot mail while registration" , e);
			}
		}
		
	}
}
