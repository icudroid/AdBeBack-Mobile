package fr.k2i.adbeback.business.jackpot;

import org.json.JSONException;
import org.json.JSONObject;

public class ScoreBean {
	private double score;
	private boolean correct;
	private long responseTime;
	private String classement;
	private long correctBrand;
	private String pseudo;
	
	public ScoreBean(JSONObject obj) throws JSONException {
		score = obj.getDouble("score");
		correct = obj.getBoolean("correct");
		responseTime = obj.getLong("responseTime");
		classement = obj.getString("classement");
		correctBrand = obj.getLong("correctBrand");
		pseudo = obj.getString("pseudo");
	}
	public ScoreBean(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getClassement() {
		return classement;
	}
	public void setClassement(String classement) {
		this.classement = classement;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public boolean isCorrect() {
		return correct;
	}
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public long getCorrectBrand() {
		return correctBrand;
	}
	public void setCorrectBrand(long correctBrand) {
		this.correctBrand = correctBrand;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pseudo == null) ? 0 : pseudo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoreBean other = (ScoreBean) obj;
		if (pseudo == null) {
			if (other.pseudo != null)
				return false;
		} else if (!pseudo.equals(other.pseudo))
			return false;
		return true;
	}	
	
}
