package pl.kamil.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.ContentType;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpStatus;
import pl.kamil.utility.MimeTypes;
import pl.kamil.utility.PathUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class StaticFileHandler implements Handler {

    private final PathUtils pathUtils;

    private static final Logger log = LoggerFactory.getLogger(StaticFileHandler.class);
    private final Path rootDirectory;

    public StaticFileHandler(PathUtils pathUtils, Path rootDirectory) {
        this.pathUtils = pathUtils;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            // converts URL string to a clean Path
            Path safePath = pathUtils.getNormalizePath(rootDirectory, request.getPath());
            // Validate if path is safe
            pathUtils.validatePathSecurity(rootDirectory, safePath);
            // If the path is a directory, we follow the standard convention of serving index.html
            Path finalPath = resolveResourcePath(safePath);
            return serveFile(finalPath);

        } catch (SecurityException e) {
            log.error("Security violation {}", e.getMessage());
            return HttpResponse.error(HttpStatus.FORBIDDEN);
        } catch (FileNotFoundException e) {
            log.error("File not found {}", e.getMessage());
            return HttpResponse.error(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("IO Error serving file {}", e.getMessage());
            return HttpResponse.error(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse serveFile(Path filePath) throws IOException {
        byte[] content = Files.readAllBytes(filePath);
        String contentType = resolveContentType(filePath);
        return HttpResponse.ok(content, contentType);
    }

    private Path resolveResourcePath(Path path) throws FileNotFoundException {
        Path actualPath = Files.isDirectory(path) ? path.resolve("index.html") : path;

        if (Files.notExists(actualPath)) {
            throw new FileNotFoundException("Resource does not exist: " + actualPath);
        }

        return actualPath;
    }

    private String resolveContentType(Path filePath) {
        try {
            String type = Files.probeContentType(filePath);

            if (type != null) {
               return type;
            }

        } catch (IOException e) {
            log.warn("Could not probe content type for {}", filePath);
        }

        // manual fall back
        return mapExtensionToMimeType(filePath.toString().toLowerCase());
    }

    private String mapExtensionToMimeType(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');

        // If there's no dot, or the dot is the very last character, we can't find an extension`
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(lastDotIndex + 1);

        return MimeTypes.MIME_TYPES.getOrDefault(
                extension,
                "application/octet-stream"
        );
    }
}
