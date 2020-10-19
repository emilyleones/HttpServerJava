package httpserverjava;

import httpserverjava.http.Request;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    @Test
    void shouldCreateRequestFromRequestString() {
        // Given
        String request = "GET /file1.txt HTTP/1.1";
        RequestParser requestParser = new RequestParser();

        // When
        Request actualRequest = requestParser.parse(List.of(request));

        // Then
        assertThat(actualRequest.getMethod()).isEqualTo("GET");
        assertThat(actualRequest.getUri()).isEqualTo("/file1.txt");
        assertThat(actualRequest.getKeepAlive()).isEqualTo(true);
    }

    @Test
    void shouldCreateRequestWithKeepAliveFalseWhenRequestLinesContainsConnectionCloseHeader() {
        // Given
        List<String> requestLines = List.of(
                "GET /file1.txt HTTP/1.1",
                "Connection: Close");
        RequestParser requestParser = new RequestParser();

        // When
        Request actualRequest = requestParser.parse(requestLines);

        // Then
        assertThat(actualRequest.getMethod()).isEqualTo("GET");
        assertThat(actualRequest.getUri()).isEqualTo("/file1.txt");
        assertThat(actualRequest.getKeepAlive()).isEqualTo(false);
    }

    @Test
    void shouldCreateRequestWithKeepAliveTrueWhenRequestLinesContainsConnectionKeepAliveHeader() {
        // Given
        List<String> requestLines = List.of(
                "GET /file1.txt HTTP/1.1",
                "Connection: Keep-Alive");
        RequestParser requestParser = new RequestParser();

        // When
        Request actualRequest = requestParser.parse(requestLines);

        // Then
        assertThat(actualRequest.getMethod()).isEqualTo("GET");
        assertThat(actualRequest.getUri()).isEqualTo("/file1.txt");
        assertThat(actualRequest.getKeepAlive()).isEqualTo(true);
    }
}