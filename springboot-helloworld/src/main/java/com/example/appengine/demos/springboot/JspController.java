package com.example.appengine.demos.springboot;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/jspController")
public class JspController {
	static Logger logger = Logger.getLogger(JspController.class);
	private Dao dao = new Dao();
	private CowinServices cowinServices = new CowinServices();
	private HelloworldController helloworldController = new HelloworldController();

	@RequestMapping(value = "/getCurrentSlotsStatus")
	public String getCurrentSlotsStatus(Map<String, Object> model, @RequestParam(value = "filterStr") String idStr) {
		Map<String, String> pinResponseMap = new HashMap();
		Map<String, String> districtResponseMap = new HashMap();
		try {
			UserNotificationPreferences userPref = dao.getUserPrefById(Long.valueOf(idStr));
			if(userPref.getPincode() != null) {
				String responsData = cowinServices.getCentresDetailByPinCode(userPref.getPincode());
				if(responsData != null) {
					pinResponseMap.put(userPref.getPincode(),responsData);
				}
			}
			else if(userPref.getDistrictId() != null) {
				String responsData = cowinServices.getCentresDetailByDistrictId(userPref.getDistrictId());
				if(responsData != null) {
					districtResponseMap.put(userPref.getDistrictId(),responsData);
				}
			}
			boolean status = helloworldController.sendNotificationByPref(userPref, pinResponseMap, districtResponseMap);
			if(!status) {
				model.put("message", "No slots available currently. We will notify you once available");
				return "success";
			}
			logger.debug("Current status slots mail sent");
		}
		catch(Exception e) 
		{
			model.put("message", "Some error occured. Please check after some time");
			return "success";
		}
		model.put("message", "Current Slots status mail Sent Successfully. Please check your mail.");
		return "success";
	}
	
}
