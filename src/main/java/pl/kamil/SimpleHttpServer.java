package pl.kamil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private final HttpRequestParser httpRequestParser;
    private final ExecutorService executorService;
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);

    public SimpleHttpServer(HttpRequestParser httpRequestParser, int poolSize) {
        this.httpRequestParser = httpRequestParser;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void start() {
        try(ServerSocket server = new ServerSocket(8080)) {
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
        try (clientSocket) {
            httpRequestParser.parse(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Error handling client: {}", e.getMessage());
        }
    }
}
