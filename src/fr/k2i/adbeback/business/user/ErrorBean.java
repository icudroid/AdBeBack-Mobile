package fr.k2i.adbeback.business.user;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorBean {
	private String field;
	private String message;
	public ErrorBean(JSONObject obj) throws JSONException {
		field = obj.getString("field");
		message = obj.getString("message");
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
