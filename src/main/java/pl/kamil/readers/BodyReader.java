package pl.kamil.readers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BodyReader {
    void transferTo(InputStream source, OutputStream destination) throws IOException;
}
