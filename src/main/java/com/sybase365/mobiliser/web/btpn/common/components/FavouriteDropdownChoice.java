package com.sybase365.mobiliser.web.btpn.common.components;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btpnwow.core.preregistered.facade.api.PreRegisteredFacade;
import com.btpnwow.core.preregistered.facade.contract.FindPreRegisteredRequest;
import com.btpnwow.core.preregistered.facade.contract.FindPreRegisteredResponse;
import com.btpnwow.core.preregistered.facade.contract.PreRegisteredFindViewType;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;


public class FavouriteDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FavouriteDropdownChoice.class);

	private boolean sortKeys;
	private boolean sortAsc;

	private long customerId;
	
//	private int[] types;
	private int types;
	
	@SpringBean(name="favClient")
	private PreRegisteredFacade favClient;
	
	/**
	 * Constructor for this DropDownChoice.
	 * @param id id for the DropDownChoice
	 */
	public FavouriteDropdownChoice(String id, boolean sortKeys, boolean sortAsc, final long customerId, int types) {
		super(id);
		
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		this.customerId = customerId;
		this.types = types;
		
		// Add Default Choice Render
		setChoiceRenderer(new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION));
	}

	/**
	 * Override the before render method. This method fethces the values to be populated in this dropdown before
	 * rendering the dropdown and sets the choices list.
	 */
	@Override
	protected void onBeforeRender() {

		// this is the place where we can determine the order of the choices
		getChoices().clear();
		// get the choices for our drop down list
		try {
			setChoices(new WildcardListModel<CodeValue>(getChoiceList()));
		} catch (final Exception e) {
			// not much we can do here but logg the exception
			log.warn("FavouriteDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}
	
	
	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList() throws Exception {
		
		final List<CodeValue> favouritesList = new ArrayList<CodeValue>();
		
		List<PreRegisteredFindViewType> favouriteObjectList = new ArrayList<PreRegisteredFindViewType>();
		
		try {
			
			final FindPreRegisteredRequest request = new FindPreRegisteredRequest();
			
			request.setCustomerId(customerId);
			
//			if ((types != null) && (types.length > 0)) {
//				request.getType().addAll(Arrays.asList(types[4]));
//			}
			
			if (types > 0) {
				request.getType().add(types);
			}
			
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			final FindPreRegisteredResponse response = favClient.find(request);
			if (response.getStatus().getCode() == 0) {
				favouriteObjectList = response.getItem();
				log.debug("FavouriteDropdownChoice:getChoiceList() ==> Airtime Denominations Count : "
						+ favouriteObjectList.size());
			} else {
				error(getLocalizer().getString("error.favourite", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("FavouriteDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}
		for (final PreRegisteredFindViewType object : favouriteObjectList) {
			favouritesList.add(new CodeValue(String.valueOf(object.getValue()), object.getName()));
		}
		if (sortKeys) {
			Collections.sort(favouritesList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(favouritesList, new CodeValueValueComparator(sortAsc));
		}
		return favouritesList;
	}

}
