package fr.k2i.adbeback.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdObjectBean {
	private String name;
	private List<String> imgFile;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getImgFile() {
		return imgFile;
	}
	public void setImgFile(List<String> imgFile) {
		this.imgFile = imgFile;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public AdObjectBean(JSONObject obj) throws JSONException {
		name = obj.getString("name");
		description = obj.getString("description");
		JSONArray qs = obj.getJSONArray("imgFile");
		imgFile = new ArrayList<String>(qs.length());
		for (int i = 0; i < qs.length(); i++) {
			imgFile.add(qs.getString(i));
		}
	}
}
