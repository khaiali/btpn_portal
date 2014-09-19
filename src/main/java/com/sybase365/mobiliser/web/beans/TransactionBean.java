package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;

public class TransactionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer authenticationMethodPayer;
    private Integer authenticationMethodPayee;
    private Long actorId;
    private Integer usecase;
    private VatAmount amount;
    private long creditAmount;
    private long debitAmount;
    private long feeAmount;
    private boolean autoCapture;
    private String text;
    private TransactionParticipant payer;
    private TransactionParticipant payee;
    private PaymentInstrument paymentInstrument;
    private String orderId;
    private long txnId;
    private String authCode;

    private String module;
    private boolean preAuthFinished;

    private boolean redirectErrorReturn;
    private String urlErrorPage;
    private String urlConfirmPage;
    private String urlFinishedPage;
    private int orderChannel;
    // added for gcash
    private String purposeOfTransfer;
    private Long fcyAmount;
    private String fcyCurrency;
    private Float exchangeRate;
    private Transaction refTransaction;

    // for add and withdraw finds
    private String beneficiaryName;

    // bill payment status
    private int statusCode;
    private String statusString;

    private long payeeFee;
    private long payerFee;

    public TransactionBean() {
	this.preAuthFinished = false;
    }

    public Integer getAuthenticationMethodPayer() {
	return authenticationMethodPayer;
    }

    public void setAuthenticationMethodPayer(Integer authenticationMethodPayer) {
	this.authenticationMethodPayer = authenticationMethodPayer;
    }

    public Integer getAuthenticationMethodPayee() {
	return authenticationMethodPayee;
    }

    public void setAuthenticationMethodPayee(Integer authenticationMethodPayee) {
	this.authenticationMethodPayee = authenticationMethodPayee;
    }

    public Long getActorId() {
	return actorId;
    }

    public void setActorId(Long actorId) {
	this.actorId = actorId;
    }

    public Integer getUsecase() {
	return usecase;
    }

    public void setUsecase(Integer usecase) {
	this.usecase = usecase;
    }

    public VatAmount getAmount() {
	return amount;
    }

    public void setAmount(VatAmount amount) {
	this.amount = amount;
    }

    public long getCreditAmount() {
	return creditAmount;
    }

    public void setCreditAmount(long creditAmount) {
	this.creditAmount = creditAmount;
    }

    public long getDebitAmount() {
	return debitAmount;
    }

    public void setDebitAmount(long debitAmount) {
	this.debitAmount = debitAmount;
    }

    public long getFeeAmount() {
	return feeAmount;
    }

    public void setFeeAmount(long feeAmount) {
	this.feeAmount = feeAmount;
    }

    public boolean isAutoCapture() {
	return autoCapture;
    }

    public void setAutoCapture(boolean autoCapture) {
	this.autoCapture = autoCapture;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public TransactionParticipant getPayer() {
	return payer;
    }

    public void setPayer(TransactionParticipant payer) {
	this.payer = payer;
    }

    public TransactionParticipant getPayee() {
	return payee;
    }

    public void setPayee(TransactionParticipant payee) {
	this.payee = payee;
    }

    public PaymentInstrument getPaymentInstrument() {
	return paymentInstrument;
    }

    public void setPaymentInstrument(PaymentInstrument paymentInstrument) {
	this.paymentInstrument = paymentInstrument;
    }

    public String getModule() {
	return module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    public boolean isPreAuthFinished() {
	return preAuthFinished;
    }

    public void setPreAuthFinished(boolean preAuthFinished) {
	this.preAuthFinished = preAuthFinished;
    }

    public String getUrlErrorPage() {
	return urlErrorPage;
    }

    public void setUrlErrorPage(String urlErrorPage) {
	this.urlErrorPage = urlErrorPage;
    }

    public String getUrlConfirmPage() {
	return urlConfirmPage;
    }

    public void setUrlConfirmPage(String urlConfirmPage) {
	this.urlConfirmPage = urlConfirmPage;
    }

    public String getUrlFinishedPage() {
	return urlFinishedPage;
    }

    public void setUrlFinishedPage(String urlFinishedPage) {
	this.urlFinishedPage = urlFinishedPage;
    }

    public Long getTxnId() {
	return txnId;
    }

    public void setTxnId(Long txnId) {
	this.txnId = txnId;
    }

    public void setAuthCode(String authCode) {
	this.authCode = authCode;
    }

    public String getAuthCode() {
	return authCode;
    }

    public boolean isRedirectErrorReturn() {
	return redirectErrorReturn;
    }

    public void setRedirectErrorReturn(boolean redirectErrorReturn) {
	this.redirectErrorReturn = redirectErrorReturn;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

    public String getPurposeOfTransfer() {
	return purposeOfTransfer;
    }

    public void setPurposeOfTransfer(String purposeOfTransfer) {
	this.purposeOfTransfer = purposeOfTransfer;
    }

    public Long getFcyAmount() {
	return fcyAmount;
    }

    public void setFcyAmount(Long fcyAmount) {
	this.fcyAmount = fcyAmount;
    }

    public String getFcyCurrency() {
	return fcyCurrency;
    }

    public void setFcyCurrency(String fcyCurrency) {
	this.fcyCurrency = fcyCurrency;
    }

    public Float getExchangeRate() {
	return exchangeRate;
    }

    public void setExchangeRate(Float exchangeRate) {
	this.exchangeRate = exchangeRate;
    }

    public void setOrderChannel(int orderChannelWeb) {
	this.orderChannel = orderChannelWeb;
    }

    public int getOrderChannel() {
	return this.orderChannel;
    }

    public Transaction getRefTransaction() {
	return refTransaction;
    }

    public void setRefTransaction(Transaction refTransaction) {
	this.refTransaction = refTransaction;
    }

    public void setBeneficiaryName(String piName) {
	this.beneficiaryName = piName;
    }

    public String getBeneficiaryName() {
	return beneficiaryName;
    }

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    public String getStatusString() {
	return statusString;
    }

    public void setStatusString(String statusString) {
	this.statusString = statusString;
    }

    public long getPayeeFee() {
	return payeeFee;
    }

    public void setPayeeFee(long payeeFee) {
	this.payeeFee = payeeFee;
    }

    public long getPayerFee() {
	return payerFee;
    }

    public void setPayerFee(long payerFee) {
	this.payerFee = payerFee;
    }

}
