package pl.kamil.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChunkedReader implements BodyReader{

    private static final Logger log = LoggerFactory.getLogger(ChunkedReader.class);

    @Override
    public void transferTo(InputStream source, OutputStream destination) throws IOException {

        byte[] buffer = new byte[8192];

        int chunkSize;
        int byteRead;
        while((chunkSize = getChunkSize(source)) != 0 &&
                (byteRead = source.read(buffer, 0, chunkSize)) != -1) {

            // get rid of CRLF
           source.skip(2);

            destination.write(buffer,0,byteRead);
            destination.flush();
        }


    }

    /*
        POST /api/chat HTTP/1.1
        Host: localhost:8080
        Transfer-Encoding: chunked
        Content-Type: text/plain

        5\r\n           <-- "5" is Hex for 5 bytes
        Hello\r\n       <-- Data (5 bytes) + CRLF
        1\r\n           <-- "1" is Hex for 1 byte
         \r\n           <-- Data (the space) + CRLF
        7\r\n           <-- "7" is Hex for 7 bytes
        Gemini!\r\n     <-- Data (7 bytes) + CRLF
        0\r\n           <-- The "End of Body" marker
        \r\n            <-- Final empty line
         */

    private int getChunkSize(InputStream input) throws IOException {
        StringBuilder hexValue = new StringBuilder();

        int b;
        while ((b = input.read()) != -1) {
            char c = (char) b;
            if (c == '\n') break;
            if (c == '\r') continue;

            hexValue.append(c);
        }
        return Integer.parseInt(hexValue.toString(), 16);
    }

}
