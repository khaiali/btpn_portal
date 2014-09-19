package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

public class ApprovalConfBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer customerTypeId;
    private Integer piTypeId;
    private Integer useCaseId;
    private Integer bulkFileTypeId;

    private String makerPrivilege;
    private String checkerPrivilege;
    private String executePrivilege;
    private String payerPrivilege;
    private String payeePrivilege;
    private String callerPrivilege;
    private String callerParentPrivilege;
    private String callerSelfPrivilege;

    public String getMakerPrivilege() {
	return makerPrivilege;
    }

    public void setMakerPrivilege(String makerPrivilege) {
	this.makerPrivilege = makerPrivilege;
    }

    public String getCheckerPrivilege() {
	return checkerPrivilege;
    }

    public void setCheckerPrivilege(String checkerPrivilege) {
	this.checkerPrivilege = checkerPrivilege;
    }

    public String getExecutePrivilege() {
	return executePrivilege;
    }

    public void setExecutePrivilege(String executePrivilege) {
	this.executePrivilege = executePrivilege;
    }

    public Integer getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(Integer customerTypeId) {
	this.customerTypeId = customerTypeId;
    }

    public Integer getPiTypeId() {
	return piTypeId;
    }

    public void setPiTypeId(Integer piTypeId) {
	this.piTypeId = piTypeId;
    }

    public Integer getUseCaseId() {
	return useCaseId;
    }

    public void setUseCaseId(Integer useCaseId) {
	this.useCaseId = useCaseId;
    }

    public Integer getBulkFileTypeId() {
	return bulkFileTypeId;
    }

    public void setBulkFileTypeId(Integer bulkFileTypeId) {
	this.bulkFileTypeId = bulkFileTypeId;
    }

    public String getPayerPrivilege() {
	return payerPrivilege;
    }

    public void setPayerPrivilege(String payerPrivilege) {
	this.payerPrivilege = payerPrivilege;
    }

    public String getPayeePrivilege() {
	return payeePrivilege;
    }

    public void setPayeePrivilege(String payeePrivilege) {
	this.payeePrivilege = payeePrivilege;
    }

    public String getCallerPrivilege() {
	return callerPrivilege;
    }

    public void setCallerPrivilege(String callerPrivilege) {
	this.callerPrivilege = callerPrivilege;
    }

    public String getCallerParentPrivilege() {
	return callerParentPrivilege;
    }

    public void setCallerParentPrivilege(String callerParentPrivilege) {
	this.callerParentPrivilege = callerParentPrivilege;
    }

    public String getCallerSelfPrivilege() {
	return callerSelfPrivilege;
    }

    public void setCallerSelfPrivilege(String callerSelfPrivilege) {
	this.callerSelfPrivilege = callerSelfPrivilege;
    }

}
