package fr.k2i.adbeback.manager;

import fr.k2i.adbeback.manager.event.ChatUpdateEvent;
import fr.k2i.adbeback.manager.listener.ChatUpateListener;


public class ChatUpdateManager {
	private static ChatUpdateManager _this;
	
	public static ChatUpdateManager getInstance(){
		if(_this == null){
			_this = new ChatUpdateManager();
		}
		return _this;
	}
	
	private ChatUpateListener listener = null;

	public void setUpdateListener(ChatUpateListener listener) {
		this.listener  = listener;
	}
	
	private void fireEvent() {
		if(listener != null){
			ChatUpdateEvent evt  = new ChatUpdateEvent(this);
			listener.onChatUpdated(evt);
		}
	}

	public void updateChat(){
		fireEvent();
	}
}
