
package pivotsecurity.rest;

import pivotsecurity.rest.ApiBase;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class Account extends ApiBase {

	public Account(){}

	public Account (String public_key, String private_key){
	    super.setKey(private_key);
	}

    public JSONObject create(String uid, String email, String channel){
		if (null == uid) uid = "";
	    if (null == email) email = "";
	    String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"channle\":\""+ channel+ "\"}";
	    return (JSONObject)super.getResult("account/create", params);
    }

	public JSONArray info(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONArray)super.getResult("account/info", params);
	}

	public JSONObject getRiskscore(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONObject)super.getResult("account/riskscore", params);
	}
	public JSONObject setRiskscore(String uid, String email, String riskscore){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"riskscore\":\""+ riskscore+ "\"}";
		return (JSONObject)super.getResult("account/updateriskscore", params);
	}
	public JSONObject getQRCode(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONObject)super.getResult("account/qrocde", params);
	}
	public JSONObject getAuthCode(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONObject)super.getResult("account/authcode", params);
	}
	public JSONArray getLogs(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONArray)super.getResult("account/logs", params);
	}

	public JSONObject lockCustomer(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONObject)super.getResult("account/lock", params);
	}
	public JSONObject unlockCustomer(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return (JSONObject)super.getResult("account/unlock", params);
	}
	public JSONObject setTraingData(String uid, String email, String data){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"data\":\""+ data+ "\"}";
		return (JSONObject)super.getResult("account/trainml", params);
	}
	public JSONObject getTestData(String uid, String email, String data){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"data\":\""+ data+ "\"}";
		return (JSONObject)super.getResult("account/testml", params);
	}
	public JSONObject getAuthWithMetadata(String uid, String email, String metadata){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"metadata\":\""+ metadata+ "\"}";
		return (JSONObject)super.getResult("account/authwithmetadata", params);
	}
	public JSONObject sendAuthWithMetadata(String uid, String email, String metadata){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"metadata\":\""+ metadata+ "\"}";
		return (JSONObject)super.getResult("account/sendauthwithmetadata", params);
	}
	public JSONObject verifyAuthWithMetadata(String uid, String email, String code){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"code\":\""+ code+ "\"}";
		return (JSONObject)super.getResult("account/verifywithmetadata", params);
	}	
	public JSONObject verifySession(String uid, String email, String sessionid){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"sessionid\":\""+ sessionid+ "\"}";
		return (JSONObject)super.getResult("account/verifysession", params);
	}	
	
}
