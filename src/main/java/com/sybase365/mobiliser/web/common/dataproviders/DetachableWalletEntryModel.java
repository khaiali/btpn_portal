package com.sybase365.mobiliser.web.common.dataproviders;

import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

@SuppressWarnings("serial")
public class DetachableWalletEntryModel extends
	LoadableDetachableModel<WalletEntry> {

    private static Logger log = LoggerFactory
	    .getLogger(DetachableWalletEntryModel.class);

    private final Long id;

    private MobiliserBasePage mobBasePage;

    public DetachableWalletEntryModel(WalletEntry app,
	    final MobiliserBasePage mobBasePage) {
	super(app);
	id = app.getId();
	this.mobBasePage = mobBasePage;
    }

    public DetachableWalletEntryModel(Long id,
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
	} else if (obj instanceof DetachableWalletEntryModel) {
	    DetachableWalletEntryModel other = (DetachableWalletEntryModel) obj;
	    return other.id == id;
	}
	return false;
    }

    @Override
    public int hashCode() {
	return this.id != null ? this.id.hashCode() : 42;
    }

    /**
     * @see org.apache.wicket.model.LoadableDetachableModel#load()
     */
    @Override
    protected WalletEntry load() {
	return getMobiliserBasePage().getWalletEntry(id);
    }

}
