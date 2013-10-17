package fr.k2i.adbeback.business.jackpot;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseBean{
	private long idBrand;
	private String urlLogo;
	private String textAd;
	
	public ResponseBean(JSONObject obj) throws JSONException {
		idBrand = obj.getLong("idBrand");
		urlLogo = obj.getString("urlLogo");
		textAd = obj.getString("textAd");
	}
	public long getIdBrand() {
		return idBrand;
	}
	public void setIdBrand(long idBrand) {
		this.idBrand = idBrand;
	}
	public String getUrlLogo() {
		return urlLogo;
	}
	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}
	public String getTextAd() {
		return textAd;
	}
	public void setTextAd(String textAd) {
		this.textAd = textAd;
	}
}
