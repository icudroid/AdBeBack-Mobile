package fr.k2i.adbeback.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.activity.EmptyActivity;
import fr.k2i.adbeback.business.blindtest.BlindTestBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class FindGameService extends Service implements IBackgroundService{

	private Timer timer ; 
	  
    private BackgroundServiceBinder binder ;  
    private List<IBackgroundServiceListener> listeners = null; 
    private Long idChanel;
    
    @Override 
    public void onCreate() { 
        timer = new Timer();  
        binder = new BackgroundServiceBinder(this);    
        _onStart(); 
    } 
  
    @Override 
    public IBinder onBind(Intent intent) { 
        Bundle objetbunble  =intent.getExtras(); 
        idChanel = objetbunble .getLong("chanelId");
        return binder; 
    } 
  
    public void _onStart(){ 
        timer.scheduleAtFixedRate(new TimerTask() { 
            public void run() { 
    			BlindTestBean res = getNextBlindTest();
                if(listeners != null && res !=null){ 
                	showNotification();
                    fireDataChanged(res);
                    FindGameService.this.stopSelf();
                    this.cancel();
            } 
        }}, 0, 10000); 
    } 
  
    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
        Bundle objetbunble  =intent.getExtras(); 
        idChanel = objetbunble .getLong("chanelId");
        _onStart(); 
        return START_NOT_STICKY; 
    } 
 
    // Ajout d'un listener  
    public void addListener(IBackgroundServiceListener listener) {  
        if(listeners == null){  
            listeners = new ArrayList<IBackgroundServiceListener>();  
        }  
        listeners.add(listener);  
    }  
 
    // Suppression d'un listener  
    public void removeListener(IBackgroundServiceListener listener) {  
        if(listeners != null){  
            listeners.remove(listener);  
        }  
    }  
 
    // Notification des listeners  
    private void fireDataChanged(Object data){  
        if(listeners != null){  
            for(IBackgroundServiceListener listener: listeners){  
                listener.dataChanged(data);  
            }  
        }  
    } 
 
    @Override 
    public void onDestroy() { 
        this.listeners.clear(); 
        this.timer.cancel(); 
    } 

    
    private BlindTestBean getNextBlindTest(){
        Map<String, String>params = new HashMap<String, String>();
        params.put("idChanel", idChanel.toString());
        BlindTestBean res = null;
        try {
			JSONObject obj = ServerConnexion.sendRequest("Android/TVJackPot/inscriptionUser", params);
			if(obj != null){
				res =  new BlindTestBean(obj);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
    	
    }
    
    
    private void showNotification() {
    	NotificationManager mNM =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.logo, text,System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(getApplicationContext(), EmptyActivity.class);
//        Long idChanel = getIntent().getExtras().getLong("chanelId");
//        intent.putExtra("chanelId", idChanel);
//        intent.putExtra("direct", true);
        
        PendingIntent contentIntent = PendingIntent.getActivity(FindGameService.this, 0,intent, 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),text, contentIntent);

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.ledOnMS = 600;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        long[] vibrate = {0,100,200,300};
        notification.vibrate = vibrate;
        
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.local_service_started, notification);
    }
}
