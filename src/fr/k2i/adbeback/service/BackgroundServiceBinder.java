package fr.k2i.adbeback.service;

import android.os.Binder;

public class BackgroundServiceBinder  extends Binder{ 
  
    private IBackgroundService service = null; 
  
    public BackgroundServiceBinder(IBackgroundService service) { 
        super(); 
        this.service = service; 
    } 
 
    public IBackgroundService getService(){ 
        return service; 
    } 
}
