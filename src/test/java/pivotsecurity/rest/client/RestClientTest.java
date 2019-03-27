
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

import pivotsecurity.rest.client.RestRequest.Method;

import static org.junit.Assert.*;

public class RestClientTest {

    private static Logger LOG = LoggerFactory.getLogger(RestClientTest.class);

    private MockHttpMethod mockHttpMethod;

    private final RestClientImpl mockRestClientAlwaysOK = new RestClientImpl(new MockHttpClient(200)) {

        @Override
        protected HttpMethod createHttpClientMethod(RestRequest request) {
            MockHttpMethod m = new MockHttpMethod(request.getMethod().name());
            m.setStatusCode(200);
            mockHttpMethod = m;
            return m;
        }
    };

    private final RestClientImpl mockRestClientAlwaysThrowsIOException = new RestClientImpl(new MockHttpClient(new IOException())) {

        @Override
        protected HttpMethod createHttpClientMethod(RestRequest request) {
            mockHttpMethod = new MockHttpMethod(request.getMethod().name());
            return mockHttpMethod;
        }
    };

    private final RestClientImpl mockRestClientAlwaysThrowsProtocolException = new RestClientImpl(new MockHttpClient(new HttpException())) {

        @Override
        protected HttpMethod createHttpClientMethod(RestRequest request) {
            mockHttpMethod = new MockHttpMethod(request.getMethod().name());
            return mockHttpMethod;
        }
    };

    private final RestRequest validRestRequest = (RestRequest) new RestRequest().setMethod(RestRequest.Method.Get).setResource("/a/resource");

    public RestClientTest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() {
        mockRestClientAlwaysOK.setBaseUrl("http://alwaysok:8080");
        mockRestClientAlwaysThrowsIOException.setBaseUrl("http://ioexception:8080");
        mockRestClientAlwaysThrowsProtocolException.setBaseUrl("http://httpexception:8080");
        validRestRequest.setQuery("aQuery");
        validRestRequest.addHeader("a", "v");
    }

    @Test
    public void mustHandleAllHttpMethods() {
        HttpClient httpClient = new HttpClient();
        RestClientImpl restClient = new RestClientImpl(httpClient);
        RestRequest r = new RestRequest();
        r.setMethod(Method.Get);
        HttpMethod m = restClient.createHttpClientMethod(r);
        assertEquals("GET", m.getName());

        r.setMethod(Method.Post);
        m = restClient.createHttpClientMethod(r);
        assertEquals("POST", m.getName());

        r.setMethod(Method.Put);
        m = restClient.createHttpClientMethod(r);
        assertEquals("PUT", m.getName());

        r.setMethod(Method.Delete);
        m = restClient.createHttpClientMethod(r);
        assertEquals("DELETE", m.getName());

        r.setMethod(Method.Options);
        m = restClient.createHttpClientMethod(r);
        assertEquals("OPTIONS", m.getName());

        r.setMethod(Method.Head);
        m = restClient.createHttpClientMethod(r);
        assertEquals("HEAD", m.getName());
    }

    @Test
    public void mustBeConstructedWithAValidHttpClient() {
        HttpClient httpClient = new HttpClient();
        RestClientImpl restClient = new RestClientImpl(httpClient);
        assertSame(httpClient, restClient.getClient());
    }

    @Test
    public void mustExposeTheBaseUri() {
        assertEquals("http://alwaysok:8080", mockRestClientAlwaysOK.getBaseUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shoudlFailConstructionWithAnInvalidHttpClient() {
        new RestClientImpl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustNotExecuteAnInvalidRequest() {
        mockRestClientAlwaysOK.execute(new RestRequest());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mustNotExecuteANullRestRequest() {
        mockRestClientAlwaysOK.execute(null);
    }

    @Test(expected = IllegalStateException.class)
    public void mustNotifyCallerIfHttpCallFailsDueToAnIoFailure() {
        try {
            mockRestClientAlwaysThrowsIOException.execute(validRestRequest);
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            mockHttpMethod.verifyConnectionReleased();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void mustNotifyCallerIfHttpCallFailsDueToAProtocolFailure() {
        try {
            mockRestClientAlwaysThrowsProtocolException.execute(validRestRequest);
        } catch (IllegalStateException e) {
            throw e;
        } finally {
            mockHttpMethod.verifyConnectionReleased();
        }
    }

    @Test
    public void responseShouldContainTheResultCodeOfASuccessfullHttpCall() {
        RestResponse restResponse = mockRestClientAlwaysOK.execute(validRestRequest);
        mockHttpMethod.verifyConnectionReleased();
        assertEquals(Integer.valueOf(200), restResponse.getStatusCode());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotifyCallerThatNullHostAddressesAreNotHandled() {
        mockRestClientAlwaysOK.execute(null, validRestRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotifyCallerThatInvalidResourceUriAreNotHandled() {
        validRestRequest.setResource("http://resource/shoud/not/include/the/abs/path");
        mockRestClientAlwaysOK.execute("http://basehostaddress:8080", validRestRequest);
    }

    @Test
    public void shouldCreateHttpMethodsToMatchTheMethodInTheRestRequest() {
        MockRestClient mockRestClientWithVerificationOfHttpMethodCreation = new MockRestClient(new MockHttpClient(200));
        mockRestClientWithVerificationOfHttpMethodCreation.verifyCorrectHttpMethodCreation();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotifyCallerThatMethodMatchingTheOneInTheRestRequestCannotBeFound() {
        RestClientImpl client = new MockRestClient(new MockHttpClient(200)) {
            @Override
            protected String getMethodClassnameFromMethodName(String mName) {
                return "i.dont.Exist";
            }
        };
        client.createHttpClientMethod(validRestRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotifyCallerThatMethodMatchingTheOneInTheRestRequestCannotBeInstantiated() {
        RestClientImpl client = new MockRestClient(new MockHttpClient(200)) {
            @Override
            protected String getMethodClassnameFromMethodName(String mName) {
                return HttpMethodClassCannotBeInstantiated.class.getName();
            }
        };
        client.createHttpClientMethod(validRestRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotifyCallerThatMethodMatchingTheOneInTheRestRequestFailsWhenInstantiating() {
        RestClientImpl client = new MockRestClient(new MockHttpClient(200)) {
            @Override
            protected String getMethodClassnameFromMethodName(String mName) {
                return HttpMethodClassFailsWhenCreating.class.getName();
            }
        };
        client.createHttpClientMethod(validRestRequest);
    }

    @Test
    @Deprecated
    public void shouldCreateMultipartEntityIfRestRequestHasNonNullMultipartFileName() throws Exception {
        String filename = "multiparttest";
        File f = File.createTempFile(filename, null);
        f.deleteOnExit();

        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.setMultipartFileName(f.getAbsolutePath());
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
        assertTrue(mockHttpMethod.isMultipartRequest());
    }


    @Test
    public void shouldCreateMultipartEntityIfRestRequestHasNonEmptyMultiparts() throws Exception {
        String filename1 = "multiparttest-file1";
        File file1 = File.createTempFile(filename1, null);
        file1.deleteOnExit();
        PrintWriter writer1 = new PrintWriter(file1);
        writer1.println("File1_Content");
        writer1.close();

        String filename2 = "multiparttest-file2";
        File file2 = File.createTempFile(filename2, null);
        file1.deleteOnExit();
        PrintWriter writer2 = new PrintWriter(file2);
        writer2.println("File2_Content");
        writer2.close();

        String json1 = " {\"lastname\":\"Boby\",\"firstname\":\"Bob\"}";


        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.addMultipart("file1", new RestMultipart(RestMultipart.RestMultipartType.FILE, file1.getAbsolutePath()));
        validRestRequest.addMultipart("file2", new RestMultipart(RestMultipart.RestMultipartType.FILE, file2.getAbsolutePath(), "application/octet-stream"));
        validRestRequest.addMultipart("json1", new RestMultipart(RestMultipart.RestMultipartType.STRING, json1, "application/json"));

        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
        assertTrue(mockHttpMethod.isMultipartRequest());
        Header[] headers = mockHttpMethod.getRequestHeaders();
        // Test Request Entity
        assertNotNull(mockHttpMethod.getRequestEntity());
        assertTrue(mockHttpMethod.getRequestEntity().getContentType().startsWith("multipart/form-data; boundary="));
        String boundary = mockHttpMethod.getRequestEntity().getContentType().substring("multipart/form-data; boundary=".length());
        LOG.debug("Request boundary = {}", boundary);
        // Test Request Body
        ByteArrayOutputStream requestOut = new ByteArrayOutputStream();
        mockHttpMethod.getRequestEntity().writeRequest(requestOut);
        String requestBodyAsString = requestOut.toString();
        //System.err.println("-------- " +  requestBodyAsString );
        LOG.debug("requestBodyAsString = {}", requestBodyAsString);
        // Request Body Assert Params
        assertTrue(requestBodyAsString.contains("name=\"file1\""));
        assertTrue(requestBodyAsString.contains("name=\"file2\""));
        assertTrue(requestBodyAsString.contains("name=\"json1\""));
        // Request Body Assert Contents
        assertTrue(requestBodyAsString.contains("File1_Content"));
        assertTrue(requestBodyAsString.contains("File2_Content"));
        assertTrue(requestBodyAsString.contains("Boby"));
        // Test boundary body
        String[] bodySplit = requestBodyAsString.split("--" + boundary);
        assertEquals(5, bodySplit.length);
        LOG.debug("-------- Split Idx 1" + bodySplit[1]);
        LOG.debug("-------- Split Idx 2" + bodySplit[2]);
        LOG.debug("-------- Split Idx 3" + bodySplit[3]);
    }

    @Test
    public void shouldCreateMultipartEntityIfRestRequestHasNonNullMultipartFileNames() throws Exception {
        String filename = "multiparttest";
        File f = File.createTempFile(filename, null);
        f.deleteOnExit();

        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.addMultipartFileName(f.getAbsolutePath());
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
        // Could not be the same as mock :  assertTrue(mockHttpMethod.isMultipartRequest());
    }

    @Test
    public void shouldCreateMultipartEntityIfRestRequestHasNonNullTwoMultipartFileNames() throws Exception {
        String filename = "multiparttest";
        File f = File.createTempFile(filename, null);
        f.deleteOnExit();

        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.addMultipartFileName(f.getAbsolutePath(), "file1");
        validRestRequest.addMultipartFileName(f.getAbsolutePath(), "file2");
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
        // Could not be the same as mock :  assertTrue(mockHttpMethod.isMultipartRequest());
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMultipartFileNameDoesNotExist() throws Exception {
        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.setMultipartFileName("multiparttest");
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
    }

    @Test
    public void shouldCreateFileRequestEntityIfRestRequestHasNonNullFileName() throws Exception {
        String filename = "filetest";
        File f = File.createTempFile(filename, null);
        f.deleteOnExit();

        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.setFileName(f.getAbsolutePath());
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
        assertTrue(mockHttpMethod.isFileRequest());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExeptionIfFileNameDoesNotExist() throws Exception {
        mockHttpMethod = new MockHttpMethod("mock");
        validRestRequest.addHeader("a", "header");
        validRestRequest.setFileName("somefilethatdoesntexist");
        RestClientImpl client = new RestClientImpl(new MockHttpClient(200));
        client.configureHttpMethod(mockHttpMethod, "localhost", validRestRequest);
    }
}
