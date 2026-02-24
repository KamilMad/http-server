package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpMethod;

import java.io.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        HttpRequest request = new HttpRequest();

        // Parse Request Line
        parseRequestLine(request, reader);
        parseHeaders(request, reader);
        parseBody(request, reader);

        //initializeRequest(request, reader, firstLine);

        log.info("Successfully parsed {} request for {}", request.getMethod(), request.getPath());
        return request;
    }

    private void parseRequestLine(HttpRequest request, BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        if (firstLine == null || firstLine.isEmpty()){
            throw new RuntimeException("First line was null or empty");
        }

        String[] parts = firstLine.split(" ");
        if (parts.length < 2) {
            throw new RemoteException("Invalid request line format");
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

    private void parseHeaders(HttpRequest request, BufferedReader reader) throws IOException {
        String line = "";
        Map<String, String> headers = new HashMap<>();

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":");
            headers.put(header[0].trim(), header[1].trim());
        }

        request.setHeaders(headers);
    }
    private void parseBody(HttpRequest request, BufferedReader reader) throws IOException {
        String contentLengthStr = request.getHeaders().get("Content-Length");
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);
            if (contentLength > 0) {
                request.setBody(readBody(reader, contentLength));
            }
        }
    }

    private byte[] readBody(Reader reader, int contentLength) throws IOException {
        char[] bodyBuffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int currentRead = reader.read(bodyBuffer, totalRead, contentLength - totalRead);
            if (currentRead == -1) break;
            totalRead += currentRead;
        }
        return new String(bodyBuffer, 0, totalRead).getBytes();
    }
}
