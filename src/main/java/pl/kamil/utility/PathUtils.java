package pl.kamil.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class PathUtils {

    private static final Logger log = LoggerFactory.getLogger(PathUtils.class);

    public   Path getNormalizePath(Path rootDirectory, String path) {
        // remove leading '/' so resolve() works correctly
        String requestedPath = path.startsWith("/") ?
                path.substring(1).trim() : path;

        // concat root and requestedPath
        return rootDirectory.resolve(requestedPath).normalize();
    }

    public void validatePathSecurity(Path rootDirectory, Path filePath) {
        if (!filePath.startsWith(rootDirectory)) {
            log.error("Client wanted to access parent directory");
            throw new SecurityException("Directory traversal attempt");
        }
    }
}
