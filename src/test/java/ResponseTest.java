import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {
    @Test
    void shouldWriteFullResponseToOutputStream() throws IOException {
        // Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new Response(Map.of("Some Header", "Some Value"), "Some Status Line", "Some Content");
        String expectedStringFromStream = "Some Status Line\n" +
                "Some Header: Some Value\n\n" +
                "Some Content";

        // When
        response.sendTo(outputStream);
        String actualStringFromStream = new String(outputStream.toByteArray());

        // Then
        assertThat(actualStringFromStream).isEqualTo(expectedStringFromStream);
    }
}