package pl.kamil.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.handlers.GetStaticFileHandler;
import pl.kamil.protocol.Route;
import pl.kamil.handlers.Handler;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.protocol.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final Logger log = LoggerFactory.getLogger(Router.class);

    public final Map<Route, Handler> commands = new HashMap<>();

    public void addRoute(String httpMethod, String path, Handler handler) {
        commands.put(new Route(HttpMethod.valueOf(httpMethod), path), handler);
    }

    public HttpResponse dispatch(HttpRequest request) {

        Route route = new Route(request.getMethod(), request.getPath());
        Handler handler = commands.get(route);

        if (handler != null) {
            return handler.handle(request);
        }

        return HttpResponse.error(HttpStatus.NOT_ALLOWED);
    }

    private HttpResponse createNotFoundResponse() {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setBody("404 - Page Not Found".getBytes());

        return response;
    }
}
