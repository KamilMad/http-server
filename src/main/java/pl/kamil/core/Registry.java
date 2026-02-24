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
    public final Map<Route, Handler> commands = new HashMap<>();
    private final Handler staticFileHandler;

    public Registry(Handler handler) {
        this.staticFileHandler = handler;
    }

    public void addRoute(String httpMethod, String path, Handler handler) {
        commands.put(new Route(HttpMethod.valueOf(httpMethod), path), handler);
    }

    public HttpResponse dispatch(HttpRequest request) {
        // Try to find an exact match (handler register in the registry)
        Route route = new Route(request.getMethod(), request.getPath());
        Handler handler = commands.get(route);

        if (handler != null) {
            return handler.handle(request);
        }

        if (request.getMethod() == HttpMethod.GET) {
            return staticFileHandler.handle(request);
        }

        // If no match exists , try the file system
        // The static handler will return 404 if file not found
        return HttpResponse.error(HttpStatus.NOT_ALLOWED);
    }

    private HttpResponse createNotFoundResponse() {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setBody("404 - Page Not Found".getBytes());

        return response;
    }
}
