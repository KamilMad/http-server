package pl.kamil.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final String httpVersion = "HTTP/1.1";
    private HttpStatus status;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;


    public HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public HttpResponse(HttpStatus status) {
        this.status = status;
    }

    public HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static class Builder {
        private final HttpStatus status;
        private String httpVersion = "HTTP/1.1";
        private Map<String, String> headers;
        private byte[] body;
        private InputStream bodyInputStream;

        public Builder(HttpStatus status) {
            this.status = status;
            headers = new HashMap<>();
        }

        public Builder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder header(String headerName, String headerValue) {
            headers.put(headerName, headerValue);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder body(InputStream body) {
            this.bodyInputStream = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

    }

    public static HttpResponse error(HttpStatus status) {
        HttpResponse response = new HttpResponse(status);
        response.setBody(("Error: " + status.getMessage()).getBytes());

        return response;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream response = new ByteArrayOutputStream();

        // Status Line
        response.write(httpVersion.getBytes());
        response.write(" ".getBytes());
        response.write(status.toString().getBytes());
        response.write("\r\n".getBytes());
        log.info("Successfully wrote the status line");

        // Headers
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((key,val) -> {
                String header = key + ": " + val + "\r\n";
                try {
                    System.out.println("Headers " + header);
                    response.write(header.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        log.info("Successfully wrote the headers");

        // Empty line
        response.write("\r\n".getBytes());

        // Body
        if (body != null) {
            response.write(body);
            log.info("Successfully wrote the body");
        }

        return response.toByteArray();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }
    @Override
    public String toString() {
        return "HttpResponse{" +
                "status=" + status +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
