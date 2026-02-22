package pl.kamil.handlers;

import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;

@FunctionalInterface
public interface Handler {
    HttpResponse handle(HttpRequest request);
}
