package fr.k2i.adbeback.manager;

import fr.k2i.adbeback.manager.event.ScoreUpdateEvent;
import fr.k2i.adbeback.manager.listener.ScoreUpateListener;


public class ScoreUpdateManager {
	
	private static ScoreUpdateManager _this;
	
	public static ScoreUpdateManager getInstance(){
		if(_this == null){
			_this = new ScoreUpdateManager();
		}
		return _this;
	}
	
	private ScoreUpateListener listener = null;

	public void setUpdateListener(ScoreUpateListener listener) {
		this.listener = listener;
	}
	
	private void fireEvent() {
		if(listener != null){
			ScoreUpdateEvent evt  = new ScoreUpdateEvent(this);
			listener.onScoreUpdated(evt);
       }
	}

	public void updateScore(){
		fireEvent();
	}
}
