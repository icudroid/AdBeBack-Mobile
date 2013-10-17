package fr.k2i.adbeback.business.jackpot;

public class JackPotClassementBean {
	private Long position;
	private String pseudo;
	private Double score;
//	private JackPotQuestionBean question;
	
	public Long getPosition() {
		return position;
	}
	public void setPosition(Long position) {
		this.position = position;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
//	public JackPotQuestionBean getQuestion() {
//		return question;
//	}
//	public void setQuestion(JackPotQuestionBean question) {
//		this.question = question;
//	}
	
}
