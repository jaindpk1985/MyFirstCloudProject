package com.example.appengine.demos.springboot;

import java.util.Date;

public class UserNotificationPreferences {

	private Long id;
	private String number;
	private String pincode;
	private String email;
	private Date reg_date;
	private String dose;
	private String age;
	private String vaccine;
	private String notificationSent;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getReg_date() {
		return reg_date;
	}
	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}
	public String getDose() {
		return dose;
	}
	public void setDose(String dose) {
		this.dose = dose;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getVaccine() {
		return vaccine;
	}
	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}
	public String getNotificationSent() {
		return notificationSent;
	}
	public void setNotificationSent(String notificationSent) {
		this.notificationSent = notificationSent;
	}
}
