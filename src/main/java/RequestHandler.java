import java.io.IOException;
import java.util.Map;

public class RequestHandler {

    private FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) throws IOException {
        String statusLine = getResponseStatusLine(request);
        String content = getResponseContent(request);
        Map<String, String> headers = getResponseHeaders(content);
        return new Response(headers, statusLine, content);
    }

    private Map<String, String> getResponseHeaders(String content) {
        return Map.of("Connection", "keep-alive",
                "Content-Type", "text/html; charset=UTF-8",
                "Content-Length", String.valueOf(content.getBytes().length),
                "Keep-Alive", "timeout=5, max=1000");
    }

    private String getResponseStatusLine(Request request) {
        return (fileService.getResourceType(request.getUri()).equals(ResourceTypeResult.NOT_FOUND)) ?
                "HTTP/1.1 404 Object Not Found" : "HTTP/1.1 200 OK";
    }

    private String getResponseContent(Request request) throws IOException {
        switch (fileService.getResourceType(request.getUri())) {
            case DIRECTORY:
                StringBuilder directoryListing = new StringBuilder();
                fileService.getDirectoryListing(request.getUri())
                        .stream().sorted()
                        .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
                return String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
            case FILE:
                return fileService.readFile(request.getUri());
            case NOT_FOUND:
                return "Resource Not Found";
            default:
                throw new IllegalStateException("No implementation for this enum value.");
        }
    }
}
