import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.core.HttpRequestParser;
import pl.kamil.core.SimpleHttpServer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ServerTest {
    private static final Logger log = LoggerFactory.getLogger(ServerTest.class);

    @BeforeEach
    public void setUp() {
        new Thread(() -> {
            HttpRequestParser parser = new HttpRequestParser();
            SimpleHttpServer server = new SimpleHttpServer(parser, 10, null);
            server.start();
        }).start();

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        };
    }

    @Test
    public void testServerIsListening() {

        try(Socket socket = new Socket("localhost", 8080)) {
            assertTrue(socket.isConnected());
        } catch (IOException e) {
            fail("Server is not listening on port 8080. Reason " + e.getMessage());
        }
    }

    @Test
    void shouldHandleMultipleConnectionsConcurrently() throws InterruptedException {
        int numberOfClients = 10;
        CountDownLatch latch = new CountDownLatch(numberOfClients);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfClients; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", 8080);
                     OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {

                    // Minimal valid HTTP request + EMPTY LINE
                    out.write("GET / HTTP/1.1\r\n\r\n".getBytes());
                    out.flush();

                    // Waiting for server response
                    in.read();

                } catch (IOException e) {
                    System.err.println("Client error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Wait for both threads to finish
        latch.await(10, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;

        // If it's multi-threaded, duration should be ~5 seconds, NOT 10+
        assertTrue(duration < 5000, "Server is likely blocking and not multi-threaded!");
    }
}
