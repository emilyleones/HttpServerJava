import org.apache.commons.io.FileUtils;
import org.assertj.core.internal.IterableElementComparisonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileServiceTest {
    private static final String ROOT_DIRECTORY = System.getProperty("java.io.tmpdir") + "TestWebRoot";

    @BeforeEach
    void setUp() throws IOException {
        String resourcesDirectory = IntegrationTest.class.getClassLoader().getResource("fixtures/web").getPath();
        File source = new File(resourcesDirectory);
        File target = new File(ROOT_DIRECTORY);
        FileUtils.copyDirectory(source, target);
    }

    @Test
    void shouldReturnResourceTypeResultDirectoryWhenResourcePathIsDirectory() {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(fileService.getResourceType("/")).isEqualTo(ResourceTypeResult.DIRECTORY);
    }

    @Test
    void shouldReturnResourceTypeResultFileWhenResourcePathIsNotADirectory() {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(fileService.getResourceType("/file1.txt")).isEqualTo(ResourceTypeResult.FILE);
    }

    @Test
    void shouldReturnResourceTypeResultNotFoundWhenResourceDoesNotExist() {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(fileService.getResourceType("/fileThatDoesNotExist.txt")).isEqualTo(ResourceTypeResult.NOT_FOUND);
    }

    @Test
    void shouldProvideDirectoryListing() throws IOException {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(fileService.getDirectoryListing("/")).containsExactlyInAnyOrder("file1.txt", "file2.txt", "subdirectory");
    }

    @Test
    void shouldProvideDirectoryListingOfSubdirectory() throws IOException {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(fileService.getDirectoryListing("/subdirectory")).containsExactlyInAnyOrder("fileInSubdirectory.txt");
    }

    @Test
    void shouldReadFile() throws IOException {
        // Given
        FileService fileService = new FileService(ROOT_DIRECTORY);

        // Then
        assertThat(new String(fileService.readFile("/file1.txt"))).isEqualTo("Hello World");
    }
}