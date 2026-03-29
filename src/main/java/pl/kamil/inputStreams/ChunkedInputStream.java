package pl.kamil.inputStreams;

import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream {
    private final InputStream inputStream;

    private boolean finished;
    private int remainingChunks;

    public ChunkedInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {

        if (finished) {
            return -1;
        }

        if (remainingChunks == 0) {
            readNextChunkSize();
            if (finished) {
                return -1;
            }
        }

        int b = inputStream.read();
        if (b == -1) {
            throw new IOException("Unexpected EOF in chunk data");
        }

        remainingChunks--;

        if (remainingChunks == 0) {
            consumeCRLF();
        }

        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (finished) return -1;

        if (remainingChunks == 0) {
            readNextChunkSize();
            if (finished) return -1;
        }

        int toRead = Math.min(len, remainingChunks);
        int n = inputStream.read(b, off, toRead);

        if (n == -1 ) {
            throw new IOException("Unexpected EOF in chunked data");
        }

        remainingChunks -= n;

        if (remainingChunks == 0) {
            consumeCRLF();
        }

        return n;
    }

    private void readNextChunkSize() throws IOException {
        String line = readLine();

        int size = Integer.parseInt(line.trim(), 16);
        if (size == 0) {
            finished = true;
            consumeCRLF();
        } else {
            remainingChunks = size;
        }
    }

    private String readLine() throws IOException {
        StringBuilder line = new StringBuilder();

        int prev = -1;
        int curr;

        while ((curr = inputStream.read()) != -1) {
            if (prev == '\n' && curr == '\r') {
                line.setLength(line.length() - 1);
                break;
            }
            line.append(curr);
            prev = curr;
        }

        return line.toString();
    }

    private void consumeCRLF() throws IOException {
        if (inputStream.read() != '\n' || inputStream.read() != '\r') {
            throw new IOException("Invalid chunk ending");
        }
    }
}
