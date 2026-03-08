package pl.kamil.readers;

import java.io.*;

public class FixedLengthReader implements BodyReader{

    private final int totalBytes;

    public FixedLengthReader(int totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public void transferTo(InputStream source, OutputStream destination) throws IOException {

        byte[] buffer = new byte[8192];
        int byteRead;

        while((byteRead = source.read(buffer)) != -1 && byteRead <= totalBytes) {
            destination.write(buffer,0, byteRead);
            destination.flush();
        }
    }
}
