package pl.kamil.protocol;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest{
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private InputStream body;
    private Map<String, String> queryParams;

    public HttpRequest() {
        this.headers = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public InputStream getBody() {
        return this.body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
