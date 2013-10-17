package fr.k2i.adbeback.business.jackpot;

import org.json.JSONException;
import org.json.JSONObject;


public class JackPotResponseBean {
	private long idResponse;
	private String response;
	public JackPotResponseBean(){}
	public JackPotResponseBean(JSONObject obj) throws JSONException {
		idResponse = obj.getLong("idResponse");
		response = obj.getString("response");
//		idResponse = resp.getId();
//		response = resp.getReponse();
	}
	public long getIdResponse() {
		return idResponse;
	}
	public void setIdResponse(long idResponse) {
		this.idResponse = idResponse;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
