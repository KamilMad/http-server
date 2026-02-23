package pl.kamil.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class StaticFileHandler implements Handler{

    private static final Logger log = LoggerFactory.getLogger(StaticFileHandler.class);
    private final Path rootDirectory;

    public StaticFileHandler(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        try {
            // remove leading "/" so resolve() works correctly
            String requestedPath = request.getPath().startsWith("/") ?
                    request.getPath().substring(1) : request.getPath();
            // concat both paths
            Path filePath = rootDirectory.resolve(requestedPath).normalize();
            log.info("Path: {}", filePath);

            // check if request want to access parent dir
            if (!filePath.startsWith(rootDirectory)) {
                log.error("Client wanted to access parent directory");
                return new HttpResponse(HttpStatus.FORBIDDEN);
            }

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                byte[] content;
                String contentType;

                contentType = Files.probeContentType(filePath);
                content = Files.readAllBytes(filePath);
                createResponse(response, content, contentType);

            } else {
                log.error("File not found");
                response.setStatus(HttpStatus.NOT_FOUND);
            }
        }catch (IOException e) {
            log.error("Internal server error");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public void createResponse(HttpResponse response, byte[] body, String contentType) {
        response.setStatus(HttpStatus.OK);
        response.setHeaders(Map.of("Content-Type", contentType));
        response.setBody(body);
    }
}
