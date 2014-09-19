package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 * This is the Salary Data for bank portals.
 * 
 * @author Vikram Gunda
 */
public class SearchSalaryDataBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dateRangeBegin;
	private Date dateRangeEnd;
	private String note;
	
	private String name;
	private FileUpload fileUpload;
	private CodeValue type;
	private CodeValue status;
	
	private String callerId;	
	private String workFlowId;
	
	private List<SalaryDataBean> salaryDataList;

	public Date getDateRangeBegin() {
		return dateRangeBegin;
	}

	public void setDateRangeBegin(Date dateRangeBegin) {
		this.dateRangeBegin = dateRangeBegin;
	}

	public Date getDateRangeEnd() {
		return dateRangeEnd;
	}

	public void setDateRangeEnd(Date dateRangeEnd) {
		this.dateRangeEnd = dateRangeEnd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}
	
	public CodeValue getType() {
		return type;
	}

	public void setType(CodeValue type) {
		this.type = type;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public CodeValue getStatus() {
		return status;
	}

	public void setStatus(CodeValue status) {
		this.status = status;
	}

	
	public String getCallerId() {
		return callerId;
	}

	public void setCallerId(String callerId) {
		this.callerId = callerId;
	}
	
	public String getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(String workFlowId) {
		this.workFlowId = workFlowId;
	}

	public List<SalaryDataBean> getSalaryDataList() {
		if (salaryDataList == null) {
			salaryDataList = new ArrayList<SalaryDataBean>();
		}
		return salaryDataList;
	}

	public void setSalaryDataList(List<SalaryDataBean> salaryDataList) {
		this.salaryDataList = salaryDataList;
	}

}
