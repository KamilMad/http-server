package pl.kamil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    public void parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        HttpRequest request = new HttpRequest();

        // Parse Request Line
        String firstLine = reader.readLine();
        if (firstLine == null || firstLine.isEmpty()) return;

        request.setMethod(extractMethod(firstLine));
        request.setPath(extractPath(firstLine));

        // Parse Headers
        Map<String, String> headers = extractHeaders(reader);
        request.setHeaders(headers);

        String contentLengthStr = headers.get("Content-Length");
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);
            if (contentLength > 0) {
                request.setBody(readBody(reader, contentLength));
            }
        }

        log.info("Successfully parsed {} request for {}", request.getMethod(), request.getPath());
    }

    private String extractMethod(String line) {
        return line.split(" ")[0].trim();
    }

    private String extractPath(String line) {
        return line.split(" ")[1].trim();
    }

    private Map<String, String> extractHeaders(BufferedReader reader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":");
            headers.put(header[0].trim(), header[1].trim());
        }

        return headers;
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
