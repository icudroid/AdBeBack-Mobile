package fr.k2i.adbeback.activity.register;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import fr.k2i.adbeback.R;
import fr.k2i.adbeback.R.id;
import fr.k2i.adbeback.R.layout;
import fr.k2i.adbeback.business.user.CountryBean;
import fr.k2i.adbeback.business.user.ErrorBean;
import fr.k2i.adbeback.business.user.SexBean;
import fr.k2i.adbeback.business.user.UserBean;
import fr.k2i.adbeback.connexion.ServerConnexion;
import fr.k2i.adbeback.exception.LoginException;

public class RegisterActivity extends Activity {
    private Map<String, ImageView> showWarning;
    private Spinner sexes;
    private Spinner countries;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        //rechar
        
        showWarning = new HashMap<String, ImageView>();
        showWarning.put("address1", (ImageView) findViewById(R.id.RegisterErrAdd1));
        showWarning.put("birthday", (ImageView) findViewById(R.id.RegisterErrBirthDay));
        showWarning.put("city", (ImageView) findViewById(R.id.RegisterErrCity));
        showWarning.put("email", (ImageView) findViewById(R.id.RegisterErrEmail));
        showWarning.put("firstName", (ImageView) findViewById(R.id.RegisterErrFirstName));
        showWarning.put("lastName", (ImageView) findViewById(R.id.RegisterErrLastName));
        showWarning.put("pseudo", (ImageView) findViewById(R.id.RegisterErrPseudo));
        showWarning.put("password", (ImageView) findViewById(R.id.RegisterErrPwd));
        showWarning.put("zipCode", (ImageView) findViewById(R.id.RegisterErrZipCode));

        dropWarnings();
        
        sexes =(Spinner)findViewById(R.id.RegisterSexSpinner);
        ArrayAdapter<SexBean> sa = new ArrayAdapter<SexBean>(this, android.R.layout.simple_spinner_item);
        sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<SexBean> allSexes = getAllSexes();
        for (SexBean sexBean : allSexes) {
        	sa.add(sexBean);
		}
        sexes.setAdapter(sa);
        
        countries =(Spinner)findViewById(R.id.RegisterCountrySpinner);
        
        ArrayAdapter<CountryBean> ca = new ArrayAdapter<CountryBean>(this, android.R.layout.simple_spinner_item);
        ca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<CountryBean> allCountries = getAllCountries();
        for (CountryBean country : allCountries) {
        	ca.add(country);
		}
        countries.setAdapter(ca);

        
        Button button = (Button) findViewById(R.id.RegisterBtn);
        button.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				dropWarnings();
				//validation de l'inscription
		        Map<String, String>params = new HashMap<String, String>();
		        params.put("pseudo", ((TextView)findViewById(R.id.RegisterTxtPseudo)).getText().toString());
		        params.put("sex", sexes.getSelectedItem().toString());
		        params.put("firstName", ((TextView)findViewById(R.id.RegisterFirstNameTxt)).getText().toString());
		        params.put("lastName", ((TextView)findViewById(R.id.RegisterLastNameTxt)).getText().toString());
		        params.put("email", ((TextView)findViewById(R.id.RegisterEmailTxt)).getText().toString());
		        params.put("address1", ((TextView)findViewById(R.id.RegisterAdd1Txt)).getText().toString());
		        params.put("address2", ((TextView)findViewById(R.id.RegisterAdd2Txt)).getText().toString());
		        params.put("zipCode", ((TextView)findViewById(R.id.RegisterZipCodeTxt)).getText().toString());
		        params.put("city", ((TextView)findViewById(R.id.RegisterCityTxt)).getText().toString());
		        params.put("country", countries.getSelectedItem().toString());
		        DatePicker bd = (DatePicker)findViewById(R.id.RegisterBirthday);
		        Calendar cal = new GregorianCalendar(bd.getYear(),bd.getMonth()-1,bd.getDayOfMonth());
		        params.put("birthday", new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime()));
		        params.put("password", ((TextView)findViewById(R.id.RegisterPwdTxt)).getText().toString());
		        
				try {
					JSONObject obj = ServerConnexion.sendRequest("Android/CreateAcount/createAccount", params);
					Integer nb = new Integer(obj.getString("results"));
					if(nb==0){
						//ouvrir la fenêtre pour dire que le compte est créer
						AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
						builder.setTitle("Votre inscription");
						builder.setMessage("Merci pour votre inscription, vous allez recevoir un email de confirmation.");
						AlertDialog alert = builder.create();
						alert.show();
					}else{
						//construction
						JSONArray jsonArray = obj.getJSONArray("rows");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							final ErrorBean err = new ErrorBean(jsonObject);
							ImageView warn = showWarning.get(err.getField());
							warn.setVisibility(View.VISIBLE);
							warn.setClickable(true);
							warn.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
									builder.setTitle("Erreur lors de l'inscription");
									builder.setMessage(err.getMessage());
									builder.setPositiveButton("Retour", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									});
									AlertDialog alert = builder.create();
									alert.show();
									
									Toast.makeText(RegisterActivity.this, err.getMessage(), 3);
								}
							});
						}

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
		});
    }
	
	
	
    private void dropWarnings() {
        //rendre invisible tous les messages d'erreurs
        Collection<ImageView> values = showWarning.values();
        for (ImageView imageView : values) {
			imageView.setVisibility(View.GONE);
		}		
	}



	private List<CountryBean> getAllCountries() {
    	List<CountryBean> res = null;
        Map<String, String>params = new HashMap<String, String>();
        try {
			JSONObject obj = ServerConnexion.sendRequest("Android/CreateAcount/getCountries", params);
			Integer nb = new Integer(obj.getString("results"));
			
			res = new ArrayList<CountryBean>(nb);
			//construction
			JSONArray jsonArray = obj.getJSONArray("rows");
			for (int i = 0; i < jsonArray.length(); i++) {
				CountryBean country = new CountryBean(jsonArray.getJSONObject(i));
				res.add(country);
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



	private List<SexBean> getAllSexes() {
    	List<SexBean> res = null;
        Map<String, String>params = new HashMap<String, String>();
        params.put("getSexes", "");

        try {
			JSONObject obj = ServerConnexion.sendRequest("Android/CreateAcount/getSexes", params);
			Integer nb = new Integer(obj.getString("results"));
			
			res = new ArrayList<SexBean>(nb);
			//construction
			JSONArray jsonArray = obj.getJSONArray("rows");
			for (int i = 0; i < jsonArray.length(); i++) {
				SexBean sexBean = new SexBean(jsonArray.getJSONObject(i));
				res.add(sexBean);
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


}


