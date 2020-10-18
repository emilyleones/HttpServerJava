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

        Request request = new Request("GET", "/");
        List<String> directoryListing = new ArrayList<>(List.of("file1.txt", "file2.txt", "subdirectory"));

        when(fileService.getResourceType(request.getUri())).thenReturn(ResourceTypeResult.DIRECTORY);
        when(fileService.getDirectoryListing(request.getUri())).thenReturn(directoryListing);

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
        assertThat(response.getContent())
                .isEqualTo("<html><body><ul><li>file1.txt</li><li>file2.txt</li><li>subdirectory</li></ul></body></html>");
    }

    @Test
    void shouldReturnResponseWithFileContentsWhenGETRequestIsOnFile() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/file1.txt");

        when(fileService.getResourceType(request.getUri())).thenReturn(ResourceTypeResult.FILE);
        when(fileService.readFile(request.getUri())).thenReturn("Hello World");

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
        assertThat(response.getContent()).isEqualTo("Hello World");
    }

    @Test
    void shouldReturn404NotFoundResponseWhenGETRequestIsOnFileThatDoesNotExist() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/fileThatDoesNotExist.txt");

        when(fileService.getResourceType(request.getUri())).thenReturn(ResourceTypeResult.NOT_FOUND);

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 404 Object Not Found");
        assertThat(response.getContent()).isEqualTo("Resource Not Found");
    }
}