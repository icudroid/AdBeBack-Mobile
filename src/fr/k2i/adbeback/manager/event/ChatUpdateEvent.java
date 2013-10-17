package fr.k2i.adbeback.manager.event;

import java.util.EventObject;

public class ChatUpdateEvent extends EventObject {
	private static final long serialVersionUID = -4428882508277683728L;

	public ChatUpdateEvent(Object source) {
		super(source);
	}
}