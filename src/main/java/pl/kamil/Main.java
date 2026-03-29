package pl.kamil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.core.Acceptor;
import pl.kamil.core.HttpRequestParser;
import pl.kamil.core.Router;
import pl.kamil.core.RequestHandler;
import pl.kamil.handlers.Handler;
import pl.kamil.handlers.POSTStaticFileHandler;
import pl.kamil.handlers.GetStaticFileHandler;
import pl.kamil.utility.PathUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        PathUtils pathUtils = new PathUtils();

        RequestHandler requestHandler = getRequestHandler(pathUtils);
        Acceptor acceptor = new Acceptor(requestHandler);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(acceptor::start);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
            acceptor.stop();
            executor.shutdown();
        }));
    }

    private static RequestHandler getRequestHandler(PathUtils pathUtils) {
        Handler getStaticFileHandler = new GetStaticFileHandler(pathUtils);
        Handler postStaticFileHandler = new POSTStaticFileHandler(pathUtils);

        Router router = new Router();
        router.addRoute("GET", "/index.html", getStaticFileHandler);
        router.addRoute("GET", "/logo.png", getStaticFileHandler);
        router.addRoute("GET", "/style.css", getStaticFileHandler);
        router.addRoute("POST", "/", postStaticFileHandler);

        HttpRequestParser httpRequestParser = new HttpRequestParser();
        return new RequestHandler(httpRequestParser, router);
    }
}