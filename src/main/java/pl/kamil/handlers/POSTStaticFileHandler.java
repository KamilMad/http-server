package pl.kamil.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kamil.protocol.HttpRequest;
import pl.kamil.protocol.HttpResponse;
import pl.kamil.protocol.HttpStatus;
import pl.kamil.utility.MimeTypes;
import pl.kamil.utility.PathUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class POSTStaticFileHandler implements Handler{

    private static final Logger log = LoggerFactory.getLogger(POSTStaticFileHandler.class);
    private final PathUtils pathUtils;
    private final Path uploadDirectory = Path.of("public").toAbsolutePath();

    public POSTStaticFileHandler(PathUtils pathUtils) {
        this.pathUtils = pathUtils;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {

        Path safePath = pathUtils.getNormalizePath(uploadDirectory, request.getPath());
        pathUtils.validatePathSecurity(uploadDirectory, safePath);
        Path parent = safePath.getParent();

        String contentType = request.getHeaders().get("Content-Type");
        String extension = contentType.substring(contentType.lastIndexOf('/') + 1);
        log.info("Extension = {}", extension);


        String fileName = UUID.randomUUID() + "." + extension;
        safePath = safePath.resolve(fileName);

        try {
            Files.copy(request.getBody(), safePath);
            return new HttpResponse.Builder(HttpStatus.CREATED).build();

        } catch (IOException e) {
            log.error("Exception when writing the file {}", e.getMessage());
            return new HttpResponse.Builder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
