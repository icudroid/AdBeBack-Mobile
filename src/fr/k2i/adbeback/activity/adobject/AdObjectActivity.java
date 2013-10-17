package fr.k2i.adbeback.activity.adobject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.business.AdObjectBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class AdObjectActivity extends Activity {

	private  AdObjectBean obj = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_object);

		long idChanel = 0;
		
		getObjectAd(idChanel);
		
		
		((TextView)findViewById(R.id.AdObjectTitle)).setText(obj.getName());
		((TextView)findViewById(R.id.AdObjectDesc)).setText(obj.getDescription());
		
		Gallery gallery = (Gallery) findViewById(R.id.galleryAdObject);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				//show fullScreen Img
			}
		});
	}

	
	
	private void getObjectAd(long idChanel) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("idChanel", new Long(idChanel).toString());
		try {
			JSONObject jsonObj = ServerConnexion.sendRequest(
					"Android/TVJackPot/getObjectAd", params);
			obj = new AdObjectBean(jsonObj);
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



	private class GetImgTask extends AsyncTask<String, Void, Bitmap> {
		protected ImageView img;
		
		public GetImgTask(ImageView img){
			this.img = img;
		}
		
		@Override
		protected Bitmap doInBackground(String... imgrUrl) {
            Bitmap bm = null;
			try {
				Map<String, String>paramsDownload = new HashMap<String, String>();
				paramsDownload.put("url", imgrUrl[0]);
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
	    		 img.setImageBitmap(bm);
	    	 }
	     }
	}
	
	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return obj.getImgFile().size();
		}

		public Object getItem(int position) {
			return obj.getImgFile().get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gallery_object_ad, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.ImgVxObjAd);
                holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
    			holder.img.setLayoutParams(new Gallery.LayoutParams(250, 200));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
        	new GetImgTask(holder.img).execute(obj.getImgFile().get(position));
            return convertView;
        }

		class ViewHolder {
			ImageView img;
		}
	}
	

}
