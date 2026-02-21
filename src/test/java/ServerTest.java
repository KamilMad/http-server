import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.HttpRequestParser;
import pl.kamil.SimpleHttpServer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ServerTest {

    @BeforeEach
    public void setUp() {
        new Thread(() -> {
            HttpRequestParser parser = new HttpRequestParser();
            SimpleHttpServer server = new SimpleHttpServer(parser, 10);
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
}
