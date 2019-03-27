
package pivotsecurity.rest.client;

import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;

public class MockRestClient extends RestClientImpl {

    public MockRestClient(HttpClient client) {
        super(client);
    }

    public void verifyCorrectHttpMethodCreation() {
        RestRequest req = new RestRequest();
        req.setMethod(RestRequest.Method.Get);
        HttpMethod m = this.createHttpClientMethod(req);
        assertTrue("method is not a GetMethod", m instanceof org.apache.commons.httpclient.methods.GetMethod);
        req.setMethod(RestRequest.Method.Post);
        m = this.createHttpClientMethod(req);
        assertTrue("method is not a PostMethod", m instanceof org.apache.commons.httpclient.methods.PostMethod);
    }

}

class HttpMethodClassCannotBeInstantiated extends HttpMethodBase {
    private HttpMethodClassCannotBeInstantiated() {

    }

    @Override
    public String getName() {
        return "Name";
    }
}

class HttpMethodClassFailsWhenCreating extends HttpMethodBase {
    public HttpMethodClassFailsWhenCreating() {
        throw new RuntimeException("Exception when instantiating");
    }

    @Override
    public String getName() {
        return "Name";
    }
}
