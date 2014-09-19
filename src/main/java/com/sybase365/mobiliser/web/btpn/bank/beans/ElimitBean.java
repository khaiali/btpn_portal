package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This is the bean used for Bank Portal Cash Out Operations
 * 
 * @author Febrie Subhan
 */
public class ElimitBean implements Serializable {

	private static final long serialVersionUID = 1L;

	Long id;
	String taskId;
	Long piType;
	Long pi;
	Long customerType;
	Long customer;
	Long useCase;
	Long singleDebitMinAmount;
	Long singleDebitMaxAmount;
	Long singleCreditMinAmount;
	Long singleCreditMaxAmount;
	Long dailyDebitMaxAmount;
	Long weeklyDebitMaxAmount;
	Long monthlyDebitMaxAmount;
	Long dailyCreditMaxAmount;
	Long weeklyCreditMaxAmount;
	Long monthlyCreditMaxAmount;
	Long dailyDebitMaxCount;
	Long weeklyDebitMaxCount;
	Long monthlyDebitMaxCount;
	Long dailyCreditMaxCount;
	Long weeklyCreditMaxCount;
	Long monthlyCreditMaxCount;
	Long maximumBalance;
	Long minimumBalance;
	Date creationDate;
	Long creator;
	String status;
	String description;
	private Date dateFrom;
	private Date dateTo;
	
	
	private CodeValue selectedPiType;
	
	private CodeValue selectedCustomerType;
	
	private CodeValue selectedUseCases;
	
	private List<ElimitBean> limitList;
	

	public Long getSingleDebitMinAmount() {
		return singleDebitMinAmount;
	}

	public void setSingleDebitMinAmount(Long singleDebitMinAmount) {
		this.singleDebitMinAmount = singleDebitMinAmount;
	}

	public Long getSingleDebitMaxAmount() {
		return singleDebitMaxAmount;
	}

	public void setSingleDebitMaxAmount(Long singleDebitMaxAmount) {
		this.singleDebitMaxAmount = singleDebitMaxAmount;
	}

	public Long getSingleCreditMinAmount() {
		return singleCreditMinAmount;
	}

	public void setSingleCreditMinAmount(Long singleCreditMinAmount) {
		this.singleCreditMinAmount = singleCreditMinAmount;
	}

	public Long getSingleCreditMaxAmount() {
		return singleCreditMaxAmount;
	}

	public void setSingleCreditMaxAmount(Long singleCreditMaxAmount) {
		this.singleCreditMaxAmount = singleCreditMaxAmount;
	}

	public Long getDailyDebitMaxAmount() {
		return dailyDebitMaxAmount;
	}

	public void setDailyDebitMaxAmount(Long dailyDebitMaxAmount) {
		this.dailyDebitMaxAmount = dailyDebitMaxAmount;
	}

	public Long getWeeklyDebitMaxAmount() {
		return weeklyDebitMaxAmount;
	}

	public void setWeeklyDebitMaxAmount(Long weeklyDebitMaxAmount) {
		this.weeklyDebitMaxAmount = weeklyDebitMaxAmount;
	}

	public Long getMonthlyDebitMaxAmount() {
		return monthlyDebitMaxAmount;
	}

	public void setMonthlyDebitMaxAmount(Long monthlyDebitMaxAmount) {
		this.monthlyDebitMaxAmount = monthlyDebitMaxAmount;
	}

	public Long getDailyCreditMaxAmount() {
		return dailyCreditMaxAmount;
	}

	public void setDailyCreditMaxAmount(Long dailyCreditMaxAmount) {
		this.dailyCreditMaxAmount = dailyCreditMaxAmount;
	}

	public Long getWeeklyCreditMaxAmount() {
		return weeklyCreditMaxAmount;
	}

	public void setWeeklyCreditMaxAmount(Long weeklyCreditMaxAmount) {
		this.weeklyCreditMaxAmount = weeklyCreditMaxAmount;
	}

	public Long getMonthlyCreditMaxAmount() {
		return monthlyCreditMaxAmount;
	}

	public void setMonthlyCreditMaxAmount(Long monthlyCreditMaxAmount) {
		this.monthlyCreditMaxAmount = monthlyCreditMaxAmount;
	}

	public Long getDailyDebitMaxCount() {
		return dailyDebitMaxCount;
	}

	public void setDailyDebitMaxCount(Long dailyDebitMaxCount) {
		this.dailyDebitMaxCount = dailyDebitMaxCount;
	}

	public Long getWeeklyDebitMaxCount() {
		return weeklyDebitMaxCount;
	}

	public void setWeeklyDebitMaxCount(Long weeklyDebitMaxCount) {
		this.weeklyDebitMaxCount = weeklyDebitMaxCount;
	}

	public Long getMonthlyDebitMaxCount() {
		return monthlyDebitMaxCount;
	}

	public void setMonthlyDebitMaxCount(Long monthlyDebitMaxCount) {
		this.monthlyDebitMaxCount = monthlyDebitMaxCount;
	}

	public Long getDailyCreditMaxCount() {
		return dailyCreditMaxCount;
	}

	public void setDailyCreditMaxCount(Long dailyCreditMaxCount) {
		this.dailyCreditMaxCount = dailyCreditMaxCount;
	}

	public Long getWeeklyCreditMaxCount() {
		return weeklyCreditMaxCount;
	}

	public void setWeeklyCreditMaxCount(Long weeklyCreditMaxCount) {
		this.weeklyCreditMaxCount = weeklyCreditMaxCount;
	}

	public Long getMonthlyCreditMaxCount() {
		return monthlyCreditMaxCount;
	}

	public void setMonthlyCreditMaxCount(Long monthlyCreditMaxCount) {
		this.monthlyCreditMaxCount = monthlyCreditMaxCount;
	}

	public Long getMaximumBalance() {
		return maximumBalance;
	}

	public void setMaximumBalance(Long maximumBalance) {
		this.maximumBalance = maximumBalance;
	}

	public Long getMinimumBalance() {
		return minimumBalance;
	}

	public void setMinimumBalance(Long minimumBalance) {
		this.minimumBalance = minimumBalance;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public Long getPiType() {
		return piType;
	}

	public void setPiType(Long piType) {
		this.piType = piType;
	}

	public Long getPi() {
		return pi;
	}

	public void setPi(Long pi) {
		this.pi = pi;
	}

	public Long getCustomerType() {
		return customerType;
	}

	public void setCustomerType(Long customerType) {
		this.customerType = customerType;
	}

	public Long getCustomer() {
		return customer;
	}

	public void setCustomer(Long customer) {
		this.customer = customer;
	}

	public Long getUseCase() {
		return useCase;
	}

	public void setUseCase(Long useCase) {
		this.useCase = useCase;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CodeValue getSelectedPiType() {
		return selectedPiType;
	}

	public void setSelectedPiType(CodeValue selectedPiType) {
		this.selectedPiType = selectedPiType;
	}

	public CodeValue getSelectedCustomerType() {
		return selectedCustomerType;
	}

	public void setSelectedCustomerType(CodeValue selectedCustomerType) {
		this.selectedCustomerType = selectedCustomerType;
	}

	public CodeValue getSelectedUseCases() {
		return selectedUseCases;
	}

	public void setSelectedUseCases(CodeValue selectedUseCases) {
		this.selectedUseCases = selectedUseCases;
	}

	public List<ElimitBean> getLimitList() {
		return limitList;
	}

	public void setLimitList(List<ElimitBean> limitList) {
		this.limitList = limitList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	

	

}
