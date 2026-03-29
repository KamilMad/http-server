package pl.kamil.core;

import pl.kamil.protocol.Route;
import pl.kamil.handlers.Handler;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.protocol.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Router {
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
        return new HttpResponse.Builder(HttpStatus.NOT_ALLOWED).build();
    }
}
