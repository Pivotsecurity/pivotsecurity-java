package pivotsecurity.rest.client;


public interface RestClient {

	/**
	 * Sets the base URL.
	 * It is the portion of the full Url not part of the
	 * resource type. For example if a resource type full Url is
	 * {@code http://host:8888/domain/resourcetype} and the resource type is
	 * {@code /resourcetype}, the base Url is {@code http://host:8888/domain}.
	 * It is meant to serve as a default value to be appended to compose the
	 * full Url when
	 * {@link pivotsecurity.rest.client.RestClient#execute(RestRequest)}
	 * is used.
	 *
	 * @param bUrl
	 *            a string with the base Url.
	 * See {@link pivotsecurity.rest.client.RestClient#execute(RestRequest)}
	 */
	void setBaseUrl(String bUrl);

	/**
	 * Retrieves the previously set base Url.
	 *
	 * @return the base Url
	 * See {@link pivotsecurity.rest.client.RestClient#setBaseUrl(String)}
	 */
	String getBaseUrl();

	/**
	 * Executes a rest request using the underlying Http client implementation.
	 *
	 * @param request
	 *            the request to be executed
	 * @return the response of the rest request
	 */
	RestResponse execute(RestRequest request);

	/**
	 * Executes the rest request.
	 *
	 * This method offers the possibility to override the base Url set on this client.
	 *
	 * @param baseUrl
	 *            the base Url
	 * @param request
	 *            the request to be executed
	 * @return the response of the rest request.
	 * See {@link pivotsecurity.rest.client.RestClient#setBaseUrl(java.lang.String)}
	 */
	RestResponse execute(String baseUrl, RestRequest request);

}