package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Transaction General Ledger Bean.
 * 
 * @author Vikram Gunda
 */
public class TransactionGeneralLedgerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer useCaseId;

	private String useCaseName;

	private CodeValue currentGL;

	private CodeValue newGL;

	private String status;

	private String createdBy;

	private String taskId;

	private Long idPi;

	public Integer getUseCaseId() {
		return useCaseId;
	}

	public void setUseCaseId(Integer useCaseId) {
		this.useCaseId = useCaseId;
	}

	public String getUseCaseName() {
		return useCaseName;
	}

	public void setUseCaseName(String useCaseName) {
		this.useCaseName = useCaseName;
	}

	public CodeValue getCurrentGL() {
		return currentGL;
	}

	public void setCurrentGL(CodeValue currentGL) {
		this.currentGL = currentGL;
	}

	public CodeValue getNewGL() {
		return newGL;
	}

	public void setNewGL(CodeValue newGL) {
		this.newGL = newGL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Long getIdPi() {
		return idPi;
	}

	public void setIdPi(Long idPi) {
		this.idPi = idPi;
	}

}
