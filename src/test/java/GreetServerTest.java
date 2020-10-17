import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetServerTest {
    @Test
    public void shouldServeGreetResponseWhenClientMessageMatches() throws IOException {
        // Given
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 8080);

        // When
        String actualResponse = client.sendMessage("hello server");

        // Then
        String expectedResponse = "hello client";
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
