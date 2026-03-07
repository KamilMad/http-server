package pl.kamil;

import pl.kamil.core.HttpRequestParser;
import pl.kamil.core.Router;
import pl.kamil.core.SimpleHttpServer;
import pl.kamil.handlers.Handler;
import pl.kamil.handlers.POSTStaticFileHandler;
import pl.kamil.handlers.GetStaticFileHandler;
import pl.kamil.utility.PathUtils;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        PathUtils pathUtils = new PathUtils();

        Handler getStaticFileHandler = new GetStaticFileHandler(pathUtils);
        Handler postStaticFileHandler = new POSTStaticFileHandler(pathUtils);

        Router router = new Router();
        router.addRoute("GET", "/index.html", getStaticFileHandler);
        router.addRoute("POST", "/**", postStaticFileHandler);

        HttpRequestParser httpRequestParser = new HttpRequestParser();
        SimpleHttpServer server = new SimpleHttpServer(httpRequestParser, 10, router);

        server.start();
    }
}