package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.error.BadRequestException;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.inputStreams.ChunkedInputStream;
import pl.kamil.inputStreams.LimitedInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parse(InputStream in) throws IOException {
        HttpRequest request = new HttpRequest();

        parseRequestLine(request, in);
        parseHeaders(request, in);
        parseBody(request, in);
        log.info("Successfully parsed {} request for {}", request.getMethod(), request.getPath());
        return request;
    }

    private String readLine(InputStream in) throws IOException {
        StringBuilder line = new StringBuilder();

        int b;
        while ((b = in.read()) != -1) {
            if (b == '\r') {
                int next = in.read();
                if (next == '\n') {
                    break;
                }
                line.append((char) b);
                if (next != -1 )
                    line.append((char) next);
            } else if (b == '\n') {
                break;
            } else {
                line.append((char)b);
            }

        }
        if (line.isEmpty() && b == -1) return null;
        return line.toString();
    }

    private void parseRequestLine(HttpRequest request, InputStream in) throws IOException {
        String line;

        while ((line = readLine(in)) != null) {
            if (!line.trim().isEmpty())
                break;
        }

        if (line == null)
            return;

        String[] parts = line.split(" ");
        if (parts.length < 3) {
            throw new BadRequestException("Request line: not enough parts");
        }

        request.setMethod(HttpMethod.valueOf(parts[0].trim()));

        String rawPath = parts[1];
        int queryIndex = rawPath.indexOf("?");

        if (queryIndex != -1) {
            request.setPath(rawPath.substring(0, queryIndex));
            request.setQueryParams(extractQueryParams(rawPath.substring(queryIndex + 1)));
        } else {
            request.setPath(rawPath);
            request.setQueryParams(Collections.emptyMap());
        }
        log.info("Request line: {}", line);
    }

    private Map<String, String> extractQueryParams(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1],
                        (existing, replacement) -> existing
                ));
    }

    private void parseHeaders(HttpRequest request, InputStream in) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;

        while ((line = readLine(in)) != null && !line.isEmpty()) {
            String[] header = line.split(":");
            headers.put(header[0].trim(), header[1].trim());
        }
        request.setHeaders(headers);
    }

    private void parseBody(HttpRequest request, InputStream in) {
        InputStream body;
        if (request.getHeaders().containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(request.getHeaders().get("Content-Length"));
            body = new LimitedInputStream(in, contentLength);
        } else if ("chunked".equals(request.getHeaders().get("Transfer-Encoding"))){
            body = new ChunkedInputStream(in);
        } else {
            body = InputStream.nullInputStream();
        }
        request.setBody(body);
    }
}
