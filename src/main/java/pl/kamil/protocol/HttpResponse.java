package pl.kamil.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final String httpVersion = "HTTP/1.1";
    private HttpStatus status;
    private Map<String, String> headers;
    private ContentType contentType;
    private byte[] body;


    public HttpResponse() {
    }

    public HttpResponse(HttpStatus status) {
        this.status = status;
    }

    public HttpResponse(HttpStatus status, Map<String, String> headers, ContentType contentType, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse error(HttpStatus status) {
        HttpResponse response = new HttpResponse(status);
        response.setBody(("Error: " + status.getMessage()).getBytes());

        return response;
    }

    public static HttpResponse ok(byte[] body, String contentType) {
        HttpResponse response = new HttpResponse();
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(body.length));
        response.setBody(body);

        return response;
    }


    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream response = new ByteArrayOutputStream();

        // Status Line
        response.write(httpVersion.getBytes());// Http Version
        response.write(" ".getBytes()); // Empty space
        response.write(status.toString().getBytes()); // Status Code and Message
        response.write("\r\n".getBytes()); // End of Status Line
        log.info("Successfully wrote the status line");

        // Headers
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((key,val) -> {
                String header = key + ": " + val + "\r\n";
                try {
                    response.write(header.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        log.info("Successfully wrote the headers");

        // Empty line
        response.write("\r\n".getBytes());
        log.info("Successfully wrote the empty line");

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

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
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
                ", contentType=" + contentType +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
