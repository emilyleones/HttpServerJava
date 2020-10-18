import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    private static final String ROOT_DIRECTORY = System.getProperty("java.io.tmpdir") + File.separator + "TestWebRoot";

    @BeforeEach
    void setUp() throws IOException {
        String resourcesDirectory = IntegrationTest.class.getClassLoader().getResource("fixtures/web").getPath();
        File source = new File(resourcesDirectory);
        File target = new File(ROOT_DIRECTORY);
        FileUtils.copyDirectory(source, target);
    }

    @Test
    void shouldServeValidHttpResponseWithFileWhenGetRequestIsSent() throws IOException {
        // Given
        TestServer testServer = new TestServer(ROOT_DIRECTORY);
        testServer.start();

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

        testServer.stop();
    }

    @Test
    void shouldServeValidHttpResponseWithFileWhenGetRequestOnSpecificResourceIsSent() throws IOException {
        // Given
        TestServer testServer = new TestServer(ROOT_DIRECTORY);
        testServer.start();
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

        testServer.stop();
    }

    @Test
    void shouldServeHtmlListingWhenGetRequestOnRootDirectoryIsSent() throws IOException {
        // Given
        TestServer testServer = new TestServer(ROOT_DIRECTORY);
        testServer.start();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080")
                .get()
                .build();

        // When
        Response response = client.newCall(request).execute();

        // Then
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string())
                .isEqualTo("<html><body><ul><li>file1.txt</li><li>file2.txt</li><li>subdirectory</li></ul></body></html>");

        testServer.stop();
    }

    @Test
    void shouldServe404NotFoundResponseWhenGetRequestIsOnResourceThatDoesNotExist() throws IOException {
        // Given
        TestServer testServer = new TestServer(ROOT_DIRECTORY);
        testServer.start();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/pathDoesNotExist")
                .get()
                .build();

        // When
        Response response = client.newCall(request).execute();

        // Then
        assertThat(response.code()).isEqualTo(404);
        assertThat(response.body().string())
                .isEqualTo("Resource Not Found");

        testServer.stop();
    }
}
