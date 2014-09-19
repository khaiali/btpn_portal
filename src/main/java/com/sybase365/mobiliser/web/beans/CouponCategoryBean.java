package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.util.tools.wicketutils.treetable.utils.TreeNodeBean;

public class CouponCategoryBean implements Serializable, TreeNodeBean,
	Comparable<CouponCategoryBean> {

    private final static long serialVersionUID = 1L;
    protected Long id;
    protected String name;
    protected Long parent;
    protected Integer categoryGroup;
    protected Boolean isActive;
    protected Integer priority;
    private List<CouponCategoryBean> children;

    @Override
    public int compareTo(CouponCategoryBean o) {
	if (this.parent <= o.parent)
	    return -1;
	else
	    return 1;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return possible object is {@link Long }
     * 
     */
    public Long getId() {
	return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *            allowed object is {@link Long }
     * 
     */
    public void setId(Long value) {
	this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
	this.name = value;
    }

    /**
     * Gets the value of the parent property.
     * 
     * @return possible object is {@link Long }
     * 
     */
    public Long getParent() {
	return parent;
    }

    /**
     * Sets the value of the parent property.
     * 
     * @param value
     *            allowed object is {@link Long }
     * 
     */
    public void setParent(Long value) {
	this.parent = value;
    }

    /**
     * Gets the value of the categoryGroup property.
     * 
     * @return possible object is {@link Integer }
     * 
     */
    public Integer getCategoryGroup() {
	return categoryGroup;
    }

    /**
     * Sets the value of the categoryGroup property.
     * 
     * @param value
     *            allowed object is {@link Integer }
     * 
     */
    public void setCategoryGroup(Integer value) {
	this.categoryGroup = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return possible object is {@link Boolean }
     * 
     */
    public Boolean isIsActive() {
	return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     * 
     */
    public void setIsActive(Boolean value) {
	this.isActive = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return possible object is {@link Integer }
     * 
     */
    public Integer getPriority() {
	return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *            allowed object is {@link Integer }
     * 
     */
    public void setPriority(Integer value) {
	this.priority = value;
    }

    public void setChildren(List<CouponCategoryBean> children) {
	this.children = children;
    }

    public List<CouponCategoryBean> getChildren() {
	if (this.children == null)
	    this.children = new ArrayList<CouponCategoryBean>();
	return children;
    }

}
