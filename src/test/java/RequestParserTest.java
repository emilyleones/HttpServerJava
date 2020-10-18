import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParserTest {

    @Test
    void shouldCreateRequestFromRequestString() {
        // Given
        String request = "GET /file1.txt HTTP/1.1";
        RequestParser requestParser = new RequestParser();

        // When
        Request actualRequest = requestParser.parse(request);

        // Then
        assertThat(actualRequest.getMethod()).isEqualTo("GET");
        assertThat(actualRequest.getUri()).isEqualTo("/file1.txt");
    }
}