package pl.kamil.inputStreams;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {
    private final InputStream input;
    private int remaining;

    public LimitedInputStream(InputStream input, int maxBytes) {
        this.input = input;
        this.remaining = maxBytes;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) return -1;
        int b = input.read();
        if (b != -1) remaining--;
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) return -1;
        int toRead = Math.min(len, remaining);
        int n = input.read(b, off, toRead);
        if (n != -1) remaining -= n;
        return n;
    }

    @Override
    public void close() throws IOException {

    }
}
