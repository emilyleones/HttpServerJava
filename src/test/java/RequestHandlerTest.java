import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestHandlerTest {
    @Test
    void shouldReturnResponseWithDirectoryListingAsHtmlWhenGETRequestIsOnDirectory() throws IOException {
        // Given
        FileService fileService = mock(FileService.class);
        RequestHandler requestHandler = new RequestHandler(fileService);

        Request request = new Request("GET", "/");
        List<String> directoryListing = new ArrayList<>(List.of("file1.txt", "file2.txt", "subdirectory"));

        when(fileService.getDirectoryListing(request.getUri())).thenReturn(directoryListing);

        // When
        Response response = requestHandler.handle(request);

        // Then
        assertThat(response.getContent())
                .isEqualTo("<html><body><ul><li>file1.txt</li><li>file2.txt</li><li>subdirectory</li></ul></body></html>");
    }
}