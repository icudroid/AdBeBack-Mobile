package fr.k2i.adbeback.manager.listener;

import java.util.EventListener;

import fr.k2i.adbeback.manager.event.ScoreUpdateEvent;

public abstract class ScoreUpateListener implements EventListener {
	abstract public void onScoreUpdated(ScoreUpdateEvent evt);
}