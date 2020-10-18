import java.io.IOException;
import java.util.Map;

public class RequestHandler {
    private final FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) throws IOException {
        String statusLine = getResponseStatusLine(request.getUri());
        String content = getResponseContent(request.getUri());
        Map<String, String> headers = getResponseHeaders(content);
        return new Response(headers, statusLine, content);
    }

    private Map<String, String> getResponseHeaders(String content) {
        return Map.of("Connection", "keep-alive",
                "Content-Type", "text/html; charset=UTF-8",
                "Content-Length", String.valueOf(content.getBytes().length),
                "Keep-Alive", "timeout=5, max=1000");
    }

    private String getResponseStatusLine(String resourcePath) {
        return (fileService.getResourceType(resourcePath).equals(ResourceTypeResult.NOT_FOUND)) ?
                "HTTP/1.1 404 Object Not Found" : "HTTP/1.1 200 OK";
    }

    private String getResponseContent(String resourcePath) throws IOException {
        switch (fileService.getResourceType(resourcePath)) {
            case DIRECTORY:
                StringBuilder directoryListing = new StringBuilder();
                fileService.getDirectoryListing(resourcePath)
                        .stream().sorted()
                        .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
                return String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
            case FILE:
                return fileService.readFile(resourcePath);
            case NOT_FOUND:
                return "Resource Not Found";
            default:
                throw new IllegalStateException("No implementation for this enum value.");
        }
    }
}
