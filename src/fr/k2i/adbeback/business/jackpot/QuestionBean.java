package fr.k2i.adbeback.business.jackpot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionBean{
	private String url;
	private List<ResponseBean> responses;
	private long duration;
	private long pauseBefore;
	
	public QuestionBean(JSONObject obj) throws JSONException {
		url = obj.getString("url");
		duration = obj.getLong("duration");
		JSONArray jsonArray = obj.getJSONArray("responses");
		pauseBefore = obj.getLong("pauseBefore");
		responses = new ArrayList<ResponseBean>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			responses.add(new ResponseBean(jsonArray.getJSONObject(i)));
		}
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<ResponseBean> getResponses() {
		return responses;
	}
	public void setResponses(List<ResponseBean> responses) {
		this.responses = responses;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getPauseBefore() {
		return pauseBefore;
	}

	public void setPauseBefore(long pauseBefore) {
		this.pauseBefore = pauseBefore;
	}
}
