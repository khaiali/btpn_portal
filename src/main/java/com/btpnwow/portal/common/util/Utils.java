package com.btpnwow.portal.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Utils {
	
	
	
	public String dateFormat(String date){
		String newDate = null;
		try {
			SimpleDateFormat inputFormat  = new SimpleDateFormat("YYYYMMDD");
	        SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMYY");

	        Date parsedDate = inputFormat.parse(date);
	        System.out.println(outputFormat.format(parsedDate));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return newDate;
	}
	
	
	public static String removePadding(String text, String allign, String type) {
		String result = "";
		char[] textChar = text.toCharArray();
		if(allign.equalsIgnoreCase("right")) {
			for(int i=0; i<textChar.length; i++) {
				if(!String.valueOf(textChar[i]).equals(type)) {
					result = text.substring(i);
					break;
				}
			}
		}
		else if(allign.equalsIgnoreCase("left")) {
			for(int i=textChar.length-1; i>0; i--) {
				if(!String.valueOf(textChar[i]).equals(type)) {
					result = text.substring(0,i+1);
					break;
				}
			}
		}
		return result;
	}
	
	
	
	
	public static HashMap getParameterMap() {
		return parameterMap;
	}

	public static void setParameterMap(HashMap parameterMap) {
		Utils.parameterMap = parameterMap;
	}

	public static HashMap parameterMap = new HashMap();
	
	
	
}