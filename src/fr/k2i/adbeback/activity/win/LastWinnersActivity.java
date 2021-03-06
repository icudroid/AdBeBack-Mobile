package fr.k2i.adbeback.activity.win;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.jackpot.HomeJackPotWinnerBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.dao.ImageDao;
import fr.k2i.adbeback.database.DbConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class LastWinnersActivity extends ListActivity {
	private List<HomeJackPotWinnerBean> lastWinner = null;
	private DbConnexion dbConnexion;
	private ImageDao imageDao;
	
	private class WinnerAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public WinnerAdapter(Context context) {
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
			return lastWinner.size();
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
			return lastWinner.get(position);
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
                convertView = mInflater.inflate(R.layout.list_item_winner, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.pseudo = (TextView) convertView.findViewById(R.id.WinnerItemFieldPseudo);
                holder.gain = (TextView) convertView.findViewById(R.id.WinnerItemFieldWin);
                holder.value = (TextView) convertView.findViewById(R.id.WinnerItemFieldValue);
                holder.date = (TextView) convertView.findViewById(R.id.WinnerItemFieldWinDate);
                holder.logo = (ImageView) convertView.findViewById(R.id.WinnerItemImgWinOn);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            	HomeJackPotWinnerBean jp = lastWinner.get(position);
            	holder.pseudo.setText(jp.getPseudo());
            	holder.gain.setText(jp.getLotName());
            	holder.value.setText(jp.getLotValue()+" "+jp.getCurrency().replace("&euro;", "euros"));
            	holder.date.setText(jp.getDt());
            	holder.logo.setImageBitmap(imageDao.getBitmapByUrl(jp.getWinOnImg()));
            return convertView;
        }

		class ViewHolder {
			TextView pseudo;
			TextView gain;
			TextView value;
			TextView date;
			ImageView logo;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setFastScrollEnabled(true);
		dbConnexion = new DbConnexion(this);
		imageDao = new ImageDao(dbConnexion);
		lastWinner = getLastWinners(); 
		setListAdapter(new WinnerAdapter(this));
	}

	private List<HomeJackPotWinnerBean> getLastWinners() {
		List<HomeJackPotWinnerBean> res = null;
        Map<String, String>params = new HashMap<String, String>();
        
        try {
			JSONObject obj = ServerConnexion.sendRequest("Android/LastWinners", params);
			Integer nb = new Integer(obj.getString("results"));
			res = new ArrayList<HomeJackPotWinnerBean>(nb);
			//construction
			JSONArray jsonArray = obj.getJSONArray("rows");
			for (int i = 0; i < jsonArray.length(); i++) {
				HomeJackPotWinnerBean jpw = new HomeJackPotWinnerBean(jsonArray.getJSONObject(i));
				res.add(jpw);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbConnexion.close();
	}

}
