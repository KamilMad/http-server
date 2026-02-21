package pl.kamil.utils;

public enum ContentType {
    HTML("text/html"),
    JSON("text/json"),
    TEXT("plain/text"),
    IMAGE_PNG("image/png");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
