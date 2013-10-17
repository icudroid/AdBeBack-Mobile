package fr.k2i.adbeback.activity.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.activity.adobject.AdObjectActivity;
import fr.k2i.adbeback.activity.main.MainActivity;
import fr.k2i.adbeback.business.blindtest.BlindTestBean;
import fr.k2i.adbeback.business.jackpot.QuestionBean;
import fr.k2i.adbeback.business.jackpot.ResponseBean;
import fr.k2i.adbeback.business.jackpot.ScoreBean;
import fr.k2i.adbeback.business.user.UserMessageBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.dao.ImageDao;
import fr.k2i.adbeback.database.DbConnexion;
import fr.k2i.adbeback.exception.LoginException;
import fr.k2i.adbeback.manager.ChatUpdateManager;
import fr.k2i.adbeback.manager.ScoreUpdateManager;
import fr.k2i.adbeback.service.BackgroundServiceBinder;
import fr.k2i.adbeback.service.FindGameService;
import fr.k2i.adbeback.service.IBackgroundService;
import fr.k2i.adbeback.service.IBackgroundServiceListener;
import fr.k2i.adbeback.util.Dispatcher;

public class TabGame extends TabActivity{
	
	private static final String AD_OBJECT = "Objet Pub";
	private static final String SCORE_FRIEND = "Score amis";
	private static final String CHAT = "Chat";
	private LayoutInflater mInflater;
	private BlindTestBean bt;
	private Long duration;
	private Long start;
	private List<Long> starts;
	private Handler timer = new Handler();
	private Handler nextQuestion = new Handler();

	private DbConnexion dbConnexion;
    private ImageDao imageDao;
	private TextView labelScore;
	private TextView textExplain;
	
	private List<ImageButton> btns ;
	private Integer cpt = null;
	private PowerManager.WakeLock fullWakeUp;

	private PowerManager pm;
	
	private TextView scoreTxt;
	private TextView wrongTxt;
	
	private boolean close = false;
	
	private boolean clicked = true;
	
	private TabSpec tabSpec;
	private String myPseudo;
	private Long versionScore = 0L;
	private Long versionChat = 0L;
	private Integer cptUnreadMsgs = 0;
	
	private ScoreUpdateManager scoreUpdateMgr;
	private ChatUpdateManager chatMgr;
	private ServiceConnection connection;
	private Vibrator vibrator;
	
	//	private long[] pattern = { 0, 200, 300 };
	
	private boolean endGame = false;
	
	private ScoreBean lastPlayer;
	private Dispatcher dispatcher;
	
	private String htmlBtnLike="<iframe frameborder='0' scrolling='no' style='border: medium none; overflow: hidden; width: 90px; height: 25px;' allowtransparency='true' src='http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.adbeback.fr%2FAdnJoy%2Fpub%2F{0}&amp;layout=button_count&amp;show_faces=false&amp;width=90&amp;action=like&amp;colorscheme=light'></iframe>";
	private View btnsLayout;
	
	private void onScoreUpdate(final Integer  postion){
		TabGame.this.runOnUiThread(new Runnable() { 
            public void run() {
//            	RelativeLayout childTabViewAt = (RelativeLayout)getTabHost().getTabWidget().getChildTabViewAt(1);
            	TextView txt = (TextView) getTabHost().getTabWidget().getChildAt(1).findViewById(android.R.id.title);
//            	TextView txt = (TextView) childTabViewAt.getChildAt(1);
            	txt.setText(SCORE_FRIEND+" ("+postion+")");            
            } 
        }); 
	}
	
	private void onChatUpdate(final Integer newMsg){
		TabGame.this.runOnUiThread(new Runnable() { 
            public void run() {
            	if(getTabHost().getCurrentTab()!=2){
            		TextView txt = (TextView) getTabHost().getTabWidget().getChildAt(2).findViewById(android.R.id.title);
//            		RelativeLayout childTabViewAt = (RelativeLayout)getTabHost().getTabWidget().getChildTabViewAt(2);
	            	cptUnreadMsgs+=newMsg;
//	            	TextView txt = (TextView) childTabViewAt.getChildAt(1);
	            	txt.setText(CHAT+" ("+cptUnreadMsgs+")");
            	}
            	//faire vibrer
            	vibrator.vibrate(300);
            } 
        }); 
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        endGame = false;
        
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setProgress(0);
        setSecondaryProgress(0);
        setTitle("En attente du BlindTest");
        myPseudo = ServerConnexion.pseudo;
        
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        getTabHost().setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if("tab3".equals(tabId)){
	            	RelativeLayout childTabViewAt = (RelativeLayout)getTabHost().getTabWidget().getChildTabViewAt(2);
	            	cptUnreadMsgs = 0;
	            	TextView txt = (TextView) childTabViewAt.getChildAt(1);
	            	txt.setText(CHAT);
				}
			}
		});
        
        scoreUpdateMgr = ScoreUpdateManager.getInstance();
        chatMgr = ChatUpdateManager.getInstance();
        
        registerChat();
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        
    	dbConnexion = new DbConnexion(this);
    	imageDao = new ImageDao(dbConnexion);
        
    	mInflater = LayoutInflater.from(this);
        
        final TabHost tabHost = getTabHost();
        
//        Intent playIntent = new Intent(this, PlayActivity.class);
        Bundle objetbunble  = this.getIntent().getExtras(); 
        Long idChanel = objetbunble .getLong("chanelId");
//        playIntent.putExtra("chanelId",idChanel);

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("Jeux")
                .setContent(new TabHost.TabContentFactory() {
					public View createTabContent(String tag) {
						View convertView = mInflater.inflate(R.layout.play, null);
						
				        scoreTxt = (TextView) convertView.findViewById(R.id.TextScore);
				        btns = new ArrayList<ImageButton>();
				        labelScore = (TextView)convertView.findViewById(R.id.LabelScore);
				        
				        textExplain = (TextView)convertView.findViewById(R.id.TextExplain);
				        textExplain.setVisibility(View.VISIBLE);
				        labelScore.setVisibility(View.INVISIBLE);
				        
				        btnsLayout = convertView.findViewById(R.id.btnsLayout);
				        btnsLayout.setVisibility(View.GONE);
				        
				        btns.add((ImageButton) convertView.findViewById(R.id.ImageButton01));
				        btns.add((ImageButton) convertView.findViewById(R.id.ImageButton02));
				        btns.add((ImageButton) convertView.findViewById(R.id.ImageButton03));
				        
				        wrongTxt = (TextView) convertView.findViewById(R.id.TextWrong);
				        wrongTxt.setVisibility(View.GONE);
				        
				        for (ImageButton btn : btns) {
							btn.setVisibility(View.GONE);
				        	btn.setOnClickListener(onResponseListener);
						}
				        
				        dispatcher = new Dispatcher(TabGame.this,(RelativeLayout) convertView.findViewById(R.id.webViewLayout));
				        
				        return convertView;
					}
				}));
        
        
//        tvScoreFriend = new TextView(this);
//        tvScoreFriend.setText(SCORE_FRIEND);
        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator(AD_OBJECT).setContent(new Intent(this, AdObjectActivity.class));

//        tabHost.addTab(tabSpec);
        
        
        
//        tabHost.addTab(tabSpec
//                .setIndicator("Score amis")
//                .setContent(new Intent(this, FirendsScoreActivity.class)));
        
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(CHAT)
                .setContent(new Intent(this, ChatActivity.class)));
        
        final Intent intent = new Intent(this,FindGameService.class);
        intent.putExtra("chanelId",idChanel);
        final IBackgroundServiceListener listener = new IBackgroundServiceListener() { 
            public void dataChanged(final Object data) { 
            	TabGame.this.runOnUiThread(new Runnable() { 
                    public void run() { 
                        // Mise à jour de l'UI
                    	prepareBT((BlindTestBean) data);
                    } 
                }); 
            } 
        }; 
         
        connection = new ServiceConnection() { 
            public void onServiceConnected(ComponentName name, IBinder service) { 
                Log.i("BackgroundService", "Connected!"); 
                IBackgroundService srv = ((BackgroundServiceBinder)service).getService(); 
                srv.addListener(listener); 
            } 
         
            public void onServiceDisconnected(ComponentName name) { 
                Log.i("BackgroundService", "Disconnected!"); 
            }  
        }; 
            
        bindService(intent,connection, Context.BIND_AUTO_CREATE); 

    }
    
    private class OnResponseListener implements OnClickListener{
		public void onClick(final View v) {
			if(clicked == false){
				TabGame.this.runOnUiThread(new Runnable() {
					
					public void run() {
						doUserResponse(v);
					}
				});
						
			}			
		}
    }

    private OnResponseListener onResponseListener = new OnResponseListener();
	private AsyncTask<PlayReqData, Void, ScoreBean> previous;
    
	/*public View createTabContent(String tag) {
		if("tab1".equals(tag)){

		}else if("tab2".equals(tag)){
	        TextView tv = new TextView(this);
	        tv.setText("Le jeux n'a pas encore commencé");
	        return tv;
		}
		return null;
	}*/
	
	private void doUserResponse(View imageButton){
		clicked = true;
		try{
			if(previous !=null){
				System.out.println("previous" + previous.getStatus());
			}
		previous = new ReqTask().execute((PlayReqData) imageButton.getTag());
		AsyncTask.Status status = previous.getStatus();
		System.out.println(status);
		}catch (Exception e) {
			e.printStackTrace();
		}
		for (ImageButton button : btns) {
			if(imageButton != button)
				button.setVisibility(View.GONE);
		}
	}
	
    private void prepareBT(BlindTestBean result){
    	unbindService(connection);
    	endGame = false;
    	fullWakeUp = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"My Tag");
		fullWakeUp.acquire();
    	this.bt = result;
    	List<QuestionBean> questions = bt.getQuestions();
		start = new Date().getTime();
		duration = bt.getBeginIn()+questions.get(0).getPauseBefore();
		starts = new ArrayList<Long>();
		
		Long d = 0L;

		for (int i = 0; i < questions.size(); i++) {
			QuestionBean q = questions.get(i);
			starts.add(duration+start+d);
			if((i+1) < questions.size()){
				d+= q.getDuration() + questions.get(i+1).getPauseBefore();
			}else{
				d+= q.getDuration();
			}
		}
		
		Intent intent = new Intent(this, FriendsScoreActivity.class);
		intent.putExtra("idJackPot", bt.getIdJackPot());
		intent.putExtra("idBlindTest", bt.getIdBlindTest());
		tabSpec.setContent(intent);
		getScoreFriends();
		//timer de progressBar pour montrer le debut
		timer.removeCallbacks(taskWaitBt);
		timer.postDelayed(taskWaitBt, 200);
    }
	
	private class ReqTask extends AsyncTask<PlayReqData, Void, ScoreBean> {
//		protected View v;
		
		@Override
		protected ScoreBean doInBackground(PlayReqData... p) {
//			v = p[0].getV();
			System.out.println("start" +p[0].getIdJackPot()+" " +p[0].getBtId() );
			Map<String, String> params = new HashMap<String, String>();
			params.put("idBlindTest", new Long(p[0].getBtId()).toString());
			params.put("numAd", new Integer(p[0].getNumAd()).toString());
			params.put("idBrandResponse", new Long(p[0].getIdBrand()).toString());
			params.put("idJackPot",  new Long(p[0].getIdJackPot()).toString());

			try {
				JSONObject obj = ServerConnexion.sendRequest(
						"Android/TVJackPot/playGame", params);
				return new ScoreBean(obj);
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
			
			return null;
		}
		
	     protected void onPostExecute(ScoreBean sb) {
	    	 	System.out.println("end" + sb.getResponseTime());
	    	 	TabGame.this.lastPlayer = sb;
	    	 	TabGame.this.onScoreUpdate(FriendsScoreActivity.updateScorePlayer(sb)+1);

//	    	 	dispatcher.runHandler("likeBtn");
//	    	 	FirendsScoreActivity.updateScorePlayer(sb);
	    	 	scoreUpdateMgr.updateScore();
				DecimalFormat df = new DecimalFormat("#.###");
				scoreTxt.setText(df.format(sb.getScore()));
				if (!sb.isCorrect()) {
					for (ImageButton button : btns) {
						PlayReqData pr = (PlayReqData)button.getTag();
						if(pr.getIdBrand()==sb.getCorrectBrand()){
							button.setVisibility(View.VISIBLE);
						}else{
							button.setVisibility(View.GONE);
						}
					}
//					v.setVisibility(View.INVISIBLE);
					wrongTxt.setVisibility(View.VISIBLE);
				}else{
		    	 	if(MainActivity.session!=null){
			    	 	dispatcher.showWebView();
			    	 	dispatcher.loadData(nextUrlLikeBtn);
		    	 	}
				}
	     }
	}
	
	private Runnable taskWaitBt = new Runnable() {
		public void run() {
			Long now = new Date().getTime();
			double durationDouble = duration;
			int p = (int) (((now-start)/durationDouble)*10000);
			Long d = ((duration -(now-start))/1000);
			String s = (((d%60)<10)?"0":"") + ((d%60));
			String m = (((d/60)<10)?"0":"")+((d/60));
			setProgress(p);
			setTitle("En attente du BlindTest ("+m+":"+s+")");
			if(p>=10000){
				timer.removeCallbacks(taskWaitBt);
				startGame();
			}else{
				timer.postDelayed(this,200);

			}
		}
	};
	
	private Runnable taskWaitNq = new Runnable() {
		public void run() {
			Long now = new Date().getTime();
			double durationDouble = duration;
			int p = (int) (((now-start)/durationDouble)*10000);
			Long d = ((duration -(now-start))/1000);
			String s = (((d%60)<10)?"0":"") + ((d%60));
			String m = (((d/60)<10)?"0":"")+((d/60));
			setProgress(p);
			setTitle(m+":"+s);
			if(p>=10000){
				nextQuestion.removeCallbacks(taskWaitNq);
				drawNextQuestion();
			}else{
				nextQuestion.postDelayed(this,200);
			}
		}
	};
	private ReqScore reqScore;
	private ReqChat reqChat;
	private String nextUrlLikeBtn;
	private void startGame(){
		
		endGame = false;
		NotificationManager mNM =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(R.string.local_service_started);
		scoreTxt.setText("0");
		cpt = 0;
        textExplain.setVisibility(View.GONE);
        labelScore.setVisibility(View.VISIBLE);
        btnsLayout.setVisibility(View.VISIBLE);
		drawNextQuestion();
    }
    
    private void drawNextQuestion(){
		if(cpt>=bt.getQuestions().size()){
			//fin du jeu demande du résultat
			Intent intent = new Intent();
			intent.putExtra("idJackPot", bt.getIdJackPot());
			setResult(RESULT_OK, intent);
			bt = null;
			endGame = true;
			this.finish();
		}else{
			clicked = false;
			dispatcher.hideWebView();
			wrongTxt.setVisibility(View.GONE);
			QuestionBean question = bt.getQuestions().get(cpt);
			nextUrlLikeBtn = htmlBtnLike.replace("{0}", question.getUrl().substring(0, question.getUrl().length()-4));
			List<ResponseBean>responses =  question.getResponses();
			
			for(int i = 0;i<responses.size();i++){
				ImageButton btn = btns.get(i);
				String url = responses.get(i).getUrlLogo();
				
	            Bitmap bm = imageDao.getBitmapByUrl(url);
	            /*if(bm == null){
	            	bm = loadImage(responses.get(i).getUrlLogo()); 
	            	bs.put(url, bm);
	            }*/
	            
	            
	            double bmW = 225;
	            double bmH = 60;
	            
	            if(bm.getHeight()>bmH && bm.getWidth()<=bmW){
	            	double scale = bmH / (double)bm.getHeight();
	            	int dstWidth = (int) (bm.getWidth() * scale) ;
	            	int dstHeight = (int) bmH;
	            	bm = Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false); 
	            }else if(bm.getWidth()>bmW && bm.getHeight()<=bmH){
	            	double scale = bmW / (double)bm.getWidth();
	            	int dstHeight = (int) (bm.getHeight() * scale) ;
	            	int dstWidth = (int) bmW;
	            	bm = Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false); 
	            }else if(bm.getWidth()>bmW && bm.getHeight()>bmH){
	            	if(bm.getWidth()>=bm.getHeight()){
		            	double scale = bmW / (double)bm.getWidth();
		            	int dstHeight = (int) (bm.getHeight() * scale) ;
		            	int dstWidth = (int) bmW;
		            	bm = Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false); 
	            	}else{
		            	double scale = bmH / (double)bm.getHeight();
		            	int dstWidth = (int) (bm.getWidth() * scale) ;
		            	int dstHeight = (int) bmH;
		            	bm = Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false); 
	            	}
	            }
	            
				btn.setImageBitmap(bm);
	            btn.setVisibility(View.VISIBLE);
            	btn.setTag(new PlayReqData(bt.getIdJackPot(),responses.get(i).getIdBrand(), cpt, bt.getIdBlindTest()));
            	
            	if(getTabHost().getCurrentTab()!=0){
            		getTabHost().setCurrentTab(0);
            	}
			}
			 
			start = starts.get(cpt);
			duration = question.getDuration();
			
			nextQuestion.removeCallbacks(taskWaitNq);
			nextQuestion.postDelayed(taskWaitNq, 200);
			cpt++;
		}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(close == false){
				close = true;
				Toast.makeText(TabGame.this, "Pour sortir du jeu, cliquez une deuxième fois sur la touche back", Toast.LENGTH_LONG).show();
				return false;
			}if(close == true){
				TabGame.this.finish();
				return true;
			}
		}
		return false;
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		onResponseListener = null;
		btns.clear();
		if(reqScore != null){
			reqScore.cancel(true);
		}
		if(reqChat !=null){
			reqChat.cancel(true);
		}
		FriendsScoreActivity.clearScore();
		NotificationManager mNM =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(R.string.local_service_started);
		if(fullWakeUp!=null)fullWakeUp.release();
		dbConnexion.close();
		
	}
	
	
	private void getScoreFriends(){
		if(reqScore == null){
			reqScore = new ReqScore();
		}
		if(!TabGame.this.endGame){
			reqScore.cancel(false);
			reqScore = new ReqScore();
			reqScore.execute();
		}
	}
	
	private class ReqScore extends AsyncTask<Void, Void, List<ScoreBean>> {
		@Override
		protected List<ScoreBean> doInBackground(Void... p) {
			if(bt == null){
				return new ArrayList<ScoreBean>();
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("idBlindTest", new Long(bt.getIdBlindTest()).toString());
			params.put("idJackPot", new Long(bt.getIdJackPot()).toString());
			params.put("version", versionScore.toString());

			try {
				JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/getScoresFriends", params);
				Integer nb = new Integer(obj.getString("results"));
				versionScore = new Long(obj.getString("version"));
				
				List<ScoreBean>scores = new ArrayList<ScoreBean>(nb+1);
				
				if(TabGame.this.lastPlayer != null)scores.add(TabGame.this.lastPlayer);
				
				//construction
				JSONArray jsonArray = obj.getJSONArray("rows");
				for (int i = 0; i < jsonArray.length(); i++) {
					ScoreBean jpw = new ScoreBean(jsonArray.getJSONObject(i));
//	            	if(myPseudo.equals(jpw.getPseudo())){
//	            		TabGame.this.onScoreUpdate(i+1);
//	            	}
					scores.add(jpw);
				}
				return scores;
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
			
			return null;
		}
		
	     protected void onPostExecute(List<ScoreBean> sbs) {
	    	 TabGame.this.onScoreUpdate(FriendsScoreActivity.updateScore(sbs,myPseudo)+1);
	    	 scoreUpdateMgr.updateScore();
	    	 getScoreFriends();
	     }
	}
	
	
	private void getChatFriends(){
		if(reqChat == null){
			reqChat = new ReqChat();
		}
		if(!TabGame.this.endGame){
			reqChat.cancel(false);
			reqChat = new ReqChat();
			reqChat.execute();
		}
	}
	
	
	private class ReqChat extends AsyncTask<Void, Void, List<UserMessageBean>> {
		@Override
		protected List<UserMessageBean> doInBackground(Void... p) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("version", versionChat.toString());
			
			try {
				JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/chatFriends", params);
				Integer nb = new Integer(obj.getString("results"));
				versionChat = new Long(obj.getString("version"));
				
				List<UserMessageBean>msgs = new ArrayList<UserMessageBean>(nb);
				//construction
				JSONArray jsonArray = obj.getJSONArray("rows");
				for (int i = 0; i < jsonArray.length(); i++) {
					UserMessageBean umb = new UserMessageBean(jsonArray.getJSONObject(i));
					msgs.add(umb);
				}
				return msgs;
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
			
			return null;
		}
		
	     protected void onPostExecute(List<UserMessageBean> msgs) {
	    	 if(msgs!=null && !msgs.isEmpty()){
	    		 ChatActivity.addMessages(msgs);
	    		 onChatUpdate(msgs.size());
//	    		 messages.addAll(msgs);
//	    		 chatAdapter.notifyDataSetChanged();
	    		 chatMgr.updateChat();
	    	 }
	    	getChatFriends();
	     }
	}
	
	
	private void registerChat(){
		Map<String, String> params = new HashMap<String, String>();

		try {
			JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/registerChat", params);
			if(obj.getBoolean("success")==false){
				registerChat();
			}else{
				ChatActivity.clearMessages();
				getChatFriends();
			}
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
	
	private class PlayReqData{
		private long idBrand;
		private int numAd;
		private long btId;
		private long idJackPot;
		private View v;
		
		public long getIdJackPot() {
			return idJackPot;
		}

		public long getIdBrand() {
			return idBrand;
		}
		
		
		public int getNumAd() {
			return numAd;
		}
		
		public long getBtId() {
			return btId;
		}

		public PlayReqData(long idJackPot,long idBrand, int numAd, long btId) {
			super();
			this.idJackPot = idJackPot;
			this.idBrand = idBrand;
			this.numAd = numAd;
			this.btId = btId;
		}
	}
}

