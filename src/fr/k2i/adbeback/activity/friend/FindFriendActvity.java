package fr.k2i.adbeback.activity.friend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.blindtest.BlindTestBean;
import fr.k2i.adbeback.business.friend.FriendBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;
import fr.k2i.adbeback.service.FindGameService;

public class FindFriendActvity extends ListActivity {
	
	
	private class GetAvatarTask extends AsyncTask<String, Void, Bitmap> {
		protected ImageView avatar;
		
		public GetAvatarTask(ImageView avatar){
			this.avatar = avatar;
		}
		
		@Override
		protected Bitmap doInBackground(String... avatarUrl) {
            Bitmap bm = null;
			try {
				Map<String, String>paramsDownload = new HashMap<String, String>();
				paramsDownload.put("url", avatarUrl[0]);
				bm = BitmapFactory.decodeStream(ServerConnexion.getinputStream("Download",paramsDownload));
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
			return bm;
		}
		
	     protected void onPostExecute(Bitmap bm) {
	    	 if(bm != null){
	    		 avatar.setImageBitmap(bm);
	    	 }
	     }
	}
	
	private class FriendAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public FriendAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return friends.size();
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return friends.get(position);
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_friends_add, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.pseudo = (TextView) convertView.findViewById(R.id.TextSpeudo);
                holder.avatar = (ImageView) convertView.findViewById(R.id.AvatarFriend);
                holder.addFriend = (ImageButton) convertView.findViewById(R.id.BtnAddFriend);
                holder.addFriend.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						FriendBean f =  (FriendBean)v.getTag();
						FindFriendActvity.this.addFriend(f);
					}
				});

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            
            
            
            	FriendBean friend = friends.get(position);
            	holder.pseudo.setText(friend.getPseudo());
            	holder.addFriend.setTag(friend);
            	//chargement de l'image
            	new GetAvatarTask(holder.avatar).execute(friend.getAvatar());
//				Map<String, String>paramsDownload = new HashMap<String, String>();
//				paramsDownload.put("url", friend.getAvatar());
//	            Bitmap bm = null;
//				try {
//					bm = BitmapFactory.decodeStream(ServerConnexion.getinputStream("Download.htm",paramsDownload));
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} catch (LoginException e) {
//					e.printStackTrace();
//				}
//				if(bm!=null)
//            	holder.avatar.setImageBitmap(bm);
            	
            return convertView;
        }

		class ViewHolder {
			ImageView avatar;
			TextView pseudo;
			ImageButton addFriend;
		}
	}
	
	
	
	private List<FriendBean> friends;
	private FriendAdapter friendAdapter;
	private TextView findFriends;
	private Timer timer ; 
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friends);
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(true);
        friends = new ArrayList<FriendBean>();
        friendAdapter = new FriendAdapter(this);
        setListAdapter(friendAdapter);
        timer = new Timer();  
        findFriends = (TextView) findViewById(R.id.TextLookForFriends);
        findFriends.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(KeyEvent.ACTION_UP == event.getAction()){
					timer.cancel();
					timer = new Timer();
			        timer.schedule(new TimerTask() { 
			            public void run() { 
			            	FindFriendActvity.this.runOnUiThread(new Runnable() { 
			                    public void run() { 
			                        // Mise à jour de l'UI
			                    	String filter = findFriends.getText().toString();
			                    	if(filter!=null && filter.length()>3){
			                    		findFriends(filter);
			                    	}else{
			                    		friends = new ArrayList<FriendBean>();
			                    	}
			                    	friendAdapter.notifyDataSetChanged();
			                    } 
			                }); 
			            	
			            } 
			        },500); 
				}
				return false;
			}
		});
	}
	
	
	protected void findFriends(String filter){
		Map<String, String> params = new HashMap<String, String>();
		params.put("pseudoFilter", filter);
		params.put("start", "0");
		params.put("limit", "8");
		try {
			JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/listUserByPseudo", params);
			Integer nb = new Integer(obj.getString("results"));
			//construction
			JSONArray jsonArray = obj.getJSONArray("rows");
			friends = new ArrayList<FriendBean>(nb);
			for (int i = 0; i < jsonArray.length(); i++) {
				FriendBean friendBean = new FriendBean(jsonArray.getJSONObject(i));
				friends.add(friendBean);
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
		friendAdapter.notifyDataSetChanged();
	}

	protected void addFriend(FriendBean f) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("idUser", Long.toString(f.getId()));
		try {
			JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/addFriend", params);
			if(obj.getBoolean("success")){
				//affichage ami ajouter à ma liste d'amis
				Toast.makeText(this, f.getPseudo()+" a été ajouté à votre liste d'amis", Toast.LENGTH_LONG);
			}else{
				//
				Toast.makeText(this, f.getPseudo()+" est déjà dans à votre liste d'amis", Toast.LENGTH_LONG);
			}
			
			Intent intent = new Intent();

			setResult(RESULT_OK, intent);
			FindFriendActvity.this.finish();
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
}
