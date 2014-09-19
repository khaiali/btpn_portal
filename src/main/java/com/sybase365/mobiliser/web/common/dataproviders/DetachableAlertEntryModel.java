package com.sybase365.mobiliser.web.common.dataproviders;

import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

@SuppressWarnings("serial")
public class DetachableAlertEntryModel extends
	LoadableDetachableModel<CustomerAlert> {

    private static Logger LOG = LoggerFactory
	    .getLogger(DetachableAlertEntryModel.class);

    private final Long id;

    private MobiliserBasePage mobBasePage;

    public DetachableAlertEntryModel(CustomerAlert app,
	    final MobiliserBasePage mobBasePage) {
	super(app);
	id = app.getCustomerId();
	this.mobBasePage = mobBasePage;
    }

    public DetachableAlertEntryModel(Long id,
	    final MobiliserBasePage mobBasePage) {
	super();
	this.id = id;
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Used for DataView with ReuseIfModelsEqualStrategy item reuse strategy
     * 
     * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
	if (obj == this) {
	    return true;
	} else if (obj == null) {
	    return false;
	} else if (obj instanceof DetachableAlertEntryModel) {
	    DetachableAlertEntryModel other = (DetachableAlertEntryModel) obj;
	    return other.id == id;
	}
	return false;
    }

    /**
     * @see org.apache.wicket.model.LoadableDetachableModel#load()
     */
    @Override
    protected CustomerAlert load() {
	return getMobiliserBasePage().findCustomerAlertByCustomer(id).get(0);

    }

    @Override
    public int hashCode() {
	return this.id != null ? this.id.hashCode() : 42;
    }

}
