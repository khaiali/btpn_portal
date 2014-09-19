package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

public class NotificationAttachmentsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private Date uploadedDate;

	private String contentType;
	
	private byte[] fileContent;

	public String getFileName() {
		return fileName;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@Override
	public boolean equals(Object bean) {
		if (bean instanceof NotificationAttachmentsBean) {
			final NotificationAttachmentsBean compareBean = (NotificationAttachmentsBean) bean;
			return compareBean.getFileName().equalsIgnoreCase(this.fileName)
					&& compareBean.getUploadedDate().equals(this.uploadedDate);
		}
		return false;
	}

}
