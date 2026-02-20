package pl.kamil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {

    public void start() {
        try(ServerSocket server = new ServerSocket(8080)) {
            Socket socket = server.accept();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
