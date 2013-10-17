package fr.k2i.adbeback.manager.listener;

import java.util.EventListener;

import fr.k2i.adbeback.manager.event.MyClassementUpdateEvent;

public abstract class MyClassementUpateListener implements EventListener {
	private String pseudo;
	
	public String getPseudo(){
		return pseudo;
	}
	public MyClassementUpateListener(String pseudo) {
		this.pseudo = pseudo;
	}
	
	abstract public void onMyClassementUpdated(MyClassementUpdateEvent evt);
}