package pivotsecurity.rest;

import org.apache.commons.httpclient.*;
import pivotsecurity.rest.client.*;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public abstract class ApiBase {

	private RestClient client = null;
	private String KEY = null;
	private String authHeader = null;

	/**
	 * Sets the Private/public Key.
	 * @param key a string with the private key.
	 */
	protected void setKey(String key){
		this.KEY = key;
	}

	/**
	 * Retrieves the previously set private/public key.
	 *
	 * @return the private key
	 */
	protected String getKey(){
		return this.KEY;
	}

	protected RestClient buildClient(){

		if (null == this.client){
	    	HttpClient httpClient = new HttpClient();
			this.client = new RestClientImpl(httpClient);
			this.client.setBaseUrl("https://api.pivotsecurity.com/api/");

			String auth = this.KEY + ":";
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(java.nio.charset.Charset.forName("US-ASCII")));
			this.authHeader = "Basic " + new String(encodedAuth);
    	}
    	
    	return client;
    }

	protected JSONObject getResult(String op, String reqbody){
    	RestClient client = this.buildClient();
		RestRequest request = new RestRequest().setMethod(RestRequest.Method.Post);
		request.addHeader("Accept", "application/json");
		request.setResource(op);
    	request.setBody(reqbody);
		request.addHeader("Authorization", this.authHeader);
		RestResponse response = client.execute(request);
    	String body = response.getBody();

    	try{
    		return new JSONObject(body);
		}catch(org.json.JSONException e){
    		// NOT JSON return just body
		}
    	return  new JSONObject("{\"result\":" + body + "}");
	}


}