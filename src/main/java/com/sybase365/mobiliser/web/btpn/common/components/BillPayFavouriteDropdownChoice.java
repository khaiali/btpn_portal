package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.custom.btpn.services.contract.api.IManageFavoriteEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.FavoriteObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.GetAllFavoritesByTypeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.GetAllFavoritesByTypeResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;

/**
 * This class displays the denomination for the Airtime topup telcos. This dropdown displays the Airtime denominations
 * based on the telco operator selected.
 * 
 * @author Vikram Gunda
 */
public class BillPayFavouriteDropdownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BillPayFavouriteDropdownChoice.class);

	private boolean sortKeys;

	private boolean sortAsc;

	private long customerId;

	/**
	 * End Point for Btpn favorites.
	 */
	@SpringBean(name = "favoriteClient")
	private IManageFavoriteEndpoint favoriteClient;

	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public BillPayFavouriteDropdownChoice(String id, boolean sortKeys, boolean sortAsc, final long customerId) {
		super(id);
		this.sortKeys = sortKeys;
		this.sortAsc = sortAsc;
		this.customerId = customerId;
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
			final List<CodeValue> billPayFavList = getChoiceList(BtpnConstants.FAVOURITE_TYPE_FF_ACCOUNT);
			billPayFavList.addAll(getChoiceList(BtpnConstants.FAVOURITE_TYPE_BILLER_ACCOUNT));
			setChoices(new WildcardListModel<CodeValue>(billPayFavList));
		} catch (final Exception e) {
			// not much we can do here but logg the exception
			LOG.warn("FavouriteDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}

		super.onBeforeRender();
	}

	/**
	 * This is used to fetch the choices for dropdown
	 */
	protected List<CodeValue> getChoiceList(final int favouriteType) throws Exception {
		final List<CodeValue> favouritesList = new ArrayList<CodeValue>();
		List<FavoriteObject> favouriteObjectList = new ArrayList<FavoriteObject>();
		try {
			final GetAllFavoritesByTypeRequest request = GetAllFavoritesByTypeRequest.class.newInstance();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			request.setFavoriteType(favouriteType);
			request.setCustomerId(customerId);
			final GetAllFavoritesByTypeResponse response = favoriteClient.getAllFavoritesByType(request);
			if (response.getStatus().getCode() == 0) {
				favouriteObjectList = response.getFovritesBean();
				LOG.debug("FavouriteDropdownChoice:getChoiceList() ==> Airtime Denominations Count : "
						+ favouriteObjectList.size());
			} else {
				error(getLocalizer().getString("error.favourite", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("FavouriteDropdownChoice:onBeforeRender() ===> Could not retrieve drop down choices", e);
		}
		for (final FavoriteObject object : favouriteObjectList) {
			favouritesList.add(new CodeValue(String.valueOf(object.getFavoriteValue()), object.getFavouriteName()));
		}
		if (sortKeys) {
			Collections.sort(favouritesList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(favouritesList, new CodeValueValueComparator(sortAsc));
		}
		return favouritesList;
	}

}
