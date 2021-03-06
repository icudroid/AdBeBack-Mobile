package fr.k2i.adbeback.business.jackpot;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JackPotQuestionBean {
	private long idQuestion;
	private String question;
	private List<JackPotResponseBean> reponses;
	
	public JackPotQuestionBean(){}
	public JackPotQuestionBean(JSONObject obj) throws JSONException {
		idQuestion = obj.getLong("idQuestion");
		question = obj.getString("question");
		reponses = new ArrayList<JackPotResponseBean>();
		JSONArray jsonArray = obj.getJSONArray("reponses");
		for (int i = 0; i < jsonArray.length(); i++) {
			reponses.add(new JackPotResponseBean(jsonArray.getJSONObject(i)));
		}
	}
	public long getIdQuestion() {
		return idQuestion;
	}
	public void setIdQuestion(long idQuestion) {
		this.idQuestion = idQuestion;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<JackPotResponseBean> getReponses() {
		return reponses;
	}
	public void setReponses(List<JackPotResponseBean> reponses) {
		this.reponses = reponses;
	}
	
	
}
