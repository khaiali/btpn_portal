package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class ManageFavoritesBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String favoriteName;

	private String favoriteValue;

	private String favoriteType;

	private CodeValue favoritesType;

	private String description;

	private boolean favoriteSuccess;

	private boolean removedSuccess;

	private String selectedLink;

	public String getFavoriteName() {
		return favoriteName;
	}

	public void setFavoriteName(String favoriteName) {
		this.favoriteName = favoriteName;
	}

	public String getFavoriteValue() {
		return favoriteValue;
	}

	public void setFavoriteValue(String favoriteValue) {
		this.favoriteValue = favoriteValue;
	}

	public String getFavoriteType() {
		return favoriteType;
	}

	public void setFavoriteType(String favoriteType) {
		this.favoriteType = favoriteType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CodeValue getFavoritesType() {
		return favoritesType;
	}

	public void setFavoritesType(CodeValue favoritesType) {
		this.favoritesType = favoritesType;
	}

	public boolean isFavoriteSuccess() {
		return favoriteSuccess;
	}

	public void setFavoriteSuccess(boolean favoriteSuccess) {
		this.favoriteSuccess = favoriteSuccess;
	}

	public String getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(String selectedLink) {
		this.selectedLink = selectedLink;
	}

	public boolean isRemovedSuccess() {
		return removedSuccess;
	}

	public void setRemovedSuccess(boolean removedSuccess) {
		this.removedSuccess = removedSuccess;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
