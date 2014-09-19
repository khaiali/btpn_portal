package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionInfo;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionsGroup;

public class RestrictionSetBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RestrictionsGroup restrictionSet;
    private RestrictionInfo restriction;
    private String cssStyle;
    private boolean showMoveUpLink = true;
    private boolean showMoveDownLink = true;

    public RestrictionsGroup getRestrictionSet() {
	return restrictionSet;
    }

    public void setRestrictionSet(RestrictionsGroup restrictionSet) {
	this.restrictionSet = restrictionSet;
    }

    public RestrictionInfo getRestriction() {
	return restriction;
    }

    public void setRestriction(RestrictionInfo restriction) {
	this.restriction = restriction;
    }

    public String getCssStyle() {
	return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
	this.cssStyle = cssStyle;
    }

    public boolean isShowMoveUpLink() {
	return showMoveUpLink;
    }

    public void setShowMoveUpLink(boolean showMoveUpLink) {
	this.showMoveUpLink = showMoveUpLink;
    }

    public boolean isShowMoveDownLink() {
	return showMoveDownLink;
    }

    public void setShowMoveDownLink(boolean showMoveDownLink) {
	this.showMoveDownLink = showMoveDownLink;
    }

}
