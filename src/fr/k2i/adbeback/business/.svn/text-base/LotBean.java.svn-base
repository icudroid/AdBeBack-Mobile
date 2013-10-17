package fr.k2i.adbeback.business;

import org.json.JSONException;
import org.json.JSONObject;

public class LotBean {
	private String name;
	private String description;
	private double value;
	private String photoUrl;
	
	public LotBean(JSONObject obj) throws JSONException {
		name = obj.getString("name");
		description = obj.getString("description");
		value = obj.getDouble("value");
		photoUrl = obj.getString("photoUrl");

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
