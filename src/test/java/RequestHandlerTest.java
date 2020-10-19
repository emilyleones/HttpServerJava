import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestHandlerTest {
    @Test
    void shouldReturnResponseWithDirectoryListingAsHtmlWhenGETRequestIsOnDirectory() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/", false);
        List<String> directoryListing = new ArrayList<>(List.of("file1.txt", "file2.txt", "subdirectory"));

        when(fileService.resolveResourceType(request.getUri())).thenReturn(ResourceTypeResult.DIRECTORY);
        when(fileService.readDirectoryListing(request.getUri())).thenReturn(directoryListing);

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
        assertThat(response.getContent())
                .isEqualTo("<html><head><title>My Http File Server</title></head><body><ul><li>file1.txt</li><li>file2.txt</li><li>subdirectory</li></ul></body></html>");
    }

    @Test
    void shouldReturnResponseWithFileContentsWhenGETRequestIsOnFile() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/file1.txt", false);

        when(fileService.resolveResourceType(request.getUri())).thenReturn(ResourceTypeResult.FILE);
        when(fileService.readFile(request.getUri())).thenReturn("Hello World");

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
        assertThat(response.getContent()).isEqualTo("Hello World");
    }

    @Test
    void shouldReturn404NotFoundResponseWhenGETRequestIsOnFileThatDoesNotExist() {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/fileThatDoesNotExist.txt", false);

        when(fileService.resolveResourceType(request.getUri())).thenReturn(ResourceTypeResult.NOT_FOUND);

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 404 Not Found");
        assertThat(response.getContent()).isEqualTo("Resource Not Found");
    }

    @Test
    void shouldReturnResponseWithValidHeadersAndEmptyContentWhenHEADRequestIsReceived() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("HEAD", "/file1.txt", false);

        when(fileService.resolveResourceType(request.getUri())).thenReturn(ResourceTypeResult.FILE);
        when(fileService.readFile(request.getUri())).thenReturn("Hello World");

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
        assertThat(response.getHeaders().get("Connection")).isEqualTo("keep-alive");
        assertThat(response.getHeaders().get("Content-Type")).isEqualTo("text/html; charset=UTF-8");
        assertThat(response.getHeaders().get("Content-Length")).isEqualTo("11");
        assertThat(response.getHeaders().get("Keep-Alive")).isEqualTo("timeout=5, max=1000");
        assertThat(response.getContent()).isEqualTo("");
    }

    @Test
    void shouldReturn500ResponseWhenAnExceptionIsThrown() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("HEAD", "/file1.txt", false);

        when(fileService.resolveResourceType(request.getUri())).thenReturn(ResourceTypeResult.FILE);
        when(fileService.readFile(request.getUri())).thenThrow(new IllegalStateException());

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 500 Internal Server Error");
        assertThat(response.getContent()).isEqualTo("An error occurred.");
    }
}