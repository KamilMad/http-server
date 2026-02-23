package pl.kamil.protocol;

public enum ContentType {
    HTML("text/html"),
    JSON("text/json"),
    TEXT("text/plain"),
    IMAGE_PNG("image/png"),
    OCTET("application/octet-stream");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
