package fr.k2i.adbeback.activity.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.jackpot.ClassementBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class ResultActivity extends Activity {
	private ClassementBean resultBean;
	private TextView classement;
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, new Intent());
		finish();
	}

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        
        //obtenir le information du résultat
        Bundle objetbunble  = this.getIntent().getExtras(); 
        Long idJackPot = objetbunble .getLong("idJackPot");

        Map<String, String>params = new HashMap<String, String>();
        params.put("getResultPage", "");
        params.put("idJackPot", idJackPot.toString());
        
        try {
			JSONObject obj = ServerConnexion.sendRequest("Android/TVJackPot/getResultPage", params);
			resultBean = new ClassementBean(obj);
			
			classement=(TextView)findViewById(R.id.TextClassement);
			
			TextView scoreTxt = (TextView)findViewById(R.id.TextScore);
			DecimalFormat df = new DecimalFormat("#.###");
			scoreTxt.setText(df.format(resultBean.getScore()));
			
			classement.setText(resultBean.getClassement()+" / "+resultBean.getNbPlayer());
			
			RelativeLayout lot = (RelativeLayout) findViewById(R.id.LayoutLot);
			TextView lost = (TextView) findViewById(R.id.TextLost);
			
			if(resultBean.getLot()!= null){
				lot.setVisibility(View.VISIBLE);
				lost.setVisibility(View.INVISIBLE);
				ImageView imgLot = (ImageView) findViewById(R.id.ImageLot);
				//imgLot.setScaleType(ImageView.ScaleType.FIT_CENTER);
				//imgLot.setLayoutParams(new GridView.LayoutParams(80, 80));
				
				//chargement de l'image
				Map<String, String>paramsDownload = new HashMap<String, String>();
				paramsDownload.put("url", resultBean.getLot().getPhotoUrl());
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
				}
				imgLot.setImageBitmap(bm);

				TextView lotname = (TextView) findViewById(R.id.TextLotName);
				lotname.setText(resultBean.getLot().getName());

				TextView description = (TextView) findViewById(R.id.TextDescription);
				description.setText(resultBean.getLot().getDescription());
				
				Button btnJp = (Button) findViewById(R.id.PlayJackPotBtn);
					if(resultBean.isShowJackPotChanel()){
						TextView jpValue = (TextView) findViewById(R.id.TextJackpotValue);
						jpValue.setText(resultBean.getJackPotValue()+" "+resultBean.getCurrency().replace("&euro;", "€"));
						btnJp.setVisibility(View.VISIBLE);
						btnJp.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								//affichage de la fenêtre pour le jackpot
								//idJackPot
								v.setOnClickListener(null);
								v.setVisibility(View.INVISIBLE);
								
								Intent intent = new Intent();
								intent.putExtra("idJackPot", resultBean.getIdJackPot());
								setResult(RESULT_OK, intent);
								
								finish();
								
							}
						});
					}else{
						btnJp.setVisibility(View.INVISIBLE);
						findViewById(R.id.TextJackpotValue).setVisibility(View.INVISIBLE);
						findViewById(R.id.LabelJackPot).setVisibility(View.INVISIBLE);
					}
				
			}else{
				lost.setVisibility(View.VISIBLE);
				lot.setVisibility(View.INVISIBLE);
				findViewById(R.id.TextJackpotValue).setVisibility(View.INVISIBLE);
				findViewById(R.id.LabelJackPot).setVisibility(View.INVISIBLE);
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
        
	}
}
