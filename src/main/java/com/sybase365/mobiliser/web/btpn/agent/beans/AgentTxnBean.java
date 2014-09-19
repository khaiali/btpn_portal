package com.sybase365.mobiliser.web.btpn.agent.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class AgentTxnBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date fromDate;

	private Date toDate;

	private String agentId;

	private String subAgentMobile;

	private CodeValue txnType;

	private CodeValue txnStatus;

	private boolean isViewHierarchy;

	private List<AgentTxnDataBean> agentTxnDatalist;

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

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getSubAgentMobile() {
		return subAgentMobile;
	}

	public void setSubAgentMobile(String subAgentMobile) {
		this.subAgentMobile = subAgentMobile;
	}

	public CodeValue getTxnType() {
		return txnType;
	}

	public void setTxnType(CodeValue txnType) {
		this.txnType = txnType;
	}

	public CodeValue getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(CodeValue txnStatus) {
		this.txnStatus = txnStatus;
	}

	public boolean isViewHierarchy() {
		return isViewHierarchy;
	}

	public void setViewHierarchy(boolean isViewHierarchy) {
		this.isViewHierarchy = isViewHierarchy;
	}

	public List<AgentTxnDataBean> getAgentTxnDatalist() {
		if (agentTxnDatalist == null) {
			agentTxnDatalist = new ArrayList<AgentTxnDataBean>();
		}
		return agentTxnDatalist;
	}

	public void setAgentTxnDatalist(List<AgentTxnDataBean> agentTxnDatalist) {
		this.agentTxnDatalist = agentTxnDatalist;
	}

}
