package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.ContentType;
import pl.kamil.protocol.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private final HttpRequestParser httpRequestParser;
    private final ExecutorService executorService;
    private final Registry registry;
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);

    public SimpleHttpServer(HttpRequestParser httpRequestParser, int poolSize, Registry registry) {
        this.httpRequestParser = httpRequestParser;
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.registry = registry;
    }

    public void start() {
        try(ServerSocket server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(8080));
            logger.info("Server listening on port {}" , 8080);


            while (true) {
                try {
                    Socket clientSocket = server.accept();
                    executorService.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    logger.error("Failed to accept connection: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error handling server: {}", e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        HttpResponse response = new HttpResponse();
        try (clientSocket;
             InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {

            HttpRequest request = httpRequestParser.parse(in);
            Thread.sleep(3000);


            response = registry.dispatch(request);
            out.write(response.getBytes());
            out.flush();

            logger.info("Client handled successfully.");
        } catch (Exception e) {
            logger.error("Error handling client: {}", e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setContentType(ContentType.TEXT);
            response.setBody("Invalid Request Format".getBytes());
        }
    }
}
