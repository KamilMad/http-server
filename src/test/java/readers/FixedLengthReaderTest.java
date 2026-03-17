package readers;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FixedLengthReaderTest {


    @Test
    void shouldTransferDataCorrectly_ForSmallData() throws IOException {
        String originalData = "Hello World";

        InputStream source = new ByteArrayInputStream(originalData.getBytes());
        ByteArrayOutputStream destination = new ByteArrayOutputStream();

        FixedLengthReader reader = new FixedLengthReader(originalData.length());

        reader.transferTo(source, destination);

        assertEquals(originalData, destination.toString());
    }

    @Test
    void shouldTransferDataCorrectly_WhenDataMatchBufferExactly() throws IOException {

        byte[] originalData = new byte[8192];
        Arrays.fill(originalData, (byte) 'A');

        ByteArrayInputStream source = new ByteArrayInputStream(originalData);
        ByteArrayOutputStream destination = new ByteArrayOutputStream();

        FixedLengthReader reader = new FixedLengthReader(originalData.length);
        reader.transferTo(source, destination);

        assertEquals(originalData.length, destination.toString().length());
    }

    @Test
    void shouldTransferDataCorrectly_WhenMultiple8KBChunks() throws IOException{
        String largeData = "A".repeat(20000);

        ByteArrayInputStream source = new ByteArrayInputStream(largeData.getBytes());
        ByteArrayOutputStream destination = new ByteArrayOutputStream();

        FixedLengthReader reader = new FixedLengthReader(largeData.length());
        reader.transferTo(source, destination);

        assertEquals(largeData.length(), destination.toString().length());
    }

}
