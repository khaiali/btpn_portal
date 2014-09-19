package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sreenivasulu
 */

public class SearchTxnDataBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date fromDate;

	private Date toDate;
	
	private CodeValue paymentInstrumentId;

	private Long svaBalance;

	private List<String> typeList;

	private List<TxnDataBean> txnDataList;

	private CodeValue txnType;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public CodeValue getPaymentInstrumentId() {
		return paymentInstrumentId;
	}

	public void setPaymentInstrumentId(CodeValue paymentInstrumentId) {
		this.paymentInstrumentId = paymentInstrumentId;
	}

	public List<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}

	public List<TxnDataBean> getTxnDataList() {
		if (txnDataList == null) {
			txnDataList = new ArrayList<TxnDataBean>();
		}
		return txnDataList;
	}

	public void setTxnDataList(List<TxnDataBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

	public Long getSvaBalance() {
		return svaBalance;
	}

	public void setSvaBalance(Long svaBalance) {
		this.svaBalance = svaBalance;
	}

	public CodeValue getTxnType() {
		return txnType;
	}

	public void setTxnType(CodeValue txnType) {
		this.txnType = txnType;
	}

}
