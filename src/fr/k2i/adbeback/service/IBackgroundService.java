package fr.k2i.adbeback.service;

public interface IBackgroundService {
    public void addListener(IBackgroundServiceListener listener); 
    public void removeListener(IBackgroundServiceListener listener); 
}
