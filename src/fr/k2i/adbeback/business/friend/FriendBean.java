package fr.k2i.adbeback.business.friend;

import org.json.JSONException;
import org.json.JSONObject;

import fr.k2i.adbeback.business.user.SexBean;

public class FriendBean {
	private long id;
	private String pseudo;
	private String avatar;
	private SexBean sexBean;
	private boolean validate;
	
	public FriendBean(JSONObject obj) throws JSONException {
		id = obj.getLong("id");
		pseudo = obj.getString("pseudo");
		avatar = obj.getString("avatar");
		sexBean = new SexBean(obj.getJSONObject("sexBean"));
		validate = obj.getBoolean("validate");
		id = obj.getLong("id");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public SexBean getSexBean() {
		return sexBean;
	}
	public void setSexBean(SexBean sexBean) {
		this.sexBean = sexBean;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
}
