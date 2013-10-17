package fr.k2i.adbeback.business.jackpot;

import org.json.JSONException;
import org.json.JSONObject;

import fr.k2i.adbeback.business.LotBean;

public class ClassementBean {
	private long classement;
	private long nbPlayer;
	private LotBean lot;
	private long idJackPot;
	private boolean showJackPotChanel = false;
	private double jackPotValue;
	private String currency;
	private double score; 
	
	public ClassementBean(JSONObject obj) throws JSONException{
		classement = obj.getLong("classement");
		nbPlayer = obj.getLong("nbPlayer");
		idJackPot = obj.getLong("idJackPot");
		showJackPotChanel = obj.getBoolean("showJackPotChanel");
		jackPotValue = obj.getDouble("jackPotValue");
		currency = obj.getString("currency");
		score = obj.getDouble("score");
		try{
			lot = new LotBean(obj.getJSONObject("lot"));
		}catch (Exception e) {
			//not présent
		}
	}
	
	public double getJackPotValue() {
		return jackPotValue;
	}
	public void setJackPotValue(double jackPotValue) {
		this.jackPotValue = jackPotValue;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public boolean isShowJackPotChanel() {
		return showJackPotChanel;
	}
	public void setShowJackPotChanel(boolean showJackPotChanel) {
		this.showJackPotChanel = showJackPotChanel;
	}
	public long getClassement() {
		return classement;
	}
	public void setClassement(long classement) {
		this.classement = classement;
	}
	public long getNbPlayer() {
		return nbPlayer;
	}
	public void setNbPlayer(long nbPlayer) {
		this.nbPlayer = nbPlayer;
	}
	public LotBean getLot() {
		return lot;
	}
	public void setLot(LotBean lot) {
		this.lot = lot;
	}
	public long getIdJackPot() {
		return idJackPot;
	}
	public void setIdJackPot(long idJackPot) {
		this.idJackPot = idJackPot;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
