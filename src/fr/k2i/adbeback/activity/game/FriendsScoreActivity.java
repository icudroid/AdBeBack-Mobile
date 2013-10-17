package fr.k2i.adbeback.activity.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.jackpot.ScoreBean;
import fr.k2i.adbeback.manager.ScoreUpdateManager;
import fr.k2i.adbeback.manager.event.MyClassementUpdateEvent;
import fr.k2i.adbeback.manager.event.ScoreUpdateEvent;
import fr.k2i.adbeback.manager.listener.MyClassementUpateListener;
import fr.k2i.adbeback.manager.listener.ScoreUpateListener;

public class FriendsScoreActivity extends ListActivity {
	private static List<ScoreBean> scores = new ArrayList<ScoreBean>();
	
//	private static List<ScoreBean> scores = new ArrayList<ScoreBean>();
	private static final Comparator<ScoreBean> compareScore = new Comparator<ScoreBean>() {
		
		public int compare(ScoreBean o1, ScoreBean o2) {
			return new Double(o2.getScore()).compareTo(new Double(o1.getScore()));
		}
	};
	
	public static int updateScore(List<ScoreBean> newScores,String pseudo){
		scores.clear();
		if(newScores != null)
			scores.addAll(newScores);
		Collections.sort(scores, compareScore);
		return scores.indexOf(new ScoreBean(pseudo));
	}
	
	public static int updateScorePlayer(ScoreBean newScore){
		int indexOf = scores.indexOf(newScore);
		if(indexOf!=-1){
			scores.set(indexOf, newScore);
		}else{
			scores.add(newScore);
		}
		Collections.sort(scores, compareScore);
		return scores.indexOf(newScore);
	}
	
	public static void clearScore(){
		scores.clear();
	}
	
//	private Long idJackPot;
//	private Long idBlindTest;
//	private Long version = 0L;
	private ScoreUpdateManager sumanager;
//	private String myPseudo;
	private ScoreFriendsAdapter scoreFriendsAdapter;
	
	private class ScoreFriendsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ScoreFriendsAdapter(Context context) {
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
			return scores.size();
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
			return scores.get(position);
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
                convertView = mInflater.inflate(R.layout.list_friends_scores, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.position = (TextView) convertView.findViewById(R.id.TextPositionFriend);
                holder.pseudo = (TextView) convertView.findViewById(R.id.TextSpeudo);
                holder.score = (TextView) convertView.findViewById(R.id.TextScoreFriend);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            	ScoreBean scoreBean = scores.get(position);
            	
            	holder.pseudo.setText(scoreBean.getPseudo());
            	holder.position.setText(Integer.toString(position+1));
            	DecimalFormat df = new DecimalFormat("#.###");
            	holder.score.setText(df.format(scoreBean.getScore()));
            return convertView;
        }

		class ViewHolder {
			TextView position;
			TextView pseudo;
			TextView score;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sumanager = ScoreUpdateManager.getInstance();
//		myPseudo = ServerConnexion.pseudo;
//		idJackPot = getIntent().getLongExtra("idJackPot", -1);
//		idBlindTest = getIntent().getLongExtra("idBlindTest", -1);
        sumanager.setUpdateListener(new ScoreUpateListener() {
			@Override
			public void onScoreUpdated(ScoreUpdateEvent evt) {
//		    	 scores = evt.getScoreBeans();
		    	 scoreFriendsAdapter.notifyDataSetChanged();
			}
		});
		getListView().setFastScrollEnabled(true);
		scoreFriendsAdapter = new ScoreFriendsAdapter(this);
		setListAdapter(scoreFriendsAdapter);
	}
	
	

}
