import java.io.IOException;
import java.net.ResponseCache;

public class RequestHandler {

    private FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) throws IOException {
        return new Response(null, "HTTP/1.1 200 OK", getResponseContent(request));
    }

    private String getResponseContent(Request request) throws IOException {
        if (fileService.getResourceType(request.getUri()).equals(ResourceTypeResult.DIRECTORY)) {
            StringBuilder directoryListing = new StringBuilder();
            fileService.getDirectoryListing(request.getUri())
                    .stream().sorted()
                    .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
            return String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
        } else {
            return fileService.readFile(request.getUri());
        }
    }
}
