package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler {

    private final HttpRequestParser httpRequestParser;
    private final Router router;
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(HttpRequestParser httpRequestParser, Router router) {
        this.httpRequestParser = httpRequestParser;
        this.router = router;
    }
    public void handleClient(Socket clientSocket) {
        try {
            while (!clientSocket.isClosed()) {
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                HttpRequest request = httpRequestParser.parse(in);
                if (request == null) break;;

                HttpResponse response = router.dispatch(request);
                byte[] bytes = response.getBytes();

                out.write(bytes);
                out.flush();

                boolean keepAlive = "keep-alive".equalsIgnoreCase(request.getHeaders().getOrDefault("Connection", "close"));
                if (!keepAlive) break;
            }
            clientSocket.close();
        } catch (Exception e) {
            logger.error("Error handling client: {}", e.getMessage());
        }
    }
}
