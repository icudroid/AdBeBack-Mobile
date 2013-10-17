package fr.k2i.adbeback.business.user;

import org.json.JSONException;
import org.json.JSONObject;

public class CountryBean {
	private String country;
	private Long id;
	public CountryBean(JSONObject obj) throws JSONException {
		country = obj.getString("country");
		id = obj.getLong("id");
	}
	public CountryBean() {
		// TODO Auto-generated constructor stub
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return country;
	}
	
}
