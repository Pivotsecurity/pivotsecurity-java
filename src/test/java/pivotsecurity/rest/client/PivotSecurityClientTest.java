
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

public class PivotSecurityClientTest {

    private static Logger LOG = LoggerFactory.getLogger(RestClientTest.class);


    public PivotSecurityClientTest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testURL() throws Exception {


    }

    @Test
    public void infoTest() {
        Account account = new Account("<Public_Key>","<Private_key>");
        account.info("A13","");
        //assertEquals("HEAD", "");
    }

    @Test
    public void authCodeTest() {
        Customer customer = new Customer("<Public_Key>","<Private_key>");
        customer.getAuthCode("A13","");
        //assertEquals("HEAD", "");
    }


}
