package com.btpnwow.portal.bank.converter;

import org.apache.wicket.Component;

import com.btpnwow.core.customer.facade.contract.UserInformationType;
import com.btpnwow.core.customer.facade.contract.UserInformationWrkType;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public abstract class BankStaffBeanConverter {

	public static UserInformationWrkType toContractWrk(BankStaffBean o, boolean ldapUser) {
		if (o == null) {
			return null;
		}
		
		UserInformationWrkType c = new UserInformationWrkType();
		c.setCustomerType(Integer.valueOf(o.getCustomerType().getId()));
		c.setDesignation(o.getDesignation());
		c.setEmail(o.getEmail());
		c.setGlCode(o.getGlCode() == null ? null : Long.valueOf(o.getGlCode().getId()));
		c.setLanguage(o.getLanguage() == null ? null : o.getLanguage().getId());
		if (ldapUser) {
			c.setLdapUserId(o.getUserId());
		}
		c.setName(o.getName());
		c.setOrgUnitId(o.getOrgUnit() == null ? null : o.getOrgUnit().getId());
		c.setShortName(o.getName());
		c.setTerritoryCode(o.getTerritoryCode());
		c.setUserId(o.getUserId());
		
		return c;
	}

	public static UserInformationType toContract(BankStaffBean o, boolean ldapUser) {
		if (o == null) {
			return null;
		}
		
		UserInformationType c = new UserInformationType();
		c.setCustomerType(Integer.valueOf(o.getCustomerType().getId()));
		c.setDesignation(o.getDesignation());
		c.setEmail(o.getEmail());
		c.setGlCode(o.getGlCode() == null ? null : Long.valueOf(o.getGlCode().getId()));
		c.setLanguage(o.getLanguage() == null ? null : o.getLanguage().getId());
		if (ldapUser) {
			c.setLdapUserId(o.getUserId());
		}
		c.setName(o.getName());
		c.setOrgUnitId(o.getOrgUnit() == null ? null : o.getOrgUnit().getId());
		c.setShortName(o.getName());
		c.setTerritoryCode(o.getTerritoryCode());
		c.setUserId(o.getUserId());
		
		return c;
	}

	public static void fromContract(UserInformationType c, BankStaffBean o) {
		if (c == null) {
			return;
		}
		
		// c.setCustomerType(Integer.valueOf(o.getSelectedRole().getId()));
		o.setDesignation(c.getDesignation());
		o.setEmail(c.getEmail());
		// o.setGlCode(c.getGlCode() == null ? null : c.getGlCode().toString());
		// c.setLanguage(o.getLanguage().getId());
		o.setName(c.getName());
		// o.setOrgUnitId(c.getOrgUnitId());
		o.setTerritoryCode(c.getTerritoryCode());
		o.setUserId(o.getUserId());
	}

	public static void fromContractWrk(UserInformationWrkType c, BankStaffBean o) {
		if (c == null) {
			return;
		}
		
		// c.setCustomerType(Integer.valueOf(o.getSelectedRole().getId()));
		o.setDesignation(c.getDesignation());
		o.setEmail(c.getEmail());
		// o.setGlCode(c.getGlCode() == null ? null : c.getGlCode().toString());
		// c.setLanguage(o.getLanguage().getId());
		o.setName(c.getName());
		// o.setOrgUnitId(c.getOrgUnitId());
		o.setTerritoryCode(c.getTerritoryCode());
		o.setUserId(o.getUserId());
	}

	public static BankStaffBean fromContractWrk(UserInformationWrkType user, ILookupMapUtility lookupMapUtility, Component component) {
		if (user == null) {
			return null;
		}
		
		CodeValue customerType = MobiliserUtils.getCodeValue("customertypes", user.getCustomerType(), lookupMapUtility, component);
		
		BankStaffBean bean = new BankStaffBean();
		bean.setCustomerId(user.getId());
		bean.setCustomerType(customerType);
		bean.setDesignation(user.getDesignation());
		bean.setEmail(user.getEmail());
		bean.setGlCode(MobiliserUtils.getCodeValue("allGlCodes", user.getGlCode(), lookupMapUtility, component));
		bean.setLanguage(MobiliserUtils.getCodeValue("languages", user.getLanguage(), lookupMapUtility, component));
		bean.setName(user.getName());
		bean.setOrgUnit(MobiliserUtils.getCodeValue("orgunits", user.getOrgUnitId(), lookupMapUtility, component));
		bean.setTerritoryCode(user.getTerritoryCode());
		bean.setType(customerType == null ? null : customerType.getIdAndValue());
		bean.setUserId(user.getUserId());
		
		return bean;
	}
}
