import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FileService {
    private String rootDirectory;

    public FileService(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public ResourceTypeResult getResourceType(String resourcePath) {
        Path fullPath = Path.of(rootDirectory + resourcePath);
        if (Files.notExists(fullPath)) return ResourceTypeResult.NOT_FOUND;
        return Files.isDirectory(fullPath) ? ResourceTypeResult.DIRECTORY : ResourceTypeResult.FILE;
    }

    public List<String> getDirectoryListing(String resourcePath) throws IOException {
        String fullPathString = rootDirectory + resourcePath;
        Path fullPath = Path.of(fullPathString);
        System.out.println(rootDirectory);

        List<String> directoryListing = Files.list(fullPath)
                .map(Path::toString)
                .map(subPath -> {
                            String[] pathElements = subPath.split("/");
                            return pathElements[pathElements.length - 1];
                        }
                )
                .collect(toList());
        return directoryListing;
    }

    public byte[] readFile(String resourcePath) throws IOException {
        Path fullPath = Path.of(rootDirectory + resourcePath);
        return Files.readAllBytes(fullPath);
    }
}
