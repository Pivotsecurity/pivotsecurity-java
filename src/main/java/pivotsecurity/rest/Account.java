
package pivotsecurity.rest;

import pivotsecurity.rest.ApiBase;
import org.json.JSONObject;

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
	    return super.getResult("account/create", params);
    }

	public JSONObject info(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/info", params);
	}

	public JSONObject getRiskscore(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/riskscore", params);
	}
	public JSONObject setRiskscore(String uid, String email, String riskscore){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"riskscore\":\""+ riskscore+ "\"}";
		return super.getResult("account/updateriskscore", params);
	}
	public JSONObject getQRCode(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/qrocde", params);
	}
	public JSONObject getAuthCode(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/authcode", params);
	}
	public JSONObject getLogs(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/logs", params);
	}

	public JSONObject lockCustomer(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/lock", params);
	}
	public JSONObject unlockCustomer(String uid, String email){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
		return super.getResult("account/unlock", params);
	}
	public JSONObject setTraingData(String uid, String email, String data){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"data\":\""+ data+ "\"}";
		return super.getResult("account/trainml", params);
	}
	public JSONObject getTestData(String uid, String email, String data){
		if (null == uid) uid = "";
		if (null == email) email = "";
		String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"data\":\""+ data+ "\"}";
		return super.getResult("account/testml", params);
	}
}
