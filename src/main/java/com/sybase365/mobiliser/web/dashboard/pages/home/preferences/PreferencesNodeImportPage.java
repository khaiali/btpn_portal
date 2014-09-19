package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.xml.sax.SAXException;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.beans.jaxb.Preferences;
import com.sybase365.mobiliser.web.beans.jaxb.PreferencesNode;
import com.sybase365.mobiliser.web.beans.jaxb.PreferencesValue;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_JOBS)
public class PreferencesNodeImportPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(PreferencesNodeImportPage.class);

    private FileUploadField upload;

    private Object uploadPreferencesFile;

    private IPreferencesNavigator backPage;

    public PreferencesNodeImportPage(IPreferencesNavigator backPage) {
        super();
        this.backPage = backPage;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	final Form form = new Form("preferencesForm",
		new CompoundPropertyModel<PreferencesNodeImportPage>(this));

	form.setMultiPart(true);

	form.add(new FeedbackPanel("errorMessages"));

	form.add(upload = new FileUploadField("uploadPreferencesFile"));

	form.add(new Button("importPreferences") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (!PortalUtils.exists(upload)
			|| !PortalUtils.exists(upload.getFileUpload())) {
		    error(getLocalizer()
			    .getString("preferences.node.import.file.required", this));
		} else {
		    importMessages();
		}

	    }
	});

        form.add(new Button("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage((MobiliserBasePage)backPage);
            };
        }.setDefaultFormProcessing(false));

	add(form);
    }

    protected void importMessages() {

	FileUpload file = upload.getFileUpload();

	try {
	    JAXBContext jc = JAXBContext.newInstance(
		    Preferences.class.getPackage().getName());

	    Unmarshaller um = jc.createUnmarshaller();

	    XMLInputFactory fac = XMLInputFactory.newInstance();

	    XMLStreamReader reader = 
	        fac.createXMLStreamReader(
	      	    new ByteArrayInputStream(file.getBytes()));

	    JAXBElement<Preferences> preferences = 
	    	    um.unmarshal(reader, Preferences.class);

	    Preferences prefsAppXml = preferences.getValue();

	    file.delete();

	    String applicationName = prefsAppXml.getName();

	    // application has to exist in order to add preference node to it
	    if (this.getPreferencesApplication(applicationName) != null) {

		for (PreferencesNode prefsNodeXml : prefsAppXml.getNode()) {

		    String nodePath = prefsNodeXml.getPath();

		    addPreferenceNode(applicationName, nodePath);

		    for (PreferencesValue prefsValue : prefsNodeXml.getPreference()) {

			PreferenceBean prefsBean = new PreferenceBean();
			prefsBean.setKey(prefsValue.getKey());
			prefsBean.setValue(prefsValue.getValue());
			prefsBean.setType(prefsValue.getType());
			prefsBean.setDescription(prefsValue.getDescription());

			setPreferenceValue(applicationName, nodePath, prefsBean);
		    }
	    	}

		backPage.refresh();

                setResponsePage((MobiliserBasePage)backPage);

		info(getLocalizer().getString("preferences.node.import.success", this));
	    }
	    else {
	    	LOG.debug("Preferences application doesn't exist");
	    	error(getLocalizer().getString("preferences.node.import.application.not.found", this));
	    }

	} catch (SAXException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("preferences.node.import.format.error", this));
	} catch (XMLStreamException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("preferences.node.import.format.error", this));
	} catch (JAXBException e) {
	    LOG.error("Error in reading and parsing import XML file: ", e);
	    error(getLocalizer().getString("preferences.node.import.format.error", this));
	} catch (Exception e) {
	    LOG.error("Error in importing preferences: ", e);
	    error(getLocalizer().getString("preferences.node.import.general.error", this));
	}
    }

    public void setUploadPreferencesFile(Object uploadPreferencesFile) {
	this.uploadPreferencesFile = uploadPreferencesFile;
    }

    public Object getUploadPreferencesFile() {
	return uploadPreferencesFile;
    }

    private boolean addPreferenceNode(String application, String nodePath) {

        LOG.debug("Adding preference node: {}/{}", application, nodePath);

	try {
            return this.setPreferencesValue(application, nodePath, 
		    "node", "", "", "");
	}
	catch (Exception e) {
            LOG.error("Exception creating preferences application", e);
	}

	return Boolean.FALSE;
    }

    private boolean setPreferenceValue(String application, String path,
	    PreferenceBean prefBean) {

        LOG.debug("Set preference value: {}:{} -> {}/{}/{}/{}", 
		new Object[] {
		    	application,
			path,
			prefBean.getKey(),
			prefBean.getValue(),
			prefBean.getType(),
			prefBean.getDescription() } );

	try {
            return this.setPreferencesValue(
		    	application,
			path,
			prefBean.getKey(),
			prefBean.getValue(),
			prefBean.getType(),
			prefBean.getDescription());
	}
	catch (Exception e) {
            LOG.error("Exception creating preferences application", e);
	}

	return Boolean.FALSE;
    }

    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }

}
