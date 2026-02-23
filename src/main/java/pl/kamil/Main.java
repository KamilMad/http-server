package pl.kamil;

import pl.kamil.core.HttpRequestParser;
import pl.kamil.core.Registry;
import pl.kamil.core.SimpleHttpServer;
import pl.kamil.handlers.Handler;
import pl.kamil.handlers.StaticFileHandler;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.ContentType;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.protocol.HttpStatus;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Handler handler = new StaticFileHandler(Path.of("public").toAbsolutePath());
        Registry registry = new Registry(handler);

        registry.addRoute(HttpMethod.GET.toString(), "/JSON", (request) -> {
            HttpResponse response = new HttpResponse();
            response.setStatus(HttpStatus.OK);
            response.setContentType(ContentType.JSON);
            response.setBody("Some JSON body".getBytes());

            return response;
        });

        registry.addRoute(HttpMethod.GET.toString(), "/TEXT", (request) -> {
            HttpResponse response = new HttpResponse();
            response.setStatus(HttpStatus.OK);
            response.setContentType(ContentType.TEXT);
            response.setBody("Some text body".getBytes());

            return response;
        });

        HttpRequestParser httpRequestParser = new HttpRequestParser();
        SimpleHttpServer server = new SimpleHttpServer(httpRequestParser, 10, registry);
        server.start();
    }
}