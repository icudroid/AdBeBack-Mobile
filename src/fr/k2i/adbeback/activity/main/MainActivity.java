package fr.k2i.adbeback.activity.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.drawable;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.R.menu;
import fr.k2i.adbeback.R.string;
import fr.k2i.adbeback.activity.friend.FriendsActivity;
import fr.k2i.adbeback.activity.friend.ValidateFriendActivity;
import fr.k2i.adbeback.activity.game.ChanelChooserActvity;
import fr.k2i.adbeback.activity.game.PlayJackPotActivity;
import fr.k2i.adbeback.activity.game.ResultActivity;
import fr.k2i.adbeback.activity.game.TabGame;
import fr.k2i.adbeback.activity.register.RegisterActivity;
import fr.k2i.adbeback.activity.win.LastWinnersActivity;
import fr.k2i.adbeback.activity.win.WonObjectActivity;
import fr.k2i.adbeback.business.user.UserBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.dao.UserDao;
import fr.k2i.adbeback.database.DbConnexion;
import fr.k2i.adbeback.exception.LoginException;
import fr.k2i.adbeback.util.Dispatcher;
import fr.k2i.adbeback.util.LoginHandler;
import fr.k2i.adbeback.util.Session;

public class MainActivity extends Activity{
	
	public static final String FB_APP_ID = "5181ea45028fa444e01a0f1d4ec7455e";
	 
	//Constante des dialogs
	private static final int DIALOG_LOGIN = 0;
	private static final int DIALOG_ERR_CONNEXION = 1;
	private static final int DIALOG_ABOUT = 2;
	private static final int DIALOG_WAIT = 3;
	private static final int DIALOG_YES_NO_REPLAY = 4;
	private static final int DIALOG_YES_NO_LOG_FACEBOOK = 5;
	
	//Contante des request des resultats des fenêtres
	private static final int REQUEST_CHANEL = 1;
	private static final int REQUEST_PLAY = 2;
	private static final int REQUEST_RESULT = 3;
	private static final int REQUEST_JACKPOT = 4;
	
	//dernière chanel choisie
	private Long lastChanelId;
	
	//pour savoir si l'on doit fermer
	private boolean close = false;

	//connexion à la base de données
	private DbConnexion dbc;

	//DAOs
	private UserDao userDao;
//	private ImageDao imageDao;

	//pour savoir le debut et la fin de la MAJ
//	private Boolean updated = false;
	
	//Login Dialog
	private View loginDialogView = null;
	
	//progressDailog pour la mise à jour
	private ProgressDialog progressDialog;
	
	public static Session session;
	
	private Dispatcher dispatcher;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//initialisation BDD et DAO
		dbc = new DbConnexion(MainActivity.this);
		userDao = new UserDao(dbc);
		ServerConnexion.userDao = userDao;
		dispatcher = new Dispatcher(this);
		dispatcher.addHandler("login", LoginHandler.class);
//		imageDao = new ImageDao(dbc.getReadableDatabase());
		
		UserBean defaultUser = userDao.getDefaultUser();
		//Connexion automatique de l'utiliseur
		if(defaultUser!=null){
			if(onConnexion(defaultUser.getPseudo(), defaultUser.getPwd(), defaultUser.getDefaultProfile())){
				//demande d'amis en attente de validation
				getFriendToValidate();
			}
	        session = Session.restore(this);
	        if (session == null) {
	        	showDialog(DIALOG_YES_NO_LOG_FACEBOOK);
//	            dispatcher.runHandler("stream");
//	            dispatcher.runHandler("login");
	        }
		}
		
		findViewById(R.id.BtnPlayTV).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(verifConnected()){
					startActivityForResult(new Intent(MainActivity.this, ChanelChooserActvity.class), REQUEST_CHANEL);
				}
			}
		});
		
//		Bundle extras = getIntent().getExtras();
//    	if(extras!=null){
//        	long chanelId = extras.getLong("chanelId", -1);
//        	if(chanelId != -1){
//		    	Intent playActivity = new Intent(MainActivity.this,PlayActivity.class);
//	        	playActivity.putExtra("chanelId",chanelId);
//	        	playActivity.putExtra("direct",true);
//				startActivityForResult(playActivity, REQUEST_PLAY);
//        	}
//    	}
		

	}

	
	private void getFriendToValidate() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("getFriendsToValidate", "");
		try {
			JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/getFriendsToValidate",
					params);
			Integer nb = new Integer(obj.getString("results"));
			if(nb>0)notifyNewFriends();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	

	private void notifyNewFriends() {
	    	NotificationManager mNM =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	        // In this sample, we'll use the same text for the ticker and the expanded notification
	        CharSequence text = getText(R.string.friends_to_validate);

	        // Set the icon, scrolling text and timestamp
	        Notification notification = new Notification(R.drawable.logo, text,System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        Intent intent = new Intent(getApplicationContext(), ValidateFriendActivity.class);
//	        Long idChanel = getIntent().getExtras().getLong("chanelId");
//	        intent.putExtra("chanelId", idChanel);
//	        intent.putExtra("direct", true);
	        
	        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0,intent, 0);

	        // Set the info for the views that show in the notification panel.
	        notification.setLatestEventInfo(this, getText(R.string.friends_to_validate_label),text, contentIntent);

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
	        mNM.notify(R.string.friends_to_validate, notification);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		 switch (id) {
			 case DIALOG_LOGIN:{
	            LayoutInflater factory = LayoutInflater.from(this);
	            loginDialogView = factory.inflate(R.layout.login, null);
	            
	            UserBean defaultUser = userDao.getDefaultUser();
	            
	            if(defaultUser!=null){
	            	TextView pseudo = (TextView)loginDialogView.findViewById(R.id.TextLogin);
	            	TextView pwd = (TextView)loginDialogView.findViewById(R.id.TextPwd);
	            	CheckBox auto = (CheckBox)loginDialogView.findViewById(R.id.CheckBoxLoginAuto);
	            	ServerConnexion.pseudo = defaultUser.getPseudo();
	            	pseudo.setText(defaultUser.getPseudo());
	            	pwd.setText(defaultUser.getPwd());
	            	auto.setChecked(defaultUser.getDefaultProfile());
	            		
	            }
	            
				 
				 return new AlertDialog.Builder(MainActivity.this)
		         .setIcon(R.drawable.alert_dialog_icon)
		         .setTitle(R.string.login_dialog_title)
		         .setView(loginDialogView)
		         .setPositiveButton(R.string.login_dialog_ok, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		     			EditText pseudo = (EditText)loginDialogView.findViewById(R.id.TextLogin);
		    			EditText pwd = (EditText)loginDialogView.findViewById(R.id.TextPwd);
		    			CheckBox ckAuto = (CheckBox)loginDialogView.findViewById(R.id.CheckBoxLoginAuto);
		            	MainActivity.this.onConnexion(pseudo.getText().toString(),pwd.getText().toString(),ckAuto.isChecked());
		             }
		         })
		         .setNegativeButton(R.string.login_dialog_cancel, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		            	 
		             }
		         })
		         .create();
				 
			 }
			 case DIALOG_ERR_CONNEXION:{
				 
				 return new AlertDialog.Builder(MainActivity.this)
		         .setIcon(R.drawable.alert_dialog_icon)
		         .setTitle(R.string.err_dialog_title)
		         .setMessage(R.string.err_dialog_message)
		         .setPositiveButton(R.string.err_dialog_ok, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		            	 MainActivity.this.showDialog(MainActivity.DIALOG_LOGIN);
		             }
		         })
		         .create();
			 }
			 case DIALOG_ABOUT:{
				 
				 return new AlertDialog.Builder(MainActivity.this)
		         .setIcon(R.drawable.alert_dialog_icon)
		         .setTitle(R.string.about_dialog_title)
		         .setMessage(R.string.about_dialog_message)
		         .setPositiveButton(R.string.about_dialog_ok, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		             }
		         })
		         .create();
			 }
			 case DIALOG_WAIT:{
				 progressDialog = new ProgressDialog(MainActivity.this);
				 progressDialog.setMessage("Mise à jour");
				 return progressDialog;
			 }
			 case DIALOG_YES_NO_REPLAY:{
				 
				 return new AlertDialog.Builder(MainActivity.this)
		         .setIcon(R.drawable.alert_dialog_icon)
		         .setTitle(R.string.replay_dialog_title)
		         .setMessage(R.string.replay_dialog_message)
		         .setPositiveButton(R.string.replay_dialog_yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
		            	 //MainActivity.this.showDialog(MainActivity.DIALOG_LOGIN);
		            	 //relancer la page d'attente sur la dernière chaine choisi
			        	if(lastChanelId != null){
					    	Intent playActivity = new Intent(MainActivity.this,TabGame.class);
				        	playActivity.putExtra("chanelId",lastChanelId);
							startActivityForResult(playActivity, REQUEST_PLAY);
			        	}
		             }
		         })
		         .setNegativeButton(R.string.replay_dialog_no, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		            	 //ne rien faire
		             }
		         })
		         .setNeutralButton(R.string.replay_dialog_yes_other, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	//lancer la page de choix d'une chaine de télévision
                    	startActivityForResult(new Intent(MainActivity.this, ChanelChooserActvity.class), REQUEST_CHANEL);
                    }
                })
		         .create();
			 }
			 case DIALOG_YES_NO_LOG_FACEBOOK:{
				 
				 return new AlertDialog.Builder(MainActivity.this)
		         .setIcon(R.drawable.alert_dialog_icon)
		         .setTitle(R.string.facebook_dialog_title)
		         .setMessage(R.string.facebook_dialog_message)
		         .setPositiveButton(R.string.facebook_dialog_yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						dispatcher.runHandler("login");
		             }
		         })
		         .setNegativeButton(R.string.facebook_dialog_no, new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int whichButton) {
		            	 //ne rien faire
		             }
		         })
		         .create();
			 }
		 }
		 return null;
	}

	protected boolean onConnexion(String pseudo, String pwd, boolean autoConnect) {
		
			Map<String, String> params = new HashMap<String, String>();
			params.put("pseudo", pseudo);
			params.put("pwd", pwd);
			
			try {
				JSONObject sendRequest = ServerConnexion.sendRequest("Android/Connexion/login",params);
				Boolean islogged = Boolean.valueOf(sendRequest.getString("success"));
				if(islogged == false){
					MainActivity.this.showDialog(DIALOG_ERR_CONNEXION);
					return false;
				}else{
					ServerConnexion.pseudo = pseudo;
					userDao.deleteAll();
					
					//connection ok => on enregistre les paramètres en base de données
					UserBean u = new UserBean();
					u.setDefaultProfile(autoConnect);
					u.setPseudo(pseudo);
					u.setPwd(pwd);
					userDao.saveUser(u);
					
					if(u.getDefaultProfile() == true){
						userDao.setDefaultProfile(u.getPseudo());
					}
					return true;
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
			return false;
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		close = false;
		switch (item.getItemId()) {
		case R.id.main_about:
			// Affichage Dialog
			 showDialog(DIALOG_ABOUT);
			return true;
		case R.id.main_last_winners:
			startActivity(new Intent(MainActivity.this,	LastWinnersActivity.class));
			return true;
		case R.id.main_login:
			// affichage dialog connexion
			showDialog(DIALOG_LOGIN);
			return true;
		case R.id.main_register:
			startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			return true;
		case R.id.main_play:
			if(verifConnected()){
				startActivityForResult(new Intent(MainActivity.this, ChanelChooserActvity.class), REQUEST_CHANEL);
			}
//			startActivity(new Intent(MainActivity.this, ChanelChooserActvity.class));
			return true;
		case R.id.main_won_object:
			if(verifConnected()){
				startActivity(new Intent(MainActivity.this, WonObjectActivity.class));
			}
			return true;
		case R.id.main_my_friends:
			if(verifConnected()){
				startActivity(new Intent(MainActivity.this, FriendsActivity.class));
			}
			return true;
		case R.id.main_facebook_connect:
			dispatcher.runHandler("login");
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	
    private boolean verifConnected() {
        Map<String, String>params = new HashMap<String, String>();
		try {
			JSONObject obj = ServerConnexion.sendRequest("Android/Connexion/isConnect", params);
			if(!obj.getBoolean("success")){
				UserBean defaultUser = userDao.getDefaultUser();
				//Connexion automatique de l'utiliseur
				if(defaultUser!=null){
					onConnexion(defaultUser.getPseudo(), defaultUser.getPwd(), defaultUser.getDefaultProfile());
					return true;
				}else{
					return false;
				}
			}else{
				return true;
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
    	return false;
	}


	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CHANEL) {
        	if(data!=null){
	        	lastChanelId = data.getLongExtra("chanelId", -1);
	        	if(lastChanelId != -1){
			    	Intent playActivity = new Intent(MainActivity.this,TabGame.class);
		        	playActivity.putExtra("chanelId",lastChanelId);
					startActivityForResult(playActivity, REQUEST_PLAY);
	        	}else{
	        		lastChanelId = null;
	        	}
        	}
        }
        
        if (requestCode == REQUEST_PLAY) {
        	if(data!=null){
	        	long idJackPot = data.getLongExtra("idJackPot", -1);
	        	if(idJackPot != -1){
			    	Intent resultActivity = new Intent(MainActivity.this,ResultActivity.class);
			    	resultActivity.putExtra("idJackPot",idJackPot);
					startActivityForResult(resultActivity, REQUEST_RESULT);
	        	}
        	}
        }
        
        if (requestCode == REQUEST_RESULT) {
        	if(data!=null){
	        	long idJackPot = data.getLongExtra("idJackPot", -1);
	        	if(idJackPot != -1){
			    	Intent playJPC = new Intent(MainActivity.this,PlayJackPotActivity.class);
			    	playJPC.putExtra("idJackPot",idJackPot);
					startActivityForResult(playJPC, REQUEST_JACKPOT);
	        	}else{
	        		showDialog(DIALOG_YES_NO_REPLAY);
	        	}
        	}
        }
        
        if (requestCode == REQUEST_JACKPOT) {
        	//demander de rejouer
        	showDialog(DIALOG_YES_NO_REPLAY);
//        	if(data!=null){
//	        	long idJackPot = data.getLongExtra("idJackPot", -1);
//	        	if(idJackPot != -1){
//			    	Intent playJPC = new Intent(MainActivity.this,PlayJackPotActivity.class);
//			    	playJPC.putExtra("idJackPot",idJackPot);
//					startActivityForResult(playJPC, REQUEST_JACKPOT);
//	        	}
//        	}
        }
    }


    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(close == false){
				close = true;
				Toast.makeText(MainActivity.this, "Appuyer une fois de plus pour quitter l'application", Toast.LENGTH_LONG).show();
				return false;
			}if(close == true){
				MainActivity.this.finish();
				return true;
			}
		}
		return false;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbc.close();
	}
}