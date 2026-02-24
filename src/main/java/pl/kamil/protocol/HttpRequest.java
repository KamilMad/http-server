package pl.kamil.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest{
    private HttpMethod method;
    private String path;
    private Map<String, String> headers;
    private byte[] body;
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

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", queryParams=" + queryParams +
                '}';
    }
}
