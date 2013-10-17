package fr.k2i.adbeback.connexion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.k2i.adbeback.business.user.UserBean;
import fr.k2i.adbeback.dao.UserDao;
import fr.k2i.adbeback.exception.LoginException;

public class ServerConnexion {
	public static String pseudo = null;
	
	private static String sessionId = null;

	//	public static String baseUrl = "http://163.104.30.71:8080/AdnJoy/";
	 public static String baseUrl = "http://192.168.0.12:8080/Adbeback_B2C/";
//	public static String baseUrl = "http://www.d-kahn.net/AdnJoy/";

	 
	public static UserDao userDao;

	
	
	public static JSONObject sendJsonRequest(String req,
			Object data) throws URISyntaxException,
			ClientProtocolException, IOException, JSONException, LoginException {
		Reader reader = new InputStreamReader(getJsonInputStream(req, data),"UTF-8");

		StringBuilder sb = new StringBuilder();
		char[] bts = new char[1024*8];

		int numChars;
		while ((numChars = reader.read(bts, 0, bts.length)) > 0) {
			sb.append(bts, 0, numChars);
		}
		reader.close();
		if (sb.length() == 0)
			return null;
		JSONObject o = new JSONObject(sb.toString());
		try{
			if(o.getInt("failed")==0){
				throw new LoginException();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return o;
	}
	
	public static JSONObject sendRequest(String req,
			Map<String, String> params) throws URISyntaxException,
			ClientProtocolException, IOException, JSONException, LoginException {
		Reader reader = new InputStreamReader(getinputStream(req, params),"UTF-8");

		StringBuilder sb = new StringBuilder();
		char[] bts = new char[1024*8];

		int numChars;
		while ((numChars = reader.read(bts, 0, bts.length)) > 0) {
			sb.append(bts, 0, numChars);
		}
		reader.close();
		if (sb.length() == 0)
			return null;
		JSONObject o = new JSONObject(sb.toString());
		try{
			if(o.getInt("failed")==0){
				throw new LoginException();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return o;
	}
	
	
	
	public static InputStream getJsonInputStream(String req,
			Object data) throws URISyntaxException,
			ClientProtocolException, IOException, JSONException, LoginException {
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(baseUrl + req);
		  
		if (sessionId != null) {
			request.addHeader("Cookie", "JSESSIONID=" + sessionId);
		}
		request.addHeader("Content-Type","application/json; charset=UTF-8");

		/*		$.postJSON = function(url, data, callback) {
	    return jQuery.ajax({
	        'type': 'POST',
	        'url': url,
	        'contentType': 'application/json',
	        'data': JSON.stringify(data),
	        'dataType': 'json',
	        'success': callback
	    });
	};*/
		
		String json = new JSONObject(data).toString();
		request.setEntity(new StringEntity(json,"UTF-8"));
		HttpResponse execute = client.execute(request);
		
		Header[] headers = execute.getHeaders("Set-Cookie");

		for (Header header : headers) {
			StringTokenizer st = new StringTokenizer(header.getValue(), ";");
			while (st.hasMoreTokens()) {
				String nextToken = st.nextToken();
				if (nextToken.startsWith("JSESSIONID=")) {
					String old = nextToken.substring("JSESSIONID=".length());
					if(!old.equals(sessionId)){
						sessionId = nextToken.substring("JSESSIONID=".length());
						//déconnecter
						UserBean defaultUser = userDao.getDefaultUser();
						//Connexion automatique de l'utiliseur
						if(defaultUser!=null){
							onConnexion(defaultUser.getPseudo(), defaultUser.getPwd(), defaultUser.getDefaultProfile());
						}
					}
				}
			}
		}

		InputStream content = execute.getEntity().getContent();
//		client.getConnectionManager().shutdown(); 
		return content;
	}

	
	public static InputStream getinputStream(String req,
			Map<String, String> params) throws URISyntaxException,
			ClientProtocolException, IOException, JSONException, LoginException {
		
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(baseUrl + req);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
		  
		if (sessionId != null) {
			request.addHeader("Cookie", "JSESSIONID=" + sessionId);
		}
		request.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		
		for (java.util.Map.Entry<String, String> param : params.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));	
		}
		
		
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
		HttpResponse execute = client.execute(request);
		
		Header[] headers = execute.getHeaders("Set-Cookie");

		for (Header header : headers) {
			StringTokenizer st = new StringTokenizer(header.getValue(), ";");
			while (st.hasMoreTokens()) {
				String nextToken = st.nextToken();
				if (nextToken.startsWith("JSESSIONID=")) {
					String old = nextToken.substring("JSESSIONID=".length());
					if(!old.equals(sessionId)){
						sessionId = nextToken.substring("JSESSIONID=".length());
						//déconnecter
						UserBean defaultUser = userDao.getDefaultUser();
						//Connexion automatique de l'utiliseur
						if(defaultUser!=null){
							onConnexion(defaultUser.getPseudo(), defaultUser.getPwd(), defaultUser.getDefaultProfile());
						}
					}
				}
			}
		}

		InputStream content = execute.getEntity().getContent();
//		client.getConnectionManager().shutdown(); 
		return content;
	}
	
	
	protected static void onConnexion(String pseudo, String pwd, boolean autoConnect) throws LoginException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pseudo", pseudo);
		params.put("pwd", pwd);
		try {
			ServerConnexion.sendRequest("Android/Connexion/login",params);
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
	
	}
	
	
}
