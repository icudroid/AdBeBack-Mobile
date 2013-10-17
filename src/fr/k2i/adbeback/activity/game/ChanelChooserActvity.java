package fr.k2i.adbeback.activity.game;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.brand.ChanelBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.dao.ImageDao;
import fr.k2i.adbeback.database.DbConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class ChanelChooserActvity extends Activity {

	private GridView mGrid;
	private List<ChanelBean> chanels;
	private DbConnexion dbConnexion;
	private ImageDao imageDao;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbConnexion.close();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			dbConnexion = new DbConnexion(this);
			imageDao = new ImageDao(dbConnexion);

			Map<String, String> params = new HashMap<String, String>();
			params.put("filterChanel", "");
			
			try {
				JSONObject obj = ServerConnexion.sendRequest(
						"Android/Chanels/getChanels", params);
				Integer nb = new Integer(obj.getString("results"));
				chanels = new ArrayList<ChanelBean>(nb);
				// construction
				JSONArray jsonArray = obj.getJSONArray("rows");
				for (int i = 0; i < jsonArray.length(); i++) {
					ChanelBean chanelBean = new ChanelBean(jsonArray
							.getJSONObject(i));
					chanels.add(chanelBean);
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

			setContentView(R.layout.chanelchooser);
			mGrid = (GridView) findViewById(R.id.chanelChooserGrid);
			mGrid.setAdapter(new ChanelsAdapter());
			mGrid.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ChanelBean chanelBean = chanels.get(position);
					// ouvrir la fenetre de jeux
					Intent intent = new Intent();
					intent.putExtra("chanelId", chanelBean.getId());

					setResult(RESULT_OK, intent);
					ChanelChooserActvity.this.finish();
				}

			});
	}

	public class ChanelsAdapter extends BaseAdapter {
		public ChanelsAdapter() {
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i;

			if (convertView == null) {
				i = new ImageView(ChanelChooserActvity.this);
				i.setScaleType(ImageView.ScaleType.FIT_CENTER);
				i.setLayoutParams(new GridView.LayoutParams(70, 70));
			} else {
				i = (ImageView) convertView;
			}

			ChanelBean chanel = chanels.get(position);
			i.setImageBitmap(imageDao.getBitmapByUrl(chanel.getLogoFile()));
			return i;
		}

		public final int getCount() {
			return chanels.size();
		}

		public final Object getItem(int position) {
			return chanels.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}
	}
}
