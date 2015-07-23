package com.jp.travelhelper.dbhelper.model;

/**
 * Bean class used to represent a town.
 * 
 * @author jp
 * 
 */
public class Town {

	private int id;

	private String townName;

	private int favorite;

	private int countryId;

	public Town() {
	}

	public Town(int id, String townName, int favorite, int countryId) {
		super();
		this.id = id;
		this.townName = townName;
		this.favorite = favorite;
		this.countryId = countryId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
}
