package fr.k2i.adbeback.business.user;

import org.json.JSONException;
import org.json.JSONObject;

public class UserMessageBean {
	private long timeMsg;
	private String pseudo;
	private String msg;
	public long getTimeMsg() {
		return timeMsg;
	}
	public void setTimeMsg(long timeMsg) {
		this.timeMsg = timeMsg;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public UserMessageBean(long timeMsg, String pseudo, String msg) {
		super();
		this.timeMsg = timeMsg;
		this.pseudo = pseudo;
		this.msg = msg;
	}
	
	public UserMessageBean(JSONObject obj) throws JSONException {
		super();
		this.timeMsg = obj.getLong("timeMsg");
		this.pseudo = obj.getString("pseudo");
		this.msg = obj.getString("msg");
	}
}
