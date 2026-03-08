package readers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import pl.kamil.readers.ChunkedReader;

import javax.naming.directory.Attribute;
import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkedReaderTest {

    @ParameterizedTest
    @CsvSource({
            "5,'5\r\n'",
            "10,'A\r\n'",
            "255,'FF\r\n'"
    })
    void shouldSucceed(int chunks, String in) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(in.getBytes());

        ChunkedReader reader = new ChunkedReader();
        //int chunkSize = reader.getChunkSize(inputStream);

      //  assertEquals(chunks, chunkSize);
    }

    @ParameterizedTest
    @MethodSource("provideChunkedBody")
    void shouldSuccessfullyReadChunkedRequest(int bytes, String chunkedBody) throws IOException{
        InputStream source = new ByteArrayInputStream(chunkedBody.getBytes());
        OutputStream destination = new ByteArrayOutputStream();

        ChunkedReader reader = new ChunkedReader();
        reader.transferTo(source, destination);

        assertEquals(bytes, destination.toString().getBytes().length);
    }

    private static Stream<Arguments> provideChunkedBody() {
        return Stream.of(
                Arguments.of(12, "5\r\nHello\r\n1\r\n \r\n6\r\nWorld!\r\n0\r\n\r\n"),
                Arguments.of(48, "5\r\n{\"id\"\r\n1e\r\n: 101, \"status\": \"processing\",\r\n0d\r\n \"type\": \"A\"}\r\n0\r\n\r\n")
        );
    }
}
