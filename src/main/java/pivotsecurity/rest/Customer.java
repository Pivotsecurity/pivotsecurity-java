
package pivotsecurity.rest;

import pivotsecurity.rest.ApiBase;
import org.json.JSONObject;

public class Customer extends ApiBase {

    public Customer(){}

    public Customer (String public_key, String private_key){
        super.setKey(public_key);
    }

    public JSONObject getAuthCode(String uid, String email){
        if (null == uid) uid = "";
        if (null == email) email = "";
        String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\"}";
        return super.getResult("customer/auth", params);
    }

    public JSONObject verify(String uid, String email, String code){
        if (null == uid) uid = "";
        if (null == email) email = "";
        String params = "{\"uid\":\"" + uid + "\",\"email\":\""+ email + "\",\"code\":\""+ code+ "\"}";
        return super.getResult("customer/verify", params);
    }

}
