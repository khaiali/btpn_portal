package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.xml.sax.SAXException;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.AttachmentSearchCriteria;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageStringContent;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageAttachments;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageTemplates;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.NotificationMessage;

@AuthorizeInstantiation(Constants.PRIV_NMGR_CREATE)
public class ImportMessagePage extends BaseNotificationMgrPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ImportMessagePage.class);
    private FileUploadField upload;
    private Object uploadAttachmentFile;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form form = new Form("importMessageForm",
		new CompoundPropertyModel<ImportMessagePage>(this));

	form.setMultiPart(true);
	form.add(new FeedbackPanel("errorMessages"));
	form.add(upload = new FileUploadField("uploadAttachmentFile"));
	form.add(new Button("importMessages") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (!PortalUtils.exists(upload)
			|| !PortalUtils.exists(upload.getFileUpload())) {
		    error(getLocalizer()
			    .getString("import.file.required", this));
		} else {
		    importMessages();
		}

	    }
	});
	add(form);
    }

    protected void importMessages() {
	FileUpload file = upload.getFileUpload();
	try {
	    JAXBContext jc;
	    jc = JAXBContext.newInstance(MessageTemplates.class.getPackage()
		    .getName());
	    Unmarshaller um = jc.createUnmarshaller();
	    SchemaFactory sf = SchemaFactory
		    .newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    Schema schema = sf.newSchema(ImportMessagePage.class
		    .getResource("notificationMessage.xsd"));
	    um.setSchema(schema);

	    XMLInputFactory fac = XMLInputFactory.newInstance();
	    XMLStreamReader reader = fac
		    .createXMLStreamReader(new ByteArrayInputStream(file
			    .getBytes()));
	    JAXBElement<MessageTemplates> templates = um.unmarshal(reader,
		    MessageTemplates.class);
	    MessageTemplates templateList = templates.getValue();
	    int successMessage = templateList.getTemplate().size();
	    for (NotificationMessage nMessage : templateList.getTemplate()) {
		try {

		    MessageTemplate message = convertMessage(nMessage);
		    int statusCode = createMessage(message);
		    if (statusCode != 0) {
			if (nMessage.getLocale() == null)
			    nMessage.setLocale("");
			successMessage--;
			if (statusCode == 202)
			    error(getLocalizer().getString(
				    "message.import.exists", this,
				    new Model<NotificationMessage>(nMessage)));
			else if (statusCode == 203)
			    error(getLocalizer().getString(
				    "message.import.missing", this,
				    new Model<NotificationMessage>(nMessage)));
			else if (statusCode == 204)
			    error(getLocalizer().getString(
				    "message.import.illegal", this,
				    new Model<NotificationMessage>(nMessage)));
			else
			    error(getLocalizer().getString(
				    "message.import.error", this,
				    new Model<NotificationMessage>(nMessage)));
		    }
		} catch (Exception e) {
		    successMessage--;
		    if (nMessage.getLocale() == null)
			nMessage.setLocale("");
		    if (("message.import.invalid.locale")
			    .equals(e.getMessage()))
			error(getLocalizer().getString(
				"message.import.invalid.locale", this,
				new Model<NotificationMessage>(nMessage)));
		    else
			error(getLocalizer().getString("message.import.error",
				this, new Model<NotificationMessage>(nMessage)));
		}
	    }
	    if (successMessage > 0) {
		if (successMessage == templateList.getTemplate().size())
		    info(getLocalizer().getString("all.message.success", this));
		else
		    info(getLocalizer().getString("all.other.message.success",
			    this));
	    }
	} catch (SAXException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("import.message.format.error", this));
	} catch (XMLStreamException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("import.message.format.error", this));
	} catch (JAXBException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("import.message.format.error", this));
	}

    }

    private MessageTemplate convertMessage(NotificationMessage nMessage)
	    throws Exception {
	MessageTemplate message = new MessageTemplate();
	message.setName(nMessage.getName());
	message.setSubject(nMessage.getSubject());
	message.setSender(nMessage.getSender());
	if (PortalUtils.exists(nMessage.getLocale())) {
	    if (nMessage.getLocale().matches(Constants.REGEX_LOCALE_VARIANT))
		message.setLocale(convertLocale(nMessage.getLocale()));
	    else
		throw new Exception("message.import.invalid.locale");
	}
	message.setTemplateType(nMessage.getTemplateType());
	MessageStringContent content = new MessageStringContent();
	if (PortalUtils.exists(nMessage.getContent().getCharset()))
	    content.setCharset(nMessage.getContent().getCharset());
	content.setContent(nMessage.getContent().getContent());
	content.setContentType(nMessage.getContent().getContentType());
	message.setContent(content);
	if (PortalUtils.exists(nMessage.getAlternativeContent())) {
	    MessageStringContent altContent = new MessageStringContent();
	    if (PortalUtils.exists(nMessage.getAlternativeContent()
		    .getCharset()))
		altContent.setCharset(nMessage.getAlternativeContent()
			.getCharset());
	    altContent
		    .setContent(nMessage.getAlternativeContent().getContent());
	    altContent.setContentType(nMessage.getAlternativeContent()
		    .getContentType());
	    message.setAlternativeContent(altContent);
	}
	for (MessageAttachments att : nMessage.getAttachments()) {
	    AttachmentSearchCriteria criteria = new AttachmentSearchCriteria();
	    if (PortalUtils.exists(att.getName()))
		criteria.setName(att.getName());
	    if (PortalUtils.exists(att.getContentType()))
		criteria.setContentType(att.getContentType());
	    List<MessageAttachment> msgAttachmentList = new ArrayList<MessageAttachment>();
	    try {
		FindAttachmentsRequest request = getNewMobiliserRequest(FindAttachmentsRequest.class);
		request.setCriteria(criteria);
		FindAttachmentsResponse response = wsTemplateClient
			.findAttachments(request);
		if (response.getStatus().getCode() == 0) {
		    msgAttachmentList = response.getAttachments();
		    if (PortalUtils.exists(msgAttachmentList)) {
			for (MessageAttachment mAtt : msgAttachmentList)
			    message.getAttachments().add(mAtt.getId());
		    }
		}
	    } catch (Exception e) {
		LOG.debug("Failed to find the attachment mathcing the search criteria in import XML file. "
			+ "ignoring the attachment content of message.");
	    }
	}
	return message;
    }

    private int createMessage(MessageTemplate message) {
	try {
	    CreateTemplateRequest request = getNewMobiliserRequest(CreateTemplateRequest.class);
	    request.setTemplate(message);
	    CreateTemplateResponse response = wsTemplateClient
		    .createTemplate(request);
	    if (response.getStatus().getCode() == 0) {
		message.setId(response.getTemplateId());
	    }
	    return response.getStatus().getCode();

	} catch (Exception e) {
	    LOG.warn("Error in creating message thru import ", e);
	    return -1;
	}
    }

    public void setUploadAttachmentFile(Object uploadAttachmentFile) {
	this.uploadAttachmentFile = uploadAttachmentFile;
    }

    public Object getUploadAttachmentFile() {
	return uploadAttachmentFile;
    }

}
