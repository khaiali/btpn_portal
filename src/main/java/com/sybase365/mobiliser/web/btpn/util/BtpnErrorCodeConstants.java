package com.sybase365.mobiliser.web.btpn.util;

import java.util.Arrays;
import java.util.List;

/**
 * This class delcares the error codes constants for Btpn.
 * 
 * @author Vikram Gunda
 */
public class BtpnErrorCodeConstants {

	/**
	 * Default Private Constructor
	 */
	private BtpnErrorCodeConstants() {

	}

	public static final List<Integer> REG_PROCEED = Arrays.asList(89, 93);

	public static final List<Integer> LOGIN_SUCCESS_TEMPORARY_PIN = Arrays.asList(331, 332);
}
