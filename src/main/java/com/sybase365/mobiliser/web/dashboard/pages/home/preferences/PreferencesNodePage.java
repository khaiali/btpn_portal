package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreference;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreferencesTree;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.beans.jaxb.Preferences;
import com.sybase365.mobiliser.web.beans.jaxb.PreferencesNode;
import com.sybase365.mobiliser.web.beans.jaxb.PreferencesValue;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.PreferencesDataProvider;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNodeViewer;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesNodePage extends HomeMenuGroup 
        implements IPreferencesNodeViewer, IAjaxIndicatorAware {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = 
            org.slf4j.LoggerFactory.getLogger(PreferencesNodePage.class);

    private IPreferencesNavigator backPage;
    private IPreferencesNodeViewer thisPage;

    final protected IPreferencesTreeEntry selectedPreferencesNode;

    private Component selectedApplication;
    private Component selectedNodePath;

        // Data Model for table list
    private PreferencesDataProvider dataProvider;

    private Form prefsForm;

    private ArrayList<PreferenceBean> prefsBeans;

        // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_prefsList = "prefsList";
    private static final String WICKET_ID_key = "key";
    private static final String WICKET_ID_value = "value";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public PreferencesNodePage(IPreferencesTreeEntry selectedNode, IPreferencesNavigator backPage) {
        super();
        this.selectedPreferencesNode = selectedNode;
        this.backPage = backPage;
        this.thisPage = this;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

        prefsForm = new Form("preferencesForm",
                new CompoundPropertyModel<PreferencesNodePage>(this));

        prefsForm.setOutputMarkupId(true);

        prefsForm.add(new FeedbackPanel("errorMessages"));

        prefsForm.add(new Button("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage((MobiliserBasePage)backPage);
            };
        });

        prefsForm.add(new Button("refreshButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                refreshPreferences();
            };
        });

        prefsForm.add(new Button("removeButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {

                if (removePreferencesNode()) {
                    info(this.getLocalizer()
                            .getString("preferences.node.remove.success", this));

                    backPage.refresh();

                    setResponsePage((MobiliserBasePage)backPage);
                }
                else {
                    error(this.getLocalizer()
                            .getString("preferences.node.remove.error", this));
                }
            };
        }.add(new SimpleAttributeModifier("onclick",
		"return confirm('"
			+ getLocalizer().getString(
				"preferences.node.remove.confirm", this)
			+ "');")));

	prefsForm.add(new Button("exportButton") {
		@Override
		public void onSubmit() {

		    List<PreferenceBean> prefsList = dataProvider.getAllPreferences();

		    if (prefsList.isEmpty()) {
			    error(getLocalizer()
				    .getString("error.prefslist.empty", this));
			    return;
		    }
		    else {
			try {
			    Preferences prefsAppXml = 
				    new Preferences();

			    prefsAppXml.setName(
				    selectedPreferencesNode.getApplicationName());

			    List<PreferencesNode> nodeList = 
				    prefsAppXml.getNode();

			    PreferencesNode prefsNodeXml = new PreferencesNode();

			    nodeList.add(prefsNodeXml);

			    prefsNodeXml.setPath(
				    selectedPreferencesNode.getPath() + "/"
				    + selectedPreferencesNode.getName());

			    List<PreferencesValue> valuesList = 
				    prefsNodeXml.getPreference();

			    for (PreferenceBean prefBean : prefsList) {

				PreferencesValue prefsValueXml = 
					new PreferencesValue();

				prefsValueXml.setKey(prefBean.getKey());
				prefsValueXml.setValue(prefBean.getValue());
				prefsValueXml.setType(prefBean.getType());
				prefsValueXml.setDescription(prefBean.getDescription());

				valuesList.add(prefsValueXml);
			    }

			    IResourceStream stream = 
				    new StringResourceStream(
					    toXml(prefsAppXml), "text/xml");

			    getRequestCycle().setRequestTarget(
				    new ResourceStreamRequestTarget(stream)
				    	.setFileName("preferences.xml"));
			}
			catch (Exception ex) {
				LOG.error(ex.getLocalizedMessage(), ex);
				error(ex.getLocalizedMessage());
			}
		    }
		}

	});

        selectedApplication = new Label("selectedApplication", 
                    new Model(selectedPreferencesNode.getApplicationName()))
                            .setOutputMarkupId(true)
                            .setVisible(true);

        prefsForm.add(selectedApplication);

        selectedNodePath = new Label("selectedNodePath", 
                    new Model(selectedPreferencesNode.getPath() + "/"
			    + selectedPreferencesNode.getName()))
                            .setOutputMarkupId(true)
                            .setVisible(true);

        prefsForm.add(selectedNodePath);

        prefsForm.add(new Button("addPreference") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(new PreferenceAddPage(selectedPreferencesNode, thisPage));
            };
        });

        refreshPreferences();

        add(prefsForm);
    }

    public String toXml(Preferences prefsApplicationXml) 
    	throws JAXBException {

	JAXBContext jaxbContext = JAXBContext.newInstance(
		prefsApplicationXml.getClass().getPackage().getName());

	Marshaller marshaller = jaxbContext.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	CharArrayWriter writer = new CharArrayWriter();

	marshaller.marshal(prefsApplicationXml, writer);

	return writer.toString();
    }

    private void createPreferencesListDataView(final List<PreferenceBean> prefs) {

        dataProvider = new PreferencesDataProvider(WICKET_ID_key, this);

        final DataView<PreferenceBean> dataView = new DataView<PreferenceBean>(
                WICKET_ID_prefsList, dataProvider) {

            @Override
            protected void onBeforeRender() {

                dataProvider.setAllPreferences(prefs);

                super.onBeforeRender();

		refreshTotalItemCount();
            }

            @Override
            protected void populateItem(final Item<PreferenceBean> item) {

                final PreferenceBean entry = item.getModelObject();

                item.add(new Label(WICKET_ID_key, entry.getKey()));
                item.add(new Label(WICKET_ID_value, entry.getValue()));

                // Edit Action
                Link<PreferenceBean> editLink = new Link<PreferenceBean>(
                        WICKET_ID_editAction, item.getModel()) {
                    @Override
                    public void onClick() {
                        setResponsePage(
                                new PreferenceEditPage(selectedPreferencesNode, 
                                        entry, thisPage));
                    }
                };
                item.add(editLink);

                // Remove Action
                Link<PreferenceBean> removeLink = new Link<PreferenceBean>(
                        WICKET_ID_removeAction, item.getModel()) {
                    @Override
                    public void onClick() {
			PreferenceBean entry = (PreferenceBean) getModelObject();
			removePreferencesValue(entry);
			removePreferencesValueFromList(entry);
                    }
                };

                removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"preferences.node.item.remove.confirm", this)
				+ "');"));

                item.add(removeLink);

                // set items in even/odd rows to different css style classes
                item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
                        true, new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
                                        : Constants.CSS_STYLE_EVEN;
                            }
                        }));

            }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = dataProvider.size();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total) {
			endIndex = total;
		    }
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }
        };

	// Navigator example: << < 1 2 > >>
	prefsForm.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	prefsForm.addOrReplace(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	prefsForm.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	prefsForm.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

	dataView.setOutputMarkupPlaceholderTag(true);

	dataView.setItemsPerPage(10);

        prefsForm.addOrReplace(dataView);

        prefsForm.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
                .getString("preferences.node.items.noItemsMsg", this)
                + "\n"
                + getLocalizer().getString("preferences.node.items.addPreferenceHelp", this)) {
            @Override
            public boolean isVisible() {
                if (dataView.getItemCount() > 0) {
                    return false;
                } else {
                    return super.isVisible();
                }
            }
        });

    }

    @Override
    public boolean hasChildren() {
        return PortalUtils.exists(selectedPreferencesNode.getKeys());
    }

    @Override
    public void refreshPreferences() {

	try {
	    this.refreshPreferencesClientSource();

            LOG.debug("Refreshing preferences node: {} -> {}", 
		    selectedPreferencesNode.getApplicationName(),
		    selectedPreferencesNode.getFullPath());

	    // returns a tree pointing to a list
	    DetailedPreferencesTree prefsTree = 
		    this.getDetailedPreferences(
			    selectedPreferencesNode.getApplicationName(), 
			    "/");

	    List<DetailedPreference> prefsNodes = prefsTree.getDetailedPreference();

	    prefsBeans = new ArrayList<PreferenceBean>();

	    // but we still interate through list
	    for (DetailedPreference prefNode : prefsNodes) {

		if (prefNode.getPath().equals(selectedPreferencesNode.getFullPath())) {

            	    LOG.debug("Found node: {}",  prefNode.getPath());

		    selectedPreferencesNode.setPrefs(prefNode.getEntry());

		    List<String> prefsKeys = selectedPreferencesNode.getKeys();

		    if (PortalUtils.exists(prefsKeys)) {
			for (String key : prefsKeys) {
			    PreferenceBean bean = new PreferenceBean();
			    bean.setKey(key);
			    bean.setValue(selectedPreferencesNode.getKeyValue(key));
			    bean.setType(selectedPreferencesNode.getKeyType(key));
			    bean.setDescription(selectedPreferencesNode.getKeyDescription(key));
			    LOG.debug("Got preference value: {}/{}",  bean.getKey(), 
				    bean.getValue());
			    prefsBeans.add(bean);
			}

			Collections.sort(prefsBeans);
		    }
		    break;
		}
	    }
	}
	catch (Exception e) {
            LOG.error("Exception refreshing preferences node", e);
	}

        createPreferencesListDataView(prefsBeans);
    }

    private boolean removePreferencesNode() {

        LOG.debug("Remove preferences node: {}:{}",
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath());

	try {
            return this.removePreferencesNode(
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath());
	}
	catch (Exception e) {
            LOG.error("Exception removing preferences node", e);
	}

	return Boolean.FALSE;
    }

    private boolean removePreferencesValue(PreferenceBean selectedPreferenceBean) {

        LOG.debug("Remove preference value: {}:{} -> {}", 
		new Object[] {
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath(),
			selectedPreferenceBean.getKey() } );

	try {
            return this.removePreferencesValue(
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath(),
			selectedPreferenceBean.getKey());
	}
	catch (Exception e) {
            LOG.error("Exception removing preferences value", e);
	}

	return Boolean.FALSE;
    }

    private void removePreferencesValueFromList(PreferenceBean selectedPreferenceBean) {
	prefsBeans.remove(selectedPreferenceBean);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return "errorMessages";
    }
    
    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }

}
