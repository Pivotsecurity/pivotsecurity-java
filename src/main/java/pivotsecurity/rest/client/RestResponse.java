
package pivotsecurity.rest.client;

/**
 * Wraps a REST response object
 */
public class RestResponse extends RestData {
	private String statusText;
	private Integer statusCode;

	/**
	 * @return the status code of this response
	 */
	public Integer getStatusCode() {
		return statusCode;
	}

	/**
	 * @param sCode the status code for this response
	 * @return this response
	 */
	public RestResponse setStatusCode(Integer sCode) {
		this.statusCode = sCode;
		return this;
	}

	/**
	 * @return the status text for this response
	 */
	public String getStatusText() {
		return statusText;
	}

	/**
	 * @param st the status text for this response
	 * @return this response
	 */
	public RestResponse setStatusText(String st) {
		this.statusText = st;
		return this;
	}

	/**
	 * @return string representation of this response
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (getStatusCode() != null)
			builder.append(String.format("[%s] %s", this.getStatusCode(), this.getStatusText()));
		builder.append(LINE_SEPARATOR);
		builder.append(super.toString());
		return builder.toString();
	}

}
