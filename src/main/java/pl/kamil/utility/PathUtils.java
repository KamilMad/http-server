package pl.kamil.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class PathUtils {

    private static final Logger log = LoggerFactory.getLogger(PathUtils.class);

    public Path getNormalizePath(Path rootDir, String requestPath) {

        // remove leading '/' so resolve() works correctly
        String cleanPath = requestPath.startsWith("/")
                ? requestPath.substring(1)
                : requestPath;

        // concat both paths
        return rootDir
                .resolve(cleanPath)
                .normalize();
    }

    public void validatePathSecurity(Path rootDirectory, Path filePath) {
        if (!filePath.startsWith(rootDirectory)) {
            log.error("Client wanted to access parent directory");
            throw new SecurityException("Directory traversal attempt");
        }
    }
}
