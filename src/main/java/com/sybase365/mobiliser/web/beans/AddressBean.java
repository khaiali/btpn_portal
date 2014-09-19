package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

public class AddressBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String street1;
    private String street2;
    private String houseNo;
    private String zip;
    private String city;
    private String state;
    private String email;
    private String phone1;
    private String phone2;
    private String fax;
    private String company;
    private String url;
    private String position;
    private Long id;
    private String title;
    private int gender;
    private String phone;
    private String SecQuesAns;
    private String birthDateString;
    private XMLGregorianCalendar birthDateAsXml;
    private String kvGender;
    private String kvCountry;
    private String securityQuestion;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getCompany() {
	return company;
    }

    public void setCompany(String company) {
	this.company = company;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getPosition() {
	return position;
    }

    public void setPosition(String position) {
	this.position = position;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getStreet1() {
	return street1;
    }

    public void setStreet1(String street1) {
	this.street1 = street1;
    }

    public String getStreet2() {
	return street2;
    }

    public void setStreet2(String street2) {
	this.street2 = street2;
    }

    public String getHouseNo() {
	return houseNo;
    }

    public void setHouseNo(String houseNo) {
	this.houseNo = houseNo;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public void setKvCountry(String kvCountry) {
	this.kvCountry = kvCountry;
    }

    public String getKvCountry() {
	return kvCountry;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getEmail() {
	return email;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getGender() {
	return gender;
    }

    public void setGender(int gender) {
	this.gender = gender;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public String getKvGender() {
	// if (kvGender == null) {
	// kvGender = new KeyValue<Integer, String>();
	// kvGender.setKey(this.gender);
	// }
	return kvGender;
    }

    public void setKvGender(String kvGender) {
	this.kvGender = kvGender;
	// if (this.kvGender != null)
	// this.gender = this.kvGender.getKey();
    }

    public String getSecurityQuestion() {
	return securityQuestion;
    }

    public void setSecurityQuestion(String securityQue) {
	this.securityQuestion = securityQue;
    }

    public String getSecQuesAns() {
	return SecQuesAns;
    }

    public void setSecQuesAns(String SecQuesAns) {
	this.SecQuesAns = SecQuesAns;
    }

    /*
     * public String getPhone1() { return PayboxUtils.exists(phone1) ? new
     * PhoneNumber(phone1, Constants
     * .getInstance().getDefaultCountryCode()).getNationalFormat() : null; }
     * 
     * public String getPhone1International() { return
     * PayboxUtils.exists(phone1) ? new PhoneNumber(phone1, Constants
     * .getInstance().getDefaultCountryCode()) .getInternationalFormat() : null;
     * }
     * 
     * public void setPhone1(String phone1) { this.phone1 =
     * PayboxUtils.exists(phone1) ? new PhoneNumber(phone1,
     * Constants.getInstance().getDefaultCountryCode())
     * .getInternationalFormat() : null; }
     * 
     * public String getPhone2() { return PayboxUtils.exists(phone2) ? new
     * PhoneNumber(phone2, Constants
     * .getInstance().getDefaultCountryCode()).getNationalFormat() : null; }
     * 
     * public String getPhone2International() { return
     * PayboxUtils.exists(phone2) ? new PhoneNumber(phone2, Constants
     * .getInstance().getDefaultCountryCode()) .getInternationalFormat() : null;
     * }
     * 
     * public void setPhone2(String phone2) { this.phone2 =
     * PayboxUtils.exists(phone2) ? new PhoneNumber(phone2,
     * Constants.getInstance().getDefaultCountryCode())
     * .getInternationalFormat() : null; }
     * 
     * public String getFax() { return PayboxUtils.exists(fax) ? new
     * PhoneNumber(fax, Constants
     * .getInstance().getDefaultCountryCode()).getNationalFormat() : null; }
     * 
     * public String getFaxInternational() { return PayboxUtils.exists(fax) ?
     * new PhoneNumber(fax, Constants .getInstance().getDefaultCountryCode())
     * .getInternationalFormat() : null; }
     * 
     * public void setFax(String fax) { this.fax = PayboxUtils.exists(fax) ? new
     * PhoneNumber(fax, Constants .getInstance().getDefaultCountryCode())
     * .getInternationalFormat() : null; }
     */
}
