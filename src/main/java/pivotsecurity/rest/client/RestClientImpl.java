
package pivotsecurity.rest.client;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.*;
/**
 * A generic REST client based on {@code HttpClient}.
 */
public class RestClientImpl implements RestClient {

    private static Logger LOG = LoggerFactory.getLogger(RestClientImpl.class);

    private final HttpClient client;

    private String baseUrl;

    /**
     * Constructor allowing the injection of an {@code
     * org.apache.commons.httpclient.HttpClient}.
     *
     * @param client the client
     *               See {@link org.apache.commons.httpclient.HttpClient}
     */
    public RestClientImpl(HttpClient client) {
        if (client == null)
            throw new IllegalArgumentException("Null HttpClient instance");
        this.client = client;
    }

    /**
     * See {@link pivotsecurity.rest.client.RestClient#setBaseUrl(java.lang.String)}
     */
    public void setBaseUrl(String bUrl) {
        this.baseUrl = bUrl;
    }

    /**
     * See {@link pivotsecurity.rest.client.RestClient#getBaseUrl()}
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the Http client instance used by this implementation.
     *
     * @return the instance of HttpClient
     * See {@link org.apache.commons.httpclient.HttpClient}
     * See {@link pivotsecurity.rest.client.RestClientImpl#RestClientImpl(HttpClient)}
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * See {@link pivotsecurity.rest.client.RestClient#execute(pivotsecurity.rest.client.RestRequest)}
     */
    public RestResponse execute(RestRequest request) {
        return execute(getBaseUrl(), request);
    }

    /**
     * See {@link pivotsecurity.rest.client.RestClient#execute(java.lang.String, pivotsecurity.rest.client.RestRequest)}
     */
    public RestResponse execute(String hostAddr, final RestRequest request) {
        if (request == null || !request.isValid())
            throw new IllegalArgumentException("Invalid request " + request);
        if (request.getTransactionId() == null)
            request.setTransactionId(Long.valueOf(System.currentTimeMillis()));
        LOG.debug("request: {}", request);
        HttpMethod m = createHttpClientMethod(request);
        configureHttpMethod(m, hostAddr, request);

        if (LOG.isDebugEnabled()) {
            try {
                LOG.info("Http Request URI : {}", m.getURI());
            } catch (URIException e) {
                LOG.error("Error URIException in debug : " + e.getMessage(), e);
            }
            LOG.debug("Http Request Method Class : {} ",    m.getClass()  );
            LOG.debug("Http Request Header : {} ",    Arrays.toString( m.getRequestHeaders()) );

            if (m instanceof EntityEnclosingMethod) {
                try {
                    ByteArrayOutputStream requestOut = new ByteArrayOutputStream();
                    ((EntityEnclosingMethod) m).getRequestEntity().writeRequest(requestOut);
                    LOG.debug("Http Request Body : {}", requestOut.toString());
                } catch (IOException e) {
                    LOG.error("Error in reading request body in debug : " + e.getMessage(), e);
                }
            }
        }

        RestResponse resp = new RestResponse();
        resp.setTransactionId(request.getTransactionId());
        resp.setResource(request.getResource());
        try {
            client.executeMethod(m);
            for (Header h : m.getResponseHeaders()) {
                resp.addHeader(h.getName(), h.getValue());
            }
            resp.setStatusCode(m.getStatusCode());
            resp.setStatusText(m.getStatusText());
            resp.setRawBody(m.getResponseBody());

             if (LOG.isDebugEnabled()) {
                LOG.debug("Http Request Path : {}", m.getPath());
                LOG.debug("Http Request Header : {} ", Arrays.toString( m.getRequestHeaders()) );
                LOG.debug("Http Response Status : {}", m.getStatusLine() );
                LOG.debug("Http Response Body : {}", m.getResponseBodyAsString() );
            }

        } catch (HttpException e) {
            String message = "Http call failed for protocol failure";
            throw new IllegalStateException(message, e);
        } catch (IOException e) {
            String message = "Http call failed for IO failure";
            throw new IllegalStateException(message, e);
        } finally {
            m.releaseConnection();
        }
        LOG.debug("response: {}", resp);
        return resp;
    }

    /**
     * Configures the instance of HttpMethod with the data in the request and
     * the host address.
     *
     * @param m        the method class to configure
     * @param hostAddr the host address
     * @param request  the rest request
     */
    protected void configureHttpMethod(HttpMethod m, String hostAddr, final RestRequest request) {
        addHeaders(m, request);
        setUri(m, hostAddr, request);
        m.setQueryString(request.getQuery());
        if (m instanceof EntityEnclosingMethod) {
            RequestEntity requestEntity = null;
            String fileName = request.getFileName();
            if (fileName != null) {
                requestEntity = configureFileUpload(fileName);
            } else {
                // Add Multipart
                Map<String, RestMultipart> multipartFiles = request.getMultipartFileNames();
                if ((multipartFiles != null) && (!multipartFiles.isEmpty())) {
                    requestEntity = configureMultipartFileUpload(m, request, requestEntity, multipartFiles);
                } else {
                    requestEntity = new RequestEntity() {
                        public boolean isRepeatable() {
                            return true;
                        }

                        public void writeRequest(OutputStream out) throws IOException {
                            PrintWriter printer = new PrintWriter(out);
                            printer.print(request.getBody());
                            printer.flush();
                        }

                        public long getContentLength() {
                            return request.getBody().getBytes().length;
                        }

                        public String getContentType() {
                            List<pivotsecurity.rest.client.RestData.Header> values = request.getHeader("Content-Type");
                            String v = "text/xml";
                            if (values.size() != 0)
                                v = values.get(0).getValue();
                            return v;
                        }
                    };
                }
            }
            ((EntityEnclosingMethod) m).setRequestEntity(requestEntity);
        } else {
            m.setFollowRedirects(request.isFollowRedirect());
        }

    }


    private RequestEntity configureMultipartFileUpload(HttpMethod m, final RestRequest request, RequestEntity requestEntity, Map<String, RestMultipart> multipartFiles) {
        MultipartRequestEntity multipartRequestEntity = null;
        // Current File Name reading for tracking missing file
        String fileName = null;

        List<Part> fileParts = new ArrayList<Part>(multipartFiles.size());
        // Read File Part
        for (Map.Entry<String, RestMultipart> multipartFile : multipartFiles.entrySet()) {
            Part filePart = createMultipart(multipartFile.getKey(), multipartFile.getValue());
            fileParts.add(filePart);
        }
        Part[] parts = fileParts.toArray(new Part[fileParts.size()]);
        multipartRequestEntity = new MultipartRequestEntity(parts, ((EntityEnclosingMethod) m).getParams());

        return multipartRequestEntity;
    }

    private Part createMultipart(String fileParamName, RestMultipart restMultipart) {
        RestMultipart.RestMultipartType type = restMultipart.getType();
        switch (type) {
            case FILE:
                String fileName = null;
                try {
                    fileName = restMultipart.getValue();
                    File file = new File(fileName);
                    FilePart filePart = new FilePart(fileParamName, file, restMultipart.getContentType(), restMultipart.getCharset());
                    LOG.info("Configure Multipart file upload paramName={} :  ContentType={} for  file={} ", new String[]{ fileParamName,  restMultipart.getContentType(), fileName});
                    return filePart;
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("File not found: " + fileName, e);
                }
            case STRING:
                StringPart stringPart = new StringPart(fileParamName, restMultipart.getValue(), restMultipart.getCharset());
                stringPart.setContentType(restMultipart.getContentType());
                LOG.info("Configure Multipart String upload paramName={} :  ContentType={} ", fileParamName, stringPart.getContentType());
                return stringPart;
            default:
                throw new IllegalArgumentException("Unknonw Multipart Type : " + type);
        }

    }



    private RequestEntity configureFileUpload(String fileName) {
        final File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        return new FileRequestEntity(file, "application/octet-stream");
    }

    public String getContentType(RestRequest request) {
        List<pivotsecurity.rest.client.RestData.Header> values = request.getHeader("Content-Type");
        String v = "text/xml";
        if (values.size() != 0)
            v = values.get(0).getValue();
        return v;
    }

    private void setUri(HttpMethod m, String hostAddr, RestRequest request) {
        String host = hostAddr == null ? client.getHostConfiguration().getHost() : hostAddr;
        if (host == null)
            throw new IllegalStateException("hostAddress is null: please config httpClient host configuration or " + "pass a valid host address or config a baseUrl on this client");
        String uriString = host + request.getResource();
        boolean escaped = request.isResourceUriEscaped();
        try {
            m.setURI(createUri(uriString, escaped));
        } catch (URIException e) {
            throw new IllegalStateException("Problem when building URI: " + uriString, e);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Building URI with null string", e);
        }
    }

    protected URI createUri(String uriString, boolean escaped) throws URIException {
        return new URI(uriString, escaped);
    }

    /**
     * factory method that maps a string with a HTTP method name to an
     * implementation class in Apache HttpClient. Currently the name is mapped
     * to <code>org.apache.commons.httpclient.methods.%sMethod</code> where
     * <code>%s</code> is the parameter mName.
     *
     * @param mName the method name
     * @return the method class
     */
    protected String getMethodClassnameFromMethodName(String mName) {
        return String.format("org.apache.commons.httpclient.methods.%sMethod", mName);
    }

    /**
     * Utility method that creates an instance of {@code
     * org.apache.commons.httpclient.HttpMethod}.
     *
     * @param request the rest request
     * @return the instance of {@code org.apache.commons.httpclient.HttpMethod}
     * matching the method in RestRequest.
     */
    @SuppressWarnings("unchecked")
    protected HttpMethod createHttpClientMethod(RestRequest request) {
        String mName = request.getMethod().toString();
        String className = getMethodClassnameFromMethodName(mName);
        try {
            Class<HttpMethod> clazz = (Class<HttpMethod>) Class.forName(className);
            if (className.endsWith("TraceMethod")) {
                return clazz.getConstructor(String.class).newInstance("http://localhost:8080/api/");
            } else {
                return clazz.newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(className + " not found: you may be using a too old or " + "too new version of HttpClient", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("An object of type " + className + " cannot be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("The default ctor for type " + className + " cannot be accessed", e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Exception when instantiating: " + className, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The ctor with String.class arg for type " + className + " cannot be invoked", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("The ctor with String.class arg for type " + className + " doesn't exist", e);
        }
    }

    private void addHeaders(HttpMethod m, RestRequest request) {
        for (RestData.Header h : request.getHeaders()) {
            m.addRequestHeader(h.getName(), h.getValue());
        }
    }
}
