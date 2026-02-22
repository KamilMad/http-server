package pl.kamil.protocol;

public record Route(
    HttpMethod method,
    String path
) {
}
