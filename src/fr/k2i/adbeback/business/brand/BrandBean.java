package fr.k2i.adbeback.business.brand;

import org.json.JSONException;
import org.json.JSONObject;


public class BrandBean {
	private Long id;
	private String name;
	private String logoFile;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogoFile() {
		return logoFile;
	}
	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}

	
	public BrandBean(JSONObject obj) throws JSONException {
		name = obj.getString("name");
		logoFile = obj.getString("logoFile");
		id = obj.getLong("id");
	}
	
}
