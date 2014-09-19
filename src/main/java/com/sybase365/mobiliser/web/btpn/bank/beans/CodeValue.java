package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This class is used as the id and value for dropdown choices.
 * 
 * @author Vikram Gunda
 */
public class CodeValue implements Serializable, Comparable<CodeValue> {

	private static final long serialVersionUID = 1L;

	/* Id of the dropdown */
	private String id;

	/* Value of the dropdown */
	private String value;

	/**
	 * Constructor.
	 */
	public CodeValue() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param id id of the dropdown
	 */
	public CodeValue(final String id) {
		super();
		this.id = id;
	}

	/**
	 * Constructor.
	 * 
	 * @param id id of the dropdown
	 * @param value value for the dropdown
	 */
	public CodeValue(String id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * gets the id of the dropdown.
	 * 
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets the id of the dropdown.
	 * 
	 * @param id id of the dropdown
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * gets the value of the dropdown.
	 * 
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * sets the value of the dropdown.
	 * 
	 * @param value value for the dropdown
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * overrides the equals method.
	 * 
	 * @param obj object to compare
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CodeValue) {
			final CodeValue object = (CodeValue) obj;
			final String id = object.getId();
			final String value = object.getValue();
			return (id != null && id.equals(this.id)) && (value != null && value.equals(this.value));
		}
		return false;
	}

	/**
	 * compareTo method.
	 * 
	 * @param obj object to compare
	 * @return int return the int value
	 */
	@Override
	public int compareTo(CodeValue obj) {
		if (obj != null) {
			return obj.getId().compareTo(this.id);
		}
		return 0;
	}

	public String getIdAndValue(){
		return this.id + " | " + this.value;
	}
	
	public void setIdAndValue(final String idAndValue){
		//Nothing to do
	}

	/**
	 * Overriding the toString method.
	 * 
	 * @return String return the String value
	 */
	@Override
	public String toString() {
		return this.id;
	}
}
