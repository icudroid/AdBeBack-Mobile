package fr.k2i.adbeback.manager.listener;

import java.util.EventListener;

import fr.k2i.adbeback.manager.event.ChatUpdateEvent;

public abstract class ChatUpateListener implements EventListener {
	abstract public void onChatUpdated(ChatUpdateEvent evt);
}