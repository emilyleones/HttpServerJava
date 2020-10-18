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
        assertThat(response.header("Connection")).isEqualTo("keep-alive");
        assertThat(response.header("Content-Type")).isEqualTo("text/html; charset=UTF-8");
        assertThat(response.header("Content-Length")).isEqualTo("11");
        assertThat(response.header("Keep-Alive")).isEqualTo("timeout=5, max=1000");
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
        assertThat(response.header("Connection")).isEqualTo("keep-alive");
        assertThat(response.header("Content-Type")).isEqualTo("text/html; charset=UTF-8");
        assertThat(response.header("Content-Length")).isEqualTo("15");
        assertThat(response.header("Keep-Alive")).isEqualTo("timeout=5, max=1000");
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
        assertThat(response.header("Connection")).isEqualTo("keep-alive");
        assertThat(response.header("Content-Type")).isEqualTo("text/html; charset=UTF-8");
        assertThat(response.header("Content-Length")).isEqualTo("92");
        assertThat(response.header("Keep-Alive")).isEqualTo("timeout=5, max=1000");
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

    @Test
    void shouldServeValidHttpResponseWithFileWhenHeadRequestIsSent() throws IOException {
        // Given
        TestServer testServer = new TestServer(ROOT_DIRECTORY);
        testServer.start();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/file1.txt")
                .head()
                .build();

        // When
        Response response = client.newCall(request).execute();

        // Then
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Connection")).isEqualTo("keep-alive");
        assertThat(response.header("Content-Type")).isEqualTo("text/html; charset=UTF-8");
        assertThat(response.header("Content-Length")).isEqualTo("11");
        assertThat(response.header("Keep-Alive")).isEqualTo("timeout=5, max=1000");

//        Unfortunately, OkHttpClient automatically cuts out the response body,
//        so even if the implementation returns a response body, the assertion below will still pass
//        assertThat(response.body().string()).isEqualTo("");

        testServer.stop();
    }
}
