package fr.k2i.adbeback.activity.win;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.drawable;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.WonObjectBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class WonObjectActivity extends ListActivity {
	private List<WonObjectBean> wonObjects;
//	private DbConnexion dbConnexion;
//	private ImageDao imageDao;
	private Bitmap logo;
	private LayoutInflater mInflater;
	
	private class WonObjectAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public WonObjectAdapter(Context context) {
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
			return wonObjects.size();
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
			return wonObjects.get(position);
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
                convertView = mInflater.inflate(R.layout.list_item_winobj, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.status = (TextView) convertView.findViewById(R.id.WonObjItemFielStatus);
                holder.value = (TextView) convertView.findViewById(R.id.WonObjItemFielValue);
                holder.date = (TextView) convertView.findViewById(R.id.WonObjItemFielDate);
                holder.logo = (ImageView) convertView.findViewById(R.id.WonObjItemLogo);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            	WonObjectBean objectBean = wonObjects.get(position);
            	
            	holder.date.setText(new SimpleDateFormat("MM/dd/yyyy").format(objectBean.getWinDate()));
            	holder.value.setText(objectBean.getValue().toString()+" euros");
            	holder.status.setText(objectBean.getStatus());
            	holder.logo.setImageBitmap(logo);

//            	holder.logo.setImageBitmap(imageDao.getBitmapByUrl(objectBean.getWinOn()));
            return convertView;
        }

		class ViewHolder {
			TextView date;
			TextView value;
			TextView status;
			ImageView logo;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logo = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
		getListView().setFastScrollEnabled(true);
//		dbConnexion = new DbConnexion(this);
//		imageDao = new ImageDao(dbConnexion.getReadableDatabase());
		mInflater = LayoutInflater.from(this);
		try {
			getWonObjects();
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		setListAdapter(new WonObjectAdapter(this));
	}

	private void getWonObjects() throws ClientProtocolException, URISyntaxException, IOException, JSONException, ParseException, LoginException {
        Map<String, String>params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("limit", "0");
		JSONObject obj = ServerConnexion.sendRequest("getWonObjects", params);
		Integer nb = new Integer(obj.getString("results"));
		wonObjects = new ArrayList<WonObjectBean>(nb);
		//construction
		JSONArray jsonArray = obj.getJSONArray("rows");
		for (int i = 0; i < jsonArray.length(); i++) {
			WonObjectBean won = new WonObjectBean(jsonArray.getJSONObject(i));
			wonObjects.add(won);
		}		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		WonObjectBean wonObjectBean = wonObjects.get(position);
		//creation dialog
		AlertDialog.Builder builder = new  AlertDialog.Builder(WonObjectActivity.this);
		builder.setTitle("Votre gain");
		
		View inflate = mInflater.inflate(R.layout.dialog_won_obj, null);

		TextView date = (TextView) inflate.findViewById(R.id.DialogWonObjItemFielDate);
		TextView desc = (TextView) inflate.findViewById(R.id.DialogWonObjItemFielDesc);
		TextView status = (TextView) inflate.findViewById(R.id.DialogWonObjItemFielStatus);
		TextView value = (TextView) inflate.findViewById(R.id.DialogWonObjItemFielValue);
		TextView name = (TextView) inflate.findViewById(R.id.DialogWonObjItemFielName);
		
		ImageView logo = (ImageView) inflate.findViewById(R.id.DialogWonObjItemLogo);
		
		
		date.setText(new SimpleDateFormat("MM/dd/yyyy").format(wonObjectBean.getWinDate()));
		desc.setText(wonObjectBean.getDescription());
		status.setText(wonObjectBean.getStatus());
		value.setText(wonObjectBean.getValue().toString());
		name.setText(wonObjectBean.getName());
		
		Map<String, String>paramsDownload = new HashMap<String, String>();
		paramsDownload.put("url", wonObjectBean.getObjUrlPhoto());
        Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(ServerConnexion.getinputStream("Download",paramsDownload));
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
		logo.setImageBitmap(bm);
		builder.setView(inflate);
		builder.setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.create().show();
	}

}
