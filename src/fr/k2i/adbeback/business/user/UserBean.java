package fr.k2i.adbeback.business.user;

public class UserBean {
	private String pseudo;
	private String pwd;
	private Boolean defaultProfile;
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Boolean getDefaultProfile() {
		return defaultProfile;
	}
	public void setDefaultProfile(Boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}
}
