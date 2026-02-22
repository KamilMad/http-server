package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.Route;
import pl.kamil.handlers.Handler;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.protocol.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private static final Logger log = LoggerFactory.getLogger(Registry.class);
    public static Map<Route, Handler> COMMANDS = new HashMap<>();

    public void addRoute(String httpMethod, String path, Handler handler) {
        COMMANDS.put(new Route(HttpMethod.valueOf(httpMethod), path), handler);
    }

    public HttpResponse dispatch(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getPath();
        log.info("Successfully extracted method and path");

        Route route = new Route(method, path);

        Handler handler = COMMANDS.get(route);

        if (handler != null) {
            return handler.handle(request);
        }

        return createNotFoundResponse();
    }

    private HttpResponse createNotFoundResponse() {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setBody("404 - Page Not Found".getBytes());

        return response;
    }
}
