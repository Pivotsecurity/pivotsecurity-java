
package pivotsecurity.rest.client;

import java.io.*;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pivotsecurity.rest.Account;
import pivotsecurity.rest.Customer;

import static org.junit.Assert.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class PivotSecurityClientTest {

    private static Logger LOG = LoggerFactory.getLogger(RestClientTest.class);
	private Account account = null;
	private Customer customer = null;


    public PivotSecurityClientTest() {
        super();
        this.account = new Account("","");
        this.customer = new Customer("","");
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() {
    }

    @Test
    public void infoTest() {
        JSONArray result = this.account.info("A13","");
       	assertEquals(result.getJSONObject(0).getString("uid"), "A13");
    }

    @Test
    public void authCodeTest() {
        JSONObject result = this.customer.getAuthCode("A13","");
       	assertEquals(result.getString("code"), "123123");
    }

    @Test
    public void verifyTest() {
        JSONObject result = this.customer.verify("A13","", "123123");
       	assertEquals(result.getString("status"), "success");
    }

}
