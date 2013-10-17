package fr.k2i.adbeback.manager.event;

import java.util.EventObject;
import java.util.List;

import fr.k2i.adbeback.business.jackpot.ScoreBean;

public class ScoreUpdateEvent extends EventObject {
	private static final long serialVersionUID = -1149834098919435517L;

	public ScoreUpdateEvent(Object source) {
		super(source);
	}
}