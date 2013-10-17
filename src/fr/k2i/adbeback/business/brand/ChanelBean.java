package fr.k2i.adbeback.business.brand;

import org.json.JSONException;
import org.json.JSONObject;

import fr.k2i.adbeback.business.user.CountryBean;


public class ChanelBean {
	private Long id;
	private String logoFile;
	private Long logoId;
	private String name;
	private CountryBean country;
	private String login;
	private String password;
	private String email;
	private String phone;
	private String fax;
	
	public ChanelBean(){}
	
	public ChanelBean(JSONObject obj) throws JSONException {
		logoId = obj.getLong("logoId");	
		country = new CountryBean(obj.getJSONObject("country"));	
		login = obj.getString("login");
		password = obj.getString("password");
		email =  obj.getString("email");
		phone = obj.getString("phone");
		fax = obj.getString("fax");
		id = obj.getLong("id");
		logoFile = obj.getString("logoFile");
		name = obj.getString("name");
	}
	
	public CountryBean getCountry() {
		return country;
	}
	public void setCountry(CountryBean country) {
		this.country = country;
	}
	public String getLogoFile() {
		return logoFile;
	}
	public void setLogoFile(String logoFile) {
		this.logoFile = logoFile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLogoId() {
		return logoId;
	}

	public void setLogoId(Long logoId) {
		this.logoId = logoId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	
	
}
