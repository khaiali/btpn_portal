package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class ConsumerBillPaymentBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CodeValue subBiller;
	private String favList;
	private String billNumber;
	private String additionalData;

	public CodeValue getSubBiller() {
		return subBiller;
	}

	public void setSubBiller(CodeValue subBiller) {
		this.subBiller = subBiller;
	}

	public String getFavList() {
		return favList;
	}

	public void setFavList(String favList) {
		this.favList = favList;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

}
