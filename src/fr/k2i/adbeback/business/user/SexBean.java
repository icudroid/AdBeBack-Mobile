package fr.k2i.adbeback.business.user;

import org.json.JSONException;
import org.json.JSONObject;

public class SexBean {
	private String sexId;
	private String sexLabel;
	public SexBean(JSONObject obj) throws JSONException {
		sexId = obj.getString("sexId");
		sexLabel = obj.getString("sexLabel");
	}
	public String getSexId() {
		return sexId;
	}
	public void setSexId(String sexId) {
		this.sexId = sexId;
	}
	public String getSexLabel() {
		return sexLabel;
	}
	public void setSexLabel(String sexLabel) {
		this.sexLabel = sexLabel;
	}
	@Override
	public String toString() {
		return sexLabel;
	}
	
}
