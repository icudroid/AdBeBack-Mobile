package fr.k2i.adbeback.activity.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.user.UserMessageBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;
import fr.k2i.adbeback.manager.ChatUpdateManager;
import fr.k2i.adbeback.manager.event.ChatUpdateEvent;
import fr.k2i.adbeback.manager.listener.ChatUpateListener;

public class ChatActivity extends ListActivity {
	private static List<UserMessageBean> messages = new ArrayList<UserMessageBean>();
	
	public static void addMessages(List<UserMessageBean> msgs){
		messages.addAll(msgs);
	}
	
	public static void clearMessages(){
		messages.clear();
	}
	
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private ChatAdapter chatAdapter;
	private ChatUpdateManager chatMgr;
	
	private class ChatAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ChatAdapter(Context context) {
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
			return messages.size();
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
			return messages.get(position);
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
                convertView = mInflater.inflate(R.layout.list_friends_chat, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.msg = (TextView) convertView.findViewById(R.id.TextChat);
                holder.pseudo = (TextView) convertView.findViewById(R.id.TextPseudo);
                holder.atTime = (TextView) convertView.findViewById(R.id.TextSendAt);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            	UserMessageBean userMessageBean = messages.get(position);
            	holder.pseudo.setText(userMessageBean.getPseudo());
            	holder.msg.setText(userMessageBean.getMsg());
            	holder.atTime.setText(sdf.format(new Date(userMessageBean.getTimeMsg())));
            return convertView;
        }

		class ViewHolder {
			TextView msg;
			TextView pseudo;
			TextView atTime;
		}
	}
	
	private EditText msgTxt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_friends);
		chatMgr = ChatUpdateManager.getInstance();
		chatMgr.setUpdateListener(new ChatUpateListener() {
			@Override
			public void onChatUpdated(ChatUpdateEvent evt) {
				chatAdapter.notifyDataSetChanged();
			}
		});
		Button btnSend = (Button) findViewById(R.id.send_button);
		msgTxt = (EditText) findViewById(R.id.embedded_text_editor);
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String message = msgTxt.getText().toString();
				if(!"".equals(message))postMessage(message);
			}
		});
		getListView().setFastScrollEnabled(true);
		chatAdapter = new ChatAdapter(this);
		setListAdapter(chatAdapter);
	}
	
	private void postMessage(String message){
		if(message == null)return;
		Map<String, String> params = new HashMap<String, String>();
		params.put("message", message);

		try {
			JSONObject obj = ServerConnexion.sendRequest("FriendsGroup/postMessage", params);
			UserMessageBean msg = new UserMessageBean(obj);
			msgTxt.setText("");
			messages.add(msg);
			chatAdapter.notifyDataSetChanged();
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
