package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Acceptor {

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            10,
            50,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final Logger logger = LoggerFactory.getLogger(Acceptor.class);

    private final RequestHandler requestHandler;

    private volatile boolean running = true;

    private ServerSocket server;

    public Acceptor(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void start() {
        try{
            server = new ServerSocket(8080);
            logger.info("Server listening on port {}" , 8080);
            while (running) {
                try {
                    Socket client = server.accept();
                    executorService.execute(() -> requestHandler.handleClient(client));
                } catch (IOException e) {
                    if (running) {
                        logger.error("Failed to accept connection: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error handling server: {}", e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (server != null && !server.isClosed()) {
                server.close();
            }
        } catch (IOException e) {
            logger.error("Error closing server: {}", e.getMessage());
        }
        executorService.shutdown();
    }
}
