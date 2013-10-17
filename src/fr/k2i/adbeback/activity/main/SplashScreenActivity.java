package fr.k2i.adbeback.activity.main;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.brand.BrandBean;
import fr.k2i.adbeback.business.brand.ChanelBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.dao.ImageDao;
import fr.k2i.adbeback.dao.UserDao;
import fr.k2i.adbeback.database.DbConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class SplashScreenActivity extends Activity {
	
	//connexion à la base de données
	private DbConnexion dbc;

	//DAOs
	private UserDao userDao;
	private ImageDao imageDao;
	
	
	private class UpdateTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			updateDb();
			return null;
		}
		
	     protected void onPostExecute(Void result) {
			Intent mainIntent = new Intent(SplashScreenActivity.this,MainActivity.class); 
			SplashScreenActivity.this.startActivity(mainIntent); 
			SplashScreenActivity.this.finish(); 
	     }
	}
	
	
	@Override 
	public void onCreate(Bundle icicle) { 
		super.onCreate(icicle); 
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.loader);
		setProgressBarVisibility(true);
		new UpdateTask().execute();
//		new Handler().postDelayed(new Runnable(){ 
//			public void run() {
//				updateDb();
//				Intent mainIntent = new Intent(SplashScreenActivity.this,MainActivity.class); 
//				SplashScreenActivity.this.startActivity(mainIntent); 
//				SplashScreenActivity.this.finish(); 
//			} 
//		},1000);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbc.close();
	}
	
	
	private void updateDbChanels() throws ClientProtocolException, URISyntaxException, IOException, JSONException, LoginException{
        Map<String, String>params = new HashMap<String, String>();

		JSONObject obj = ServerConnexion.sendRequest("Android/Chanels/getChanels", params);
		Integer nb = new Integer(obj.getString("results"));
		List<ChanelBean> chanels = new ArrayList<ChanelBean>(nb);
		//construction
		JSONArray jsonArray = obj.getJSONArray("rows");
		for (int i = 0; i < jsonArray.length(); i++) {
			ChanelBean chanelBean = new ChanelBean(jsonArray.getJSONObject(i));
			chanels.add(chanelBean);
			if(imageDao.getBitmapByUrl(chanelBean.getLogoFile())==null){
				params.clear();
				params.put("url", chanelBean.getLogoFile());
	            imageDao.saveBitmap(chanelBean.getLogoFile(), ServerConnexion.getinputStream("Download",params));
			}
		}

	}
	
	private void updateDbBrands() throws ClientProtocolException, URISyntaxException, IOException, JSONException, LoginException{
        Map<String, String>params = new HashMap<String, String>();

        JSONObject obj = ServerConnexion.sendRequest("Android/UpdateDBImage", params);
		Integer nb = new Integer(obj.getString("results"));
		List<BrandBean> brands = new ArrayList<BrandBean>(nb);
		//construction
		JSONArray jsonArray = obj.getJSONArray("rows");
		for (int i = 0; i < jsonArray.length(); i++) {
			BrandBean brandBean = new BrandBean(jsonArray.getJSONObject(i));
			brands.add(brandBean);
			if(imageDao.getBitmapByUrl(brandBean.getLogoFile())==null){
				params.clear();
				params.put("url", brandBean.getLogoFile());
	            imageDao.saveBitmap(brandBean.getLogoFile(), ServerConnexion.getinputStream("Download",params));
			}
		}

	}
	
	
	/**
	 * Mise à jour de la base de données 
	 */
	private void updateDb() {
        try {
    		dbc = new DbConnexion(SplashScreenActivity.this);
    		userDao = new UserDao(dbc);
    		imageDao = new ImageDao(dbc);
    		ServerConnexion.userDao = userDao;

        	updateDbChanels();
        	updateDbBrands();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
