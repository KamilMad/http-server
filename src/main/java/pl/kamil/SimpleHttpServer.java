package pl.kamil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {

    private final HttpRequestParser httpRequestParser;

    public SimpleHttpServer(HttpRequestParser httpRequestParser) {
        this.httpRequestParser = httpRequestParser;
    }

    public void start() {
        try(ServerSocket server = new ServerSocket(8080)) {
            Socket socket = server.accept();
            httpRequestParser.parse(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
