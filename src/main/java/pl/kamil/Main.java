package pl.kamil;

public class Main {
    public static void main(String[] args) {
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        SimpleHttpServer server = new SimpleHttpServer(httpRequestParser);
        server.start();
    }
}