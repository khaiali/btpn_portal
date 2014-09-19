package com.sybase365.mobiliser.web.btpn.common.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountInformationType;
import com.btpnwow.core.account.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountRequest;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.CodeValueCodeComparator;
import com.sybase365.mobiliser.web.btpn.util.CodeValueValueComparator;

public class AccountDropDownChoice extends DropDownChoice<CodeValue> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(AccountDropDownChoice.class);

	@SpringBean(name="accountClient")
	private AccountFacade accountClient;
	
	private boolean sortKeys;

	private boolean sortAsc;
	
	private int identificationType;
	
	private String identification;
	
	private Collection<Integer> piTypes;
	
	private Collection<Integer> piClasses;
	
	/**
	 * Constructor for this DropDownChoice.
	 * 
	 * @param id id for the DropDownChoice
	 */
	public AccountDropDownChoice(String id,
			boolean sortKeys, boolean sortAsc,
			String identification, int identificationType, 
			Collection<Integer> piTypes, Collection<Integer> piClasses) {
		
		super(id);
		
		this.identification = identification;
		this.identificationType = identificationType;
		
		this.piTypes = piTypes;
		this.piClasses = piClasses;
		
		setChoiceRenderer(new ChoiceRenderer<CodeValue>(BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION));
	}

	/**
	 * Override the before render method. This method fethces the values to be populated in this dropdown before
	 * rendering the dropdown and sets the choices list.
	 */
	@Override
	protected void onBeforeRender() {
		getChoices().clear();
		
		try {
			setChoices(new WildcardListModel<CodeValue>(getChoiceList()));
		} catch (Throwable ex) {
			log.error("An exception was thrown", ex);
		}

		super.onBeforeRender();
	}
	
	protected List<CodeValue> getChoiceList() throws Exception {
		final List<CodeValue> choiceList = new ArrayList<CodeValue>();
		
		try {
			CustomerIdentificationType cid = new CustomerIdentificationType();
			cid.setType(identificationType);
			cid.setValue(identification);
			
			FindCustomerAccountRequest request = new FindCustomerAccountRequest();
			request.setCallback(null);
			request.setConversationId(UUID.randomUUID().toString());
			request.setOrigin("mobiliser-web");
			request.setRepeat(Boolean.FALSE);
			request.setTraceNo(UUID.randomUUID().toString());
			request.setIdentification(cid);
			
			if (piClasses != null) {
				request.getPaymentInstrumentClass().addAll(piClasses);
			}
			if (piTypes != null) {
				request.getPaymentInstrumentType().addAll(piTypes);
			}
			
			request.setFlags(0);
			
			FindCustomerAccountResponse response = accountClient.find(request);
			
			if (MobiliserUtils.success(response)) {
				List<AccountInformationType> list = response.getAccount();
				
				for (AccountInformationType e : list) {
					String value = null;
					
					if (e.getPaymentInstrumentClass() == 0) {
						switch (e.getPaymentInstrumentType()) {
						case 0:
							value = "Saving Account";
							break;
						case 1:
							value = e.getAlias();
							break;
						case 2:
							value = "E-Money";
							break;
						}
					}
					
					if (value == null) {
						value = e.getData();
						
						if (value == null) {
							value = Integer.toString(e.getPaymentInstrumentType()).concat(" ").concat(Long.toString(e.getId()));
						}
					}
					
					choiceList.add(new CodeValue(Long.toString(e.getId()), value));
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable ex) {
			log.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
		
		if (sortKeys) {
			Collections.sort(choiceList, new CodeValueCodeComparator(sortAsc));
		} else {
			Collections.sort(choiceList, new CodeValueValueComparator(sortAsc));
		}
		
		return choiceList;
	}

}
