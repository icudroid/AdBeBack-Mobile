package fr.k2i.adbeback.manager.event;

import java.util.EventObject;

public class MyClassementUpdateEvent extends EventObject {
	private static final long serialVersionUID = -1149834098911435517L;
	private Integer position;
	
	
	public Integer getPosition() {
		return position;
	}


	public MyClassementUpdateEvent(Object source,Integer position) {
		super(source);
		this.position = position;
	}
}