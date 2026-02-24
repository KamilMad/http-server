package pl.kamil.utility;

import java.util.Map;

public class MimeTypes {
    public static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            ".css", "text/css",
            ".js",  "application/javascript",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg"
    );
}
