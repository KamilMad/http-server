package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import pl.kamil.core.HttpRequestParser;
import pl.kamil.protocol.ContentType;
import pl.kamil.protocol.HttpMethod;
import pl.kamil.protocol.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpRequestParserTest {

    private HttpRequestParser parser;

    @BeforeEach
    void setUp() {
        parser = new HttpRequestParser();
    }

    @Nested
    class HappyPathTests {

        @ParameterizedTest
        @MethodSource("provideAllHappyPathRequests")
        void shouldParseSimpleRequest(String expectedMethod, String expectedPath, int expectedParamCount, Map<String, String> params, String rawLine) throws IOException {

            HttpRequest request = parse(rawLine);

            assertEquals(expectedMethod, request.getMethod().toString());
            assertEquals(expectedPath, request.getPath());
            assertEquals(expectedParamCount, request.getQueryParams().size());
            assertEquals(params, request.getQueryParams());
        }

        private static Stream<Arguments> provideAllHappyPathRequests() {
            return Stream.of(
                    // no query parameters
                    Arguments.of("GET", "/index.html", 0, Collections.emptyMap(),"GET /index.html HTTP/1.1"),
                    Arguments.of("POST", "/api/data", 0, Collections.emptyMap(), "POST /api/data HTTP/1.1"),
                    Arguments.of("DELETE", "/api/data", 0, Collections.emptyMap(), "DELETE /api/data HTTP/1.1"),

                    // with query parameters
                    Arguments.of("GET", "/search", 1, Map.of("q", "test"), "GET /search?q=test HTTP/1.1"),
                    Arguments.of("GET", "/index.html", 2, Map.of("key1", "value1", "key2", "value2"),
                            "GET /index.html?key1=value1&key2=value2 HTTP/1.1"),
                    Arguments.of("DELETE", "/index.html", 2, Map.of("key1", "value1", "key2", "value2"),
                            "DELETE /index.html?key1=value1&key2=value2 HTTP/1.1")
            );
        }

        @ParameterizedTest
        @MethodSource("provideRequestsWithHeaders")
        void shouldParseRequestWithHeaders(String rawLine, Map<String, String> headers) throws IOException {

            HttpRequest request = parse(rawLine);

            assertEquals(headers, request.getHeaders());
        }

        private static Stream<Arguments> provideRequestsWithHeaders() {
            return Stream.of(
                    // Multiple standard headers
                    Arguments.of(
                            "GET / HTTP/1.1\r\nHost: localhost\r\nUser-Agent: JUnit\r\nAccept: */*\r\n\r\n",
                            Map.of(
                                    "Host", "localhost",
                                    "User-Agent", "JUnit",
                                    "Accept", "*/*"
                            )
                    ),

                    // Headers with spaces in values
                    Arguments.of(
                            "GET / HTTP/1.1\r\nContent-Type: text/html; charset=UTF-8\r\n\r\n",
                            Map.of("Content-Type", "text/html; charset=UTF-8")
                    ),

                    // Header with no space after colon
                    Arguments.of(
                            "GET / HTTP/1.1\r\nCustom-Header:value\r\n\r\n",
                            Map.of("Custom-Header", "value")
                    )
            );
        }
    }

    @Nested
    class NegativePathTests {
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {
                " ",
                "HELLO WORLD",
                "/index.html HTTP/1.1",
                // non-existing method
                "NOTREALMETHOD /index.html HTTP/1.1",
                // malformed
                " / / HTTP/1.1\r\n\r\n",
                "\r\n\r\n"

        })
        //@MethodSource("provideNegativePathRequests")
        void shouldThrowExceptionWhenEmptyOrGarbageRequest(String rawLine) throws IOException {
            assertThrows(Exception.class , () ->parse(rawLine));


        }
    }

    private HttpRequest parse(String line) throws IOException {
        String raw = line.endsWith("\r\n\r\n") ? line : line + "\r\n\r\n";
        return parser.parse(new ByteArrayInputStream(raw.getBytes()));
    }
}
