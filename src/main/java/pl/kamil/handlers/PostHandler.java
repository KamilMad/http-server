package pl.kamil.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpStatus;
import pl.kamil.utility.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PostHandler implements Handler{

    private static final Logger log = LoggerFactory.getLogger(PostHandler.class);
    private final PathUtils pathUtils;
    private final Path uploadDirectory;

    public PostHandler(PathUtils pathUtils, Path rootDirectory) {
        this.pathUtils = pathUtils;
        this.uploadDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {

        byte[] body = request.getBody();
        if (body == null || body.length == 0) {
            return HttpResponse.error(HttpStatus.BAD_REQUEST);
        }

        try {
            // normalize path
            Path safePath = pathUtils.getNormalizePath(uploadDirectory, request.getPath());

            pathUtils.validatePathSecurity(uploadDirectory, safePath);

            Path parent = safePath.getParent();
            log.info("Parent: {}", parent);
            log.info("Safe path is: {}", safePath);
            if (parent != null ) {
                Files.createDirectories(parent);
                log.info("Successfully crated dir structures ");
            }

            // save the bytes
            Files.write(safePath, body);

            return HttpResponse.created();
        } catch (IOException e) {
            return HttpResponse.error(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
