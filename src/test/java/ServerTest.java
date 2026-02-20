import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import org.junit.*;

import static org.junit.Assert.*;

public class ServerTest {

    @Test
    public void testServerIsListening() {

        try(Socket socket = new Socket("localhost", 8080)) {
            assertTrue(socket.isConnected());
        } catch (IOException e) {
            fail("Server is not listening on port 8080. Reason " + e.getMessage());
        }
    }
}
