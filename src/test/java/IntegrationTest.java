import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    @Test
    void shouldServeValidHttpResponseWithFileWhenGetRequestIsSent() throws IOException {
        // Given
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/file1.txt")
                .get()
                .build();

        // When
        Response response = client.newCall(request).execute();

        // Then
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("Hello World");
    }

    @Test
    void shouldServeValidHttpResponseWithFileWhenGetRequestOnSpecificResourceIsSent() throws IOException {
        // Given
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/file2.txt")
                .get()
                .build();

        // When
        Response response = client.newCall(request).execute();

        // Then
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("Not Hello World");
    }
}
